package com.kattysoft.web;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.*;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 06.12.2016
 */
public class AppSettingsControllerTest {
    private MockMvc mockMvc;

    private AppSettingsController appSettingsController;

    @BeforeClass
    public void setup() {
        appSettingsController = new AppSettingsController();
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(appSettingsController).build();
    }

    @Test
    public void testGetAppSettings() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/appsettings").accept(MediaType.parseMediaType("application/json;charset=UTF-8")));
        MvcResult mvcResult = resultActions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println("Result: " + content);

        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.userName").exists())
            .andExpect(jsonPath("$.leftMenu").exists())
            .andExpect(jsonPath("$.rightMenu").exists());
    }
}