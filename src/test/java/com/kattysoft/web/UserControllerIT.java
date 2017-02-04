/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.web;

import com.kattysoft.core.UserService;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.Specification;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 15.01.2017
 */
public class UserControllerIT {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeClass
    public void setup() {
        userController = new UserController();
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = new ArrayList<>();

        for (int i = 6; i <= 10; i++) {
            User user = new User();
            user.setId(UUID.fromString("12345678-1234-1234-1234-1234567890AB"));
            user.setFirstName("Имя " + i);
            user.setLastName("Фамилия " + i);
            user.setMiddleName("Отчество " + i);
            user.setTitle("Фамилия И. О. " + i);
            users.add(user);
        }
        Page<User> page = new Page<>(20, users);
        when(userService.getUsers(argThat(new Matcher<Specification>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof Specification && ((Specification) o).getOffset() == 5 && ((Specification) o).getSize() == 5;
            }

            @Override
            public void describeMismatch(Object o, Description description) {

            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

            }

            @Override
            public void describeTo(Description description) {

            }
        }))).thenReturn(page);

        ResultActions resultActions = this.mockMvc.perform(get("/users").param("size", "5").param("offset", "5").accept(MediaType.parseMediaType("application/json;charset=UTF-8")));
        MvcResult mvcResult = resultActions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("Result: " + content);

        MatcherAssert.assertThat(content, CoreMatchers.equalTo("{\"data\":[{\"id\":\"12345678-1234-1234-1234-1234567890ab\",\"login\":null,\"title\":\"Фамилия И. О. 6\",\"firstName\":\"Имя 6\",\"middleName\":\"Отчество 6\",\"lastName\":\"Фамилия 6\",\"groups\":[],\"groupsTitle\":[]},{\"id\":\"12345678-1234-1234-1234-1234567890ab\",\"login\":null,\"title\":\"Фамилия И. О. 7\",\"firstName\":\"Имя 7\",\"middleName\":\"Отчество 7\",\"lastName\":\"Фамилия 7\",\"groups\":[],\"groupsTitle\":[]},{\"id\":\"12345678-1234-1234-1234-1234567890ab\",\"login\":null,\"title\":\"Фамилия И. О. 8\",\"firstName\":\"Имя 8\",\"middleName\":\"Отчество 8\",\"lastName\":\"Фамилия 8\",\"groups\":[],\"groupsTitle\":[]},{\"id\":\"12345678-1234-1234-1234-1234567890ab\",\"login\":null,\"title\":\"Фамилия И. О. 9\",\"firstName\":\"Имя 9\",\"middleName\":\"Отчество 9\",\"lastName\":\"Фамилия 9\",\"groups\":[],\"groupsTitle\":[]},{\"id\":\"12345678-1234-1234-1234-1234567890ab\",\"login\":null,\"title\":\"Фамилия И. О. 10\",\"firstName\":\"Имя 10\",\"middleName\":\"Отчество 10\",\"lastName\":\"Фамилия 10\",\"groups\":[],\"groupsTitle\":[]}],\"offset\":5,\"total\":20}"));
    }
}
