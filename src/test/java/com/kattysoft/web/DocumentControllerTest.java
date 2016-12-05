package com.kattysoft.web;

import com.kattysoft.core.DocumentService;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 29.11.2016
 */
public class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeClass
    public void setup() {
        documentController = new DocumentController();
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    public void testListDocuments() throws Exception {
        List<Document> documents = new ArrayList<Document>();

        Document document1 = new Document();
        document1.setId("1");
        document1.setTitle("Title 1");
        documents.add(document1);

        Document document2 = new Document();
        document2.setId("2");
        document2.setTitle("Title 2");
        documents.add(document2);

        when(documentService.listDocuments(any(Specification.class))).thenReturn(documents);

        ResultActions resultActions = this.mockMvc.perform(get("/documents").accept(MediaType.parseMediaType("application/json;charset=UTF-8")));
        MvcResult mvcResult = resultActions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("Result: " + content);

        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].title").value("Title 1"))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].title").value("Title 2"));
    }
}