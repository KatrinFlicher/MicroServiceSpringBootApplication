package by.training.zaretskaya.constants;

public class Constants {
    public static final String PATTERN_FOR_NAME_COLLECTION = "^[\\w]{3,20}$";
    public static final String RESOURCE_COLLECTION = "Collection";
    public static final String RESOURCE_DOCUMENT = "Document";

    public static final Integer MAX_SIZE_FOR_CACHE_COLLECTIONS = 50;
    public static final String START_PAGE = "1";
    public static final String DEFAULT_LIMIT_SIZE = "10";


    //Exceptions
    public static final String NEGATIVE_CACHE_LIMIT = "Cache limit with $value is impossible.";
    public static final String INCOMPATIBLE_FORMAT_CACHE_ALGORITHM = "Format $value for cache algorithm is incompatible.";

    public static final String VARIABLE_FIELD_NAME = "name";
    public static final String VARIABLE_FIELD_LIMIT = "name";
    public static final String VARIABLE_FIELD_ALGORITHM = "algorithm";

    public static final String ROLLBACK_ON = "true";

}
