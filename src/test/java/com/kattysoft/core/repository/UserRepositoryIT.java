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

import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import static com.kattysoft.core.impl.UserServiceImpl.md5;
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
    @Sql("file:db/user_groups.sql")
    public void testAddUser() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setTitle("Test User Title");
        user.setLogin("login1");
        user.setFirstName("First Name22");
        user.setMiddleName("Middle Name");
        user.setLastName("Last Name");

        user.getGroups().add(UUID.randomUUID());
        user.getGroups().add(UUID.randomUUID());

        userRepository.save(user);

        User savedUser = userRepository.findOne(id);
        assertThat(savedUser.getTitle(), equalTo("Test User Title"));
    }

    @Test
    @Sql("file:db/users.sql")
    @Sql("file:db/sampleData/usersData.sql")
    @Sql("file:db/user_groups.sql")
    public void testFindAll() {
        Iterable<User> users = userRepository.findAll();
        int n = 0;
        for (User user : users) {
//            System.out.println(user.getId() + " " + user.getTitle() + " " + String.join(",", user.getGroups().stream().map(UUID::toString).collect(Collectors.toList())));
            System.out.println(user.getId() + " " + user.getTitle());
            n++;
        }
        System.out.println("----------------");

        assertThat(n, equalTo(40));

        List<User> list = userRepository.findByTitleContaining("Волков");
        int m = 0;
        for (User user : list) {
            System.out.println(user.getId() + " " + user.getTitle());
            m++;
        }
        System.out.println("----------------");

        assertThat(m, equalTo(2));

        List<User> list2 = userRepository.findIdAndTitleByTitle("Волков%");
        int k = 0;
        for (User user : list2) {
            System.out.println(user.getId() + " " + user.getTitle());
            k++;
        }
        System.out.println("----------------");

        assertThat(k, equalTo(2));
    }

    @Test
    @Sql("file:db/users.sql")
    @Sql("file:db/sampleData/usersData.sql")
    public void testFindByLoginAndPassword() throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String login = "test";
        String pass = "123";
        String salt = "_sdf345sf34";
        String hashedPass = md5(md5(md5(pass) + login) + salt);
        System.out.println(">>> " + hashedPass);

        User user = userRepository.findByLoginAndPassword(login, hashedPass);
        assertThat(user.getTitle(), equalTo("Ивашов В. Н."));
    }

    @Test
    @Sql("file:db/users.sql")
    @Sql("file:db/sampleData/usersData.sql")
    public void testFindAllPageable() {
        Sort sort = new Sort("lastName");
        PageRequest pageRequest = new PageRequest(0, 10, sort);
        Page<User> page = userRepository.findAll(pageRequest);
        int n = 0;
        for (User user : page.getContent()) {
            System.out.println(user.getId() + " " + user.getTitle());
            n++;
        }
        System.out.println("Total: " + page.getTotalElements());
        System.out.println("----------------");
        pageRequest = new PageRequest(1, 5, sort);
        page = userRepository.findAll(pageRequest);
        n = 0;
        for (User user : page.getContent()) {
            System.out.println(user.getId() + " " + user.getTitle());
            n++;
        }
        System.out.println("Total: " + page.getTotalElements());
        System.out.println("----------------");

        assertThat(n, equalTo(5));
    }

    @Test
    @Sql("file:db/users.sql")
    @Sql("file:db/sampleData/usersData.sql")
    public void testCriteria() {
        ContainerCondition condition = new ContainerCondition();
        ContainerCondition subcondition = new ContainerCondition();

        condition.setOperation(ContainerOperation.AND);
        condition.getConditions().add(new ValueCondition("lastName", Operation.EQUAL, "Ивашов"));
        condition.getConditions().add(subcondition);

        subcondition.setOperation(ContainerOperation.OR);
        subcondition.getConditions().add(new ValueCondition("firstName", Operation.EQUAL, "Виктор"));
        subcondition.getConditions().add(new ValueCondition("firstName", Operation.EQUAL, "Никита"));

        org.springframework.data.jpa.domain.Specification<User> specification = SpecificationUtil.conditionToSpringSpecification(condition, User.class);

        List<User> users = userRepository.findAll(specification);
        for (User user : users) {
            System.out.println(user.getId() + " " + user.getLastName() + " " + user.getFirstName());
        }
        assertThat(users.get(0).getLastName(), equalTo("Ивашов"));
        assertThat(users.get(0).getFirstName(), equalTo("Виктор"));
        assertThat(users.get(1).getLastName(), equalTo("Ивашов"));
        assertThat(users.get(1).getFirstName(), equalTo("Никита"));
    }
}
