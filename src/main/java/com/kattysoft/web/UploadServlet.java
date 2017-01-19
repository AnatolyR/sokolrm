/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.web;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 24.04.2016
 */
public class UploadServlet extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        dataSource = (DataSource) applicationContext.getBean("pgDb");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();

        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("java.io.tmpdir");
        factory.setRepository(repository);

        ServletFileUpload upload = new ServletFileUpload(factory);

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            List<FileItem> items = upload.parseRequest(req);

            for (FileItem item : items) {
                String id = UUID.randomUUID().toString();
                String fileName = item.getName();

                String objectId = req.getParameter("objectId");
                InputStream inputStream = item.getInputStream();
                conn = dataSource.getConnection();
                ps = conn.prepareStatement("INSERT INTO files (id, objectId, title, size, content) VALUES (?::uuid, ?::uuid, ?, ?, ?)");
                ps.setString(1, id);
                ps.setString(2, objectId);
                ps.setString(3, fileName);
                ps.setInt(4, (int) item.getSize());
                ps.setBinaryStream(5, inputStream, (int) item.getSize());
                ps.executeUpdate();
            }
        } catch (FileUploadException | SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("SELECT id, objectId, title, size, content FROM files WHERE id = ?::uuid");
            String id = req.getParameter("id");
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                String fileName = rs.getString("title");
                InputStream inputStream = rs.getBinaryStream("content");
                resp.setContentType("application/octet-stream");
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                IOUtils.copy(inputStream, resp.getOutputStream());
                resp.flushBuffer();
                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }
}
