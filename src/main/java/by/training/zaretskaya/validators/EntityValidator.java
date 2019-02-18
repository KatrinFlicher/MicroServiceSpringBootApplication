package by.training.zaretskaya.validators;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.CollectionWrongParameters;
import by.training.zaretskaya.exception.DocumentIsInvalidUnderTheScheme;
import by.training.zaretskaya.exception.ResourceIsExistException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.impl.FactoryCache;
import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EntityValidator {
    @Autowired
    @Qualifier("CollectionCachedDAO")
    CollectionDAO<Collection> collectionDAO;

    @Autowired
    @Qualifier("DocumentCachedDAO")
    DocumentDAO<Document> documentDAO;


    public void checkNewNameForTable(String newName) {
        try {
            collectionDAO.getById(newName);
            throw new ResourceIsExistException(Constants.RESOURCE_COLLECTION, newName);
        } catch (ResourceNotFoundException e) {
            //We can update name of table
            //I know that it's bad practice to leave the block "catch" empty, but I don't assume what is written here.
        }
    }

    public void checkValidationCacheLimit(int cacheLimit) {
        if (cacheLimit < 0) {
            throw new CollectionWrongParameters(Constants.NEGATIVE_CACHE_LIMIT, String.valueOf(cacheLimit));
        }
    }

    public void checkValidationAlgorithm(String algorithm) {
        try {
            FactoryCache.TypeCache.valueOf(algorithm);
        } catch (IllegalArgumentException e) {
            throw new CollectionWrongParameters(Constants.INCOMPATIBLE_FORMAT_CACHE_ALGORITHM, algorithm);
        }
    }

    public void checkExistenceOfCollection(String collectionName) {
        collectionDAO.getById(collectionName);
    }

    public void checkExistenceOfCollectionAndDocument(String collectionName, String documentName) {
        checkExistenceOfCollection(collectionName);
        documentDAO.get(collectionName, documentName);
    }

    public void checkAbsenceOfNewDocumentInTheTable(String collectionName, String documentName) {
        try {
            documentDAO.get(collectionName, documentName);
            throw new ResourceIsExistException(Constants.RESOURCE_DOCUMENT, documentName);
        } catch (ResourceNotFoundException e) {
            //Also
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
