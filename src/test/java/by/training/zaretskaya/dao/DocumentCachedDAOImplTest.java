package by.training.zaretskaya.dao;

import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.cache.FactoryCache;
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
                .createCache(mockCollection.getCacheAlgorithm(), mockCollection.getCacheLimit()));
        Mockito.when(collectionDAO.getById(Mockito.anyString())).thenReturn(mockCollection);
    }

    @Test
    public void testCreateDocument() {
        documentCachedDAO.create(mockCollection.getName(), mockDocument);
        verify(documentDAO).create(mockCollection.getName(), mockDocument);
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
        Document updatedDocument = new Document(mockDocument.getKey(), "Doctor");
        String key = mockDocument.getKey();
        documentCachedDAO.update(mockCollection.getName(), key, updatedDocument);
        verify(documentDAO).update(mockCollection.getName(), key, updatedDocument);
    }

    @Test
    public void testListDocuments() {
        List<Document> documents = Arrays.asList(mockDocument, mockDocument, mockDocument);
        Mockito.when(documentDAO.list(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(documents);
        assertEquals(documents, documentCachedDAO.list(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()));
    }
}