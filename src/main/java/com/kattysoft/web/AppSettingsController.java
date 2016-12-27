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

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 06.12.2016
 */
@RestController
@Deprecated
public class AppSettingsController {
//    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/appsettings", produces = "application/json; charset=utf-8")
    public String getAppSettings() throws IOException {
        InputStream inputStream = AppSettingsController.class.getResourceAsStream("/appSettings.json");
        String json = IOUtils.toString(inputStream, "UTF-8");
//        JsonNode jsonNode = mapper.readTree(json);
//        return mapper.writeValueAsString(jsonNode);
        return json;
    }
}
