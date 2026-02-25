package com.nocode.core.parser;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.entity.DatabaseType;
import com.nocode.core.entity.ForeignKeyInfo;
import com.nocode.core.entity.TableInfo;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 表解析器抽象基类
 */
public abstract class AbstractTableParser implements TableParser {
    protected DatabaseType databaseType = DatabaseType.MYSQL;
    protected String schema = "public";

    /**
     * 设置数据库类型
     */
    public void setDatabaseType(DatabaseType type) {
        this.databaseType = type;
    }

    /**
     * 设置 schema 名称
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取 schema 名称
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 获取所有模式名（默认返回空列表，子类可重写）
     */
    @Override
    public List<String> getSchemas(Connection conn) {
        return new ArrayList<>();
    }

    /**
     * 获取指定模式下的所有表名（默认调用getTables，子类可重写）
     */
    @Override
    public List<String> getTablesBySchema(Connection conn, String schema) {
        return getTables(conn);
    }

    /**
     * 获取指定表的外键关系（默认返回空列表，子类可重写）
     */
    @Override
    public List<ForeignKeyInfo> getForeignKeys(Connection conn, String tableName) {
        return new ArrayList<>();
    }

    /**
     * 获取所有外键关系（默认返回空列表，子类可重写）
     */
    @Override
    public List<ForeignKeyInfo> getAllForeignKeys(Connection conn) {
        return new ArrayList<>();
    }

    /**
     * 获取数据库类型枚举
     */
    public DatabaseType getDbType() {
        return databaseType;
    }

    @Override
    public TableInfo parseTable(Connection conn, String tableName) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setSnakeName(tableName);
        tableInfo.setCamelName(toCamelCase(tableName));

        List<ColumnInfo> columns = new ArrayList<>();
        String primaryKey = null;
        String primaryKeyType = "Long";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(getColumnsQuery(tableName))) {

            while (rs.next()) {
                ColumnInfo column = new ColumnInfo();
                String name = rs.getString("COLUMN_NAME");
                String type = rs.getString("DATA_TYPE");
                String comment = rs.getString("COLUMN_COMMENT");
                String nullable = rs.getString("IS_NULLABLE");
                String columnKey = rs.getString("COLUMN_KEY");
                String extra = rs.getString("EXTRA");
                String columnDef = rs.getString("COLUMN_DEFAULT");
                Integer length = getInteger(rs, "CHARACTER_MAXIMUM_LENGTH");
                Integer precision = getInteger(rs, "NUMERIC_PRECISION");
                Integer scale = getInteger(rs, "NUMERIC_SCALE");

                column.setName(name);
                column.setType(type);
                column.setJavaType(mapJavaType(type));
                column.setComment(StringUtils.hasText(comment) ? comment : "");
                column.setNullable("YES".equals(nullable));
                // 根据数据库类型判断主键
                column.setPrimaryKey(isPrimaryKey(columnKey, extra));
                column.setAutoIncrement(isAutoIncrement(extra, databaseType));
                column.setDefaultValue(columnDef);
                column.setLength(length);
                column.setPrecision(precision);
                column.setScale(scale);

                columns.add(column);

                if (column.isPrimaryKey()) {
                    primaryKey = name;
                    primaryKeyType = column.getJavaType();
                }
            }

            tableInfo.setColumns(columns);
            tableInfo.setPrimaryKeyColumn(primaryKey);
            tableInfo.setPrimaryKeyType(primaryKeyType);
            tableInfo.setComment(getTableComment(conn, tableName));

        } catch (Exception e) {
            throw new RuntimeException("解析表结构失败: " + tableName, e);
        }

        return tableInfo;
    }

    /**
     * 判断是否为主键
     */
    protected boolean isPrimaryKey(String columnKey, String extra) {
        // MySQL 使用 COLUMN_KEY = 'PRI'
        if ("PRI".equals(columnKey) || "1".equals(columnKey)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为自增列
     */
    protected boolean isAutoIncrement(String extra, DatabaseType dbType) {
        if (extra == null) return false;
        extra = extra.toLowerCase();
        // MySQL
        if (extra.contains("auto_increment")) {
            return true;
        }
        // PostgreSQL
        if (extra.contains("identity") || extra.contains("nextval")) {
            return true;
        }
        return false;
    }

    /**
     * 获取表注释
     */
    protected abstract String getTableComment(Connection conn, String tableName);

    /**
     * 查询列信息的SQL
     */
    protected abstract String getColumnsQuery(String tableName);

    /**
     * 查询表注释的SQL
     */
    protected abstract String getTableCommentQuery(String tableName);

    /**
     * 查询所有表的SQL
     */
    protected abstract String getTablesQuery();

    /**
     * 映射Java类型
     */
    protected String mapJavaType(String sqlType) {
        if (sqlType == null) return "Object";

        sqlType = sqlType.toLowerCase();
        switch (sqlType) {
            case "bigint":
                return "Long";
            case "int":
            case "integer":
            case "mediumint":
            case "smallint":
            case "tinyint":
                return "Integer";
            case "float":
            case "double":
            case "decimal":
            case "numeric":
            case "dec":
                return "java.math.BigDecimal";
            case "date":
            case "datetime":
            case "timestamp":
                return "java.util.Date";
            case "time":
                return "java.sql.Time";
            case "varchar":
            case "char":
            case "text":
            case "mediumtext":
            case "longtext":
            case "enum":
            case "set":
                return "String";
            case "blob":
            case "mediumblob":
            case "longblob":
            case "binary":
            case "varbinary":
                return "byte[]";
            case "boolean":
            case "bit":
                return "Boolean";
            default:
                return "String";
        }
    }

    /**
     * 下划线转驼峰
     */
    protected String toCamelCase(String name) {
        if (name == null) return null;
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }

    /**
     * 安全获取Integer
     */
    protected Integer getInteger(ResultSet rs, String column) {
        try {
            Object value = rs.getObject(column);
            if (value == null) return null;
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
