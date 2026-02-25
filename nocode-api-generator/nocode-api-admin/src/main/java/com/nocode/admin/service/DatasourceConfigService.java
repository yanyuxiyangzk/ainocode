package com.nocode.admin.service;

import com.nocode.admin.entity.DatasourceConfigEntity;
import com.nocode.admin.repository.DatasourceConfigRepository;
import com.nocode.core.entity.DatabaseType;
import com.nocode.core.entity.DatasourceConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据源配置服务
 */
@Service
public class DatasourceConfigService {
    private final DatasourceConfigRepository repository;

    public DatasourceConfigService(DatasourceConfigRepository repository) {
        this.repository = repository;
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
}
