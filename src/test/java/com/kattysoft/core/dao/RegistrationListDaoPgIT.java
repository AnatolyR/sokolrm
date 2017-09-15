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
package com.kattysoft.core.dao;

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
import java.util.UUID;

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