package com.stashinvest.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.stashinvest.http.AccountKeyCallableTask;
import com.stashinvest.jdbc.ConnectionManager;
import com.stashinvest.rest.User;
import com.stashinvest.rest.Users;
import com.stashinvest.util.HashHelper;
import com.stashinvest.util.RetryerHelper;
import com.stashinvest.util.VerificationUtil;
import com.stashinvest.db.Updatable;

public class DBHelper implements Updatable<User> {
	private static final Logger log = Logger.getLogger(DBHelper.class);
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private List<String> errorMessages;

	public DBHelper() throws SQLException {
		initializeDatabase();
		errorMessages = new ArrayList<>();
	}

	private synchronized void setConnection() throws SQLException {
		connection = ConnectionManager.Instance.getConnection();
	}

	public void initializeDatabase() throws SQLException {
		try {
			setConnection();
			statement = connection.createStatement();
			statement.executeUpdate(String.format(
					"CREATE DATABASE IF NOT EXISTS %s", DBConstants.DB_NAME));
			statement.executeUpdate(String.format(
					"CREATE TABLE IF NOT EXISTS %s.%s("
							+ "id INT NOT NULL AUTO_INCREMENT,"
							+ "email VARCHAR(200) NOT NULL UNIQUE,"
							+ "phone_number VARCHAR(20) NOT NULL UNIQUE,"
							+ "full_name VARCHAR(200),"
							+ "password VARCHAR(100) NOT NULL,"
							+ "%3$skey%3$s VARCHAR(100) NOT NULL UNIQUE,"
							+ "account_key VARCHAR(100) UNIQUE,"
							+ "metadata VARCHAR(2000)," + "PRIMARY KEY(id))",
					DBConstants.DB_NAME, DBConstants.DB_USERS_TABLE, "`"));
		} catch (SQLException e) {
			log.error("Error creating database: " + DBConstants.DB_NAME);
			throw new SQLException();
		} finally {
			close();
		}
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	/**
	 * @return DB users sorted by the id in DESC order
	 * @throws SQLException
	 */
	public synchronized Users getUsersByCreationTime() throws SQLException {
		try {
			setConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(String.format(
					"SELECT * FROM %s.%s ORDER BY id DESC",
					DBConstants.DB_NAME, DBConstants.DB_USERS_TABLE));
			return getUsersObjectFromResultSet(resultSet);
		} catch (SQLException e) {
			log.error("Error getting users from database", e);
			throw new SQLException();
		} finally {
			close();
		}
	}

	public synchronized Users getUsersFilteredByQuery(String query)
			throws SQLException {
		try {
			setConnection();
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery(String
							.format("SELECT * FROM %1$s.%2$s WHERE %3$s like %4$s%5$s%6$s%5$s%4$s OR %7$s = %4$s%6$s%4$s OR %8$s = %4$s%6$s%4$s ORDER BY id DESC",
									DBConstants.DB_NAME,
									DBConstants.DB_USERS_TABLE, "metadata",
									"'", "%", query, "full_name", "email"));

			return getUsersObjectFromResultSet(resultSet);
		} catch (SQLException e) {
			log.error("Error getting users from database filtered by query"
					+ query, e);
			throw new SQLException();
		} finally {
			close();
		}
	}

	public synchronized void updateUserAccountKey(User user) {
		if (user != null) {
			try {
				setConnection();
				preparedStatement = connection
						.prepareStatement(String
								.format("update %1$s.%2$s set account_key = %3$s%4$s%3$s where email = %3$s%5$s%3$s",
										DBConstants.DB_NAME,
										DBConstants.DB_USERS_TABLE, "'",
										user.getAccountKey(), user.getEmail()));

				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				log.error(
						"Error update user account_key with email"
								+ user.getEmail(), e);
			} finally {
				close();
			}
		}
	}

	public synchronized void generateUserAccountKey(User user) {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(() -> {
			new RetryerHelper<User>(this).retry(5, TimeUnit.SECONDS, 30,
					new AccountKeyCallableTask(user), IOException.class);
		});
		executorService.shutdown();
	}

	public synchronized Users addUser(User user) throws SQLException {
		try {
			setConnection();
			preparedStatement = connection
					.prepareStatement("INSERT INTO users_service_db.users VALUES (default, ?, ?, ?, ? , ?, ?, ?)");
			preparedStatement.setString(1, user.getEmail());
			preparedStatement.setString(2, user.getPhoneNumber());
			preparedStatement.setString(3, user.getFullName());
			preparedStatement.setString(4,
					HashHelper.getSaltedPassword(user.getPassword()));
			String key = HashHelper.getSHA256Hash(user.getEmail()
					+ user.getPhoneNumber());
			preparedStatement.setString(5, key);
			preparedStatement.setString(6, null);
			preparedStatement.setString(7, user.getMetadata());
			VerificationUtil.verifyValidUserParameters(user, errorMessages);
			preparedStatement.executeUpdate();
			log.info("Generating user account key for: " + user);
			// Generate a new user that only contains email + key to retrieve
			// account_key information
			User newUser = new User();
			newUser.setEmail(user.getEmail());
			newUser.setKey(key);
			generateUserAccountKey(newUser);
			preparedStatement = connection
					.prepareStatement(String
							.format("SELECT * FROM users_service_db.users WHERE email = %1$s%2$s%1$s",
									"'", user.getEmail()));
			resultSet = preparedStatement.executeQuery();
			return getUsersObjectFromResultSet(resultSet);
		} catch (SQLException e) {
			errorMessages.add(e.getMessage());
			// return a null user object if errorMessages size is greater than 1
			// which indicates that verification has failed on user parameters
			// or
			// a duplicate entry already exist in the users table
			if (errorMessages.size() > 1
					|| e.getMessage().contains("Duplicate")) {
				log.error(String.format("Error adding user %s to the database",
						user), e);
				return null;
			} else {
				throw new SQLException(e.getMessage());
			}
		} finally {
			close();
		}
	}

	/**
	 * @param resultSet
	 *            ResultSet object containing DB rows
	 * @return Users object
	 * @throws SQLException
	 */
	private Users getUsersObjectFromResultSet(ResultSet resultSet)
			throws SQLException {
		Users users = new Users();
		List<User> usersList = new ArrayList<>();
		while (resultSet.next()) {
			User user = new User();
			user.setEmail(resultSet.getString("email"));
			user.setPhoneNumber(resultSet.getString("phone_number"));
			user.setFullName(resultSet.getString("full_name"));
			user.setPassword(resultSet.getString("password"));
			user.setKey(resultSet.getString("key"));
			user.setAccountKey(resultSet.getString("account_key"));
			user.setMetadata(resultSet.getString("metadata"));
			usersList.add(user);
		}
		users.setUsers(usersList);
		return users;
	}

	/**
	 * Closes all open connections
	 */
	private synchronized void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			Logger.getLogger(getClass()).error("Error closing connection", e);
		}
	}

	@Override
	public void update(User user) {
		updateUserAccountKey(user);
	}
}
