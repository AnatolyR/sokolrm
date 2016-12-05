package com.kattysoft.core.impl;

import com.kattysoft.core.DocumentService;
import com.kattysoft.core.dao.DocumentDao;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.12.2016
 */
public class DocumentServiceImplTest {

    @Mock
    private DocumentDao documentDao;

    @InjectMocks
    private DocumentService documentService;

    @BeforeClass
    public void setup() {
        documentService = new DocumentServiceImpl();
        MockitoAnnotations.initMocks(this);
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
}