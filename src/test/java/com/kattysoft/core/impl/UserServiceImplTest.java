/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.impl;

import com.kattysoft.core.UserService;
import com.kattysoft.core.model.User;
import com.kattysoft.core.repository.UserRepository;
import com.kattysoft.core.specification.Specification;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.01.2017
 */
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeClass
    public void setup() {
        userService = new UserServiceImpl();
        MockitoAnnotations.initMocks(this);

        List<User> users = new ArrayList<>();
        for (int i = 3; i <= 4; i++) {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setFirstName("Имя " + i);
            user.setLastName("Фамилия " + i);
            user.setMiddleName("Отчество " + i);
            user.setTitle("Фамилия И. О. " + i);
            users.add(user);
        }

        Page<User> repoPage = new Page<User>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 10;
            }

            @Override
            public <S> Page<S> map(Converter<? super User, ? extends S> var1) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List getContent() {
                return users;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator iterator() {
                return null;
            }
        };
        when(userRepository.findAll(argThat(new Matcher<Pageable>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof Pageable && ((Pageable) o).getPageSize() == 2 && ((Pageable) o).getOffset() == 2;
            }

            @Override
            public void describeMismatch(Object o, Description description) {
                description.appendText("Pageable wrong");
            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Pageable must match");
            }
        }))).thenReturn(repoPage);
    }

    @Test
    public void testGetUsers() {
        Specification specification = new Specification();
        specification.setOffset(2);
        specification.setSize(2);
        com.kattysoft.core.model.Page<User> users = userService.getUsers(specification);

        for (User user : users.getContent()) {
            System.out.println(user.getLastName() + " " + user.getFirstName());
        }
        System.out.println("==========");

        assertThat(users.getContent().size(), equalTo(2));
        assertThat(users.getContent().get(0).getLastName(), equalTo("Фамилия 3"));
    }
}
