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

import com.kattysoft.core.ContragentService;
import com.kattysoft.core.UserService;
import com.kattysoft.core.model.Contragent;
import com.kattysoft.core.model.User;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.12.2016
 */
@RestController
public class DictionaryController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContragentService contragentService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/dictionary", produces = "application/json; charset=utf-8")
    public String getDictionary(String id, String query) throws IOException {
        if ("organizationPersons".equals(id)) {
            List<User> users = userService.getUsersByShortTitle(query);
            List<ObjectNode> nodes = users.stream().map(u -> {
                ObjectNode node = mapper.createObjectNode();
                node.put("id", u.getId().toString());
                node.put("title", u.getTitle());
                return node;
            }).collect(Collectors.toList());
            String json = mapper.writeValueAsString(nodes);
            return json;
        } else if ("correspondent".equals(id)) {
            List<Contragent> contragents = contragentService.getContragentsByTitle(query);
            List<ObjectNode> nodes = contragents.stream().map(c -> {
                ObjectNode node = mapper.createObjectNode();
                node.put("id", c.getId().toString());
                node.put("title", c.getTitle());
                return node;
            }).collect(Collectors.toList());
            String json = mapper.writeValueAsString(nodes);
            return json;
        }
        return "[]";
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setContragentService(ContragentService contragentService) {
        this.contragentService = contragentService;
    }
}
