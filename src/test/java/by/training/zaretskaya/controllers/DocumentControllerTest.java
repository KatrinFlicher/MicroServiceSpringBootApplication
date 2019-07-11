package by.training.zaretskaya.controllers;

import by.training.zaretskaya.services.IDocumentService;
import by.training.zaretskaya.models.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(value = DocumentController.class, secure = false)
public class DocumentControllerTest {

    private Document mockDocument = new Document("Popova", "Intern 2 cat");
    private String exampleDocumentJson = "{\"key\": \"Popova\",\"value\":\"Intern 2 cat\"}";
    private String expectedValue = "Intern 2 cat";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IDocumentService documentService;


    @Test
    public void testGetDocument() throws Exception {
        Mockito.when(
                documentService.get(Mockito.anyString(), Mockito.anyString())).thenReturn(mockDocument);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/rest/doctors/docs/Popova").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(expectedValue, result.getResponse().getContentAsString());
        //JSONAssert.assertEquals(exampleDocumentJson, result.getResponse().getContentAsString(), false);
    }


    @Test
    public void testCreateDocument() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/rest/doctors/docs")
                .accept(MediaType.APPLICATION_JSON)
                .content(exampleDocumentJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(documentService).create(Mockito.anyString(), Mockito.any(Document.class));
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals("http://localhost/rest/doctors/docs/Popova",
                response.getHeader(HttpHeaders.LOCATION));
    }


    @Test
    public void testDeleteDocument() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/rest/doctors/docs/Popova")
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(documentService).delete(Mockito.anyString(), Mockito.anyString());
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void testUpdateDocument() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/rest/doctors/docs/Popova")
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"key\": \"\",\"value\":\"Laborant\"}")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Mockito.verify(documentService)
                .update(Mockito.anyString(), Mockito.anyString(), Mockito.any(Document.class));
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

//    @Test
//    public void listDocuments() throws Exception {
//        String expected = "[" + exampleDocumentJson + "," + exampleDocumentJson + "," + exampleDocumentJson + "]";
//        List<Document> documents = Arrays.asList(mockDocument, mockDocument, mockDocument);
//        Mockito.when(documentService
//                .list(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
//                .thenReturn(documents);
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/rest/doctors/docs")
//                .accept(MediaType.APPLICATION_JSON);
//        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
//        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
//    }
}