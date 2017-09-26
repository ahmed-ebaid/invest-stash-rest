package com.stashinvest.db;

public class DBConstants {
	public static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	public static final String DB_NAME = "users_service_db";
	public static final String DB_USERS_TABLE = "users";
	public static final String DB_URL = "jdbc:mysql://localhost:3306/"
			+ DB_NAME
			+ "?autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	public static final String DB_USER = "ahmedebaid";
	public static final String DB_PASSWORD = "stash_invest";
	public static final int CONN_POOL_SIZE = 20;
}
