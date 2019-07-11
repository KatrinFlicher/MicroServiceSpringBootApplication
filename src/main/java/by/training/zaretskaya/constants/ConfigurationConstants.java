package by.training.zaretskaya.constants;

public class ConfigurationConstants {
    public static final String NODE_NAME = "name";
    public static final String NODE_HOST = "host";

    //Database configuration
    public static final String DATA_SOURCE_DRIVER_POSTGRESQL = "org.postgresql.Driver";
    public static final String DATA_SOURCE_URL = "url";
    public static final String DATA_SOURCE_USERNAME = "username";
    public static final String DATA_SOURCE_PASSWORD = "password";
    public static final Class DATA_SOURCE_TYPE = com.zaxxer.hikari.HikariDataSource.class;
}
