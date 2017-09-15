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

import org.apache.commons.io.IOUtils;
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
