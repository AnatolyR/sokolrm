package com.kattysoft.core.dao;

import com.kattysoft.core.impl.InstallationServiceImpl;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.12.2017
 */
@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = {"classpath:applicationContextForTests.xml"})
public class DBUtilDaoPgTestIT extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("pgDb")
    private DataSource dataSource;
    
    @Autowired
    private ApplicationContext applicationContext;

    private DBUtilDao utilDao;

    @BeforeClass
    public void setup() {
        utilDao = (DBUtilDao) applicationContext.getAutowireCapableBeanFactory().createBean(DBUtilDaoPg.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }

    @DataProvider(name = "sqls")
    public Object[][] createData() {
        return new Object[][]{
            {"configs.sql", "SELECT count(*) FROM configs", "63"},
            {"data.sql", "SELECT count(*) FROM documents", "95"},
            {"files.sql", "SELECT count(*) FROM files", "20"},
            {"schema.sql", "SELECT count(*) FROM documents", "0"},
            {"schema_configs.sql", "SELECT count(*) FROM configs", "0"},
            {"schema_files.sql", "SELECT count(*) FROM files", "0"}
        };
    }

    public static void main(String[] args) {
        try {
            Files.walk(Paths.get("/Users/anatolii/Documents/sokolrm/db/initial")).filter(Files::isRegularFile).map(Path::getFileName).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(dataProvider = "sqls")
    @Sql("file:db/drop_test_schema.sql")
    public void testImportSQL(String name, String sql, String result) throws Exception {
        InputStream stream = InstallationServiceImpl.class.getResourceAsStream("/db/initial/" + name);
        utilDao.importSQL(name, stream, (i, n) -> {});
        check(sql, result);
    }

    @Test
    @Sql("file:db/drop_test_schema.sql")
    public void testImportAdmin() throws Exception {
        InputStream stream1 = InstallationServiceImpl.class.getResourceAsStream("/db/initial/schema.sql");
        utilDao.importSQL("schema.sql", stream1, (i, n) -> {});
        InputStream stream2 = InstallationServiceImpl.class.getResourceAsStream("/db/initial/admin.sql");
        utilDao.importSQL("admin.sql", stream2, (i, n) -> {});
        check("SELECT count(*) FROM users", "1");
        check("SELECT count(*) FROM groups", "1");
        check("SELECT count(*) FROM user_groups", "1");
        check("SELECT count(*) FROM accessrecords", "4");
    }

    public void check(String sql, String expected) throws SQLException, ParseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String actual = resultSet.getString(1);
            System.out.println("SQL: " + sql);
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actual);
            assertThat(actual, equalTo(expected));

        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(preparedStatement);
        }
    }

    @Test
    @Sql("file:db/drop_test_schema.sql")
    public void testIsSchemaEmpty() throws Exception {
        assertTrue(utilDao.isSchemaEmpty());
    }

    @Test
    @Sql("file:db/documents.sql")
    public void testIsSchemaNotEmpty() throws Exception {
        assertFalse(utilDao.isSchemaEmpty());
    }
}