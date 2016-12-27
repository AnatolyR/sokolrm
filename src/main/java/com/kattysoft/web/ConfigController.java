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

import com.kattysoft.core.ConfigService;
import com.kattysoft.core.UserService;
import com.kattysoft.core.model.User;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 06.12.2016
 */
@RestController
public class ConfigController {
    @Autowired
    private ConfigService configService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/config", produces = "application/json; charset=utf-8")
    public String getConfig(String id) {
        JsonNode config = configService.getConfig(id);
        if (id.startsWith("lists/")) {
            config = config.get("gridConfig");
        }
        if (id.equals("appSettings")) {
            User user = userService.getCurrentUser();
            String userTitle = user.getTitle();
            ((ObjectNode) config).put("userName", userTitle);
        }
        return config.toString();
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
