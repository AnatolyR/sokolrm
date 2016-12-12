/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public JsonNode getConfig(String configName) {
        File configFile = new File(configPath + configName + ".json");
        try {
            return mapper.readTree(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Can not read config", e);
        }
    }
}
