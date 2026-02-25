package com.nocode.admin.config;

import com.nocode.admin.service.DatasourceConfigService;
import com.nocode.core.datasource.DatasourceRegistry;
import com.nocode.core.entity.DatasourceConfig;
import com.nocode.core.metadata.MetadataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据源初始化加载器
 * 在应用启动时从数据库加载数据源配置
 */
@Slf4j
@Component
public class DatasourceInitializer implements CommandLineRunner {
    private final DatasourceConfigService configService;
    private final DatasourceRegistry registry;
    private final MetadataCache metadataCache;

    public DatasourceInitializer(DatasourceConfigService configService,
                                  DatasourceRegistry registry,
                                  MetadataCache metadataCache) {
        this.configService = configService;
        this.registry = registry;
        this.metadataCache = metadataCache;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("========================================");
        log.info("开始加载数据源配置...");
        log.info("========================================");

        try {
            // 1. 首先从数据库加载
            List<DatasourceConfig> configs = configService.findAllEnabledConfigs();
            log.info("从数据库获取到 {} 个数据源配置", configs != null ? configs.size() : 0);

            if (configs == null || configs.isEmpty()) {
                log.info("数据库中没有数据源配置，使用配置文件中的配置");
                configs = registry.getFileConfigs();
            }

            if (configs == null || configs.isEmpty()) {
                log.warn("未找到任何数据源配置");
                return;
            }

            int successCount = 0;
            int skipCount = 0;
            int failCount = 0;

            for (DatasourceConfig config : configs) {
                if (!config.isEnabled()) {
                    log.info("数据源 [{}] 已禁用，跳过", config.getName());
                    skipCount++;
                    continue;
                }

                try {
                    // 检查是否已经在内存中（配置文件已加载）
                    if (registry.containsDatasource(config.getName())) {
                        log.info("数据源 [{}] 已存在（来自配置文件），跳过", config.getName());
                        skipCount++;
                        continue;
                    }

                    log.info("加载数据源: {}", config.getName());
                    log.info("  JDBC URL: {}", config.getJdbcUrl());

                    registry.registerDatasource(config);
                    log.info("  注册成功");

                    metadataCache.refreshDatasource(config, registry.getDatasource(config.getName()));
                    log.info("  缓存刷新成功");

                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.error("数据源 [{}] 加载失败: {}", config.getName(), e.getMessage(), e);
                }
            }

            log.info("========================================");
            log.info("数据源加载完成: 成功 {}, 跳过 {}, 失败 {}", successCount, skipCount, failCount);
            log.info("当前已加载的数据源: {}", registry.getDatasourceNames());
            log.info("========================================");

        } catch (Exception e) {
            log.error("从数据库加载数据源失败: {}", e.getMessage(), e);
            log.info("将使用配置文件中的数据源配置...");
        }
    }
}
