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
package com.kattysoft.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.*;
import com.kattysoft.core.model.Contragent;
import com.kattysoft.core.model.DictionaryValue;
import com.kattysoft.core.model.Space;
import com.kattysoft.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private AccessRightService accessRightService;

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
        } else if ("freespaces".equals(id)) {
            List<Space> spaces = spaceService.getSpaces();
            List<ObjectNode> nodes = spaces.stream().filter(s -> s.getRegistrationListId() == null).map(s -> {
                ObjectNode node = mapper.createObjectNode();
                node.put("id", s.getId().toString());
                node.put("title", s.getTitle());
                return node;
            }).collect(Collectors.toList());
            String json = mapper.writeValueAsString(nodes);
            return json;
        } else if ("spaces".equals(id)) {
            List<Space> spaces = spaceService.getSpaces();
            List<ObjectNode> nodes = spaces.stream().map(s -> {
                ObjectNode node = mapper.createObjectNode();
                node.put("id", s.getId().toString());
                node.put("title", s.getTitle());
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
        } else {
            List<DictionaryValue> values = dictionaryService.getValuesForDictionaryId(id);

            ArrayNode data = mapper.createArrayNode();

            values.forEach(v -> {
                ObjectNode item = mapper.createObjectNode();
                item.put("id", v.getTitle());
                item.put("title", v.getTitle());
                data.add(item);
            });
            return data.toString();
        }
//        return "[]";
    }

    @RequestMapping(value = "/dictionaryinfo", produces = "application/json; charset=utf-8")
    public String getConfig(String id) {
        String fullId = "dictionaries/" + id;
        JsonNode config = configService.getConfig2(fullId);

        List<DictionaryValue> values = dictionaryService.getValuesForDictionaryId(id);

        ArrayNode data = mapper.createArrayNode();

        values.forEach(v -> {
            ObjectNode item = mapper.createObjectNode();
            item.put("id", v.getId().toString());
            item.put("title", v.getTitle());
            data.add(item);
        });

        if (accessRightService.checkRights("_dictionaries", id, "", AccessRightLevel.ADD)) {
            ((ObjectNode) config).put("addable", true);
        }
        if (accessRightService.checkRights("_dictionaries", id, "", AccessRightLevel.DELETE)) {
            ((ObjectNode) config).put("selectable", true);
            ((ObjectNode) config).put("deletable", true);
        }

        ((ObjectNode) config).set("data", data);

        return config.toString();
    }

    @RequestMapping(value = "/dictionaryvalues", produces = "application/json; charset=utf-8")
    public ObjectNode getDictionaryValues(String searchText) {
        List<ObjectNode> userNodes = new ArrayList<>();

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(userNodes);
        page.put("offset", 0);
        page.put("total", 0);

        return page;
    }

    @RequestMapping("deleteDictionaryValues")
    public String deleteDictionaryValues(String[] ids) {
        dictionaryService.deleteDictionaryValues(Arrays.asList(ids));
        return "true";
    }

    @RequestMapping("addDictionaryValue")
    public DictionaryValue addDictionaryValue(String dictionaryId, String data) throws IOException {
        if (dictionaryId == null || dictionaryId.isEmpty()) {
            throw new RuntimeException("dictionaryId is null");
        }
        JsonNode node = mapper.readTree(data);
        String title = node.get("title").asText();

        if (title.isEmpty()) {
            throw new RuntimeException("Empty title");
        }

        if (dictionaryService.isValueExist(dictionaryId, title)) {
            throw new SokolException("Значение уже существует");
        }

        DictionaryValue value = new DictionaryValue();
        value.setTitle(title);
        value.setDictionaryId(dictionaryId);
        String id = dictionaryService.addDictionaryValue(value);
        DictionaryValue reloadedValue = dictionaryService.getDictionaryValue(id);

        return reloadedValue;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setContragentService(ContragentService contragentService) {
        this.contragentService = contragentService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
