package com.kattysoft.core.impl;

import org.codehaus.jackson.JsonNode;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.*;

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

        assertThat(config.get("title").asText(), equalTo("Test"));
        assertThat(config.get("columnsVisible").isArray(), is(true));
        assertThat(config.get("columns").isArray(), is(true));
    }
}