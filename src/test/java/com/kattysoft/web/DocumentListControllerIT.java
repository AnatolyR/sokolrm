package com.kattysoft.web;

import com.kattysoft.core.DocumentService;
import com.kattysoft.core.impl.ConfigServiceImpl;
import com.kattysoft.core.impl.TitleServiceImpl;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
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
public class DocumentListControllerIT {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentListController documentListController;

    @BeforeClass
    public void setup() {
        ConfigServiceImpl configService = new ConfigServiceImpl();
        configService.setConfigPath("config/");

        documentListController = new DocumentListController();
        MockitoAnnotations.initMocks(this);
        documentListController.setConfigService(configService);
        documentListController.setTitleService(new TitleServiceImpl());
        this.mockMvc = MockMvcBuilders.standaloneSetup(documentListController).build();
    }

    @Test
    public void testListDocuments() throws Exception {
        List<Document> documents = new ArrayList<Document>();

        Document document1 = new Document();
        document1.setId("1");
        document1.setTitle("Title 1");
        document1.getFields().put("type", "incomingDocument");
        documents.add(document1);

        Document document2 = new Document();
        document2.setId("2");
        document2.setTitle("Title 2");
        document2.getFields().put("type", "notExistType");
        documents.add(document2);

        when(documentService.listDocuments(any(Specification.class))).thenReturn(documents);
        when(documentService.getTotalCount(any(Specification.class))).thenReturn(2);

        ResultActions resultActions = this.mockMvc.perform(get("/documents").param("listId", "documents").accept(MediaType.parseMediaType("application/json;charset=UTF-8")));
        MvcResult mvcResult = resultActions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("Result: " + content);

        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.total").value(2))
            .andExpect(jsonPath("$.offset").value(0))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value("1"))
            .andExpect(jsonPath("$.data[0].title").value("Title 1"))
            .andExpect(jsonPath("$.data[1].id").value("2"))
            .andExpect(jsonPath("$.data[1].title").value("Title 2"));

        MatcherAssert.assertThat(content, CoreMatchers.equalTo("{\"data\":[{\"id\":\"1\",\"title\":\"Title 1\",\"type\":\"Входящий\",\"space\":null,\"kind\":null,\"status\":\"[null]\"},{\"id\":\"2\",\"title\":\"Title 2\",\"type\":\"[notExistType]\",\"space\":null,\"kind\":null,\"status\":\"[null]\"}],\"offset\":0,\"total\":2}"));
    }
}