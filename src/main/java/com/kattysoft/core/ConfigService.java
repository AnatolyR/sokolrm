/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import org.codehaus.jackson.JsonNode;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 06.12.2016
 */
public interface ConfigService {
    JsonNode getConfig(String configName);
    com.fasterxml.jackson.databind.JsonNode getConfig2(String configName);
}
