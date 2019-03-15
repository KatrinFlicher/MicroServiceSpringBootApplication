package by.training.zaretskaya.controllers;

import by.training.zaretskaya.distribution.DistributedService;
import by.training.zaretskaya.distribution.RollbackService;
import by.training.zaretskaya.interfaces.ICollectionService;
import by.training.zaretskaya.models.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CollectionController.class, secure = false)
public class CollectionControllerTest {

    private Collection mockCollection = new Collection("cats", 12, "LFU", "{\n" +
            "  \"type\": \"string\",\n" +
            "  \"minLength\": 2,\n" +
            "  \"maxLength\": 3\n" +
            "}");
    private String exampleCollectionJson = " {\n" +
            "        \"name\": \"cats\",\n" +
            "        \"cacheLimit\": 12,\n" +
            "        \"algorithm\": \"LFU\"\n" +
            "    }";

    String jo = "{\n" +
            "  \"type\": \"string\",\n" +
            "  \"minLength\": 2,\n" +
            "  \"maxLength\": 30\n" +
            "}";

//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private ICollectionService collectionService;
//    @MockBean
//    DistributedService distributedService;
//    @MockBean
//    RollbackService rollbackService;
//
//    @Test
//    public void testCreateCollection() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .post("/rest")
//                .accept(MediaType.APPLICATION_JSON)
//                .content(exampleCollectionJson)
//                .contentType(MediaType.APPLICATION_JSON);
//        when(distributedService.isMyGroup(Mockito.anyString())).thenReturn(true);
//        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//        MockHttpServletResponse response = result.getResponse();
//        Mockito.verify(collectionService).create(Mockito.any(Collection.class));
//        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
//        assertEquals("http://localhost/rest/cats",
//                response.getHeader(HttpHeaders.LOCATION));
//    }

//    @Test
//    public void testListCollections() throws Exception {
//        String expected = "[" + exampleCollectionJson + "," + exampleCollectionJson + "," + exampleCollectionJson + "]";
//        List<Collection> collections = Arrays.asList(mockCollection, mockCollection, mockCollection);
//        Mockito.when(collectionService
//                .listCollections(Mockito.anyInt(), Mockito.anyInt()))
//                .thenReturn(collections);
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/rest")
//                .accept(MediaType.APPLICATION_JSON);
//        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
//    }

//    @Test
//    public void testGetCollectionById() throws Exception {
//        when(
//                collectionService.getById(Mockito.anyString())).thenReturn(mockCollection);
//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(
//                "/rest/cats").accept(MediaType.APPLICATION_JSON);
//        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//        JSONAssert.assertEquals(exampleCollectionJson, result.getResponse().getContentAsString(), false);
//    }
//
//    @Test
//    public void testDeleteCollection() throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .delete("/rest/doctors")
//                .accept(MediaType.APPLICATION_JSON);
//        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//        Mockito.verify(collectionService).delete(Mockito.anyString());
//        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
//    }

//    @Test
//    public void testUpdateCollectionName() throws Exception {
//        MvcResult result = prepareUpdate("name");
//        Mockito.verify(collectionService)
//                .updateName(Mockito.anyString(), Mockito.anyString());
//        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
//    }
//
//    @Test
//    public void testUpdateCollectionCacheLimit() throws Exception {
//        MvcResult result = prepareUpdate("limit");
//        Mockito.verify(collectionService)
//                .updateCacheLimit(Mockito.anyString(), Mockito.anyInt());
//        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
//    }
//
//    @Test
//    public void testUpdateCollectionAlgorithm() throws Exception {
//        MvcResult result = prepareUpdate("algorithm");
//        Mockito.verify(collectionService)
//                .updateAlgorithm(Mockito.anyString(), Mockito.anyString());
//        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
//    }
//
//    private MvcResult prepareUpdate(String changeableValue) throws Exception {
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .put("/rest/cats/" + changeableValue)
//                .accept(MediaType.APPLICATION_JSON)
//                .content(exampleCollectionJson)
//                .contentType(MediaType.APPLICATION_JSON);
//        return mockMvc.perform(requestBuilder).andReturn();
//    }
}