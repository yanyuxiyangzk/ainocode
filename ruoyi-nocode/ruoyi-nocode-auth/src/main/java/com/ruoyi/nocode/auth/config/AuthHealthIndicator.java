package com.ruoyi.nocode.auth.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Auth 服务健康检查指示器
 * 
 * 提供认证服务特有的健康状态检查
 * 
 * @author ruoyi-nocode
 */
@Component
@ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
public class AuthHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        int errorCode = checkAuthStatus();
        if (errorCode != 0) {
            return Health.down()
                    .withDetail("error-code", errorCode)
                    .withDetail("message", "Auth service health check failed")
                    .build();
        }
        return Health.up()
                .withDetail("status", "healthy")
                .withDetail("service", "ruoyi-nocode-auth")
                .build();
    }

    /**
     * 检查认证服务状态
     * 
     * @return 0 表示正常，非 0 表示异常
     */
    private int checkAuthStatus() {
        // 可以添加：数据库连接检查、Redis 连接检查、Token 服务检查等
        return 0;
    }
}
