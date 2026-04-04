package com.ruoyi.nocode.common.core.codegen.engine;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.config.DbTypeConverter;
import com.ruoyi.nocode.common.core.codegen.model.ColumnInfo;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表上下文
 * <p>
 * 用于从数据库提取表结构元数据
 *
 * @author ruoyi
 */
public class TableContext {

    private static final Logger log = LoggerFactory.getLogger(TableContext.class);

    /**
     * 从数据库表名获取表信息
     *
     * @param tableName 表名
     * @param config    配置
     * @return 表信息
     */
    public static TableInfo getTableInfo(String tableName, CodeGenConfig config) {
        try (Connection connection = getConnection(config)) {
            return getTableInfo(tableName, connection, config);
        } catch (Exception e) {
            log.error("Failed to get table info: {}", tableName, e);
            throw new RuntimeException("Failed to get table info: " + e.getMessage(), e);
        }
    }

    /**
     * 从数据库连接获取表信息
     */
    public static TableInfo getTableInfo(String tableName, Connection connection, CodeGenConfig config) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setTablePrefix(config.getTablePrefix());

        // 获取表注释
        try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) {
                tableInfo.setTableComment(rs.getString("REMARKS"));
            }
        }

        // 获取列信息
        List<ColumnInfo> columns = new ArrayList<>();
        ColumnInfo primaryKey = null;

        try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                ColumnInfo column = extractColumn(rs, config, tableName);
                columns.add(column);
                if (column.isPrimaryKey()) {
                    primaryKey = column;
                }
            }
        }

        tableInfo.setColumns(columns);
        tableInfo.setPrimaryKey(primaryKey);

        // 处理表信息
        processTableInfo(tableInfo, config);

        return tableInfo;
    }

    /**
     * 提取列信息
     */
    private static ColumnInfo extractColumn(ResultSet rs, CodeGenConfig config, String tableName) throws SQLException {
        ColumnInfo column = new ColumnInfo();

        String columnName = rs.getString("COLUMN_NAME");
        String columnType = rs.getString("TYPE_NAME");
        int columnSize = rs.getInt("COLUMN_SIZE");
        int decimalDigits = rs.getInt("DECIMAL_DIGITS");
        String remarks = rs.getString("REMARKS");
        String defaultValue = rs.getString("COLUMN_DEF");
        int nullable = rs.getInt("NULLABLE");

        column.setColumnName(columnName);
        column.setColumnType(columnType);
        column.setColumnSize(columnSize);
        column.setDecimalDigits(decimalDigits);
        column.setComment(remarks != null ? remarks : "");
        column.setDefaultValue(defaultValue);
        column.setNullable(nullable == DatabaseMetaData.columnNullable);
        column.setJavaFieldName(ColumnInfo.toCamelCase(columnName));

        // 转换Java类型
        String javaType = DbTypeConverter.toJavaType(columnType, columnSize, decimalDigits, config.getDbType());
        column.setJavaType(javaType);

        // 检查是否为主键（后续会更新）
        column.setPrimaryKey(false);
        column.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));

        // 检查是否为逻辑删除字段
        if (config.isUseLogicDelete() && config.getLogicDeleteField().equalsIgnoreCase(columnName)) {
            column.setLogicDeleteField(true);
        }

        // 检查是否为租户字段
        if (config.isUseTenant() && config.getTenantField().equalsIgnoreCase(columnName)) {
            column.setTenantField(true);
        }

        // 必填字段处理
        column.setRequired(!column.isNullable() && !column.isPrimaryKey() && !"YES".equals(rs.getString("IS_AUTOINCREMENT")));

        // 展示类型
        column.setDisplayType(determineDisplayType(column));

        return column;
    }

    /**
     * 确定展示类型
     */
    private static String determineDisplayType(ColumnInfo column) {
        String javaType = column.getJavaType();
        String columnName = column.getColumnName().toLowerCase();

        // 特殊字段判断
        if (columnName.contains("status") || columnName.contains("状态")) {
            return "select";
        }
        if (columnName.contains("type") || columnName.contains("类型")) {
            return "select";
        }
        if (columnName.contains("sex") || columnName.contains("性别")) {
            return "radio";
        }
        if (columnName.contains("yes") || columnName.contains("no") || columnName.contains("是否")) {
            return "checkbox";
        }
        if (columnName.contains("time") || columnName.contains("date") || columnName.contains("时间")) {
            if (columnName.contains("start") || columnName.contains("begin") || columnName.contains("开始")) {
                return "datetime";
            }
            if (columnName.contains("end") || columnName.contains("finish") || columnName.contains("结束")) {
                return "datetime";
            }
            return "date";
        }
        if (columnName.contains("email") || columnName.contains("邮箱")) {
            return "input";
        }
        if (columnName.contains("phone") || columnName.contains("tel") || columnName.contains("电话")) {
            return "input";
        }
        if (columnName.contains("url") || columnName.contains("link") || columnName.contains("链接")) {
            return "input";
        }
        if (columnName.contains("image") || columnName.contains("img") || columnName.contains("photo") || columnName.contains("图片")) {
            return "image";
        }
        if (columnName.contains("file") || columnName.contains("attachment") || columnName.contains("附件")) {
            return "file";
        }
        if (columnName.contains("content") || columnName.contains("description") || columnName.contains("详情") || columnName.contains("描述")) {
            if (column.getColumnSize() > 500) {
                return "textarea";
            }
        }
        if (columnName.contains("money") || columnName.contains("amount") || columnName.contains("金额")) {
            return "input";
        }
        if (columnName.contains("sort") || columnName.contains("order") || columnName.contains("seq") || columnName.contains("顺序")) {
            return "input";
        }

        // 根据Java类型判断
        if ("String".equals(javaType)) {
            if (column.getColumnSize() > 500) {
                return "textarea";
            }
            return "input";
        }
        if ("Integer".equals(javaType) || "Long".equals(javaType) || "Double".equals(javaType) || "BigDecimal".equals(javaType)) {
            return "input";
        }
        if ("Boolean".equals(javaType)) {
            return "radio";
        }
        if ("LocalDate".equals(javaType)) {
            return "date";
        }
        if ("LocalDateTime".equals(javaType) || "Timestamp".equals(javaType)) {
            return "datetime";
        }

        return "input";
    }

    /**
     * 处理表信息（实体类名、主键等）
     */
    private static void processTableInfo(TableInfo tableInfo, CodeGenConfig config) {
        String tableName = tableInfo.getTableName();

        // 移除表前缀，获取实体类名
        String entityName;
        if (config.isAutoRemovePrefix() && config.getTablePrefix() != null && !config.getTablePrefix().isEmpty()) {
            entityName = tableName.substring(config.getTablePrefix().length());
        } else {
            entityName = tableName;
        }

        // 下划线转驼峰并首字母大写
        entityName = ColumnInfo.toCamelCase(entityName);
        entityName = Character.toUpperCase(entityName.charAt(0)) + entityName.substring(1);

        tableInfo.setEntityName(entityName);
        tableInfo.setEntityComment(tableInfo.getTableComment());
        tableInfo.setRemovalPrefixName(entityName);

        // 设置主键信息
        if (tableInfo.getPrimaryKey() != null) {
            tableInfo.setPrimaryKeyFieldName(tableInfo.getPrimaryKey().getJavaFieldName());
            tableInfo.setPrimaryKeyJavaType(tableInfo.getPrimaryKey().getJavaType());
        }

        // 设置业务主键（第一个非自增主键列）
        tableInfo.getColumns().stream()
                .filter(c -> c.isPrimaryKey() && !c.isAutoIncrement())
                .findFirst()
                .ifPresent(tableInfo::setBizKey);

        // 如果没有业务主键，使用主键
        if (tableInfo.getBizKey() == null && tableInfo.getPrimaryKey() != null) {
            tableInfo.setBizKey(tableInfo.getPrimaryKey());
        }

        // 设置时间
        tableInfo.setCreateTime(LocalDateTime.now());
        tableInfo.setUpdateTime(LocalDateTime.now());

        // 设置逻辑删除
        if (config.isUseLogicDelete()) {
            tableInfo.setUseLogicDelete(true);
            tableInfo.setLogicDeleteField(config.getLogicDeleteField());
            tableInfo.setLogicDeleteValue(config.getLogicDeleteValue());
            tableInfo.setLogicNotDeleteValue(config.getLogicNotDeleteValue());
        }

        // 设置租户
        if (config.isUseTenant()) {
            tableInfo.setUseTenant(true);
            tableInfo.setTenantField(config.getTenantField());
        }
    }

    /**
     * 获取数据库连接
     */
    private static Connection getConnection(CodeGenConfig config) throws SQLException {
        try {
            DriverManager.registerDriver((Driver) Class.forName(config.getJdbcDriver()).newInstance());
        } catch (Exception e) {
            log.warn("Driver registration failed, may already be registered", e);
        }
        return DriverManager.getConnection(config.getJdbcUrl(), config.getJdbcUsername(), config.getJdbcPassword());
    }

    /**
     * 获取所有表名
     */
    public static List<String> getAllTableNames(CodeGenConfig config) {
        List<String> tableNames = new ArrayList<>();
        try (Connection connection = getConnection(config)) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();

            try (ResultSet rs = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }
        } catch (Exception e) {
            log.error("Failed to get table names", e);
            throw new RuntimeException("Failed to get table names: " + e.getMessage(), e);
        }
        return tableNames;
    }

    /**
     * 检查表是否存在
     */
    public static boolean tableExists(String tableName, CodeGenConfig config) {
        try (Connection connection = getConnection(config)) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();

            try (ResultSet rs = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"})) {
                return rs.next();
            }
        } catch (Exception e) {
            log.error("Failed to check table exists: {}", tableName, e);
            return false;
        }
    }
}
