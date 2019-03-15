package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Configuration;
import by.training.zaretskaya.models.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DistributedServiceTest {
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    DistributedService distributedService;

    private Collection mockCollection = new Collection("cats", 12, "LFU", "{\n" +
            "  \"type\": \"string\",\n" +
            "  \"minLength\": 2,\n" +
            "  \"maxLength\": 3\n" +
            "}");

    @Before
    public void tuneUpMockCollection() {
        Configuration.startUp("node4");
    }


    @Test
    public void isMyGroup() {
        assertEquals(true, distributedService.isMyGroup("doctors"));
    }

    @Test
    public void sendGetObject() {

    }

    @Test
    public void redirectGet() {
    }

    @Test
    public void sendPostObject() {
    }

    @Test
    public void redirectPost() {
    }

    @Test
    public void sendUpdateObject() {
    }

    @Test
    public void redirectPut() {
    }

    @Test
    public void sendDeleteObject() {
    }

    @Test
    public void redirectDelete() {
    }

    @Test
    public void redirectListCollection() {
    }

    @Test
    public void sendListToReplica() {
    }

    @Test
    public void redirectListDocument() {
    }
}