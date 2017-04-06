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

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 05.04.2017
 */
public class RegistrationListDaoPg implements RegistrationListDao {
    private static final Logger log = LoggerFactory.getLogger(RegistrationListDaoPg.class);
    @Autowired
    private DataSource dataSource;

    @Override
    public Integer produceNextNumber(UUID listId) {
        Connection connection = null;
        PreparedStatement prst = null;
        ResultSet resultSet = null;
        Integer result = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            prst = connection.prepareStatement("update registrationlists set count = count + 1 where id = ?::uuid;");
            prst.setString(1, listId.toString());
            prst.executeUpdate();
            prst = connection.prepareStatement("select count from registrationlists where id = ?::uuid;");
            prst.setString(1, listId.toString());
            resultSet = prst.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                log.error("Can not rollback transaction", e1);
            }
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(prst);
            DbUtils.closeQuietly(resultSet);
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                log.error("Could not prodcue number", e);
            }
            DbUtils.closeQuietly(connection);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
