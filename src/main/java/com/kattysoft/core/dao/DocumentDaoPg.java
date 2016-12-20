/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.dao;

import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;
import org.apache.commons.dbutils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.07.2016
 */
public class DocumentDaoPg implements DocumentDao {
    @Autowired
    private DataSource dataSource;

    public Document getDocument(String documentId, List<String> fieldsNames) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM documents WHERE id = ?::UUID");
            preparedStatement.setString(1, documentId);
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.next()) {
                Document document = new Document();
                String id = resultSet.getString("id");
                document.setId(id);
                String title = resultSet.getString("title");
                document.setTitle(title);
                String type = resultSet.getString("type");
                document.setType(type);

                Map<String, Object> fields = new HashMap<>();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    String columnTypeName = metaData.getColumnTypeName(i);

                    if ("timestamp".equals(columnTypeName)) {
                        Timestamp timestamp = resultSet.getTimestamp(columnLabel);
                        fields.put(columnLabel, timestamp != null ? new Date(timestamp.getTime()) : null);
                    } else if ("int4".equals(columnTypeName)) {
                        fields.put(columnLabel, resultSet.getInt(columnLabel));
                    } else if ("_varchar".equals(columnTypeName)) {
                        Array array = resultSet.getArray(columnLabel);
                        fields.put(columnLabel, array != null ? array.getArray() : new java.util.ArrayList());
                    } else {
                        fields.put(columnLabel, resultSet.getString(columnLabel));
                    }
                }

                document.setFields(fields);

                return document;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
    }

    public List<Document> getDocumentsList(Specification specification) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            List<Document> documents = new ArrayList<Document>();
            connection = dataSource.getConnection();
            resultSet = connection.createStatement().executeQuery("SELECT * FROM documents;");
            while (resultSet.next()) {
                Document document = new Document();
                String id = resultSet.getString("id");
                document.setId(id);
                String title = resultSet.getString("title");
                document.setTitle(title);
                documents.add(document);
            }
            return documents;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(resultSet);
        }
    }

    public Integer getTotalCount(Specification specification) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            resultSet = connection.createStatement().executeQuery("SELECT count(*) FROM documents;");
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(resultSet);
        }
    }

    public String saveDocument(Document document) {
        Connection connection = null;
        PreparedStatement prst = null;

        boolean update = document.getId() != null;

        try {
            connection = dataSource.getConnection();
            Map<String, Object> fields = document.getFields();
            Map<String, String> columnTypes = getDocumentsMetadata(connection);

            List<String> fieldsNames = new ArrayList<>();
            List<String> fieldsValuesPlaceholder = new ArrayList<>();

            if (!update) {
                fieldsNames.add("type");
                fieldsValuesPlaceholder.add("?");
            }

            fields.forEach((name, value) -> {
                String columnType = columnTypes.get(name);

                if (columnType != null) {
                    fieldsNames.add("\"" + name + "\"");
                    fieldsValuesPlaceholder.add("?");
                }
            });
            String sql;
            if (update) {
                sql = "UPDATE documents SET (" + String.join(", ", fieldsNames) + ") = (" + String.join(", ", fieldsValuesPlaceholder) + ") WHERE id = ?::uuid;";
            } else {
                String id = UUID.randomUUID().toString();
                document.setId(id);
                sql = "INSERT INTO documents (" + String.join(", ", fieldsNames) + ", id) VALUES (" + String.join(", ", fieldsValuesPlaceholder) + ", ?::uuid);";
            }

            prst = connection.prepareStatement(sql);

            int ix = 1;

            if (!update) {
                prst.setString(ix++, document.getType());
            }

            for (String name : fields.keySet()) {
                Object value = fields.get(name);
                String columnType = columnTypes.get(name);
                if (columnType != null) {
                    if ("timestamp".equals(columnType)) {
                        if (value != null) {
                            Date date = (Date) value;
                            prst.setTimestamp(ix, new java.sql.Timestamp(date.getTime()));
                        } else {
                            prst.setNull(ix, Types.TIMESTAMP);
                        }
                    } else if ("int4".equals(columnType)) {
                        if (value != null) {
                            prst.setInt(ix, (Integer) value);
                        } else {
                            prst.setNull(ix, java.sql.Types.INTEGER);
                        }
                    } else if ("_varchar".equals(columnType)) {
                        if (value != null) {
                            Array inArray = connection.createArrayOf("varchar", ((List) value).toArray());
                            prst.setArray(ix, inArray);
                        } else {
                            prst.setNull(ix, Types.ARRAY);
                        }
                    } else if ("varchar".equals(columnType)) {
                        if (value != null) {
                            prst.setString(ix, (String) value);
                        } else {
                            prst.setNull(ix, Types.VARCHAR);
                        }
                    } else {
                        throw new RuntimeException("Unknown column type '" + columnType + "' for '" + name + "'");
                    }
                    ix++;
                }
            }
            prst.setString(ix, document.getId());
            if (update) {
                prst.executeUpdate();
            } else {
                prst.execute();
            }
            return document.getId();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(prst);
            DbUtils.closeQuietly(connection);
        }
    }

    public boolean deleteDocument(String documentId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM documents WHERE id = ?::uuid");
            preparedStatement.setString(1, documentId);
            boolean result = preparedStatement.executeUpdate() == 1;
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(preparedStatement);
            DbUtils.closeQuietly(connection);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, String> getDocumentsMetadata(Connection conn) throws SQLException {
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        ResultSet columnTypesMeta = databaseMetaData.getColumns(null, null, "documents", null);
        Map<String, String> columnTypes = new HashMap<>();
        while (columnTypesMeta.next()) {
            String columnName = columnTypesMeta.getString(4);
            String columnTypeName = columnTypesMeta.getString(6);
            columnTypes.put(columnName, columnTypeName);
        }
        return columnTypes;
    }
}
