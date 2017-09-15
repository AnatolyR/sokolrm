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

import org.codehaus.jackson.JsonNode;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 06.12.2016
 */
public class ConfigServiceImplTest {

    @Test
    public void testGetConfig() throws Exception {
        ConfigServiceImpl configService = new ConfigServiceImpl();
        configService.setConfigPath("config/");

        JsonNode config = configService.getConfig("lists/documentsList");
        config = config.get("gridConfig");

        System.out.println("Result: " + config.toString());

        assertThat(config.get("title").asText(), equalTo("Документы"));
        assertThat(config.get("columnsVisible").isArray(), is(true));
        assertThat(config.get("columns").isArray(), is(true));
    }
}