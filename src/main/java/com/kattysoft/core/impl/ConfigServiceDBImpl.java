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
import com.kattysoft.core.dao.ConfigFileDao;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 06.12.2016
 */
public class ConfigServiceDBImpl implements ConfigService {
    private ObjectMapper mapper = new ObjectMapper();
    private com.fasterxml.jackson.databind.ObjectMapper mapper2 = new com.fasterxml.jackson.databind.ObjectMapper();
    
    @Autowired
    private ConfigFileDao configFileDao;

    @Override
    public JsonNode getConfig(String configName) {
        byte[] configContent = configFileDao.getContent("/" + configName + ".json");
        if (configContent == null) {
            return null;
        }
        try {
            return mapper.readTree(configContent);
        } catch (IOException e) {
            throw new RuntimeException("Can not read config '" + configName + "' from DB", e);
        }
    }

    @Override
    public com.fasterxml.jackson.databind.JsonNode getConfig2(String configName) {
        byte[] configContent = configFileDao.getContent("/" + configName + ".json");
        if (configContent == null) {
            return null;
        }
        try {
            return mapper2.readTree(configContent);
        } catch (IOException e) {
            throw new RuntimeException("Can not read config '" + configName + "' from DB", e);
        }
    }

    public void setConfigFileDao(ConfigFileDao configFileDao) {
        this.configFileDao = configFileDao;
    }
}
