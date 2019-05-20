package by.training.zaretskaya.constants;

public class Constants {
    public static final String PATTERN_FOR_NAME_COLLECTION = "^[\\w]{3,20}$";
    public static final String RESOURCE_COLLECTION = "Collection";
    public static final String RESOURCE_DOCUMENT = "Document";

    public static final Integer MAX_SIZE_FOR_CACHE_COLLECTIONS = 50;
    public static final String DEFAULT_OBJECT_TO_COMPARE = "";
    public static final String DEFAULT_LIMIT_SIZE = "4";


    //Exceptions
    public static final String NEGATIVE_CACHE_LIMIT = "Cache limit with $value is impossible.";
    public static final String INCOMPATIBLE_FORMAT_CACHE_ALGORITHM = "Format $value for cache algorithm is incompatible.";
    public static final String COLLECTION_NAME_NOT_SUPPORTED = "This name for collection is not supported.";
    public static final String DOCUMENT_IS_INVALID_UNDER_THE_SCHEME = "Document is not supported to the json scheme.";

    public static final boolean ROLLBACK_ON = true;
    public static final boolean REPLICA_ON = true;
    public static final boolean MAIN_GROUP_OFF = false;

    public static final int POSITION_ID_COLLECTION = 0;
    public static final int POSITION_ID_DOCUMENT = 1;
    public static final String NAME_APPLICATION = "/rest";
    public static final String DOCUMENTS = "/docs/";



}
