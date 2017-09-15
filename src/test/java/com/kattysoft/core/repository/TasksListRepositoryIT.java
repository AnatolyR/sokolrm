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

import com.kattysoft.core.model.TasksList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.02.2017
 */
@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:jpaTestContext.xml" })
public class TasksListRepositoryIT extends AbstractTestNGSpringContextTests {
    @Autowired
    private ApplicationContext applicationContext;

    private TasksListRepository tasksListRepository;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() throws IOException {

        tasksListRepository = applicationContext.getBean(TasksListRepository.class);
    }

    @Test
    @Sql("file:db/taskslists.sql")
    @Sql("file:db/tasks.sql")
    public void testAddTasksList() {
        TasksList tasksList = new TasksList();
        UUID id = UUID.randomUUID();
        tasksList.setId(id);
        UUID documentId = UUID.randomUUID();
        tasksList.setDocumentId(documentId);
        UUID userId = UUID.randomUUID();
        tasksList.setUserId(userId);
        tasksList.setType("resolution");

        tasksList.setComment("Comment 1");

        tasksListRepository.save(tasksList);

        TasksList savedList = tasksListRepository.findOne(id);
        assertThat(savedList.getComment(), equalTo("Comment 1"));

        TasksList findedList = tasksListRepository.findOneByDocumentIdAndUserIdAndType(documentId, userId, "resolution");
        assertThat(findedList.getComment(), equalTo("Comment 1"));
    }
}
