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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
