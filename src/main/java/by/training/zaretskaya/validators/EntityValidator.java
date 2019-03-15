package by.training.zaretskaya.validators;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.CollectionWrongParameters;
import by.training.zaretskaya.exception.DocumentIsInvalidUnderTheScheme;
import by.training.zaretskaya.exception.ResourceIsExistException;
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
        if (collectionDAO.consist(newName)) {
            throw new ResourceIsExistException(Constants.RESOURCE_COLLECTION, newName);
        }
    }

    public void checkValidationCollection(Collection collection) {
        if (collection.getCacheLimit() < 0) {
            throw new CollectionWrongParameters(Constants.NEGATIVE_CACHE_LIMIT, String.valueOf(collection.getCacheLimit()));
        }
        try {
            FactoryCache.TypeCache.valueOf(collection.getAlgorithm());
        } catch (IllegalArgumentException e) {
            throw new CollectionWrongParameters(Constants.INCOMPATIBLE_FORMAT_CACHE_ALGORITHM, collection.getAlgorithm());
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
        if (documentDAO.consist(collectionName, documentName)) {
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
