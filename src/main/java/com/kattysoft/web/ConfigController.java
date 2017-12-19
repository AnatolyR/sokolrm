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

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kattysoft.core.*;
import com.kattysoft.core.model.User;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.util.UUID;

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

    private ObjectMapper mapper = new ObjectMapper();
    private DefaultPrettyPrinter printer;
    {
        DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("  ", DefaultIndenter.SYS_LF);
        printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith(indenter);
        printer.indentArraysWith(indenter);
        
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Autowired
    private AccessRightService accessRightService;

    @RequestMapping(value = "/config", produces = "application/json; charset=utf-8")
    public String getConfig(String id) {
        if (id == null || id.split("/").length > 2 || !id.replace("/", "").matches("^[a-zA-Z]+$")) {
            throw new SokolException("Empty or wrong config id");
        }
        JsonNode config = configService.getConfig(id);
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

    @RequestMapping(value = "/rawConfig", produces = "application/json; charset=utf-8")
    public String getRawConfig(String id) {
        if (!accessRightService.checkRights("_system", "configFiles", null, AccessRightLevel.READ)) {
            throw new NoAccessRightsException("No access rights for config file");
        }
        com.fasterxml.jackson.databind.JsonNode config = configService.getConfigById(id);
        
        return config != null ? config.toString() : "{}";
    }

    @RequestMapping(value = "/saveConfig")
    public String saveEntity(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        com.fasterxml.jackson.databind.node.ObjectNode data = (com.fasterxml.jackson.databind.node.ObjectNode) mapper.readTree(requestBody);
        UUID uuid = UUID.fromString(data.get("id").asText());
        com.fasterxml.jackson.databind.JsonNode contentNode = data.get("content");
        String content = mapper.writer(printer).writeValueAsString(contentNode);
//        content = content.replace("{", "\n{");
        content = content.replace("\" : true", "\": true");
        content = content.replace("\" : false", "\": false");
        content = content.replace("\" : [", "\": [");
        content = content.replace("\" : {", "\": {");
        content = content.replace("\" : \"", "\": \"");

        configService.saveConfig(uuid, content.getBytes());

        return uuid.toString();
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
