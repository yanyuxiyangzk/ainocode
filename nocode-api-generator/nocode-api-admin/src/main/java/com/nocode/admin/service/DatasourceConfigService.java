package com.nocode.admin.service;

import com.nocode.admin.entity.DatasourceConfigEntity;
import com.nocode.admin.repository.DatasourceConfigRepository;
import com.nocode.core.entity.DatabaseType;
import com.nocode.core.entity.DatasourceConfig;
import com.nocode.core.datasource.DatasourceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据源配置服务
 */
@Service
public class DatasourceConfigService {
    private static final Logger log = LoggerFactory.getLogger(DatasourceConfigService.class);

    private final DatasourceConfigRepository repository;
    private final DatasourceRegistry datasourceRegistry;

    public DatasourceConfigService(DatasourceConfigRepository repository, DatasourceRegistry datasourceRegistry) {
        this.repository = repository;
        this.datasourceRegistry = datasourceRegistry;
    }

    /**
     * 保存数据源配置
     */
    @Transactional
    public DatasourceConfigEntity save(DatasourceConfig config) {
        DatasourceConfigEntity entity = new DatasourceConfigEntity();
        entity.setName(config.getName());
        entity.setJdbcUrl(config.getJdbcUrl());
        entity.setUsername(config.getUsername());
        // 密码简单Base64加密
        if (config.getPassword() != null) {
            entity.setPassword(Base64.getEncoder().encodeToString(config.getPassword().getBytes()));
        }
        entity.setDriverClassName(config.getDriverClassName());
        entity.setDatabaseType(config.getDatabaseType() != null ? config.getDatabaseType().name() : null);
        entity.setInitialSize(config.getInitialSize());
        entity.setMinIdle(config.getMinIdle());
        entity.setMaxActive(config.getMaxActive());
        entity.setMaxWait(config.getMaxWait());
        entity.setEnabled(config.isEnabled());
        return repository.save(entity);
    }

    /**
     * 根据名称删除数据源配置
     */
    @Transactional
    public void deleteByName(String name) {
        repository.deleteByName(name);
    }

    /**
     * 获取所有启用的数据源配置列表
     */
    public List<DatasourceConfigEntity> findAllEnabled() {
        return repository.findByEnabledTrue();
    }

    /**
     * 根据名称查找
     */
    public DatasourceConfigEntity findByName(String name) {
        return repository.findByName(name).orElse(null);
    }

    /**
     * 检查名称是否存在
     */
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    /**
     * 实体转换为DatasourceConfig
     */
    public DatasourceConfig toDatasourceConfig(DatasourceConfigEntity entity) {
        DatasourceConfig config = new DatasourceConfig();
        config.setName(entity.getName());
        config.setJdbcUrl(entity.getJdbcUrl());
        config.setUsername(entity.getUsername());
        // 密码解密
        if (entity.getPassword() != null) {
            config.setPassword(new String(Base64.getDecoder().decode(entity.getPassword())));
        }
        config.setDriverClassName(entity.getDriverClassName());
        if (entity.getDatabaseType() != null) {
            config.setDatabaseType(DatabaseType.valueOf(entity.getDatabaseType()));
        }
        config.setInitialSize(entity.getInitialSize() != null ? entity.getInitialSize() : 5);
        config.setMinIdle(entity.getMinIdle() != null ? entity.getMinIdle() : 5);
        config.setMaxActive(entity.getMaxActive() != null ? entity.getMaxActive() : 20);
        config.setMaxWait(entity.getMaxWait() != null ? entity.getMaxWait() : 60000L);
        config.setEnabled(entity.getEnabled() != null ? entity.getEnabled() : true);
        return config;
    }

    /**
     * DatasourceConfig转换为实体
     */
    public DatasourceConfigEntity toEntity(DatasourceConfig config) {
        DatasourceConfigEntity entity = new DatasourceConfigEntity();
        entity.setName(config.getName());
        entity.setJdbcUrl(config.getJdbcUrl());
        entity.setUsername(config.getUsername());
        if (config.getPassword() != null) {
            entity.setPassword(Base64.getEncoder().encodeToString(config.getPassword().getBytes()));
        }
        entity.setDriverClassName(config.getDriverClassName());
        if (config.getDatabaseType() != null) {
            entity.setDatabaseType(config.getDatabaseType().name());
        }
        entity.setInitialSize(config.getInitialSize());
        entity.setMinIdle(config.getMinIdle());
        entity.setMaxActive(config.getMaxActive());
        entity.setMaxWait(config.getMaxWait());
        entity.setEnabled(config.isEnabled());
        return entity;
    }

    /**
     * 获取所有启用的DatasourceConfig列表
     */
    public List<DatasourceConfig> findAllEnabledConfigs() {
        return findAllEnabled().stream()
                .map(this::toDatasourceConfig)
                .collect(Collectors.toList());
    }

    /**
     * 测试数据源连接
     *
     * @param config 数据源配置
     * @return 测试结果
     */
    public Map<String, Object> testConnection(DatasourceConfig config) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", config.getName());
        result.put("success", false);
        result.put("message", "");

        Connection conn = null;
        long startTime = System.currentTimeMillis();

        try {
            Class.forName(config.getDriverClassName());
            conn = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());

            long elapsedTime = System.currentTimeMillis() - startTime;
            result.put("success", true);
            result.put("message", "连接成功");
            result.put("elapsedTime", elapsedTime);
            result.put("catalog", conn.getCatalog());
            result.put("databaseProductName", conn.getMetaData().getDatabaseProductName());
            result.put("databaseProductVersion", conn.getMetaData().getDatabaseProductVersion());

            log.info("数据源连接测试成功: name={}, elapsedTime={}ms", config.getName(), elapsedTime);
        } catch (ClassNotFoundException e) {
            result.put("message", "驱动类未找到: " + config.getDriverClassName());
            log.error("数据源连接测试失败: name={}, error={}", config.getName(), e.getMessage());
        } catch (SQLException e) {
            result.put("message", "SQL异常: " + e.getMessage());
            result.put("errorCode", e.getErrorCode());
            result.put("sqlState", e.getSQLState());
            log.error("数据源连接测试失败: name={}, error={}", config.getName(), e.getMessage());
        } catch (Exception e) {
            result.put("message", "连接失败: " + e.getMessage());
            log.error("数据源连接测试失败: name={}, error={}", config.getName(), e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.warn("关闭连接失败", e);
                }
            }
        }

        return result;
    }

    /**
     * 测试数据源连接（通过实体）
     *
     * @param entity 数据源配置实体
     * @return 测试结果
     */
    public Map<String, Object> testConnection(DatasourceConfigEntity entity) {
        return testConnection(toDatasourceConfig(entity));
    }

    /**
     * 测试数据源连接（通过名称）
     *
     * @param name 数据源名称
     * @return 测试结果
     */
    public Map<String, Object> testConnectionByName(String name) {
        DatasourceConfigEntity entity = findByName(name);
        if (entity == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("name", name);
            result.put("success", false);
            result.put("message", "数据源不存在: " + name);
            return result;
        }
        return testConnection(entity);
    }

    /**
     * 注册并测试数据源
     *
     * @param config 数据源配置
     * @return 测试结果
     */
    public Map<String, Object> registerAndTestConnection(DatasourceConfig config) {
        Map<String, Object> testResult = testConnection(config);

        if ((Boolean) testResult.get("success")) {
            datasourceRegistry.registerDatasource(config);
            log.info("数据源注册成功: name={}", config.getName());
        }

        return testResult;
    }

    /**
     * 刷新数据源
     */
    public void refreshDatasources() {
        datasourceRegistry.refreshAll(findAllEnabledConfigs());
        log.info("数据源已刷新");
    }
}
