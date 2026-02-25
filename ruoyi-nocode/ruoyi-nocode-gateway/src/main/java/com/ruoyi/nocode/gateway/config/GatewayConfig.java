package com.ruoyi.nocode.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Gateway 动态配置类
 * 
 * 支持从 Nacos 配置中心动态刷新的配置 Bean
 * 
 * @author ruoyi-nocode
 */
@Configuration
public class GatewayConfig {

    /**
     * IP 限流 Key 解析器
     * 用于基于客户端 IP 的限流策略
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }

    /**
     * 用户 ID 限流 Key 解析器
     * 用于基于用户 ID 的限流策略
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }

    /**
     * API 路径限流 Key 解析器
     * 用于基于 API 路径的限流策略
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
}
