package by.training.zaretskaya.services;

import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import by.training.zaretskaya.validators.EntityValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceImplTest {
    private Document mockDocument = new Document("Popova", "Intern 2 cat");

    private String nameCollection = "cats";

    @Mock
    DocumentDAO<Document> documentDAO;

    @Mock
    EntityValidator validator;

    @InjectMocks
    DocumentServiceImpl documentService;

    @Test
    public void testCreate() {
        documentService.create(nameCollection, mockDocument);
        verify(validator).checkExistenceOfCollection(nameCollection);
        verify(validator).checkAbsenceOfNewDocumentInTheTable(nameCollection, mockDocument.getKey());
        verify(validator).validationDocumentUnderTheScheme(nameCollection, mockDocument);
        verify(documentDAO).create(nameCollection, mockDocument);
    }

    @Test
    public void testGet() {
        when(documentDAO.get(nameCollection, mockDocument.getKey())).thenReturn(mockDocument);
        Document document = documentService.get(nameCollection, mockDocument.getKey());
        verify(validator).checkExistenceOfCollection(nameCollection);
        assertEquals(mockDocument, document);
    }

    @Test
    public void testDelete() {
        documentService.delete(nameCollection, mockDocument.getKey());
        verify(validator).checkExistenceOfCollectionAndDocument(nameCollection, mockDocument.getKey());
        verify(documentDAO).delete(nameCollection, mockDocument.getKey());
    }

    @Test
    public void testUpdate() {
        documentService.update(nameCollection, mockDocument.getKey(), mockDocument);
        verify(validator).checkExistenceOfCollectionAndDocument(nameCollection, mockDocument.getKey());
        verify(documentDAO).update(nameCollection, mockDocument.getKey(), mockDocument);
    }

    @Test
    public void testList() {
        List<Document> expectedDocuments = Arrays.asList(mockDocument, mockDocument, mockDocument);
        when(documentDAO.list(nameCollection , 1, 10)).thenReturn(expectedDocuments);
        List documents = documentService.list(nameCollection, 1, 10);
        verify(validator).checkExistenceOfCollection(nameCollection);
        assertEquals(expectedDocuments, documents);
    }
}