package com.nocode.core.datasource;

import com.nocode.core.config.NocodeApiProperties;
import com.nocode.core.entity.DatasourceConfig;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源注册中心
 */
@Slf4j
@Component
public class DatasourceRegistry {
    /** 数据源缓存 key: datasourceName */
    private final Map<String, DruidDataSource> datasourceCache = new ConcurrentHashMap<>();
    /** 数据源配置缓存 */
    private final Map<String, DatasourceConfig> configCache = new ConcurrentHashMap<>();
    /** 默认数据源名称 */
    private volatile String defaultDatasourceName;
    /** 配置文件中的数据源（备用） */
    private final java.util.List<DatasourceConfig> fileConfigs;

    public DatasourceRegistry(NocodeApiProperties properties) {
        this.fileConfigs = properties.getDatasources() != null ? properties.getDatasources() : new java.util.ArrayList<>();
        initDatasources(fileConfigs);
    }

    /**
     * 初始化数据源 - 从数据库加载，失败则从配置文件加载
     */
    public void initDatasources(java.util.List<DatasourceConfig> configs) {
        if (configs == null || configs.isEmpty()) {
            log.warn("未配置数据源");
            return;
        }

        for (DatasourceConfig config : configs) {
            if (!config.isEnabled()) {
                log.info("数据源 [{}] 已禁用，跳过初始化", config.getName());
                continue;
            }
            registerDatasource(config);
        }
    }

    /**
     * 注册数据源（内存中）
     */
    public void registerDatasource(DatasourceConfig config) {
        if (datasourceCache.containsKey(config.getName())) {
            log.warn("数据源 [{}] 已存在，将被替换", config.getName());
            unregisterDatasource(config.getName());
        }

        try {
            DruidDataSource dataSource = config.createDataSource();
            dataSource.init();
            datasourceCache.put(config.getName(), dataSource);
            configCache.put(config.getName(), config);

            if (defaultDatasourceName == null) {
                defaultDatasourceName = config.getName();
            }

            log.info("数据源 [{}] 注册成功", config.getName());
        } catch (Exception e) {
            log.error("数据源 [{}] 初始化失败: {}", config.getName(), e.getMessage());
            throw new RuntimeException("数据源初始化失败: " + config.getName(), e);
        }
    }

    /**
     * 注销数据源
     */
    public void unregisterDatasource(String name) {
        DruidDataSource ds = datasourceCache.remove(name);
        if (ds != null) {
            ds.close();
            log.info("数据源 [{}] 已注销", name);
        }
        configCache.remove(name);
    }

    /**
     * 获取数据源
     */
    public DataSource getDatasource(String name) {
        DataSource ds = datasourceCache.get(name);
        if (ds == null) {
            throw new RuntimeException("数据源不存在: " + name);
        }
        return ds;
    }

    /**
     * 获取数据源配置
     */
    public DatasourceConfig getDatasourceConfig(String name) {
        return configCache.get(name);
    }

    /**
     * 获取默认数据源名称
     */
    public String getDefaultDatasourceName() {
        return defaultDatasourceName;
    }

    /**
     * 获取默认数据源
     */
    public DataSource getDefaultDatasource() {
        if (defaultDatasourceName == null) {
            throw new RuntimeException("未配置默认数据源");
        }
        return getDatasource(defaultDatasourceName);
    }

    /**
     * 获取所有数据源名称
     */
    public java.util.List<String> getDatasourceNames() {
        return new java.util.ArrayList<>(datasourceCache.keySet());
    }

    /**
     * 数据源是否存在
     */
    public boolean containsDatasource(String name) {
        return datasourceCache.containsKey(name);
    }

    /**
     * 刷新所有数据源
     */
    public void refreshAll(java.util.List<DatasourceConfig> configs) {
        // 关闭现有数据源
        for (String name : new java.util.ArrayList<>(datasourceCache.keySet())) {
            unregisterDatasource(name);
        }
        defaultDatasourceName = null;
        // 重新初始化
        initDatasources(configs);
    }

    /**
     * 关闭所有数据源
     */
    public void closeAll() {
        for (DruidDataSource ds : datasourceCache.values()) {
            ds.close();
        }
        datasourceCache.clear();
        configCache.clear();
        defaultDatasourceName = null;
        log.info("所有数据源已关闭");
    }

    /**
     * 获取配置文件中的数据源列表
     */
    public java.util.List<DatasourceConfig> getFileConfigs() {
        return new java.util.ArrayList<>(fileConfigs);
    }
}
