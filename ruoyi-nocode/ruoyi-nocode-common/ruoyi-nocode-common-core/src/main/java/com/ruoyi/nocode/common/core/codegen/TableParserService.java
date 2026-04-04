package com.ruoyi.nocode.common.core.codegen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表结构解析服务
 * <p>
 * 从数据库读取表结构信息并转换为TableMetaData
 *
 * @author ruoyi
 */
public class TableParserService {

    private static final Logger log = LoggerFactory.getLogger(TableParserService.class);

    /**
     * 单例实例
     */
    private static volatile TableParserService instance;

    /**
     * 数据源
     */
    private DataSource dataSource;

    /**
     * 表前缀配置
     */
    private String tablePrefix = "";

    /**
     * 作者
     */
    private String author = "ruoyi";

    /**
     * 邮箱
     */
    private String email = "ruoyi@ruoyi.com";

    /**
     * 包名
     */
    private String packageName = "com.ruoyi.system";

    /**
     * 基础路径
     */
    private String basePath = "/tmp/codegen";

    /**
     * 类名注释正则
     */
    private static final Pattern TABLE_COMMENT_PATTERN = Pattern.compile("(.*?)(?:\\s*\\(\\s*(\\w+)\\s*\\))?\\s*$");

    private TableParserService() {
    }

    /**
     * 获取单例实例
     */
    public static TableParserService getInstance() {
        if (instance == null) {
            synchronized (TableParserService.class) {
                if (instance == null) {
                    instance = new TableParserService();
                }
            }
        }
        return instance;
    }

    /**
     * 设置数据源
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 设置配置
     */
    public void setConfig(String tablePrefix, String author, String email, String packageName, String basePath) {
        this.tablePrefix = tablePrefix;
        this.author = author;
        this.email = email;
        this.packageName = packageName;
        this.basePath = basePath;
    }

    /**
     * 解析表结构
     *
     * @param tableName 表名
     * @return 表元数据
     */
    public TableMetaData parseTable(String tableName) {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }

        log.info("Parsing table structure: {}", tableName);

        try (Connection conn = dataSource.getConnection()) {
            TableMetaData tableMeta = new TableMetaData();
            tableMeta.setTableName(tableName);
            tableMeta.setTablePrefix(tablePrefix);
            tableMeta.setAuthor(author);
            tableMeta.setEmail(email);
            tableMeta.setPackageName(packageName);
            tableMeta.setBasePath(basePath);
            tableMeta.setCreateTime(LocalDateTime.now());

            // 解析表注释
            parseTableComment(conn, tableMeta);

            // 解析列信息
            parseColumns(conn, tableMeta);

            // 设置实体名称
            String entityName = TypeConvertUtil.toClassName(tableName, tablePrefix);
            tableMeta.setEntityName(entityName);
            tableMeta.setVariableName(TypeConvertUtil.toVariableName(entityName));

            // 设置模块名
            tableMeta.setModuleName(extractModuleName(tableName));

            log.info("Successfully parsed table '{}', found {} columns", tableName, tableMeta.getColumns().size());
            return tableMeta;

        } catch (SQLException e) {
            log.error("Failed to parse table: {}", tableName, e);
            throw new RuntimeException("Failed to parse table: " + tableName, e);
        }
    }

    /**
     * 解析所有表
     *
     * @return 表名列表
     */
    public List<String> listTables() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }

        List<String> tables = new ArrayList<>();
        String sql = "SHOW TABLES";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tables.add(rs.getString(1));
            }

        } catch (SQLException e) {
            log.error("Failed to list tables", e);
            throw new RuntimeException("Failed to list tables", e);
        }

        return tables;
    }

    /**
     * 解析表注释
     */
    private void parseTableComment(Connection conn, TableMetaData tableMeta) throws SQLException {
        String sql = "SHOW CREATE TABLE " + tableMeta.getTableName();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String createSql = rs.getString(2);
                // 从CREATE语句中提取注释
                Matcher matcher = Pattern.compile("COMMENT='([^']*)'").matcher(createSql);
                if (matcher.find()) {
                    tableMeta.setTableComment(matcher.group(1));
                }
            }
        }
    }

    /**
     * 解析列信息
     */
    private void parseColumns(Connection conn, TableMetaData tableMeta) throws SQLException {
        String sql = "SHOW FULL COLUMNS FROM " + tableMeta.getTableName();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ColumnMetaData column = new ColumnMetaData();

                String columnName = rs.getString("Field");
                String columnType = rs.getString("Type");
                String columnComment = rs.getString("Comment");
                String columnKey = rs.getString("Key");
                String extra = rs.getString("Extra");
                String isNull = rs.getString("Null");
                String defaultValue = rs.getString("Default");

                column.setColumnName(columnName);
                column.setColumnType(columnType);
                column.setColumnComment(columnComment != null ? columnComment : "");
                column.setPrimaryKey(TypeConvertUtil.isPrimaryKey(columnName, columnKey));
                column.setAutoIncrement(TypeConvertUtil.isAutoIncrement(columnType, extra));
                column.setNullable(!"NO".equalsIgnoreCase(isNull));
                column.setDefaultValue(defaultValue);
                column.setJavaType(TypeConvertUtil.toJavaType(columnType));
                column.setJdbcType(TypeConvertUtil.toJdbcType(columnType));
                column.setFieldName(TypeConvertUtil.toCamelCase(columnName));

                tableMeta.getColumns().add(column);

                // 设置主键列
                if (column.isPrimaryKey()) {
                    if (tableMeta.getPrimaryKeyColumn() == null) {
                        tableMeta.setPrimaryKeyColumn(column);
                    }
                }
            }
        }
    }

    /**
     * 从表名提取模块名
     */
    private String extractModuleName(String tableName) {
        String name = tableName;
        if (tablePrefix != null && !tablePrefix.isBlank()) {
            if (name.toLowerCase().startsWith(tablePrefix.toLowerCase())) {
                name = name.substring(tablePrefix.length());
            }
        }
        // 取第一个下划线前的部分作为模块名
        int underscoreIndex = name.indexOf('_');
        if (underscoreIndex > 0) {
            return name.substring(0, underscoreIndex);
        }
        return name;
    }

    /**
     * 构建表元数据（不连接数据库，用于测试）
     */
    public static TableMetaData buildMockTable(String tableName, String tableComment, List<ColumnMetaData> columns) {
        TableMetaData tableMeta = new TableMetaData();
        tableMeta.setTableName(tableName);
        tableMeta.setTableComment(tableComment);
        tableMeta.setEntityName(TypeConvertUtil.toClassName(tableName, ""));
        tableMeta.setVariableName(TypeConvertUtil.toVariableName(tableMeta.getEntityName()));
        tableMeta.setPackageName("com.ruoyi.system");
        tableMeta.setAuthor("ruoyi");
        tableMeta.setEmail("ruoyi@ruoyi.com");
        tableMeta.setBasePath("/tmp/codegen");
        tableMeta.setCreateTime(LocalDateTime.now());
        tableMeta.setColumns(columns);

        // 设置主键列
        for (ColumnMetaData column : columns) {
            if (column.isPrimaryKey()) {
                tableMeta.setPrimaryKeyColumn(column);
                break;
            }
        }

        return tableMeta;
    }
}
