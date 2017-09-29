package com.stashinvest.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import com.stashinvest.db.DBConstants;

public enum ConnectionManager {
	Instance;
	private BasicDataSource bds = new BasicDataSource();

	ConnectionManager() {
		// Set database driver name
		bds.setDriverClassName(DBConstants.DRIVER_CLASS_NAME);

		// Set database url
		bds.setUrl(DBConstants.DB_URL);

		// Set database user
		bds.setUsername(DBConstants.DB_USER);

		// Set database password
		bds.setPassword(DBConstants.DB_PASSWORD);

		// Set the connection pool size
		bds.setInitialSize(DBConstants.CONN_POOL_SIZE);
	}

	public Connection getConnection() throws SQLException {
		return bds.getConnection();
	}
}
