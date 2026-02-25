package com.nocode.core.executor;

import com.nocode.core.datasource.DatasourceRegistry;
import com.nocode.core.datasource.DynamicDataSource;
import com.nocode.core.entity.*;
import com.nocode.core.metadata.MetadataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * SQL执行器
 */
@Slf4j
@Component
public class SqlExecutor {
    private final DatasourceRegistry registry;
    private final MetadataCache metadataCache;

    public SqlExecutor(DatasourceRegistry registry, MetadataCache metadataCache) {
        this.registry = registry;
        this.metadataCache = metadataCache;
    }

    /**
     * 查询列表（分页）
     */
    public ApiResult queryList(String datasourceName, String tableName, ApiQueryParam param) {
        DatasourceConfig config = registry.getDatasourceConfig(datasourceName);
        DataSource ds = registry.getDatasource(datasourceName);
        TableInfo tableInfo = metadataCache.getTableInfo(config, ds, tableName);

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

            QueryBuilder queryBuilder = new QueryBuilder(tableInfo);
            queryBuilder.table(tableName);

            // 构建查询SQL
            String countSql = queryBuilder.buildCountSql();
            String querySql = queryBuilder.buildQuerySql(param);

            // 执行查询
            Long total = jdbcTemplate.queryForObject(countSql, Long.class);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(querySql);

            // 转换列名：下划线转驼峰
            List<Map<String, Object>> convertedRows = convertColumnNames(rows);

            return ApiResult.ok(convertedRows, total, param.getPage(), param.getSize());
        } catch (Exception e) {
            log.error("查询列表失败: {}.{}", datasourceName, tableName, e);
            return ApiResult.fail("查询失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 查询单条记录
     */
    public ApiResult queryOne(String datasourceName, String tableName, Object id) {
        DatasourceConfig config = registry.getDatasourceConfig(datasourceName);
        DataSource ds = registry.getDatasource(datasourceName);
        TableInfo tableInfo = metadataCache.getTableInfo(config, ds, tableName);

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

            // 根据主键类型转换 id 参数
            Object convertedId = convertIdByType(id, tableInfo);

            QueryBuilder queryBuilder = new QueryBuilder(tableInfo);
            queryBuilder.table(tableName);
            queryBuilder.eq(tableInfo.getPrimaryKeyColumn(), convertedId);

            // 构建查询SQL（whereClause 已包含 WHERE 1=1 和后续条件）
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ").append(queryBuilder.getAlias()).append(".* FROM ")
               .append(queryBuilder.escapeTable(tableName)).append(" ").append(queryBuilder.getAlias());
            // 直接拼接 whereClause，它已经以 " WHERE 1=1" 开头
            sql.append(queryBuilder.getWhereClause());
            // 硬编码 LIMIT 1 OFFSET 0，避免 PostgreSQL 驱动对参数化分页的兼容性问题
            sql.append(" LIMIT 1 OFFSET 0");

            List<Object> params = queryBuilder.getParams();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());

            if (rows.isEmpty()) {
                return ApiResult.fail("记录不存在");
            }

            Map<String, Object> result = convertColumnNames(Collections.singletonList(rows.get(0))).get(0);
            return ApiResult.ok(result);
        } catch (Exception e) {
            log.error("查询单条记录失败: {}.{}", datasourceName, tableName, e);
            return ApiResult.fail("查询失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 新增记录
     */
    public ApiResult insert(String datasourceName, String tableName, Map<String, Object> data) {
        DatasourceConfig config = registry.getDatasourceConfig(datasourceName);
        DataSource ds = registry.getDatasource(datasourceName);
        TableInfo tableInfo = metadataCache.getTableInfo(config, ds, tableName);

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try (Connection conn = DataSourceUtils.getConnection(ds)) {
            QueryBuilder queryBuilder = new QueryBuilder(tableInfo);
            queryBuilder.table(tableName);

            // 检查主键是否为空，如果为空则生成 UUID+MD5
            String pkColumn = tableInfo.getPrimaryKeyColumn();
            Object pkValue = data.get(toCamelCase(pkColumn));
            if (pkValue == null) {
                pkValue = data.get(pkColumn);
            }
            if (pkValue == null || pkValue.toString().isEmpty()) {
                // 生成 UUID 并转换为 MD5
                String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
                String md5Id = md5(uuid);
                data.put(toCamelCase(pkColumn), md5Id);
            }

            String sql = queryBuilder.buildInsertSql();
            List<String> columns = queryBuilder.getInsertColumns();

            // PostgreSQL 使用 RETURNING 子句获取自增ID
            if (config.getDatabaseType() == DatabaseType.POSTGRESQL) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    int idx = 1;
                    for (String col : columns) {
                        Object value = data.get(toCamelCase(col));
                        if (value == null && data.containsKey(col)) {
                            value = data.get(col);
                        }
                        // 根据列类型转换值，处理 timestamp/date/time
                        ColumnInfo colInfo = getColumnInfoByName(col, tableInfo);
                        if (colInfo != null) {
                            value = convertValueByType(value, colInfo.getType());
                        }
                        ps.setObject(idx++, value);
                    }

                    // 执行查询并获取返回的ID
                    List<Map<String, Object>> resultSet = new ArrayList<>();
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Map<String, Object> row = new HashMap<>();
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();
                            for (int i = 1; i <= columnCount; i++) {
                                String columnName = metaData.getColumnLabel(i);
                                row.put(columnName, rs.getObject(i));
                            }
                            resultSet.add(row);
                        }
                    }

                    int affected = 1;
                    Object generatedId = null;
                    if (!resultSet.isEmpty()) {
                        Object idValue = resultSet.get(0).get(tableInfo.getPrimaryKeyColumn());
                        if (idValue != null) {
                            generatedId = idValue;
                        }
                    }

                    Map<String, Object> result = new HashMap<>();
                    result.put("affected", affected);
                    if (generatedId != null) {
                        result.put("id", generatedId);
                    }

                    return ApiResult.ok(result);
                }
            } else {
                // MySQL 和其他数据库使用 RETURN_GENERATED_KEYS
                try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    int idx = 1;
                    for (String col : columns) {
                        Object value = data.get(toCamelCase(col));
                        if (value == null && data.containsKey(col)) {
                            value = data.get(col);
                        }
                        // 根据列类型转换值，处理 timestamp/date/time
                        ColumnInfo colInfo = getColumnInfoByName(col, tableInfo);
                        if (colInfo != null) {
                            value = convertValueByType(value, colInfo.getType());
                        }
                        ps.setObject(idx++, value);
                    }
                    int affected = ps.executeUpdate();

                    // 获取自增ID
                    Object generatedId = null;
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        generatedId = rs.getObject(1);
                    }

                    Map<String, Object> result = new HashMap<>();
                    result.put("affected", affected);
                    if (generatedId != null) {
                        result.put("id", generatedId);
                    }

                    return ApiResult.ok(result);
                }
            }
        } catch (Exception e) {
            log.error("新增记录失败: {}.{}", datasourceName, tableName, e);
            return ApiResult.fail("新增失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 更新记录
     */
    public ApiResult update(String datasourceName, String tableName, Object id, Map<String, Object> data) {
        DatasourceConfig config = registry.getDatasourceConfig(datasourceName);
        DataSource ds = registry.getDatasource(datasourceName);
        TableInfo tableInfo = metadataCache.getTableInfo(config, ds, tableName);

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

            QueryBuilder queryBuilder = new QueryBuilder(tableInfo);
            queryBuilder.table(tableName);

            String sql = queryBuilder.buildUpdateSql(id);
            List<Object> params = new ArrayList<>();

            // 添加更新字段值
            String pkColumn = tableInfo.getPrimaryKeyColumn();
            for (ColumnInfo col : tableInfo.getColumns()) {
                if (col.getName().equals(pkColumn) || col.isAutoIncrement()) continue;

                Object value = data.get(toCamelCase(col.getName()));
                if (value == null && data.containsKey(col.getName())) {
                    value = data.get(col.getName());
                }
                // 根据列类型转换值，处理 timestamp/date/time
                value = convertValueByType(value, col.getType());
                params.add(value);
            }
            // 添加主键值
            params.add(id);

            int affected = jdbcTemplate.update(sql, params.toArray());

            return ApiResult.ok(affected);
        } catch (Exception e) {
            log.error("更新记录失败: {}.{}", datasourceName, tableName, e);
            return ApiResult.fail("更新失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 删除记录
     */
    public ApiResult delete(String datasourceName, String tableName, Object id) {
        DatasourceConfig config = registry.getDatasourceConfig(datasourceName);
        DataSource ds = registry.getDatasource(datasourceName);
        TableInfo tableInfo = metadataCache.getTableInfo(config, ds, tableName);

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

            QueryBuilder queryBuilder = new QueryBuilder(tableInfo);
            queryBuilder.table(tableName);

            String sql = queryBuilder.buildDeleteByIdSql();
            int affected = jdbcTemplate.update(sql, id);

            if (affected == 0) {
                return ApiResult.fail("记录不存在");
            }

            return ApiResult.ok(affected);
        } catch (Exception e) {
            log.error("删除记录失败: {}.{}", datasourceName, tableName, e);
            return ApiResult.fail("删除失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 批量删除
     */
    public ApiResult batchDelete(String datasourceName, String tableName, List<Object> ids) {
        DatasourceConfig config = registry.getDatasourceConfig(datasourceName);
        DataSource ds = registry.getDatasource(datasourceName);
        TableInfo tableInfo = metadataCache.getTableInfo(config, ds, tableName);

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

            QueryBuilder queryBuilder = new QueryBuilder(tableInfo);
            queryBuilder.table(tableName);
            queryBuilder.in(tableInfo.getPrimaryKeyColumn(), ids);

            String sql = queryBuilder.buildDeleteSql();
            int affected = jdbcTemplate.update(sql, queryBuilder.getParams().toArray());

            return ApiResult.ok(affected);
        } catch (Exception e) {
            log.error("批量删除失败: {}.{}", datasourceName, tableName, e);
            return ApiResult.fail("批量删除失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 批量新增
     */
    public ApiResult batchInsert(String datasourceName, String tableName, List<Map<String, Object>> dataList) {
        DatasourceConfig config = registry.getDatasourceConfig(datasourceName);
        DataSource ds = registry.getDatasource(datasourceName);
        TableInfo tableInfo = metadataCache.getTableInfo(config, ds, tableName);
        String pkColumn = tableInfo.getPrimaryKeyColumn();

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try (Connection conn = DataSourceUtils.getConnection(ds)) {
            conn.setAutoCommit(false);

            QueryBuilder queryBuilder = new QueryBuilder(tableInfo);
            queryBuilder.table(tableName);

            String sql = queryBuilder.buildInsertSql();
            List<String> columns = queryBuilder.getInsertColumns();

            // 为每条数据检查并生成主键
            for (Map<String, Object> data : dataList) {
                Object pkValue = data.get(toCamelCase(pkColumn));
                if (pkValue == null) {
                    pkValue = data.get(pkColumn);
                }
                if (pkValue == null || pkValue.toString().isEmpty()) {
                    String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
                    data.put(toCamelCase(pkColumn), md5(uuid));
                }
            }

            int totalAffected = 0;
            for (Map<String, Object> data : dataList) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    int idx = 1;
                    for (String col : columns) {
                        Object value = data.get(toCamelCase(col));
                        if (value == null && data.containsKey(col)) {
                            value = data.get(col);
                        }
                        // 根据列类型转换值，处理 timestamp/date/time
                        ColumnInfo colInfo = getColumnInfoByName(col, tableInfo);
                        if (colInfo != null) {
                            value = convertValueByType(value, colInfo.getType());
                        }
                        ps.setObject(idx++, value);
                    }
                    totalAffected += ps.executeUpdate();
                }
            }

            conn.commit();
            return ApiResult.ok(totalAffected);
        } catch (Exception e) {
            log.error("批量新增失败: {}.{}", datasourceName, tableName, e);
            return ApiResult.fail("批量新增失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 自定义查询
     */
    public ApiResult customQuery(String datasourceName, String sql, List<Object> params) {
        DataSource ds = registry.getDatasource(datasourceName);

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params.toArray());
            List<Map<String, Object>> convertedRows = convertColumnNames(rows);
            return ApiResult.ok(convertedRows);
        } catch (Exception e) {
            log.error("自定义查询失败: {}", datasourceName, e);
            return ApiResult.fail("查询失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 执行SQL（DDL/DML）
     */
    public ApiResult execute(String datasourceName, String sql) {
        DataSource ds = registry.getDatasource(datasourceName);

        DynamicDataSource.setCurrentDatasource(datasourceName);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
            int affected = jdbcTemplate.update(sql);

            Map<String, Object> result = new HashMap<>();
            result.put("affected", affected);
            result.put("message", "执行成功");

            // 如果是SELECT查询，返回结果集
            if (sql.trim().toUpperCase().startsWith("SELECT")) {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
                result.put("data", convertColumnNames(rows));
                result.put("count", rows.size());
            }

            return ApiResult.ok(result);
        } catch (Exception e) {
            log.error("执行SQL失败: {}", datasourceName, e);
            return ApiResult.fail("执行失败: " + e.getMessage());
        } finally {
            DynamicDataSource.clearCurrentDatasource();
        }
    }

    /**
     * 根据主键类型转换 id 参数
     * 解决 PostgreSQL 中 bigint = varchar 类型不匹配的问题
     */
    private Object convertIdByType(Object id, TableInfo tableInfo) {
        if (id == null) return id;
        
        // 获取主键列的类型信息
        String pkColumn = tableInfo.getPrimaryKeyColumn();
        for (com.nocode.core.entity.ColumnInfo col : tableInfo.getColumns()) {
            if (col.getName().equals(pkColumn)) {
                String javaType = col.getJavaType();
                
                // 如果主键是 Long/Integer 类型，将 id 转换为对应类型
                if ("Long".equals(javaType) || "long".equals(javaType) || 
                    "Integer".equals(javaType) || "int".equals(javaType)) {
                    try {
                        if (id instanceof Number) {
                            return id;
                        }
                        return Long.parseLong(id.toString());
                    } catch (NumberFormatException e) {
                        // 转换失败，返回原值
                        return id;
                    }
                }
                // varchar/text 类型保持字符串
                break;
            }
        }
        return id;
    }

    /**
     * 转换列名：下划线转驼峰
     */
    private List<Map<String, Object>> convertColumnNames(List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> converted = new LinkedHashMap<>();
            for (String key : row.keySet()) {
                String camelKey = toCamelCase(key);
                converted.put(camelKey, row.get(key));
            }
            result.add(converted);
        }
        return result;
    }

    /**
     * 下划线转驼峰
     */
    private String toCamelCase(String name) {
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
     * 根据列类型转换值，用于处理 PostgreSQL timestamp/date/time 类型
     */
    private Object convertValueByType(Object value, String sqlType) {
        if (value == null) return null;
        if (sqlType == null) return value;

        String typeUpper = sqlType.toUpperCase();

        // 时间类型需要转换
        if (typeUpper.contains("TIMESTAMP") || typeUpper.contains("DATE") || typeUpper.contains("TIME")) {
            if (value instanceof java.sql.Timestamp) {
                return value;
            }
            if (value instanceof java.util.Date) {
                return new java.sql.Timestamp(((java.util.Date) value).getTime());
            }
            if (value instanceof String) {
                try {
                    // 尝试解析为 Timestamp
                    return java.sql.Timestamp.valueOf((String) value);
                } catch (IllegalArgumentException e) {
                    // 如果格式不是标准 timestamp 格式，尝试其他方式
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        java.util.Date date = sdf.parse((String) value);
                        return new java.sql.Timestamp(date.getTime());
                    } catch (Exception ex) {
                        // 如果还是失败，返回原值，让数据库自己处理
                        return value;
                    }
                }
            }
        }
        return value;
    }

    /**
     * 根据列名获取对应的 ColumnInfo
     */
    private ColumnInfo getColumnInfoByName(String columnName, TableInfo tableInfo) {
        for (ColumnInfo col : tableInfo.getColumns()) {
            if (col.getName().equals(columnName)) {
                return col;
            }
        }
        return null;
    }

    /**
     * MD5 加密
     */
    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
