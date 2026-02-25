package com.nocode.core.parser;

import com.nocode.core.entity.DatabaseType;
import com.nocode.core.entity.DatasourceConfig;
import com.nocode.core.entity.ForeignKeyInfo;
import com.nocode.core.entity.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.List;

/**
 * 表解析器工厂
 */
@Slf4j
@Component
public class TableParserFactory {
    private final MySqlTableParser mySqlParser = new MySqlTableParser();
    private final PostgreSqlTableParser postgreSqlParser = new PostgreSqlTableParser();
    private final OracleTableParser oracleTableParser = new OracleTableParser();
    private final SqlServerTableParser sqlServerTableParser = new SqlServerTableParser();

    /**
     * 获取指定数据源的解析器
     */
    public TableParser getParser(DatasourceConfig config) {
        DatabaseType dbType = config.getDatabaseType();
        TableParser parser;
        switch (dbType) {
            case MYSQL:
                parser = mySqlParser;
                break;
            case POSTGRESQL:
                parser = postgreSqlParser;
                // 从 JDBC URL 中提取 schema
                String jdbcUrl = config.getJdbcUrl();
                String schema = extractSchema(jdbcUrl, "postgresql");
                log.info("PostgreSQL JDBC URL: {}, 提取的schema: {}", jdbcUrl, schema);
                if (schema != null) {
                    ((AbstractTableParser) parser).setSchema(schema);
                    log.info("已设置 PostgreSQL schema 为: {}", schema);
                }
                break;
            case ORACLE:
                parser = oracleTableParser;
                break;
            case SQLSERVER:
                parser = sqlServerTableParser;
                break;
            default:
                throw new RuntimeException("不支持的数据库类型: " + dbType);
        }
        return parser;
    }

    /**
     * 从 JDBC URL 中提取 schema
     */
    private String extractSchema(String jdbcUrl, String dbType) {
        if (jdbcUrl == null) return null;

        switch (dbType) {
            case "postgresql":
                // 格式: jdbc:postgresql://host:port/db?currentSchema=schemaName
                try {
                    // 直接解析字符串，查找 currentSchema 参数
                    int queryIndex = jdbcUrl.indexOf('?');
                    if (queryIndex != -1) {
                        String query = jdbcUrl.substring(queryIndex + 1);
                        for (String param : query.split("&")) {
                            String[] keyValue = param.split("=");
                            if (keyValue.length == 2 && "currentschema".equalsIgnoreCase(keyValue[0])) {
                                String schema = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                log.info("从JDBC URL提取到PostgreSQL schema: {}", schema);
                                return schema;
                            }
                        }
                    }
                    log.info("JDBC URL中没有指定currentSchema参数");
                } catch (Exception e) {
                    log.warn("解析JDBC URL失败: {}", e.getMessage());
                }
                // 默认返回 null，由解析器使用 "public" schema
                return null;
            default:
                return null;
        }
    }

    /**
     * 获取所有表列表
     */
    public List<String> getTables(DatasourceConfig config, Connection conn) {
        return getParser(config).getTables(conn);
    }

    /**
     * 获取所有模式列表
     */
    public List<String> getSchemas(DatasourceConfig config, Connection conn) {
        return getParser(config).getSchemas(conn);
    }

    /**
     * 获取指定模式下的表列表
     */
    public List<String> getTablesBySchema(DatasourceConfig config, Connection conn, String schema) {
        TableParser parser = getParser(config);
        // 设置 schema
        if (parser instanceof AbstractTableParser) {
            ((AbstractTableParser) parser).setSchema(schema);
        }
        return parser.getTablesBySchema(conn, schema);
    }

    /**
     * 解析表结构
     */
    public TableInfo parseTable(DatasourceConfig config, Connection conn, String tableName) {
        return parseTable(config, conn, tableName, null);
    }

    /**
     * 解析表结构（支持指定 schema）
     */
    public TableInfo parseTable(DatasourceConfig config, Connection conn, String tableName, String schema) {
        TableParser parser = getParser(config);
        // 如果指定了 schema，覆盖默认设置
        if (schema != null && !schema.isEmpty() && parser instanceof AbstractTableParser) {
            ((AbstractTableParser) parser).setSchema(schema);
            log.info("解析表结构时设置 schema 为: {}", schema);
        }
        return parser.parseTable(conn, tableName);
    }

    /**
     * 获取指定表的外键关系
     */
    public List<ForeignKeyInfo> getForeignKeys(DatasourceConfig config, Connection conn, String tableName) {
        return getParser(config).getForeignKeys(conn, tableName);
    }

    /**
     * 获取所有外键关系
     */
    public List<ForeignKeyInfo> getAllForeignKeys(DatasourceConfig config, Connection conn) {
        return getAllForeignKeys(config, conn, null);
    }

    /**
     * 获取所有外键关系（支持指定 schema）
     */
    public List<ForeignKeyInfo> getAllForeignKeys(DatasourceConfig config, Connection conn, String schema) {
        TableParser parser = getParser(config);
        // 如果指定了 schema，覆盖默认设置
        if (schema != null && !schema.isEmpty() && parser instanceof AbstractTableParser) {
            ((AbstractTableParser) parser).setSchema(schema);
            log.info("获取外键时设置 schema 为: {}", schema);
        }
        return parser.getAllForeignKeys(conn);
    }
}
