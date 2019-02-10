package by.training.zaretskaya.services;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.DocumentIsInvalidUnderTheScheme;
import by.training.zaretskaya.exception.DocumentNotFoundException;
import by.training.zaretskaya.exception.ResourceIsExistException;
import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.interfaces.IDocumentService;
import by.training.zaretskaya.models.Document;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentServiceImpl implements IDocumentService {

    @Autowired
    @Qualifier("CollectionCachedDAO")
    CollectionDAO collectionDAO;

    @Autowired
    @Qualifier("DocumentCachedDAO")
    DocumentDAO documentDAO;


    @Override
    public void create(String nameCollection, Document document) {
        try {
            checkExistenceOfDocument(nameCollection, document.getKey());
            throw new ResourceIsExistException(Constants.RESOURCE_DOCUMENT, document.getKey());
        } catch (DocumentNotFoundException e) {
            validationDocument(document, collectionDAO.getById(nameCollection).getJsonScheme());
            documentDAO.create(nameCollection, document);
        }
    }

    @Override
    public Document get(String nameCollection, String nameResource) {
        return documentDAO.get(nameCollection, nameResource);
    }

    @Override
    public void delete(String nameCollection, String nameResource) {
        checkExistenceOfDocument(nameCollection, nameResource);
        documentDAO.delete(nameCollection, nameResource);
    }

    @Override
    public void update(String nameCollection, String nameResource, Document document) {
        checkExistenceOfDocument(nameCollection, nameResource);
        validationDocument(document, collectionDAO.getById(nameCollection).getJsonScheme());
        documentDAO.update(nameCollection, nameResource, document);
    }

    @Override
    public List list(String nameCollection, int page, int size) {
        return documentDAO.list(nameCollection, page, size);
    }

    private void checkExistenceOfDocument(String nameCollection, String nameResource) {
        get(nameCollection, nameResource);
    }

    private void validationDocument(Document document, String jsonSchemaString) {
        JSONObject jsonSchema = new JSONObject(jsonSchemaString);
        Schema schema = SchemaLoader.load(jsonSchema);
        try {
            if (document.getValue().getClass() == String.class) {
                schema.validate(document.getValue());
            } else {
                JSONObject jsonSubject = new JSONObject(document);
                schema.validate(jsonSubject);
            }

        } catch (ValidationException exception) {
            throw new DocumentIsInvalidUnderTheScheme();
        }
    }
}
