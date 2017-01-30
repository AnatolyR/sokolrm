/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.SpaceService;
import com.kattysoft.core.model.Space;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 30.01.2017
 */
@RestController
public class SpaceController {
    @Autowired
    private ConfigService configService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SpaceService spaceService;

    @RequestMapping(value = "/spaces", produces = "application/json; charset=utf-8")
    public String getConfig() {
        String fullId = "lists/spaces";
        JsonNode config = configService.getConfig2(fullId);

        List<Space> values = spaceService.getSpaces();

        ArrayNode data = mapper.createArrayNode();

        values.forEach(v -> {
            ObjectNode item = mapper.createObjectNode();
            item.put("id", v.getId().toString());
            item.put("title", v.getTitle());
            data.add(item);
        });

        ((ObjectNode) config).set("data", data);

        return config.toString();
    }

    @RequestMapping("deleteSpaces")
    public String deleteDictionaryValues(String[] ids) {
        spaceService.deleteSpaces(Arrays.asList(ids));
        return "true";
    }

    @RequestMapping("addSpace")
    public Space addDictionaryValue(String data) throws IOException {
        JsonNode node = mapper.readTree(data);
        String title = node.get("title").asText();

        if (title.isEmpty()) {
            throw new RuntimeException("Empty title");
        }

        if (spaceService.isValueExist(title)) {
            throw new SokolException("Значение уже существует");
        }

        Space space = new Space();
        space.setTitle(title);
        String id = spaceService.addSpace(space);
        Space reloadedSpace = spaceService.getSpace(id);

        return reloadedSpace;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
