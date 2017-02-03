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
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
public class GroupRepositoryIT extends AbstractTestNGSpringContextTests {
    @Autowired
    private ApplicationContext applicationContext;

    private GroupRepository groupRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() throws IOException {

        groupRepository = applicationContext.getBean(GroupRepository.class);
    }

    @Test
    @Sql("file:db/groups.sql")
    public void testAddGroup() {
        Group group = new Group();
        UUID id = UUID.fromString("b0243b1d-5268-4b34-9bae-850c3bf11da4");
        group.setId(id);
        group.setTitle("Test Group " + id.toString());

        ObjectNode data = mapper.createObjectNode();
        data.put("testField", "testValue");
        group.setData(data);

        groupRepository.save(group);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(applicationContext.getBean(DataSource.class));
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from groups g where g.id = 'b0243b1d-5268-4b34-9bae-850c3bf11da4'::uuid;");
        for (Map<String, Object> stringObjectMap : list) {
            for (String key : stringObjectMap.keySet()) {
                System.out.println(key + " = " + stringObjectMap.get(key));
            }
            System.out.println("=======================");
        }

        assertThat(list.get(0).get("id"), equalTo(UUID.fromString("b0243b1d-5268-4b34-9bae-850c3bf11da4")));
        assertThat(list.get(0).get("title"), equalTo("Test Group b0243b1d-5268-4b34-9bae-850c3bf11da4"));
        assertThat(list.get(0).get("ardata").toString(), equalTo("{\"testField\": \"testValue\"}"));
    }

    @Test
    @Sql("file:db/groups.sql")
    @Sql("file:db/sampleData/groupsData.sql")
    public void getGroup() {
        Group group = groupRepository.findOne(UUID.fromString("b0243b1d-5268-4b34-9bae-850c3bf11da4"));
        System.out.println(group.getTitle() + " " + group.getData());
        assertThat(group.getTitle(), equalTo("Test Group b0243b1d-5268-4b34-9bae-850c3bf11da4"));
        assertThat(group.getData().toString(), equalTo("{\"testField\":\"testValue\"}"));
    }
}
