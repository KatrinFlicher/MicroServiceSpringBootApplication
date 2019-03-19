package by.training.zaretskaya.constants;

public class SQLConstants {
    //For Collection
    public static final String CREATE_NAMED_TABLE_FOR_DOCUMENTS =
            "CREATE TABLE $tableName(key VARCHAR (255) PRIMARY KEY,value text NOT NULL)";
    public static final String DROP_NAMED_DOCUMENT_TABLE = "DROP TABLE $tableName";

    //For Document
    public static final String INSERT_DOCUMENT_TO_TABLE = "INSERT INTO $tableName VALUES (?, ?)";
    public static final String SELECT_DOCUMENT_BY_KEY = "SELECT key, value FROM $tableName WHERE key = ?";
    public static final String DELETE_DOCUMENT_BY_KEY = "DELETE FROM $tableName WHERE key = ?";
    public static final String UPDATE_DOCUMENT_BY_KEY = "UPDATE $tableName SET value=? WHERE key = ?";
    public static final String SELECT_ALL_DOCUMENTS_FROM_TABLE = "SELECT key, value FROM $tableName WHERE key > ? " +
            "ORDER BY key LIMIT ?";
    public static final String MOCK_NAME_COLLECTION = "$tableName";

    public static final String DOCUMENT_KEY = "key";
    public static final String DOCUMENT_VALUE = "value";

}
