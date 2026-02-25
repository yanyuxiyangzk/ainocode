package com.ruoyi.nocode.gateway.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Gateway 自定义健康检查指示器
 * 
 * 提供 Gateway 特有的健康状态检查
 * 
 * @author ruoyi-nocode
 */
@Component
@ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
public class GatewayHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // 检查 Gateway 核心组件状态
        int errorCode = checkGatewayStatus();
        if (errorCode != 0) {
            return Health.down()
                    .withDetail("error-code", errorCode)
                    .withDetail("message", "Gateway health check failed")
                    .build();
        }
        return Health.up()
                .withDetail("status", "healthy")
                .withDetail("service", "ruoyi-nocode-gateway")
                .build();
    }

    /**
     * 检查 Gateway 状态
     * 
     * @return 0 表示正常，非 0 表示异常
     */
    private int checkGatewayStatus() {
        // 这里可以添加更多健康检查逻辑
        // 例如：检查路由配置、检查 Redis 连接等
        return 0;
    }
}
