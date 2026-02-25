package com.nocode.core.parser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Oracle表解析器
 */
public class OracleTableParser extends AbstractTableParser {

    @Override
    public List<String> getTables(Connection conn) {
        List<String> tables = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT table_name FROM user_tables ORDER BY table_name")) {
            while (rs.next()) {
                tables.add(rs.getString("table_name"));
            }
        } catch (Exception e) {
            throw new RuntimeException("获取表列表失败", e);
        }
        return tables;
    }

    @Override
    protected String getTableComment(Connection conn, String tableName) {
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT comments FROM user_tab_comments WHERE table_name = '" + tableName + "'")) {
            if (rs.next()) {
                String comment = rs.getString(1);
                return comment != null ? comment : "";
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "";
    }

    @Override
    protected String getColumnsQuery(String tableName) {
        return "SELECT utc.column_name, utc.data_type, utc.data_default, utc.nullable, " +
               "utc.char_col_decl_length AS character_maximum_length, " +
               "utc.data_precision, utc.data_scale, " +
               "CASE WHEN col.column_name IS NOT NULL THEN 'PRI' END AS column_key, " +
               "ucc.comments AS column_comment " +
               "FROM user_tab_columns utc " +
               "LEFT JOIN user_col_comments ucc ON utc.table_name = ucc.table_name AND utc.column_name = ucc.column_name " +
               "LEFT JOIN (" +
               "  SELECT a.column_name FROM user_constraints c " +
               "  JOIN user_cons_columns a ON c.constraint_name = a.constraint_name " +
               "  WHERE c.table_name = '" + tableName + "' AND c.constraint_type = 'P'" +
               ") col ON utc.column_name = col.column_name " +
               "WHERE utc.table_name = '" + tableName + "' " +
               "ORDER BY utc.column_id";
    }

    @Override
    protected String getTableCommentQuery(String tableName) {
        return "SELECT comments FROM user_tab_comments WHERE table_name = '" + tableName + "'";
    }

    @Override
    protected String getTablesQuery() {
        return "SELECT table_name FROM user_tables ORDER BY table_name";
    }

    @Override
    public String getDatabaseType() {
        return "Oracle";
    }

    @Override
    protected String mapJavaType(String sqlType) {
        if (sqlType == null) return "Object";

        sqlType = sqlType.toUpperCase();
        switch (sqlType) {
            case "NUMBER":
                return "java.math.BigDecimal";
            case "INTEGER":
            case "INT":
                return "Long";
            case "VARCHAR2":
            case "VARCHAR":
            case "CHAR":
            case "CLOB":
            case "NCLOB":
                return "String";
            case "DATE":
            case "TIMESTAMP":
                return "java.util.Date";
            case "BLOB":
            case "RAW":
            case "LONG RAW":
                return "byte[]";
            case "FLOAT":
                return "java.math.BigDecimal";
            default:
                return "String";
        }
    }
}
