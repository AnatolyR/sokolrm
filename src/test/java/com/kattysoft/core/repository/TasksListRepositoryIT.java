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
