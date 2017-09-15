/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kattysoft.web;

import com.kattysoft.core.AccessRightService;
import com.kattysoft.core.DocumentService;
import com.kattysoft.core.TitleService;
import com.kattysoft.core.impl.ConfigServiceImpl;
import com.kattysoft.core.model.Document;
import org.apache.commons.io.IOUtils;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 08.12.2016
 */
public class DocumentCardControllerIT {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @Mock
    private AccessRightService accessRightService;

    @Mock
    private TitleService titleService;

    @InjectMocks
    private DocumentCardController documentCardController;

    @BeforeClass
    public void setup() {
        ConfigServiceImpl configService = new ConfigServiceImpl();
        configService.setConfigPath("config/");

        documentCardController = new DocumentCardController();
        documentCardController.setConfigService(configService);
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(documentCardController).build();
    }

    @Test
    public void testGetDocumentCard() throws Exception {
        Document document1 = new Document();
        document1.setId("1");
        document1.setTitle("Title 1");
        document1.setType("incomingDocument");

        Map<String, Object> fields = new HashMap<>();
        fields.put("field1", "fieldValue1");
        fields.put("field2", "fieldValue2");
        document1.setFields(fields);

        when(documentService.getDocument(eq("1"))).thenReturn(document1);

        ResultActions resultActions = this.mockMvc.perform(get("/card").param("id", "1").accept(MediaType.parseMediaType("application/json;charset=UTF-8")));
        MvcResult mvcResult = resultActions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("Result: " + content);

        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.data").isMap())
            .andExpect(jsonPath("$.form").isMap())
            .andExpect(jsonPath("$.form.fields").isArray());
    }

    @Test
    public void testSaveDocument() throws Exception {
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Document document = (Document) args[0];
            System.out.println("Document fields: " + Arrays.toString(document.getFields().entrySet().toArray()));

            return null;
        }).when(documentService).saveDocument(anyObject());

        String content = IOUtils.toString(this.getClass().getResourceAsStream("DocumentCardControllerIT.document.json"), "UTF-8");
        ResultActions resultActions = this.mockMvc.perform(post("/savedocument").content(content));
        MvcResult mvcResult = resultActions.andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Result: " + responseContent);
        resultActions.andExpect(status().isOk())
            .andExpect(content().string("20a8fac1-cebd-455a-8eb6-ce34e59c3631"));
    }
}