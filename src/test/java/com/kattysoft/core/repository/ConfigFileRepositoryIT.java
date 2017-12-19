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

import com.kattysoft.core.model.ConfigFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
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
 * Date: 15.12.2016
 */
@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:jpaTestContext.xml" })
public class ConfigFileRepositoryIT extends AbstractTestNGSpringContextTests {
    @Autowired
    private ApplicationContext applicationContext;

    private ConfigFileRepository configFileRepository;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() throws IOException {

        configFileRepository = applicationContext.getBean(ConfigFileRepository.class);
    }

    @Test
    @Sql("file:db/configs.sql")
    @Sql("file:db/sampleData/configsData.sql")
    public void testCriteria() {
//        ContainerCondition condition = new ContainerCondition();
//        ContainerCondition subcondition = new ContainerCondition();
//
//        condition.setOperation(ContainerOperation.AND);
//        condition.getConditions().add(new ValueCondition("lastName", Operation.EQUAL, "Ивашов"));
//        condition.getConditions().add(subcondition);
//
//        subcondition.setOperation(ContainerOperation.OR);
//        subcondition.getConditions().add(new ValueCondition("firstName", Operation.EQUAL, "Виктор"));
//        subcondition.getConditions().add(new ValueCondition("firstName", Operation.EQUAL, "Никита"));
//
//        org.springframework.data.jpa.domain.Specification<ConfigFile> specification = SpecificationUtil.conditionToSpringSpecification(condition, ConfigFile.class);

        List<ConfigFile> configFiles = configFileRepository.findAll((Specification<ConfigFile>) null);
        
        assertThat(configFiles.get(0).getPath(), equalTo("/types/incomingDocumentType.json"));
    }
}
