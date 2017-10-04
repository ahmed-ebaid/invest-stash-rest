package com.stashinvest.db;

public class DBConstants {
    public static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String DB_NAME = "users_service_db";
    public static final String DB_USERS_TABLE = "users";
    public static final String DB_URL = "jdbc:mysql://localhost:3306";
    public static final String DB_USER = "ahmedebaid";
    public static final String DB_PASSWORD = "stash_invest";
    public static final int CONN_POOL_SIZE = 20;
    // for a reference for explanation of the following CONN_PROPERTIES, check
    // https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html
    public static final String CONN_PROPERTIES = "serverTimezone=UTC";
}
