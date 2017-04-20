/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.impl;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.04.2017
 */
public class ReportServiceImplTest {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private ReportServiceImpl reportService;

    private BasicDataSource dataSource;

    @BeforeClass
    public void setup() {
        System.setProperty("apple.awt.UIElement", "true");
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/anatolii?currentSchema=sokol");
        dataSource.setUsername("anatolii");
        dataSource.setPassword("");

        reportService = new ReportServiceImpl();
        reportService.setDataSource(dataSource);
    }

    @Test(enabled = false)
    public void test() throws IOException {
        byte[] template = IOUtils.toByteArray(ReportServiceImplTest.class.getResourceAsStream("template.jrxml"));

        byte[] report = reportService.generateReport(template);

        FileUtils.writeByteArrayToFile(new File("report.pdf"), report);
    }
}
