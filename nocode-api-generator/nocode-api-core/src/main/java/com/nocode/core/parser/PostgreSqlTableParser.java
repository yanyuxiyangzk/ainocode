package com.nocode.core.parser;

import com.nocode.core.entity.DatabaseType;
import com.nocode.core.entity.ForeignKeyInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL表解析器
 */
@Slf4j
public class PostgreSqlTableParser extends AbstractTableParser {

    public PostgreSqlTableParser() {
        setDatabaseType(DatabaseType.POSTGRESQL);
    }

    /**
     * 获取 PostgreSQL 中 schema 名称的查询形式
     * 如果 schema 名称包含大写字母或特殊字符，需要使用双引号包裹
     */
    private String getSchemaQuotedName(String schemaName) {
        if (schemaName == null || schemaName.isEmpty()) {
            return "public";
        }
        // 如果 schema 名称包含大写字母，需要使用双引号
        if (schemaName.matches(".*[A-Z].*") || schemaName.contains("_") || schemaName.contains("-")) {
            return "\"" + schemaName + "\"";
        }
        return schemaName.toLowerCase();
    }

    @Override
    public List<String> getTables(Connection conn) {
        List<String> tables = new ArrayList<>();
        // PostgreSQL 内部存储的 schema 名称是小写的，需要转换为小写进行匹配
        String schemaName = schema != null && !schema.isEmpty() ? schema : "public";
        String schemaQuoted = getSchemaQuotedName(schemaName);
        String sql = "SELECT tablename FROM pg_tables WHERE schemaname = '" + schemaName.toLowerCase() + "' ORDER BY tablename";
        log.info("PostgreSQL 查询表列表, schema: {} -> {}, SQL: {}", schema, schemaName, sql);
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                tables.add(rs.getString("tablename"));
            }
            log.info("PostgreSQL 查询到 {} 个表: {}", tables.size(), tables);
        } catch (Exception e) {
            log.error("获取表列表失败, schema: {}", schemaName, e);
            throw new RuntimeException("获取表列表失败: " + e.getMessage(), e);
        }
        return tables;
    }

    @Override
    public List<String> getSchemas(Connection conn) {
        List<String> schemas = new ArrayList<>();
        // 查询时使用小写匹配，但返回原始大小写的名称
        String sql = "SELECT schema_name FROM information_schema.schemata WHERE schema_name NOT IN ('pg_catalog', 'information_schema') ORDER BY schema_name";
        log.info("PostgreSQL 查询所有模式");
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                schemas.add(rs.getString("schema_name"));
            }
            log.info("PostgreSQL 查询到 {} 个模式: {}", schemas.size(), schemas);
        } catch (Exception e) {
            log.error("获取模式列表失败", e);
            throw new RuntimeException("获取模式列表失败: " + e.getMessage(), e);
        }
        return schemas;
    }

    @Override
    public List<String> getTablesBySchema(Connection conn, String schemaName) {
        List<String> tables = new ArrayList<>();
        String schema = schemaName != null && !schemaName.isEmpty() ? schemaName : "public";
        String sql = "SELECT tablename FROM pg_tables WHERE schemaname = '" + schema.toLowerCase() + "' ORDER BY tablename";
        log.info("PostgreSQL 查询表列表, schema: {} -> {}", schemaName, schema);
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                tables.add(rs.getString("tablename"));
            }
            log.info("PostgreSQL 查询到 {} 个表: {}", tables.size(), tables);
        } catch (Exception e) {
            log.error("获取表列表失败, schema: {}", schema, e);
            throw new RuntimeException("获取表列表失败: " + e.getMessage(), e);
        }
        return tables;
    }

    @Override
    protected String getTableComment(Connection conn, String tableName) {
        // PostgreSQL 表名默认小写存储，使用 lower() 确保能正确匹配
        String lowerTableName = tableName.toLowerCase();
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT obj_description('" + schema + "." + lowerTableName + "'::regclass)")) {
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
        // PostgreSQL 表名默认小写存储，使用 lower() 确保能正确匹配
        String lowerTableName = tableName.toLowerCase();
        return "SELECT c.column_name, c.data_type, c.is_nullable, c.column_default, " +
               "c.character_maximum_length, c.numeric_precision, c.numeric_scale, " +
               "pk.column_key, co.description AS column_comment, " +
               "CASE WHEN c.column_default LIKE 'nextval%' THEN 'identity' ELSE '' END AS extra " +
               "FROM information_schema.columns c " +
               "LEFT JOIN (" +
               "  SELECT a.attname AS column_name, 'PRI' AS column_key " +
               "  FROM pg_index i " +
               "  JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = any(i.indkey) " +
               "  WHERE i.indrelid = '" + schema + "." + lowerTableName + "'::regclass AND i.indisprimary" +
               ") pk ON c.column_name = pk.column_name " +
               "LEFT JOIN pg_catalog.pg_description co " +
               "  ON co.objoid = c.table_name::regclass AND co.objsubid = c.ordinal_position " +
               "WHERE c.table_schema = '" + schema + "' AND c.table_name = '" + lowerTableName + "' " +
               "ORDER BY c.ordinal_position";
    }

    @Override
    protected String getTableCommentQuery(String tableName) {
        // PostgreSQL 表名默认小写存储，使用 lower() 确保能正确匹配
        String lowerTableName = tableName.toLowerCase();
        return "SELECT obj_description('" + schema + "." + lowerTableName + "'::regclass)";
    }

    @Override
    protected String getTablesQuery() {
        // PostgreSQL 内部存储的 schema 名称是小写的，需要转换为小写进行匹配
        String schemaName = schema != null && !schema.isEmpty() ? schema.toLowerCase() : "public";
        return "SELECT tablename FROM pg_tables WHERE schemaname = '" + schemaName + "' ORDER BY tablename";
    }

    @Override
    public String getDatabaseType() {
        return "PostgreSQL";
    }

    @Override
    public DatabaseType getDbType() {
        return DatabaseType.POSTGRESQL;
    }

    @Override
    protected String mapJavaType(String sqlType) {
        if (sqlType == null) return "Object";

        sqlType = sqlType.toLowerCase();
        switch (sqlType) {
            case "bigint":
                return "Long";
            case "integer":
            case "int":
            case "int4":
            case "int8":
                return "Long";
            case "smallint":
                return "Integer";
            case "real":
            case "float4":
            case "float8":
            case "numeric":
            case "decimal":
            case "money":
                return "java.math.BigDecimal";
            case "boolean":
            case "bool":
                return "Boolean";
            case "date":
                return "java.util.Date";
            case "timestamp":
            case "timestamptz":
            case "timestamp with time zone":
                return "java.util.Date";
            case "time":
            case "timetz":
                return "java.sql.Time";
            case "json":
            case "jsonb":
                return "String";
            case "text":
            case "varchar":
            case "char":
            case "bpchar":
            case "name":
                return "String";
            case "bytea":
                return "byte[]";
            default:
                return "String";
        }
    }

    @Override
    protected boolean isPrimaryKey(String columnKey, String extra) {
        // PostgreSQL 通过 column_key = 'PRI' 标识主键（在 getColumnsQuery 中设置）
        if ("PRI".equals(columnKey) || "1".equals(columnKey)) {
            return true;
        }
        return false;
    }

    @Override
    public List<ForeignKeyInfo> getForeignKeys(Connection conn, String tableName) {
        List<ForeignKeyInfo> fkList = new ArrayList<>();
        String lowerTableName = tableName.toLowerCase();
        
        // 使用 information_schema 查询外键，兼容性更好
        String sql = "SELECT " +
                     "  tc.constraint_name, " +
                     "  kcu.column_name, " +
                     "  ccu.table_name AS referenced_table, " +
                     "  ccu.column_name AS referenced_column " +
                     "FROM information_schema.table_constraints tc " +
                     "JOIN information_schema.key_column_usage kcu " +
                     "  ON tc.constraint_name = kcu.constraint_name " +
                     "  AND tc.table_schema = kcu.table_schema " +
                     "JOIN information_schema.constraint_column_usage ccu " +
                     "  ON ccu.constraint_name = tc.constraint_name " +
                     "  AND ccu.table_schema = tc.table_schema " +
                     "WHERE tc.constraint_type = 'FOREIGN KEY' " +
                     "  AND tc.table_schema = '" + schema + "' " +
                     "  AND tc.table_name = '" + lowerTableName + "'";
        
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                ForeignKeyInfo fk = new ForeignKeyInfo();
                fk.setFkName(rs.getString("constraint_name"));
                fk.setTableName(tableName);
                fk.setColumnName(rs.getString("column_name"));
                fk.setReferencedTableName(rs.getString("referenced_table"));
                fk.setReferencedColumnName(rs.getString("referenced_column"));
                fkList.add(fk);
            }
        } catch (Exception e) {
            log.error("获取外键信息失败: {}", tableName, e);
            // 不抛异常，返回空列表
        }
        return fkList;
    }

    @Override
    public List<ForeignKeyInfo> getAllForeignKeys(Connection conn) {
        List<ForeignKeyInfo> fkList = new ArrayList<>();
        
        // 使用 information_schema 查询所有外键，兼容性更好
        String sql = "SELECT " +
                     "  tc.table_name, " +
                     "  tc.constraint_name, " +
                     "  kcu.column_name, " +
                     "  ccu.table_name AS referenced_table, " +
                     "  ccu.column_name AS referenced_column " +
                     "FROM information_schema.table_constraints tc " +
                     "JOIN information_schema.key_column_usage kcu " +
                     "  ON tc.constraint_name = kcu.constraint_name " +
                     "  AND tc.table_schema = kcu.table_schema " +
                     "JOIN information_schema.constraint_column_usage ccu " +
                     "  ON ccu.constraint_name = tc.constraint_name " +
                     "  AND ccu.table_schema = tc.table_schema " +
                     "WHERE tc.constraint_type = 'FOREIGN KEY' " +
                     "  AND tc.table_schema = '" + schema + "'";
        
        try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                ForeignKeyInfo fk = new ForeignKeyInfo();
                fk.setFkName(rs.getString("constraint_name"));
                fk.setTableName(rs.getString("table_name"));
                fk.setColumnName(rs.getString("column_name"));
                fk.setReferencedTableName(rs.getString("referenced_table"));
                fk.setReferencedColumnName(rs.getString("referenced_column"));
                fkList.add(fk);
            }
        } catch (Exception e) {
            log.error("获取所有外键信息失败", e);
            // 不抛异常，返回空列表
        }
        return fkList;
    }
}
