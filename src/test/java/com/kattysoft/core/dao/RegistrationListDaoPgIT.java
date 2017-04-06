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

public class RegistrationListDaoPgIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private ApplicationContext applicationContext;

    private RegistrationListDao registrationListDao;

    @Autowired
    @Qualifier("pgDb")
    private DataSource dataSource;

    @BeforeClass
    public void setup() {
        registrationListDao = (RegistrationListDao) applicationContext.getAutowireCapableBeanFactory().createBean(RegistrationListDaoPg.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }

    @Test
    @Sql("file:db/registrationlists.sql")
    @Sql("file:db/sampleData/registrationlistsData.sql")
    public void updateCounter() {
        assertThat(registrationListDao.produceNextNumber(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040")), equalTo(3));
        assertThat(registrationListDao.produceNextNumber(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040")), equalTo(4));
        assertThat(registrationListDao.produceNextNumber(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040")), equalTo(5));

//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                RegistrationListDaoPg registrationListDao = new RegistrationListDaoPg();
//                registrationListDao.setDataSource(dataSource);
//                System.out.println(Thread.currentThread().getId() + " >>>> " + registrationListDao.produceNextNumber(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040")));
//                System.out.println(Thread.currentThread().getId() + " >>>> " + registrationListDao.produceNextNumber(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040")));
//                System.out.println(Thread.currentThread().getId() + " >>>> " + registrationListDao.produceNextNumber(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040")));
//            }
//        };
//
//        for (int i = 0; i < 5; i++) {
//            new Thread(runnable).start();
//        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}