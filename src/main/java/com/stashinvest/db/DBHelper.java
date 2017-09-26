package com.stashinvest.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import com.stashinvest.http.ServiceRequest;
import com.stashinvest.jdbc.ConnectionManager;
import com.stashinvest.rest.AccountKeyServiceRequest;
import com.stashinvest.rest.AccountKeyServiceResponse;
import com.stashinvest.rest.User;
import com.stashinvest.rest.Users;
import com.stashinvest.util.HashHelper;
import com.stashinvest.util.VerificationUtil;

public class DBHelper {
	private static final Logger log = Logger.getLogger(DBHelper.class);
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private List<String> errorMessages;

	public DBHelper() throws SQLException {
		errorMessages = new ArrayList<>();
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public synchronized Users getUsersByCreationTime() throws SQLException {
		try {
			// Statements allow to issue SQL queries to the database
			connection = ConnectionManager.Instance.getConnection();
			statement = connection.createStatement();
			// Result set get the result of the SQL query
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
			// Statements allow to issue SQL queries to the database
			connection = ConnectionManager.Instance.getConnection();
			statement = connection.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery(String
					.format("SELECT * FROM %1$s.%2$s WHERE %3$s like %4$s%5$s%6$s%5$s%4$s OR %7$s = %4$s%6$s%4$s OR %8$s = %4$s%6$s%4$s ORDER BY id DESC",
							DBConstants.DB_NAME, DBConstants.DB_USERS_TABLE,
							"metadata", "'", "%", query, "full_name", "email"));

			return getUsersObjectFromResultSet(resultSet);
		} catch (SQLException e) {
			log.error("Error getting users from database filtered by query"
					+ query, e);
			throw new SQLException();
		} finally {
			close();
		}
	}

	public synchronized void updateUserAccountKey(
			AccountKeyServiceResponse response) {
		try {
			// Statements allow to issue SQL queries to the database
			connection = ConnectionManager.Instance.getConnection();
			// Result set get the result of the SQL query
			preparedStatement = connection
					.prepareStatement(String
							.format("update %1$s.%2$s set account_key = %3$s%4$s%3$s where email = %3$s%5$s%3$s",
									DBConstants.DB_NAME,
									DBConstants.DB_USERS_TABLE, "'",
									response.getAccountKey(),
									response.getEmail()));
			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			log.error(
					"Error update user account_key with email"
							+ response.getEmail(), e);
		} finally {
			close();
		}
	}

	public synchronized Users addUser(User user) throws SQLException {
		try {
			connection = ConnectionManager.Instance.getConnection();
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
			preparedStatement = connection
					.prepareStatement(String
							.format("SELECT * FROM users_service_db.users WHERE email = %1$s%2$s%1$s",
									"'", user.getEmail()));
			resultSet = preparedStatement.executeQuery();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Callable<AccountKeyServiceResponse> yourTask = () -> {
						AccountKeyServiceRequest request = new AccountKeyServiceRequest();
						request.setEmail(user.getEmail());
						request.setKey(key);
						ServiceRequest serviceRequest = new ServiceRequest();
						return serviceRequest.post(request);
					};

					Retryer<AccountKeyServiceResponse> retryer = RetryerBuilder
							.<AccountKeyServiceResponse> newBuilder()
							.retryIfResult(
									Predicates
											.<AccountKeyServiceResponse> isNull())
							.retryIfExceptionOfType(IOException.class)
							.withWaitStrategy(
									WaitStrategies.fixedWait(300,
											TimeUnit.MILLISECONDS))
							.withStopStrategy(
									StopStrategies.stopAfterAttempt(5)).build();

					try {
						AccountKeyServiceResponse response = retryer
								.call(yourTask);
						updateUserAccountKey(response);
					} catch (RetryException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
			return getUsersObjectFromResultSet(resultSet);
		} catch (SQLException e) {
			log.error(
					String.format("Error adding user %s to the database", user),
					e);
			errorMessages.add(e.getMessage());
			if (e.getMessage().contains("Duplicate")) {
				return null;
			} else {
				throw new SQLException();
			}
		}
	}

	private Users getUsersObjectFromResultSet(ResultSet resultSet)
			throws SQLException {
		List<User> usersList = new ArrayList<>();
		// ResultSet is initially before the first data set
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
		Users users = new Users();
		users.setUsers(usersList);
		return users;
	}

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

	public static void main(String[] args) {
	}
}
