package com.stashinvest.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import com.stashinvest.helper.HashHelper;
import com.stashinvest.rest.User;
import com.stashinvest.rest.Users;

public class DBHelper {
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private List<String> errorStrings;

	public DBHelper() throws SQLException {
		initializeConnection();
		errorStrings = new ArrayList<>();
	}

	public List<String> getErrorStrings() {
		return errorStrings;
	}

	public void setErrorStrings(String error) {
		errorStrings.add(error);
	}

	private Connection initializeConnection() throws SQLException {
		return connection = DataSource.Instance.getBds().getConnection();
	}

	public synchronized Users getUsersByCreationTime() throws SQLException {
		try {
			// Statements allow to issue SQL queries to the database
			statement = connection.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery(String.format(
					"SELECT * FROM %s.%s ORDER BY id DESC",
					DBConstants.DB_NAME, DBConstants.DB_USERS_TABLE));
			return getUsersObjectFromResultSet(resultSet);
		} finally {
			close();
		}
	}

	public synchronized Users getUsersFilteredByQuery(String query)
			throws SQLException {
		try {
			// Statements allow to issue SQL queries to the database
			statement = connection.createStatement();
			// Result set get the result of the SQL query
			String dbQuery = String
					.format("SELECT * FROM %1$s.%2$s WHERE %3$s like %4$s%5$s%6$s%5$s%4$s OR %7$s = %4$s%6$s%4$s OR %8$s = %4$s%6$s%4$s ORDER BY id DESC",
							DBConstants.DB_NAME, DBConstants.DB_USERS_TABLE,
							"metadata", "'", "%", query, "full_name", "email");
			resultSet = statement.executeQuery(dbQuery);

			return getUsersObjectFromResultSet(resultSet);
		} finally {
			close();
		}
	}

	public synchronized Users addUser(User user) {
		try {
			preparedStatement = connection
					.prepareStatement("INSERT INTO users_service_db.users VALUES (default, ?, ?, ?, ? , ?, ?, ?)");
			preparedStatement.setString(1, user.getEmail());
			preparedStatement.setString(2, user.getPhoneNumber());
			preparedStatement.setString(3, user.getFullName());
			preparedStatement.setString(4,
					HashHelper.getSaltedPassword(user.getPassword()));
			preparedStatement.setString(
					5,
					HashHelper.getSHA256Hash(user.getEmail()
							+ user.getPhoneNumber()));
			preparedStatement.setString(6, null);
			preparedStatement.setString(7, user.getMetadata());
			preparedStatement.executeUpdate();
			preparedStatement = connection
					.prepareStatement(String
							.format("SELECT * FROM users_service_db.users WHERE email = %1$s%2$s%1$s",
									"'", user.getEmail()));
			resultSet = preparedStatement.executeQuery();
			return getUsersObjectFromResultSet(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return null;
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

	public boolean isValidQuery(String query) {
		return !query.isEmpty() && (query.length() <= 2000);
	}

	private void close() {
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
}
