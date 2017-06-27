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

import com.kattysoft.core.*;
import com.kattysoft.core.model.User;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private AccessRightService accessRightService;

    @RequestMapping(value = "/config", produces = "application/json; charset=utf-8")
    public String getConfig(String id) {
        if (id == null || id.split("/").length > 2 || !id.replace("/", "").matches("^[a-zA-Z]+$")) {
            throw new SokolException("Empty or wrong config id");
        }
        JsonNode config = configService.getConfig(id);
        if (id.startsWith("lists/")) {
            config = config.get("gridConfig");
        }
        if (id.equals("appSettings")) {
            User user = userService.getCurrentUser();
            String userTitle = user.getTitle();
            ((ObjectNode) config).put("userName", userTitle);
        }

        if (config.has("checkAccessRightsForItems") && config.get("checkAccessRightsForItems").asBoolean()) {
            ((ObjectNode) config).remove("checkAccessRightsForItems");
            config.get("items").forEach(n -> {
                String itemId = n.has("id") ? n.get("id").asText() : null;
                if (itemId != null) {
                    if (!accessRightService.checkRights("_system", itemId, null, AccessRightLevel.LIST)) {
                        ((ObjectNode) n).put("disabled", true);
                    }
                }
            });
        }
        if (config.has("checkAccessRightsForColumnLinks") && config.get("checkAccessRightsForColumnLinks").asBoolean()) {
            ((ObjectNode) config).remove("checkAccessRightsForColumnLinks");
            String itemId = config.get("gridConfig").get("id").asText();
            if (!accessRightService.checkRights("_system", itemId, null, AccessRightLevel.READ)) {
                config.get("gridConfig").get("columns").forEach(n -> {
                    ((ObjectNode) n).remove("render");
                    ((ObjectNode) n).remove("linkType");
                });
            }
        }
        
        if ("dictionaries/organizationPersons".equals(id)) {
            if (accessRightService.checkRights("_system", "users", "", AccessRightLevel.CREATE)) {
                ((ObjectNode) config).put("addable", "link");
            }
        } else if ("dictionaries/contragents".equals(id)) {
            if (accessRightService.checkRights("_dictionaries", "contragents", "", AccessRightLevel.CREATE)) {
                ((ObjectNode) config).put("addable", "link");
            }
        } 

        return config.toString();
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
