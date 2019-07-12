package by.training.zaretskaya.controllers;

import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.distribution.DistributedCollectionService;
import by.training.zaretskaya.services.ICollectionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CollectionController.class, secure = false)
public class CollectionControllerTest {
    private final Collection mockCollection = new Collection("cats", 12, "LFU",
            "{\n" +
                    "  \"type\": \"string\",\n" +
                    "  \"minLength\": 2,\n" +
                    "  \"maxLength\": 3\n" +
                    "}");
    private final String exampleCollectionJson = "{\"name\":\"cats\",\"cacheAlgorithm\":\"LFU\"," +
            "\"cacheLimit\":12,\"jsonScheme\":\"{\\n  \\\"type\\\": \\\"string\\\",\\n  " +
            "\\\"minLength\\\": 2,\\n  \\\"maxLength\\\": 3\\n}\"}";
    @MockBean
    DistributedCollectionService<Collection> distributedService;
    @MockBean
    Node node;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    @Qualifier("CollectionService")
    private ICollectionService<Collection> collectionService;

    @Test
    public void getCollectionById() throws Exception {
        when(collectionService.getById(Mockito.anyString())).thenReturn(mockCollection);
        mockMvc.perform(get("/rest/" + mockCollection.getName())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().json(exampleCollectionJson));
    }

    @Test
    public void createCollection() throws Exception {
        mockMvc.perform(post("/rest")
                .accept(MediaType.APPLICATION_JSON)
                .content(exampleCollectionJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/rest/cats"));
        Mockito.verify(collectionService).create(Mockito.any(Collection.class));
        Mockito.verify(distributedService).create(Mockito.any(Collection.class));
    }

    @Test
    public void updateCollection() throws Exception {
        mockMvc.perform(put("/rest/" + mockCollection.getName())
                .accept(MediaType.APPLICATION_JSON)
                .content(exampleCollectionJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        Mockito.verify(collectionService).update(Mockito.anyString(), Mockito.any(Collection.class));
        Mockito.verify(distributedService).update(Mockito.anyString(), Mockito.any(Collection.class));
    }

    @Test
    public void deleteCollection() throws Exception {
        mockMvc.perform(delete("/rest/" + mockCollection.getName())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        Mockito.verify(collectionService).delete(Mockito.anyString());
        Mockito.verify(distributedService).delete(Mockito.anyString());
    }

    @Test
    public void listCollections() throws Exception {
        String expectedList = "[" + exampleCollectionJson + "," + exampleCollectionJson +
                "," + exampleCollectionJson + "]";
        List<Collection> collections = Arrays.asList(mockCollection, mockCollection, mockCollection);
        when(collectionService.list("", 4))
                .thenReturn(collections);
        mockMvc.perform(get("/rest")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedList));
    }
}