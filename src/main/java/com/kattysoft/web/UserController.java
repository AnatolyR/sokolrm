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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.UserService;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.Specification;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.UUID;
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

    @Autowired
    private ConfigService configService;

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

    @RequestMapping(value = "/usercard")
    public ObjectNode getUserCard(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Empty user id");
        }
        User user = id.equals("new/user") ? new User() : userService.getUserById(id);
        if (user == null) {
            throw new SokolException("Карточка пользователя не найдена");
        }

        JsonNode formConfig = configService.getConfig2("forms/userForm");

        ObjectNode card = mapper.createObjectNode();
        card.set("form", formConfig);
        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(user);
        card.set("data", data);
        card.put("containerType", "user");

        return card;
    }

    @RequestMapping(value = "/saveuser")
    public String saveUser(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;
        ObjectNode fields = (ObjectNode) data.get("fields");

        User user = mapper.treeToValue(fields, User.class);
        user.setId(uuid);

        String id = userService.saveUser(user);

        return id;
    }

    @RequestMapping(value = "/deleteuser")
    public String deleteUser(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("User id is empty");
        }
        userService.deleteUser(id);
        return "true";
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}