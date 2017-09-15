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
