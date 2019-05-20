package by.training.zaretskaya.services;

import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.interfaces.IDocumentService;
import by.training.zaretskaya.models.Document;
import by.training.zaretskaya.validators.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("DocumentService")
public class DocumentServiceImpl implements IDocumentService<Document> {

    private DocumentDAO<Document> documentDAO;
    private EntityValidator validator;

    @Autowired
    public DocumentServiceImpl(@Qualifier("DocumentCachedDAO") DocumentDAO<Document> documentDAO,
                               EntityValidator validator) {
        this.documentDAO = documentDAO;
        this.validator = validator;
    }

    @Override
    public void create(String nameCollection, Document document) {
        validator.checkExistenceOfCollection(nameCollection);
        validator.checkAbsenceOfNewDocumentInTheTable(nameCollection, document.getKey());
        validator.validationDocumentUnderTheScheme(nameCollection, document);
        documentDAO.create(nameCollection, document);
    }

    @Override
    public Document get(String nameCollection, String nameResource) {
        validator.checkExistenceOfCollection(nameCollection);
        return documentDAO.get(nameCollection, nameResource);
    }

    @Override
    public void delete(String nameCollection, String nameResource) {
        documentDAO.delete(nameCollection, nameResource);
    }

    @Override
    public void update(String nameCollection, String nameResource, Document document) {
        validator.validationDocumentUnderTheScheme(nameCollection, document);
        documentDAO.update(nameCollection, nameResource, document);
    }

    @Override
    public List<Document> list(String nameCollection, String objectToCompare, int size) {
        validator.checkExistenceOfCollection(nameCollection);
        return documentDAO.list(nameCollection, objectToCompare, size);
    }
}
