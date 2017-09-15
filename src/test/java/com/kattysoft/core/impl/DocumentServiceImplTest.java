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
package com.kattysoft.core.impl;

import com.kattysoft.core.AccessRightLevel;
import com.kattysoft.core.AccessRightService;
import com.kattysoft.core.DocumentService;
import com.kattysoft.core.UserService;
import com.kattysoft.core.dao.DocumentDao;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.Specification;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.12.2016
 */
public class DocumentServiceImplTest {

    @Mock
    private DocumentDao documentDao;

    @Mock
    private UserService userService;
    
    @Mock
    private AccessRightService accessRightService;

    @InjectMocks
    private DocumentService documentService;

    @BeforeClass
    public void setup() {
        ConfigServiceImpl configService = new ConfigServiceImpl();
        configService.setConfigPath("config/");

        documentService = new DocumentServiceImpl();
        MockitoAnnotations.initMocks(this);
        ((DocumentServiceImpl) documentService).setConfigService(configService);
        ((DocumentServiceImpl) documentService).setTitleService(new TitleServiceImpl());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setTitle("Test User");
        when(userService.getCurrentUser()).thenReturn(user);
        when(accessRightService.checkDocumentRights(any(Document.class), any(String.class), any(AccessRightLevel.class))).thenReturn(true);
        when(accessRightService.checkRights(any(String.class), any(String.class), any(String.class), any(AccessRightLevel.class))).thenReturn(true);
    }

    @Test
    public void testListDocuments() throws Exception {
        List<Document> mockDocuments = new ArrayList<Document>();

        Document document1 = new Document();
        document1.setId("1");
        document1.setTitle("Title 1");
        mockDocuments.add(document1);

        Document document2 = new Document();
        document2.setId("2");
        document2.setTitle("Title 2");
        mockDocuments.add(document2);

        when(documentDao.getDocumentsList(any(Specification.class))).thenReturn(mockDocuments);

        List<Document> documents = documentService.listDocuments(null);

        assertThat(documents.get(0).getId(), equalTo("1"));
        assertThat(documents.get(0).getTitle(), equalTo("Title 1"));
        assertThat(documents.get(1).getId(), equalTo("2"));
        assertThat(documents.get(1).getTitle(), equalTo("Title 2"));
    }

    @Test
    public void testCreateDocument() throws Exception {
        Document document = new Document();
        document.setType("incomingDocument");
        Map<String, Object> fields = new HashMap<>();
        fields.put("addressee", Arrays.asList("580f62b3-7b96-4109-a321-dc7d24109a1a", "722b151c-f9d7-4222-b541-cfc554695510", "dc175f6e-b18d-495f-aca9-58c956e48a42"));
        document.setFields(fields);

        when(userService.getUserTitleById(eq("580f62b3-7b96-4109-a321-dc7d24109a1a"))).thenReturn("Поляков И. В.");
        when(userService.getUserTitleById(eq("722b151c-f9d7-4222-b541-cfc554695510"))).thenReturn("Ивашов В. Н.");
        when(userService.getUserTitleById(eq("dc175f6e-b18d-495f-aca9-58c956e48a42"))).thenReturn("Беломестов Г. В.");

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Document savedDoc = (Document) args[0];
            System.out.println("Document fields: " + Arrays.toString(document.getFields().entrySet().toArray()));
            assertThat(savedDoc.getFields().get("addresseeTitle"), equalTo(Arrays.asList("Поляков И. В.", "Ивашов В. Н.", "Беломестов Г. В.")));

            return null;
        }).when(documentDao).saveDocument(anyObject());

        documentService.saveDocument(document);
    }

    @Test
    public void testCreateDocumentWithOneDictionaryValue() throws Exception {
        Document document = new Document();
        document.setType("incomingDocument");
        Map<String, Object> fields = new HashMap<>();
        fields.put("addressee", Collections.singletonList("580f62b3-7b96-4109-a321-dc7d24109a1a"));
        document.setFields(fields);

        when(userService.getUserTitleById(eq("580f62b3-7b96-4109-a321-dc7d24109a1a"))).thenReturn("Поляков И. В.");

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Document savedDoc = (Document) args[0];
            System.out.println("Document fields: " + Arrays.toString(document.getFields().entrySet().toArray()));
            assertThat(savedDoc.getFields().get("addresseeTitle"), equalTo(Collections.singletonList("Поляков И. В.")));

            return null;
        }).when(documentDao).saveDocument(anyObject());

        documentService.saveDocument(document);
    }
}