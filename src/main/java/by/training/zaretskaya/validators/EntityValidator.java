package by.training.zaretskaya.validators;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.CollectionWrongParameters;
import by.training.zaretskaya.exception.DocumentIsInvalidUnderTheScheme;
import by.training.zaretskaya.exception.ResourceIsExistException;
import by.training.zaretskaya.cache.FactoryCache;
import by.training.zaretskaya.dao.CollectionDAO;
import by.training.zaretskaya.dao.DocumentDAO;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EntityValidator {
    private static final Logger log = LogManager.getLogger(EntityValidator.class);
    private CollectionDAO<Collection> collectionDAO;
    private DocumentDAO<Document> documentDAO;

    @Autowired
    public EntityValidator(@Qualifier("CollectionCachedDAO") CollectionDAO<Collection> collectionDAO,
                           @Qualifier("DocumentCachedDAO") DocumentDAO<Document> documentDAO){
        this.collectionDAO = collectionDAO;
        this.documentDAO = documentDAO;
    }


    public void checkNewNameForTable(String newName) {
        if (collectionDAO.contains(newName)) {
            log.error("Collection is already exist in Data Base");
            throw new ResourceIsExistException(Constants.RESOURCE_COLLECTION, newName);
        }
    }

    public void checkValidationCollection(Collection collection) {
        if (collection.getCacheLimit() < 0) {
            log.error("Request with wrong value for cache limit");
            throw new CollectionWrongParameters(Constants.NEGATIVE_CACHE_LIMIT, String.valueOf(collection.getCacheLimit()));
        }
        try {
            FactoryCache.TypeCache.valueOf(collection.getCacheAlgorithm().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Request with wrong value for cache cacheAlgorithm", e);
            throw new CollectionWrongParameters(Constants.INCOMPATIBLE_FORMAT_CACHE_ALGORITHM, collection.getCacheAlgorithm());
        }
    }

    public void checkExistenceOfCollection(String collectionName) {
        collectionDAO.getById(collectionName);
    }

    public void checkAbsenceOfNewDocumentInTheTable(String collectionName, String documentName) {
        if (documentDAO.contains(collectionName, documentName)) {
            log.error("Document is already exist in Data Base");
            throw new ResourceIsExistException(Constants.RESOURCE_DOCUMENT, documentName);
        }
    }

    public void validationDocumentUnderTheScheme(String nameCollection, Document document) {
        String jsonSchemaString = collectionDAO.getById(nameCollection).getJsonScheme();
        JSONObject jsonSchema = new JSONObject(jsonSchemaString);
        Schema schema = SchemaLoader.load(jsonSchema);
        try {
            schema.validate(document.getValue());
        } catch (ValidationException exception) {
            throw new DocumentIsInvalidUnderTheScheme();
        }
    }
}
