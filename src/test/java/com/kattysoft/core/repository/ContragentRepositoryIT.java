/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import com.kattysoft.core.model.Contragent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 19.12.2016
 */
@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:jpaTestContext.xml" })
public class ContragentRepositoryIT extends AbstractTestNGSpringContextTests {
    @Autowired
    private ApplicationContext applicationContext;

    private ContragentRepository contragentRepository;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() throws IOException {

        contragentRepository = applicationContext.getBean(ContragentRepository.class);
    }

    @Test
    @Sql("file:db/contragents.sql")
    @Sql("file:db/sampleData/contragentsData.sql")
    public void testFindAll() {
        Iterable<Contragent> contragents = contragentRepository.findAll();
        int n = 0;
        for (Contragent contragent : contragents) {
            System.out.println(contragent.getId() + " " + contragent.getTitle());
            n++;
        }
        System.out.println("----------------");

        assertThat(n, equalTo(16));

        List<Contragent> filteredContragents = contragentRepository.findByTitleContaining("ООО", new PageRequest(0, 10));
        int m = 0;
        for (Contragent contragent : filteredContragents) {
            System.out.println(contragent.getId() + " " + contragent.getTitle());
            m++;
        }
        System.out.println("----------------");

        assertThat(m, equalTo(4));
    }
}
