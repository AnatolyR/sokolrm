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
package com.kattysoft.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

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
