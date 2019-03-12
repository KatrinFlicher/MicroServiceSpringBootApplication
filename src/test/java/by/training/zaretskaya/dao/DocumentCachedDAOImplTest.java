package by.training.zaretskaya.dao;

import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.impl.FactoryCache;
import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DocumentCachedDAOImplTest {

    private Document mockDocument = new Document("Popova", "Intern 2 cat");

    private Collection mockCollection = new Collection("cats", 12, "LFU", "{\n" +
            "  \"type\": \"string\",\n" +
            "  \"minLength\": 2,\n" +
            "  \"maxLength\": 3\n" +
            "}");

    @Mock
    private DocumentDAO documentDAO;

    @Mock
    private CollectionDAO collectionDAO;

    @InjectMocks
    private DocumentCachedDAOImpl documentCachedDAO;

    @Before
    public void tuneUpMockCollection() {
        mockCollection.setCache(FactoryCache
                .createCache(mockCollection.getAlgorithm(), mockCollection.getCacheLimit()));
        Mockito.when(collectionDAO.getById(Mockito.anyString())).thenReturn(mockCollection);
    }

    @Test
    public void testCreateDocument() {
        documentCachedDAO.create(mockCollection.getName(), mockDocument);
        verify(documentDAO).create(mockCollection.getName(), mockDocument);
        assertEquals(mockDocument, documentCachedDAO.get(mockCollection.getName(), mockDocument.getKey()));
        verify(documentDAO, times(0))
                .get(mockCollection.getName(), mockDocument.getKey());
    }

    @Test
    public void testGetDocument() {
        Mockito.when(documentDAO.get(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockDocument);
        assertEquals(mockDocument, documentCachedDAO.get(mockCollection.getName(), mockDocument.getKey()));
        documentCachedDAO.get(mockCollection.getName(), mockDocument.getKey());
        verify(documentDAO, times(1)).get(mockCollection.getName(), mockDocument.getKey());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteDocument() {
        documentCachedDAO.create(mockCollection.getName(), mockDocument);
        Mockito.when(
                documentDAO.get(Mockito.anyString(), Mockito.anyString())).thenThrow(ResourceNotFoundException.class);
        documentCachedDAO.delete(mockCollection.getName(), mockDocument.getKey());
        verify(documentDAO).delete(mockCollection.getName(), mockDocument.getKey());
        documentCachedDAO.get(mockCollection.getName(), mockDocument.getKey());
    }

    @Test
    public void testUpdateDocument() {
        String newDocumentValue = "Doctor";
        documentCachedDAO.create(mockCollection.getName(), mockDocument);
        documentCachedDAO.update(mockCollection.getName(), mockDocument.getKey(),
                new Document(mockDocument.getKey(), newDocumentValue));
        verify(documentDAO).update(Mockito.anyString(), Mockito.anyString(), Mockito.any(Document.class));
        assertEquals(newDocumentValue,
                documentCachedDAO.get(mockCollection.getName(), mockDocument.getKey()).getValue());
    }

//    @Test
//    public void testListDocuments() {
//        List<Document> documents = Arrays.asList(mockDocument, mockDocument, mockDocument);
//        Mockito.when(documentDAO.list(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
//                .thenReturn(documents);
//        assertEquals(documents, documentCachedDAO.list(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()));
//    }
}