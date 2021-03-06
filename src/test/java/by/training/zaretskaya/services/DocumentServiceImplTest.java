package by.training.zaretskaya.services;

import by.training.zaretskaya.dao.DocumentDAO;
import by.training.zaretskaya.models.Document;
import by.training.zaretskaya.validators.EntityValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceImplTest {
    @Mock
    DocumentDAO<Document> documentDAO;
    @Mock
    EntityValidator validator;
    @InjectMocks
    DocumentServiceImpl documentService;
    private Document mockDocument = new Document("Popova", "Intern 2 cat");
    private String nameCollection = "cats";

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
        verify(documentDAO).delete(nameCollection, mockDocument.getKey());
    }

    @Test
    public void testUpdate() {
        documentService.update(nameCollection, mockDocument.getKey(), mockDocument);
        verify(documentDAO).update(nameCollection, mockDocument.getKey(), mockDocument);
    }

    @Test
    public void testList() {
        List<Document> expectedDocuments = Arrays.asList(mockDocument, mockDocument, mockDocument);
        when(documentDAO.list(nameCollection, "", 10)).thenReturn(expectedDocuments);
        List documents = documentService.list(nameCollection, "", 10);
        verify(validator).checkExistenceOfCollection(nameCollection);
        assertEquals(expectedDocuments, documents);
    }
}