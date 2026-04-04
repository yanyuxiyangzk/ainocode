package com.ruoyi.nocode.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Gateway Rate Limit Configuration
 *
 * Supports dynamic configuration changes via Nacos
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "gateway.security.rate-limit")
public class RateLimitConfig {

    /**
     * Whether rate limiting is enabled
     */
    private boolean enabled = true;

    /**
     * Global rate limit settings
     */
    private RateLimitPolicy global = new RateLimitPolicy();

    /**
     * IP-based rate limit settings
     */
    private RateLimitPolicy ip = new RateLimitPolicy();

    /**
     * User-based rate limit settings
     */
    private RateLimitPolicy user = new RateLimitPolicy();

    @Data
    public static class RateLimitPolicy {
        /**
         * Enable this policy
         */
        private boolean enabled = true;

        /**
         * Requests per second
         */
        private int requestsPerSecond = 10;

        /**
         * Burst capacity
         */
        private int burstCapacity = 20;
    }
}
