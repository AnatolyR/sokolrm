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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.UserService;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 15.01.2017
 */
@RestController
public class UserController {
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private UserService userService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/users")
    public ObjectNode getUsers(Integer offset, Integer size) {
        if (offset == null) {
            offset = 0;
        }
        if (size == null) {
            size =  DEFAULT_PAGE_SIZE;
        }

        Specification spec = new Specification();
        spec.setOffset(offset);
        spec.setSize(size);

        Page<User> users = userService.getUsers(spec);
        List<ObjectNode> userNodes = users.getContent().stream().map(user ->
            (ObjectNode) mapper.valueToTree(user)
        ).collect(Collectors.toList());

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(userNodes);
        page.put("offset", offset);
        page.put("total", users.getTotal());

        return page;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
