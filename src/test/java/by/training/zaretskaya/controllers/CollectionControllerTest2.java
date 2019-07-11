package by.training.zaretskaya.controllers;

import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.services.DistributedCollectionService;
import by.training.zaretskaya.services.ICollectionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = CollectionController.class, secure = false)
@ContextConfiguration({"classpath*:spring/applicationContext.xml"})
public class CollectionControllerTest2 {

    @MockBean
    DistributedCollectionService distributedService;
    @MockBean
    Node node;
    private Collection mockCollection = new Collection("cats", 12, "LFU", "{\n" +
            "  \"type\": \"string\",\n" +
            "  \"minLength\": 2,\n" +
            "  \"maxLength\": 3\n" +
            "}");
    private String exampleCollectionJson = " {\n" +
            "        \"name\": \"cats\",\n" +
            "        \"cacheLimit\": 12,\n" +
            "        \"cacheAlgorithm\": \"LFU\"\n" +
            "        \"jsonScheme\": {\n" +
            "  \"type\": \"string\",\n" +
            "  \"minLength\": 2,\n" +
            "  \"maxLength\": 30\n} \n}";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ICollectionService collectionService;

    @Test
    public void getCollectionById() throws Exception {
        when(collectionService.getById(Mockito.anyString())).thenReturn(mockCollection);
        mockMvc.perform(get("/rest/" + mockCollection.getName())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().json(exampleCollectionJson));
    }

    @Test
    public void createCollection() {
    }

    @Test
    public void updateCollection() {
    }

    @Test
    public void deleteCollection() {
    }

    @Test
    public void listCollections() {
    }
}