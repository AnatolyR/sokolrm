package com.kattysoft.core.dao;

import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.*;
import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.07.2016
 */

@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:applicationContextForTests.xml" })

public class DocumentDaoPgIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private ApplicationContext applicationContext;

    private DocumentDao documentDao;

    @Autowired
    @Qualifier("pgDb")
    private DataSource dataSource;

    @BeforeClass
    public void setup() {
        documentDao = (DocumentDao) applicationContext.getAutowireCapableBeanFactory().createBean(DocumentDaoPg.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql
    public void testGetDocumentsList() throws Exception {
        Specification spec = new Specification();
        spec.setFields(Arrays.asList("id",
            "documentNumber",
            "type",
            "status",
            "title",
            "registrationDate"));
        List<Document> documentsList = documentDao.getDocumentsList(spec);
        for (Document document : documentsList) {
            System.out.println(document.getId() + " " + document.getTitle() + " " + Arrays.toString(document.getFields().entrySet().toArray()));
        }
        assertThat(documentsList.size(), equalTo(6));
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql("testDocuments.sql")
    public void testGetDocumentsListWithCondition() throws Exception {
        Specification spec = new Specification();
        spec.setFields(Arrays.asList("id",
            "documentNumber",
            "type",
            "status",
            "title",
            "registrationDate"));

        String listConditionSql = "type = 'test'";
        Condition listCondition = new SqlCondition(listConditionSql);
        ContainerCondition condition = new ContainerCondition();
        condition.setOperation(ContainerOperation.AND);
        condition.getConditions().add(listCondition);
        spec.setCondition(condition);

        List<Document> documentsList = documentDao.getDocumentsList(spec);
        for (Document document : documentsList) {
            System.out.println(document.getId() + " " + document.getTitle() + " " + Arrays.toString(document.getFields().entrySet().toArray()));
        }
        assertThat(documentsList.size(), equalTo(1));
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql("testDocuments.sql")
    public void testGetDocumentsListWithConditionAndLimit() throws Exception {
        Specification spec = new Specification();
        spec.setFields(Arrays.asList("id",
            "documentNumber",
            "type",
            "status",
            "title",
            "registrationDate"));

        String listConditionSql = "type = 'incomingDocument'";
        Condition listCondition = new SqlCondition(listConditionSql);
        ContainerCondition condition = new ContainerCondition();
        condition.setOperation(ContainerOperation.AND);
        condition.getConditions().add(listCondition);
        spec.setCondition(condition);
        spec.setSize(3);
        spec.setOffset(2);

        List<Document> documentsList = documentDao.getDocumentsList(spec);
        for (Document document : documentsList) {
            System.out.println(document.getId() + " " + document.getTitle() + " " + Arrays.toString(document.getFields().entrySet().toArray()));
        }
        assertThat(documentsList.size(), equalTo(3));
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql
    public void testTotalCount() {
        Specification spec = new Specification();
        Integer totalCount = documentDao.getTotalCount(spec);
        assertThat(totalCount, equalTo(6));
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql("testDocuments.sql")
    public void testGetDocument() {
        Document document = documentDao.getDocument("d0e38e2e-18bd-48fd-91ec-5e102519cd06", null);

        System.out.println("Fields: " + Arrays.toString(document.getFields().entrySet().toArray()));

        assertThat(document.getId(), equalTo("d0e38e2e-18bd-48fd-91ec-5e102519cd06"));
        assertThat(document.getTitle(), equalTo("Test document 1rtyr2355777"));
        assertThat(document.getType(), equalTo("incomingDocument"));
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql("testDocuments.sql")
    public void testUpdateDocument() throws SQLException, ParseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM documents WHERE id = ?::UUID");
            preparedStatement.setString(1, "d0e38e2e-18bd-48fd-91ec-5e102519cd06");
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            assertThat(resultSet.getString("title"), equalTo("Test document 1rtyr2355777"));
            assertThat(resultSet.getInt("pagesQuantity"), equalTo(2));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            assertThat(dateFormat.format(new Date(resultSet.getTimestamp("registrationDate").getTime())), equalTo("23.04.2016 23:32"));
            assertThat(Arrays.asList((String[]) resultSet.getArray("addressee").getArray()), equalTo(Arrays.asList("bbb3bfbf-66af-41b8-8a6d-24f6e527386a",
                "be547579-8664-4221-8dde-029ef242b517", "137e29ed-acb2-4f06-b6f6-74ed1b332caa")));

            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(preparedStatement);

            Document document = new Document();
            document.setType("incomingDocument");
            document.setId("d0e38e2e-18bd-48fd-91ec-5e102519cd06");
            Map<String, Object> fields = new HashMap<>();
            fields.put("title", "New title");
            fields.put("pagesQuantity", 3);
            fields.put("registrationDate", dateFormat.parse("12.12.2016 11:12"));
            fields.put("addressee", Collections.singletonList("be547579-8664-4221-8dde-029ef242b517"));
            document.setFields(fields);

            documentDao.saveDocument(document);

            preparedStatement = connection.prepareStatement("SELECT * FROM documents WHERE id = ?::UUID");
            preparedStatement.setString(1, "d0e38e2e-18bd-48fd-91ec-5e102519cd06");
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            assertThat(resultSet.getString("title"), equalTo("New title"));
            assertThat(resultSet.getInt("pagesQuantity"), equalTo(3));
            assertThat(dateFormat.format(new Date(resultSet.getTimestamp("registrationDate").getTime())), equalTo("12.12.2016 11:12"));
            assertThat(Arrays.asList((String[]) resultSet.getArray("addressee").getArray()), equalTo(Collections.singletonList("be547579-8664-4221-8dde-029ef242b517")));

        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(preparedStatement);
        }
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql("testDocuments.sql")
    public void testInsertDocument() throws SQLException, ParseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            resultSet = connection.createStatement().executeQuery("SELECT count(*) FROM documents");
            resultSet.next();
            assertThat(resultSet.getInt(1), equalTo(6));
            DbUtils.closeQuietly(resultSet);

            Document document = new Document();
            document.setType("incomingDocument");
            Map<String, Object> fields = new HashMap<>();
            fields.put("title", "New inserted document");
            fields.put("pagesQuantity", 5);
            fields.put("registrationDate", dateFormat.parse("12.12.2016 11:54"));
            fields.put("addressee", Arrays.asList("bbb3bfbf-66af-41b8-8a6d-24f6e527386a", "be547579-8664-4221-8dde-029ef242b517"));
            document.setFields(fields);

            String id = documentDao.saveDocument(document);

            resultSet = connection.createStatement().executeQuery("SELECT count(*) FROM documents");
            resultSet.next();
            assertThat(resultSet.getInt(1), equalTo(7));
            DbUtils.closeQuietly(resultSet);

            preparedStatement = connection.prepareStatement("SELECT * FROM documents WHERE id = ?::UUID");
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            assertThat(resultSet.getString("title"), equalTo("New inserted document"));
            assertThat(resultSet.getInt("pagesQuantity"), equalTo(5));
            assertThat(dateFormat.format(new Date(resultSet.getTimestamp("registrationDate").getTime())), equalTo("12.12.2016 11:54"));
            assertThat(Arrays.asList((String[]) resultSet.getArray("addressee").getArray()), equalTo(Arrays.asList("bbb3bfbf-66af-41b8-8a6d-24f6e527386a", "be547579-8664-4221-8dde-029ef242b517")));

        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(preparedStatement);
        }
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql("testDocuments.sql")
    public void testDeleteDocument() {
        Specification spec = new Specification();
        Integer totalCount = documentDao.getTotalCount(spec);
        assertThat(totalCount, equalTo(6));
        boolean result = documentDao.deleteDocument("10984d8e-8e18-4384-95bd-8743d1f98676");
        assertThat(result, is(true));
        totalCount = documentDao.getTotalCount(spec);
        assertThat(totalCount, equalTo(5));
    }

    @Test
    @Sql("file:db/documents.sql")
    @Sql("testDocuments.sql")
    public void testUpdateHistory() throws SQLException, ParseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();

            documentDao.saveHistory("d0e38e2e-18bd-48fd-91ec-5e102519cd06", "{\"test\" : \"good\"}");

            preparedStatement = connection.prepareStatement("SELECT history FROM documents WHERE id = ?::UUID");
            preparedStatement.setString(1, "d0e38e2e-18bd-48fd-91ec-5e102519cd06");
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            assertThat(resultSet.getString("history"), equalTo("{\"test\": \"good\"}"));

        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(preparedStatement);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}