/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.dao;

import com.kattysoft.core.model.User;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.04.2017
 */
public class AttachesDaoPg implements AttachesDao {
    @Autowired
    private DataSource dataSource;

    @Override
    public byte[] getContent(String id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        byte[] result = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("SELECT id, objectId, title, size, content FROM files WHERE id = ?::UUID");
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                String fileName = rs.getString("title");
                InputStream inputStream = rs.getBinaryStream("content");

                result = IOUtils.toByteArray(inputStream);

                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
        return result;
    }

    @Override
    public void addContent(String reportObjectId, String name, User user, Date date, byte[] bytes) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String id = UUID.randomUUID().toString();

            InputStream inputStream = new ByteArrayInputStream(bytes);
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("INSERT INTO files (id, objectId, title, size, content, author, authorTitle, created) VALUES (?::UUID, ?::UUID, ?, ?, ?, ?::UUID, ?, ?)");
            ps.setString(1, id);
            ps.setString(2, reportObjectId);
            ps.setString(3, name);
            ps.setInt(4, bytes.length);
            ps.setBinaryStream(5, inputStream, bytes.length);
            ps.setString(6, user.getId().toString());
            ps.setString(7, user.getTitle());
            ps.setTimestamp(8, new java.sql.Timestamp(date.getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(ps);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
