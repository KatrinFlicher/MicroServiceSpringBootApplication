package by.training.zaretskaya.services;

import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.validators.EntityValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionServiceImplTest {
//    @Mock
//    CollectionDAO<Collection> collectionDAO;
//    @Mock
//    EntityValidator validator;
//    @InjectMocks
//    CollectionServiceImpl collectionService;
//    private Collection mockCollection = new Collection("cats", 12, "LFU", "{\n" +
//            "  \"type\": \"string\",\n" +
//            "  \"minLength\": 2,\n" +
//            "  \"maxLength\": 3\n" +
//            "}");
//
//    @Test
//    public void testCreate() {
//        collectionService.create(mockCollection);
//        verify(validator).checkNewNameForTable(mockCollection.getName());
//        verify(validator).checkValidationCacheLimit(mockCollection.getCacheLimit());
//        verify(validator).checkValidationAlgorithm(mockCollection.getAlgorithm());
//        verify(collectionDAO).create(mockCollection);
//    }
//
//    @Test
//    public void testGetById() {
//        when(collectionDAO.getById(Mockito.anyString())).thenReturn(mockCollection);
//        Collection actualCollection = collectionService.getById(mockCollection.getName());
//        assertEquals(mockCollection, actualCollection);
//    }
//
//    @Test
//    public void testDelete() {
//        collectionService.delete(Mockito.anyString());
//        verify(collectionDAO).delete(Mockito.anyString());
//    }
//
//    @Test
//    public void testUpdateName() {
//        collectionService.updateName(mockCollection.getName(), Mockito.anyString());
//        verify(validator).checkNewNameForTable(Mockito.anyString());
//        verify(collectionDAO).updateName(Mockito.anyString(), Mockito.anyString());
//    }
//
//    @Test
//    public void testUpdateCacheLimit() {
//        collectionService.updateCacheLimit(mockCollection.getName(), Mockito.anyInt());
//        verify(validator).checkValidationCacheLimit(Mockito.anyInt());
//        verify(collectionDAO).updateCacheLimit(Mockito.anyString(), Mockito.anyInt());
//    }
//
//    @Test
//    public void testUpdateAlgorithm() {
//        collectionService.updateAlgorithm(mockCollection.getName(), Mockito.anyString());
//        verify(validator).checkValidationAlgorithm(Mockito.anyString());
//        verify(collectionDAO).updateAlgorithm(Mockito.anyString(), Mockito.anyString());
//    }
//
//    @Test
//    public void testListCollections() {
//        List<Collection> expectedCollections = Arrays.asList(mockCollection, mockCollection, mockCollection);
//        when(collectionDAO.list(Mockito.anyInt(), Mockito.anyInt())).thenReturn(expectedCollections);
//        List<Collection> collections = collectionService.listCollections(1, 10);
//        assertEquals(expectedCollections, collections);
//    }
}