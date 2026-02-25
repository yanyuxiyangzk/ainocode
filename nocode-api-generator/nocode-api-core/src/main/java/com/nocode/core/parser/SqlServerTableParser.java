package com.nocode.core.parser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL Server表解析器
 */
public class SqlServerTableParser extends AbstractTableParser {

    @Override
    public List<String> getTables(Connection conn) {
        List<String> tables = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME")) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            throw new RuntimeException("获取表列表失败", e);
        }
        return tables;
    }

    @Override
    protected String getTableComment(Connection conn, String tableName) {
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT CAST(extended_properties.value AS NVARCHAR(500)) AS description " +
                "FROM sys.tables t " +
                "LEFT JOIN sys.extended_properties ON t.object_id = major_id AND minor_id = 0 " +
                "WHERE t.name = '" + tableName + "'")) {
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
        return "SELECT c.COLUMN_NAME, c.DATA_TYPE, c.IS_NULLABLE, c.COLUMN_DEFAULT, " +
               "c.CHARACTER_MAXIMUM_LENGTH, c.NUMERIC_PRECISION, c.NUMERIC_SCALE, " +
               "CASE WHEN pk.COLUMN_NAME IS NOT NULL THEN 'PRI' END AS column_key, " +
               "ep.value AS column_comment " +
               "FROM INFORMATION_SCHEMA.COLUMNS c " +
               "LEFT JOIN (" +
               "  SELECT ku.COLUMN_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
               "  JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE ku ON tc.CONSTRAINT_NAME = ku.CONSTRAINT_NAME " +
               "  WHERE tc.TABLE_NAME = '" + tableName + "' AND tc.CONSTRAINT_TYPE = 'PRIMARY KEY'" +
               ") pk ON c.COLUMN_NAME = pk.COLUMN_NAME " +
               "LEFT JOIN sys.columns sc ON sc.object_id = OBJECT_ID('" + tableName + "') AND c.COLUMN_NAME = sc.name " +
               "LEFT JOIN sys.extended_properties ep ON ep.major_id = sc.object_id AND ep.minor_id = sc.column_id AND ep.name = 'MS_Description' " +
               "WHERE c.TABLE_NAME = '" + tableName + "' " +
               "ORDER BY c.ORDINAL_POSITION";
    }

    @Override
    protected String getTableCommentQuery(String tableName) {
        return "SELECT CAST(extended_properties.value AS NVARCHAR(500)) FROM sys.tables t " +
               "LEFT JOIN sys.extended_properties ON t.object_id = major_id AND minor_id = 0 " +
               "WHERE t.name = '" + tableName + "'";
    }

    @Override
    protected String getTablesQuery() {
        return "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME";
    }

    @Override
    public String getDatabaseType() {
        return "SQL Server";
    }

    @Override
    protected String mapJavaType(String sqlType) {
        if (sqlType == null) return "Object";

        sqlType = sqlType.toLowerCase();
        switch (sqlType) {
            case "bigint":
                return "Long";
            case "int":
            case "smallint":
            case "tinyint":
                return "Integer";
            case "float":
            case "real":
            case "decimal":
            case "numeric":
            case "money":
            case "smallmoney":
                return "java.math.BigDecimal";
            case "bit":
                return "Boolean";
            case "date":
            case "datetime":
            case "datetime2":
            case "smalldatetime":
                return "java.util.Date";
            case "time":
                return "java.sql.Time";
            case "char":
            case "varchar":
            case "text":
            case "nchar":
            case "nvarchar":
            case "ntext":
                return "String";
            case "binary":
            case "varbinary":
            case "image":
                return "byte[]";
            case "uniqueidentifier":
                return "String";
            case "sql_variant":
                return "Object";
            default:
                return "String";
        }
    }
}
