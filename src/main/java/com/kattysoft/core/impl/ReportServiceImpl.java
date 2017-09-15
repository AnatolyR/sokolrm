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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.AttachService;
import com.kattysoft.core.ReportService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.UserService;
import com.kattysoft.core.model.Attach;
import com.kattysoft.core.model.User;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.04.2017
 */
public class ReportServiceImpl implements ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private AttachService attachService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserService userService;

    @Override
    public void startReportGeneration(String id, ObjectNode data) {
        log.info("Start report " + id);
        UUID templateId = attachService.getAttachesForObject(id).stream().filter(a -> a.getTitle().endsWith(".jrxml")).findFirst().map(Attach::getId).orElse(null);
        if (templateId == null) {
            throw new SokolException("Template is null");
        }
        generateReport(templateId.toString(), userService.getCurrentUser(), id);
        log.info("Complete report " + id);
    }

    @Override
    public ReportStatus getReportStatus(String id) {
        return null;
    }

    public void generateReport(String templateId, User user, String reportObjectId) {
        byte[] content = attachService.getContent(templateId);
        if (content == null) {
            throw new SokolException("Report template not found");
        }
        InputStream reportStream = new ByteArrayInputStream(content);
        try {

            byte[] report = generateReport(content);

            Date date = new Date();
            String name = dateFormat.format(date) + ".pdf";
            attachService.addContent(reportObjectId, name, user, date, report);
        } catch (Exception e) {
            throw new SokolException("Can not create report", e);
        } finally {
            try {
                reportStream.close();
            } catch (Exception e) {
                log.warn("Can not close reportStream", e);
            }
        }
    }

    public byte[] generateReport(byte[] content) {
        InputStream reportStream = new ByteArrayInputStream(content);
        try {
            JasperDesign jd = JRXmlLoader.load(reportStream);
            JasperReport jr = JasperCompileManager.compileReport(jd);

            Map<String, Object> parameters = new HashMap<String, Object>();

            JasperPrint jp = JasperFillManager.fillReport(jr, parameters, dataSource.getConnection());

            JRPdfExporter exporter = new JRPdfExporter();

            exporter.setExporterInput(new SimpleExporterInput(jp));

            ByteArrayOutputStream report = new ByteArrayOutputStream();
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(report));

            exporter.exportReport();

            return report.toByteArray();
        } catch (Exception e) {
            throw new SokolException("Can not create report", e);
        } finally {
            try {
                reportStream.close();
            } catch (Exception e) {
                log.warn("Can not close reportStream", e);
            }
        }
    }

    public void setAttachService(AttachService attachService) {
        this.attachService = attachService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
