/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.ReportService;
import com.kattysoft.core.SokolException;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.04.2017
 */
@RestController
public class ReportController {
    @Autowired
    private ConfigService configService;

    @Autowired
    private ReportService reportService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/report", produces = "application/json; charset=utf-8")
    public String getConfig(String id) {
        if (id == null || !id.matches("^[a-zA-Z]+$")) {
            throw new SokolException("Empty or wrong report id");
        }
        JsonNode config = configService.getConfig("reports/" + id);

        return config.toString();
    }

    @RequestMapping(value = "generate")
    public String generate(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);

        String id = data.get("id").asText();
        if (id == null || !id.matches("^[a-zA-Z]+$")) {
            throw new SokolException("Empty or wrong report id");
        }
        JsonNode config = configService.getConfig("reports/" + id);
        String attachesId = config.get("attachesId").asText();

        reportService.startReportGeneration(attachesId, data);

        return "true";
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
