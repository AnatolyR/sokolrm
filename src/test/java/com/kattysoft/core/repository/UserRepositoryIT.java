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

import com.kattysoft.core.model.User;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 15.12.2016
 */
@TestExecutionListeners({SqlScriptsTestExecutionListener.class})
@ContextConfiguration(locations = { "classpath:jpaTestContext.xml" })
public class UserRepositoryIT extends AbstractTestNGSpringContextTests {
    @Autowired
    private ApplicationContext applicationContext;

    private UserRepository userRepository;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void beforeClass() throws IOException {

        userRepository = applicationContext.getBean(UserRepository.class);
    }

    @Test
    @Sql("file:db/users.sql")
    public void testAddUser() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setTitle("Test User Title");
        user.setLogin("login1");
        user.setPassword("password1");
        user.setFirstName("First Name");
        user.setMiddleName("Middle Name");
        user.setLastName("Last Name");

        userRepository.save(user);

        User savedUser = userRepository.findOne(id);
        assertThat(savedUser.getTitle(), equalTo("Test User Title"));
    }

    @Test
    @Sql("file:db/users.sql")
    @Sql("file:db/sampleData/usersData.sql")
    public void testFindAll() {
        Iterable<User> users = userRepository.findAll();
        int n = 0;
        for (User user : users) {
            System.out.println(user.getId() + " " + user.getTitle());
            n++;
        }
        System.out.println("----------------");

        assertThat(n, equalTo(20));

        List<User> list = userRepository.findByTitleContaining("Волков");
        int m = 0;
        for (User user : list) {
            System.out.println(user.getId() + " " + user.getTitle());
            m++;
        }
        System.out.println("----------------");

        assertThat(m, equalTo(2));
    }
}
