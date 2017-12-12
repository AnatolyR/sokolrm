package com.kattysoft.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 04.10.2017
 */
public class DBUtilDaoPg implements DBUtilDao {
    private static final Logger log = LoggerFactory.getLogger(DBUtilDaoPg.class);

    private DataSource dataSource;

    @Override
    public boolean isTableExist(String tableName) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement("select current_schema();");
             ResultSet rs = pst.executeQuery()) {

            rs.next();
            String schemaName = rs.getString(1);

            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, schemaName, tableName, new String[]{"TABLE"});
            return tables.next();
        } catch (SQLException e) {
            throw new RuntimeException("Can not get table metadata", e);
        }
    }

    public void importSQL(String name, InputStream in, Progress progress) {
        Scanner s = new Scanner(in);
        s.useDelimiter("(;(\r)?\n)|(--\n)");

        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement();) {

            progress.tell(5, 10);
            while (s.hasNext()) {
                String line = s.next();
                if (line.startsWith("/*!") && line.endsWith("*/")) {
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }
                line = line.trim();
                if (line.length() > 0 && !line.contains("CREATE SCHEMA") && !line.startsWith("SET ")) {
                    log.debug("Execute: {}", line);
                    st.execute(line);
                }
            }
            progress.tell(10, 10);
        } catch (SQLException e) {
            throw new RuntimeException("Can not import SQL " + name, e);
        }
    }

    @Override
    public boolean isSchemaEmpty() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement("select current_schema();");
             ResultSet rs = pst.executeQuery()) {

            rs.next();
            String schemaName = rs.getString(1);

            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, schemaName, null, new String[]{"TABLE"});
            return !tables.next();

        } catch (SQLException e) {
            throw new RuntimeException("Can not get table metadata", e);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
