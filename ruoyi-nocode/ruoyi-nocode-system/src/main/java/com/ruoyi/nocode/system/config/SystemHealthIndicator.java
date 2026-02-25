package com.ruoyi.nocode.system.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * System 服务健康检查指示器
 * 
 * 提供系统服务特有的健康状态检查
 * 
 * @author ruoyi-nocode
 */
@Component
@ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
public class SystemHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        int errorCode = checkSystemStatus();
        if (errorCode != 0) {
            return Health.down()
                    .withDetail("error-code", errorCode)
                    .withDetail("message", "System service health check failed")
                    .build();
        }
        return Health.up()
                .withDetail("status", "healthy")
                .withDetail("service", "ruoyi-nocode-system")
                .build();
    }

    /**
     * 检查系统服务状态
     * 
     * @return 0 表示正常，非 0 表示异常
     */
    private int checkSystemStatus() {
        // 可以添加：数据库连接检查、Redis 连接检查、插件系统检查等
        return 0;
    }
}
