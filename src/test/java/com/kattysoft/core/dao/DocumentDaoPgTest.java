package com.kattysoft.core.dao;

import com.kattysoft.core.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.07.2016
 */

@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:applicationContextForTests.xml" })

public class DocumentDaoPgTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ApplicationContext applicationContext;

    private DocumentDao documentDao;

    @BeforeClass
    public void setup() {
        documentDao = (DocumentDao) applicationContext.getAutowireCapableBeanFactory().createBean(DocumentDaoPg.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql
    public void testGetDocumentsList() throws Exception {
        List<Document> documentsList = documentDao.getDocumentsList(null);
        for (Document document : documentsList) {
            System.out.println(document.getId() + " " + document.getTitle());
        }
        assertThat(documentsList.size(), equalTo(6));
    }

//    @Test
//    public void testGetDocument() throws Exception {
//
//    }
//
//    @Test
//    public void testSaveDocument() throws Exception {
//
//    }
//
//    @Test
//    public void testDeleteDocument() throws Exception {
//
//    }
//
//    @Test
//    public void testGetDocumentsList1() throws Exception {
//
//    }
}