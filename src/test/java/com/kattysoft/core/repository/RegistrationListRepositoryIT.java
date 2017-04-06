/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 27.01.2017
 */
@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:jpaTestContext.xml" })
public class RegistrationListRepositoryIT extends AbstractTestNGSpringContextTests {
    @Autowired
    private ApplicationContext applicationContext;

    private RegistrationListRepository registrationListRepository;

    private ObjectMapper mapper = new ObjectMapper();

//    @BeforeClass
//    @SuppressWarnings("unchecked")
//    public void beforeClass() throws IOException {
//
//        registrationListRepository = applicationContext.getBean(RegistrationListRepository.class);
//    }
//
//    @Test
//    @Sql("file:db/registrationlists.sql")
//    @Sql("file:db/sampleData/registrationlistsData.sql")
//    public void updateCounter() {
//        registrationListRepository.updateCounterByName(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040"));
//        int lastInsertId = registrationListRepository.getLastInsertId();
//        System.out.println(">>>>>> " + lastInsertId);
//
//        registrationListRepository.updateCounterByName(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040"));
//        lastInsertId = registrationListRepository.getLastInsertId();
//        System.out.println(">>>>>> " + lastInsertId);
//
//        registrationListRepository.updateCounterByName(UUID.fromString("d48c9468-e328-4108-a08a-535931a25040"));
//        lastInsertId = registrationListRepository.getLastInsertId();
//        System.out.println(">>>>>> " + lastInsertId);
//    }
}
