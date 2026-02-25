package com.nocode.core.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源路由
 */
@Slf4j
@Component
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> currentDatasource = new ThreadLocal<>();
    private final DatasourceRegistry registry;

    public DynamicDataSource(DatasourceRegistry registry) {
        this.registry = registry;

        Map<Object, Object> targetDataSources = new java.util.HashMap<>();
        for (String name : registry.getDatasourceNames()) {
            targetDataSources.put(name, registry.getDatasource(name));
        }

        setDefaultTargetDataSource(registry.getDefaultDatasource());
        setTargetDataSources(targetDataSources);
        afterPropertiesSet();
    }

    /**
     * 设置当前数据源
     */
    public static void setCurrentDatasource(String name) {
        currentDatasource.set(name);
    }

    /**
     * 清除当前数据源
     */
    public static void clearCurrentDatasource() {
        currentDatasource.remove();
    }

    /**
     * 获取当前数据源
     */
    public static String getCurrentDatasourceName() {
        return currentDatasource.get();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String name = currentDatasource.get();
        if (name == null) {
            name = registry.getDefaultDatasourceName();
        }

        if (!registry.containsDatasource(name)) {
            log.warn("数据源 [{}] 不存在，使用默认数据源", name);
            name = registry.getDefaultDatasourceName();
        }

        return name;
    }

    /**
     * 刷新数据源
     */
    public void refreshDatasources() {
        Map<Object, Object> targetDataSources = new java.util.HashMap<>();
        for (String name : registry.getDatasourceNames()) {
            targetDataSources.put(name, registry.getDatasource(name));
        }
        setTargetDataSources(targetDataSources);
        afterPropertiesSet();
        log.info("动态数据源已刷新，共 {} 个数据源", targetDataSources.size());
    }
}
