package com.nocode.core.metadata;

import com.nocode.core.entity.DatasourceConfig;
import com.nocode.core.entity.ForeignKeyInfo;
import com.nocode.core.entity.TableInfo;
import com.nocode.core.parser.TableParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 元数据缓存
 */
@Slf4j
@Component
public class MetadataCache {
    private final TableParserFactory parserFactory;
    private final Map<String, Map<String, TableInfo>> cache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> tablesCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MetadataCache(TableParserFactory parserFactory) {
        this.parserFactory = parserFactory;
        // 定时清理缓存，2小时执行一次
        scheduler.scheduleAtFixedRate(this::clearStaleCache, 2, 2, TimeUnit.HOURS);
    }

    /**
     * 获取指定数据源的表列表
     */
    public List<String> getTables(DatasourceConfig config, DataSource dataSource) {
        String key = config.getName();
        log.info("getTables: datasource={}, cacheKey={}", key, key);

        List<String> tables = tablesCache.get(key);
        
        // 如果缓存存在且非空，直接返回
        if (tables != null && !tables.isEmpty()) {
            log.info("  使用缓存中的表列表，共 {} 个表", tables.size());
            return tables;
        }
        
        // 如果缓存存在但是空的，尝试重新查询
        if (tables != null && tables.isEmpty()) {
            log.warn("  缓存中表列表为空，尝试重新查询...");
            tablesCache.remove(key);
        }
        
        log.info("  缓存为空，开始加载表列表...");
        try (Connection conn = dataSource.getConnection()) {
            tables = parserFactory.getTables(config, conn);
            tablesCache.put(key, tables);
            log.info("数据源 [{}] 表列表已加载，共 {} 个表: {}", key, tables.size(), tables);
        } catch (Exception e) {
            log.error("获取表列表失败: datasource={}", key, e);
            throw new RuntimeException("获取表列表失败: " + key, e);
        }

        return tables;
    }

    /**
     * 获取指定数据源的模式列表
     */
    public List<String> getSchemas(DatasourceConfig config, DataSource dataSource) {
        String key = config.getName();
        log.info("getSchemas: datasource={}", key);

        try (Connection conn = dataSource.getConnection()) {
            List<String> schemas = parserFactory.getSchemas(config, conn);
            log.info("数据源 [{}] 模式列表已加载，共 {} 个模式: {}", key, schemas.size(), schemas);
            return schemas;
        } catch (Exception e) {
            log.error("获取模式列表失败: datasource={}", key, e);
            throw new RuntimeException("获取模式列表失败: " + key, e);
        }
    }

    /**
     * 获取指定模式下的表列表
     */
    public List<String> getTablesBySchema(DatasourceConfig config, DataSource dataSource, String schema) {
        String key = config.getName() + "_" + schema;
        log.info("getTablesBySchema: datasource={}, schema={}, cacheKey={}", key, schema, key);

        List<String> tables = tablesCache.get(key);
        
        // 如果缓存存在且非空，直接返回
        if (tables != null && !tables.isEmpty()) {
            log.info("  使用缓存中的表列表，共 {} 个表", tables.size());
            return tables;
        }
        
        // 如果缓存存在但是空的（可能是因为之前查询失败或没有正确传参），尝试重新查询
        if (tables != null && tables.isEmpty()) {
            log.warn("  缓存中表列表为空，可能之前的查询有问题，尝试重新查询...");
            tablesCache.remove(key);
        }
        
        log.info("  缓存为空，开始加载表列表...");
        try (Connection conn = dataSource.getConnection()) {
            tables = parserFactory.getTablesBySchema(config, conn, schema);
            tablesCache.put(key, tables);
            log.info("数据源 [{}] 模式 [{}] 表列表已加载，共 {} 个表: {}", key, schema, tables.size(), tables);
        } catch (Exception e) {
            log.error("获取表列表失败: datasource={}, schema={}", key, schema, e);
            throw new RuntimeException("获取表列表失败: " + key + ", schema: " + schema, e);
        }

        return tables;
    }

    /**
     * 获取指定表的结构信息
     */
    public TableInfo getTableInfo(DatasourceConfig config, DataSource dataSource, String tableName) {
        return getTableInfo(config, dataSource, tableName, null);
    }

    /**
     * 获取指定表的结构信息（支持指定 schema）
     */
    public TableInfo getTableInfo(DatasourceConfig config, DataSource dataSource, String tableName, String schema) {
        String dsKey = config.getName() + (schema != null ? "_" + schema : "");
        Map<String, TableInfo> tableCache = cache.computeIfAbsent(dsKey, k -> new ConcurrentHashMap<>());

        TableInfo tableInfo = tableCache.get(tableName);
        if (tableInfo == null) {
            try (Connection conn = dataSource.getConnection()) {
                tableInfo = parserFactory.parseTable(config, conn, tableName, schema);
                // 设置数据源配置，用于后续SQL构建
                tableInfo.setDatasourceConfig(config);
                tableCache.put(tableName, tableInfo);
                log.debug("表 [{}] 结构已加载，共 {} 个字段", tableName, tableInfo.getColumns().size());
            } catch (Exception e) {
                log.error("解析表结构失败: {}", tableName, e);
                throw new RuntimeException("解析表结构失败: " + tableName, e);
            }
        }

        return tableInfo;
    }

    /**
     * 刷新指定数据源的缓存
     */
    public void refreshDatasource(DatasourceConfig config, DataSource dataSource) {
        String key = config.getName();
        log.info("刷新数据源缓存: {}", key);

        // 先清除缓存
        cache.remove(key);
        tablesCache.remove(key);
        log.info("  缓存已清除");

        // 重新加载
        try {
            getTables(config, dataSource);
            log.info("  数据源 [{}] 缓存刷新成功", key);
        } catch (Exception e) {
            log.error("  数据源 [{}] 缓存刷新失败: {}", key, e.getMessage());
            throw e;
        }
    }

    /**
     * 刷新指定表的缓存
     */
    public void refreshTable(DatasourceConfig config, DataSource dataSource, String tableName) {
        String dsKey = config.getName();
        Map<String, TableInfo> tableCache = cache.get(dsKey);
        if (tableCache != null) {
            tableCache.remove(tableName);
        }
        // 重新加载
        getTableInfo(config, dataSource, tableName);
        log.debug("表 [{}] 缓存已刷新", tableName);
    }

    /**
     * 清除所有缓存
     */
    public void clearAll() {
        cache.clear();
        tablesCache.clear();
        log.info("所有元数据缓存已清除");
    }

    /**
     * 清除过期缓存（空方法，可扩展）
     */
    private void clearStaleCache() {
        log.debug("检查元数据缓存有效期...");
        // 当前实现不标记过期，如需要可扩展LRU策略
    }

    /**
     * 获取缓存的表数量
     */
    public int getCachedTableCount(String datasourceName) {
        Map<String, TableInfo> tableCache = cache.get(datasourceName);
        return tableCache != null ? tableCache.size() : 0;
    }

    /**
     * 获取缓存的数据源数量
     */
    public int getCachedDatasourceCount() {
        return cache.size();
    }

    /**
     * 获取指定表的外键关系
     */
    public List<ForeignKeyInfo> getForeignKeys(DatasourceConfig config, DataSource dataSource, String tableName) {
        String dsKey = config.getName();
        log.info("getForeignKeys: datasource={}, table={}", dsKey, tableName);
        try (Connection conn = dataSource.getConnection()) {
            return parserFactory.getForeignKeys(config, conn, tableName);
        } catch (Exception e) {
            log.error("获取外键信息失败: datasource={}, table={}", dsKey, tableName, e);
            throw new RuntimeException("获取外键信息失败: " + tableName, e);
        }
    }

    /**
     * 获取所有外键关系
     */
    public List<ForeignKeyInfo> getAllForeignKeys(DatasourceConfig config, DataSource dataSource) {
        return getAllForeignKeys(config, dataSource, null);
    }

    /**
     * 获取所有外键关系（支持指定 schema）
     */
    public List<ForeignKeyInfo> getAllForeignKeys(DatasourceConfig config, DataSource dataSource, String schema) {
        String dsKey = config.getName();
        log.info("getAllForeignKeys: datasource={}, schema={}", dsKey, schema);
        try (Connection conn = dataSource.getConnection()) {
            return parserFactory.getAllForeignKeys(config, conn, schema);
        } catch (Exception e) {
            log.error("获取所有外键信息失败: datasource={}, schema={}", dsKey, schema, e);
            throw new RuntimeException("获取所有外键信息失败", e);
        }
    }
}
