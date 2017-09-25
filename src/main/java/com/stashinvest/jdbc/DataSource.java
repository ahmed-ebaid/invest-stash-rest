package com.stashinvest.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;

public enum DataSource {
	Instance;
	private BasicDataSource bds = new BasicDataSource();

	DataSource() {
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

	public BasicDataSource getBds() {
		return bds;
	}

	public void setBds(BasicDataSource bds) {
		this.bds = bds;
	}
}
