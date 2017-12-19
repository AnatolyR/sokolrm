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

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.04.2017
 */
public class ConfigFileDaoPg implements ConfigFileDao {
    @Autowired
    private DataSource dataSource;

    @Override
    public byte[] getContent(String path) {
        byte[] result = null;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = createPreparedStatementForGetContent(conn, path);
            ResultSet rs = ps.executeQuery();
        ) {
            if (rs.next()) {
                InputStream inputStream = rs.getBinaryStream("content");

                result = IOUtils.toByteArray(inputStream);

                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Can not get content for config '" + path + "'", e);
        } 
        return result;
    }
    
    private PreparedStatement createPreparedStatementForGetContent(Connection conn, String path) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT content FROM configs WHERE path = ?");
        ps.setString(1, path);
        return ps;
    }

    @Override
    public byte[] getContent(UUID id) {
        byte[] result = null;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = createPreparedStatementForGetContent(conn, id);
            ResultSet rs = ps.executeQuery();
        ) {
            if (rs.next()) {
                InputStream inputStream = rs.getBinaryStream("content");

                if (inputStream != null) {
                    result = IOUtils.toByteArray(inputStream);
                    inputStream.close();
                }

            }
        } catch (Exception e) {
            throw new RuntimeException("Can not get content for config '" + id + "'", e);
        }
        return result;
    }

    private PreparedStatement createPreparedStatementForGetContent(Connection conn, UUID id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT content FROM configs WHERE id = ?::uuid");
        ps.setString(1, id.toString());
        return ps;
    }

    @Override
    public void saveContent(UUID uuid, byte[] content) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = createPreparedStatementForSaveContent(conn, uuid, content);
            ) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement createPreparedStatementForSaveContent(Connection conn, UUID id, byte[] content) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE configs SET content = ? WHERE id = ?::uuid;");
        ps.setBinaryStream(1, new ByteArrayInputStream(content));
        ps.setString(2, id.toString());
        return ps;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
