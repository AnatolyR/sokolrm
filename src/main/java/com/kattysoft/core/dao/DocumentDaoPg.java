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
package com.kattysoft.core.dao;

import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.*;
import org.apache.commons.dbutils.DbUtils;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.07.2016
 */
public class DocumentDaoPg implements DocumentDao {
    private static final Logger log = LoggerFactory.getLogger(DocumentDaoPg.class);
    @Autowired
    private DataSource dataSource;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private SimpleDateFormat correctionalDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

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
                String space = resultSet.getString("space");
                document.setSpace(space);

                String status = resultSet.getString("status");
                document.setStatus(status);

                Map<String, Object> fields = new HashMap<>();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    String columnTypeName = metaData.getColumnTypeName(i);

                    if ("timestamp".equals(columnTypeName)) {
                        Timestamp timestamp = resultSet.getTimestamp(columnLabel);
                        fields.put(columnLabel, timestamp != null ? new Date(timestamp.getTime()) : null);
                    } else if ("int4".equals(columnTypeName)) {
                        fields.put(columnLabel, resultSet.getObject(columnLabel));
                    } else if ("_varchar".equals(columnTypeName)) {
                        Array array = resultSet.getArray(columnLabel);
                        fields.put(columnLabel, array != null ? Arrays.asList((String[]) array.getArray()) : new java.util.ArrayList());
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

    protected Object getValue(String columnType, String columnName, ResultSet resultSet) {
        try {
            Object result;
            if ("timestamp".equals(columnType)) {
                Timestamp timestamp = resultSet.getTimestamp(columnName);
                result = timestamp != null ? new Date(timestamp.getTime()) : null;
            } else if ("int4".equals(columnType)) {
                result = resultSet.getInt(columnName);
            } else if ("_varchar".equals(columnType)) {
                Array array = resultSet.getArray(columnName);
                result = array != null ? array.getArray() : new java.util.ArrayList();
            } else {
                result = resultSet.getString(columnName);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("[SOKOL-DAO-DOC-VAL] Can not get value from resultset", e);
        }
    }

    public List<Document> getDocumentsList(Specification spec) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            List<Document> documents = new ArrayList<Document>();
            connection = dataSource.getConnection();

            Map<String, String> columnTypes;
            if (spec.getJoin() == null) {
                columnTypes = getMetadata(connection, "documents", null);
            } else {
                columnTypes = new HashMap<>();
                columnTypes.putAll(getMetadata(connection, "documents", "d"));
                columnTypes.putAll(getMetadata(connection, "tasks", "t"));
            }

            List<String> fieldsNames = spec.getFields();

            List<String> columns = new ArrayList<>();
            if (!fieldsNames.contains("id") && !fieldsNames.contains("d.id")) {
                if (spec.getJoin() == null) {
                    columns.add("id");
                } else {
                    columns.add("d.\"id\" as d_id");
                }
            }
            if (!fieldsNames.contains("title") && !fieldsNames.contains("d.title")) {
                if (spec.getJoin() == null) {
                    columns.add("title");
                } else {
                    columns.add("d.\"title\" as d_title");
                }
            }
            columns.addAll(fieldsNames.stream().map(name -> {
                if (name.contains(".")) {
                    String[] split = name.split("\\.");
                    return split[0] + ".\"" + split[1] + "\" as " + split[0] + "_" + split[1] + "";
                } else {
                    return "\"" + name + "\"";
                }
            }).collect(Collectors.toList()));

            List<Object> paramsValues = new ArrayList<>();
            if (spec.getSearchText() != null && !spec.getSearchText().isEmpty()) {
                columns.add("ts_headline('russian', searchtext, plainto_tsquery('russian', ?), 'StartSel = <mark>, StopSel = </mark>') as textheadline");
                paramsValues.add(spec.getSearchText());
                fieldsNames.add("textheadline");
                columnTypes.put("textheadline", "text");
            }

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT");
            sql.append(" ");
            sql.append(String.join(", ", columns));
            sql.append(" FROM documents d ");

            if (spec.getJoin() != null) {
                sql.append(spec.getJoin());
                sql.append(" ");
            }
            if (spec.getCondition() != null) {
                Condition condition = spec.getCondition();
                String conditionSql = conditionToSql(condition, paramsValues, columnTypes);

                if (spec.getSearchText() != null && !spec.getSearchText().isEmpty()) {
                    conditionSql += " AND to_tsvector('russian', searchtext) @@ plainto_tsquery('russian', ?)";
                    paramsValues.add(spec.getSearchText());
                }

                sql.append("WHERE ");
                sql.append(conditionSql);
            }
            if (spec.getSort() != null && spec.getSort().size() > 0) {
                String column = spec.getSort().get(0).getField();
                if (spec.getJoin() != null) {
                    column = column.replace(".", "_");
                }
                SortOrder order = spec.getSort().get(0).getOrder();
                if (columnTypes.containsKey(column)) {
                    if (spec.getJoin() != null) {
                        sql.append(" ORDER BY ").append(column).append(" ").append(order);
                    } else {
                        sql.append(" ORDER BY \"").append(column).append("\" ").append(order);
                    }
                }
            }
            if (spec.getSize() != null) {
                sql.append(" LIMIT ");
                sql.append(spec.getSize());
            }
            if (spec.getOffset() != null) {
                sql.append(" OFFSET ");
                sql.append(spec.getOffset());
            }
            sql.append(";");


            String sqlStr = sql.toString();
            log.info("LIST SQL: {}", sqlStr);

            PreparedStatement preparedStatement = connection.prepareStatement(sqlStr);
            int i = 1;
            for (Object paramsValue : paramsValues) {
                if (paramsValue instanceof String) {
                    preparedStatement.setString(i++, (String) paramsValue);
                } else {
                    preparedStatement.setObject(i++, paramsValue);
                }
            }
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Document document = new Document();
                String id = resultSet.getString(spec.getJoin() == null ? "id" : "d_id");
                document.setId(id);
                String title = resultSet.getString(spec.getJoin() == null ? "title" : "d_title");
                document.setTitle(title);

                final ResultSet finalResultSet = resultSet;
                Map<String, Object> fields = new HashMap<>();
                fieldsNames.forEach(name -> {
                    String aliasName = name.replace(".", "_");
                    String columnType = columnTypes.get(aliasName);
                    Object value = getValue(columnType, aliasName, finalResultSet);
                    fields.put(name, value);
                });
                document.setFields(fields);

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

    private String conditionToSql(Condition condition, List<Object> paramsValues, Map<String, String> columnTypes) {
        if (condition instanceof ContainerCondition) {
            List<String> subconditions = new ArrayList<>();
            ((ContainerCondition) condition).getConditions().forEach(c -> {
                String sql = conditionToSql(c, paramsValues, columnTypes);
                if (sql != null && !sql.isEmpty()) {
                    subconditions.add("(" + sql + ")");
                }
            });
            String str = String.join(" " + ((ContainerCondition) condition).getOperation().toString() + " ", subconditions);
            return str;
        } else if (condition instanceof ValueCondition) {
            return valueConditionToString((ValueCondition) condition, paramsValues, columnTypes);
        } else if (condition instanceof SqlCondition) {
            return ((SqlCondition) condition).getSql();
        }
        return "";
    }

    private String valueConditionToString(ValueCondition valueCondition, List<Object> paramsValues, Map<String, String> columnTypes) {
        if (valueCondition.getField() == null) {
            return null;
        }
        String columnType = columnTypes.get(valueCondition.getField().replace(".", "_"));
        if (columnType == null || valueCondition.getOperation() == null) {
            return null;
        }
        String field;
        if (valueCondition.getField().contains(".")) {
            String[] split = valueCondition.getField().split("\\.");
            field = split[0] + ".\"" + split[1] + "\"";
        } else {
            field = "\"" + valueCondition.getField() + "\"";
        }
        Object value = valueCondition.getValue();

        if (value == null || value.toString().isEmpty()) {
            if (valueCondition.getOperation() == Operation.EQUAL) {
                String sql = field + " IS NULL ";
                if ("varchar".equals(columnType)) {
                    sql += "OR " + field + " = ''";
                }
                return sql;
            } else if (valueCondition.getOperation() == Operation.NOT_EQUAL) {
                String sql = field + " IS NOT NULL ";
                if ("varchar".equals(columnType)) {
                    sql += "AND " + field + " != ''";
                }
                return sql;
            } else {
                return null;
            }
        }

        if ("timestamp".equals(columnType)) {
            try {
                value = new Timestamp(dateFormat.parse((String) value).getTime());
            } catch (ParseException e) {
                return null;
            }
        } else if ("int4".equals(columnType)) {
            try {
                value = new Integer((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if ("_varchar".equals(columnType)) {
            if (valueCondition.getOperation() ==  Operation.LIKE) {
                String sql = field + " @> ARRAY[?] ";
                paramsValues.add(value);
                return sql;
            } else {
                return null;
            }
        }

        if ((valueCondition.getOperation() ==  Operation.LIKE
            || valueCondition.getOperation() ==  Operation.STARTS
            || valueCondition.getOperation() ==  Operation.ENDS) && !(value instanceof String)) {
            return null;
        }

        String operation;
        if (valueCondition.getOperation() ==  Operation.LIKE) {
            value = "%" + value + "%";
            operation = "LIKE";
        } else if (valueCondition.getOperation() == Operation.STARTS) {
            value = value + "%";
            operation = "LIKE";
        } else if (valueCondition.getOperation() == Operation.ENDS) {
            value = "%" + value;
            operation = "LIKE";
        } else if (valueCondition.getOperation() == Operation.EQUAL) {
            operation = "=";
            if (value instanceof Timestamp) {
                try {
                    Timestamp value2 = new Timestamp(correctionalDateFormat.parse(valueCondition.getValue() + ":59.999").getTime());

                    String sql = " (" + field + " >= ? AND " + field + " < ?)";
                    paramsValues.add(value);
                    paramsValues.add(value2);
                    return sql;
                } catch (ParseException e) {
                    return null;
                }
            }
        } else if (valueCondition.getOperation() == Operation.GREAT) {
            operation = ">";
        } else if (valueCondition.getOperation() == Operation.GREAT_OR_EQUAL) {
            operation = ">=";
        } else if (valueCondition.getOperation() == Operation.LESS) {
            operation = "<";
        } else if (valueCondition.getOperation() == Operation.LESS_OR_EQUAL) {
            operation = "<=";
        } else if (valueCondition.getOperation() == Operation.NOT_EQUAL) {
            operation = "!=";
        } else {
            return null;
        }

        String sql = field + " " + operation + ("uuid".equals(columnType) ? " ?::uuid " : " ? ");
        paramsValues.add(value);
        return sql;
    }

    public Integer getTotalCount(Specification spec) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            Map<String, String> columnTypes;
            if (spec.getJoin() == null) {
                columnTypes = getMetadata(connection, "documents", null);
            } else {
                columnTypes = new HashMap<>();
                columnTypes.putAll(getMetadata(connection, "documents", "d"));
                columnTypes.putAll(getMetadata(connection, "tasks", "t"));
            }

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT");
            sql.append(" ");
            sql.append("count(*)");
            sql.append(" FROM documents d ");
            List<Object> paramsValues = new ArrayList<>();
            if (spec.getJoin() != null) {
                sql.append(spec.getJoin());
                sql.append(" ");
            }
            if (spec.getCondition() != null) {
                Condition condition = spec.getCondition();
                String conditionSql = conditionToSql(condition, paramsValues, columnTypes);

                if (spec.getSearchText() != null && !spec.getSearchText().isEmpty()) {
                    conditionSql += " AND to_tsvector('russian', searchtext) @@ plainto_tsquery('russian', ?)";
                    paramsValues.add(spec.getSearchText());
                }

                sql.append("WHERE ");
                sql.append(conditionSql);
            }
            sql.append(";");

            String sqlStr = sql.toString();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStr);
            int i = 1;
            for (Object paramsValue : paramsValues) {
                preparedStatement.setObject(i++, paramsValue);
            }
            resultSet = preparedStatement.executeQuery();

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
            Map<String, String> columnTypes = getMetadata(connection, "documents", null);

            List<String> fieldsNames = new ArrayList<>();
            List<String> fieldsValuesPlaceholder = new ArrayList<>();

            fields.forEach((name, value) -> {
                String columnType = columnTypes.get(name);

                if (columnType != null) {
                    fieldsNames.add(name);
                    fieldsValuesPlaceholder.add("?");
                }
            });

            if (fieldsNames.contains("title")) {
                fieldsNames.add("searchtext");
                fieldsValuesPlaceholder.add("?");
                columnTypes.put("searchtext", "text");
                String searchtext = (String) fields.get("title");
                fields.put("searchtext", searchtext);
            }

            String sql;
            List<String> fieldsNamesQuoted = fieldsNames.stream().map(n -> "\"" + n + "\"").collect(Collectors.toList());
            if (update) {
                sql = "UPDATE documents SET (" + String.join(", ", fieldsNamesQuoted) + ") = (" + String.join(", ", fieldsValuesPlaceholder) + ") WHERE id = ?::uuid;";
            } else {
                String id = UUID.randomUUID().toString();
                document.setId(id);
                sql = "INSERT INTO documents (type, " + String.join(", ", fieldsNamesQuoted) + ", id) VALUES (?, " + String.join(", ", fieldsValuesPlaceholder) + ", ?::uuid);";
            }

            prst = connection.prepareStatement(sql);

            int ix = 1;

            if (!update) {
                prst.setString(ix++, document.getType());
            }

            for (String name : fieldsNames) {
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
                    } else if ("text".equals(columnType)) {
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

    public void saveHistory(String documentId, String history) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE documents SET history = ?::json WHERE id = ?::uuid");
            preparedStatement.setString(2, documentId);

            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(history);
            preparedStatement.setObject(1, jsonObject);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(preparedStatement);
            DbUtils.closeQuietly(connection);
        }
    }

    @Override
    public String getHistory(String documentId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT history FROM documents WHERE id = ?::UUID");
            preparedStatement.setString(1, documentId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("history");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(preparedStatement);
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

    public Map<String, String> getMetadata(Connection conn, String table, String prefix) throws SQLException {
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        ResultSet columnTypesMeta = databaseMetaData.getColumns(null, null, table, null);
        Map<String, String> columnTypes = new HashMap<>();
        while (columnTypesMeta.next()) {
            String columnName = columnTypesMeta.getString(4);
            if (prefix != null) {
                columnName = prefix + "_" + columnName;
            }
            String columnTypeName = columnTypesMeta.getString(6);
            columnTypes.put(columnName, columnTypeName);
        }
        return columnTypes;
    }
}
