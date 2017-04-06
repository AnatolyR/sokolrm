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
import com.kattysoft.core.DictionaryService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 06.12.2016
 */
@RestController
public class SimpleDictionaryController {
    @Autowired
    private ConfigService configService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping(value = "/simpledictionary", produces = "application/json; charset=utf-8")
    public String getConfig(String id) {

        List<String> titles = dictionaryService.getValuesTitlesForDictionaryId(id);

        ArrayNode data = mapper.createArrayNode();

        titles.forEach(t -> {
            ObjectNode item = mapper.createObjectNode();
            item.put("title", t);
            data.add(item);
        });

        return data.toString();
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }
}
