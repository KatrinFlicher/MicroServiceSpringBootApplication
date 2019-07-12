package by.training.zaretskaya.dao;

import by.training.zaretskaya.cache.ICache;
import by.training.zaretskaya.cache.LFUCacheImpl;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.models.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CollectionCachedDAOImplTest {

    private Collection mockCollection = new Collection("cats", 12, "LFU", "{\n" +
            "  \"type\": \"string\",\n" +
            "  \"minLength\": 2,\n" +
            "  \"maxLength\": 3\n" +
            "}");
    @Mock
    @Qualifier("CollectionDAO")
    private CollectionDAO collectionDAO;
    private ICache<String, Collection> mapCollection;
    private CollectionCachedDAOImpl collectionCachedDAO;

    @Before
    public void setUp() {
        mapCollection = new LFUCacheImpl(Constants.MAX_SIZE_FOR_CACHE_COLLECTIONS);
        collectionCachedDAO = new CollectionCachedDAOImpl(collectionDAO, mapCollection);
    }

    @Test
    public void testGetCollectionById() {
        Mockito.when(collectionDAO.getById(Mockito.anyString())).thenReturn(mockCollection);
        assertEquals(mockCollection, collectionCachedDAO.getById(mockCollection.getName()));
        verify(collectionDAO, times(1)).getById(mockCollection.getName());
    }

    @Test
    public void testCreateCollection() {
        collectionCachedDAO.create(mockCollection);
        verify(collectionDAO).create(mockCollection);
//        assertEquals(mockCollection, collectionCachedDAO.getById(mockCollection.getName()));
//        verify(collectionDAO, times(0)).getById(mockCollection.getName());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteCollection() {
        collectionCachedDAO.create(mockCollection);
        Mockito.when(
                collectionDAO.getById(Mockito.anyString())).thenThrow(ResourceNotFoundException.class);
        collectionCachedDAO.delete(mockCollection.getName());
        verify(collectionDAO).delete(mockCollection.getName());
        collectionCachedDAO.getById(mockCollection.getName());
    }

    @Test
    public void testUpdateCollection() {
        int newValueCacheLimit = 50;
        String newValueAlgorithm = "LRU";
        Collection collection = new Collection(mockCollection);
        collection.setCacheAlgorithm(newValueAlgorithm);
        collection.setCacheLimit(newValueCacheLimit);
        collectionCachedDAO.create(mockCollection);
        collectionCachedDAO.update(mockCollection.getName(), collection);
        verify(collectionDAO).update(Mockito.anyString(), Mockito.any(Collection.class));
    }

    @Test
    public void testListCollections() {
        List<Collection> collections = Arrays.asList(mockCollection, mockCollection, mockCollection);
        Mockito.when(collectionDAO.list(Mockito.anyString(), Mockito.anyInt())).thenReturn(collections);
        assertEquals(collections, collectionCachedDAO.list(Mockito.anyString(), Mockito.anyInt()));
    }
}