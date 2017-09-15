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
