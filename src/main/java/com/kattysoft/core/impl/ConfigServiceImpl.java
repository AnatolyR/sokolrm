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
package com.kattysoft.core.impl;

import com.kattysoft.core.ConfigService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 06.12.2016
 */
public class ConfigServiceImpl implements ConfigService {
    private String configPath;
    private ObjectMapper mapper = new ObjectMapper();
    private com.fasterxml.jackson.databind.ObjectMapper mapper2 = new com.fasterxml.jackson.databind.ObjectMapper();

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public JsonNode getConfig(String configName) {
        File configFile = new File(configPath + configName + ".json");
        if (!configFile.exists()) {
            return null;
        }
        try {
            return mapper.readTree(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Can not read config", e);
        }
    }

    @Override
    public com.fasterxml.jackson.databind.JsonNode getConfig2(String configName) {
        File configFile = new File(configPath + configName + ".json");
        if (!configFile.exists()) {
            return null;
        }
        try {
            return mapper2.readTree(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Can not read config", e);
        }
    }
}
