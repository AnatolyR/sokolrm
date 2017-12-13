package com.kattysoft.core.dao;

import org.apache.commons.io.IOUtils;
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
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 13.12.2017
 */
@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:applicationContextForTests.xml" })
public class ConfigFileDaoPgTestIT extends AbstractTestNGSpringContextTests {
    @Autowired
    private ApplicationContext applicationContext;

    private ConfigFileDao configFileDao;

    @Autowired
    @Qualifier("pgDb")
    private DataSource dataSource;

    @BeforeClass
    public void setup() {
        configFileDao = (ConfigFileDao) applicationContext.getAutowireCapableBeanFactory().createBean(ConfigFileDaoPg.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }

    @Test
    @Sql("file:db/configs.sql")
    @Sql("file:db/sampleData/configsData.sql")
    public void testGetContent() throws IOException {
        byte[] expected = IOUtils.toByteArray(ConfigFileDaoPgTestIT.class.getResourceAsStream("incomingDocumentType.json"));
        assertThat(configFileDao.getContent("/types/incomingDocumentType.json"), equalTo(expected));
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
