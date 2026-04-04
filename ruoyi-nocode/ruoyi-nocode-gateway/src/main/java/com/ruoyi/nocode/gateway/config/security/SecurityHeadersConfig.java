package com.ruoyi.nocode.gateway.config.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 安全响应头配置
 *
 * 配置 HTTP 安全响应头，防止常见 Web 安全漏洞
 *
 * @author ruoyi-nocode
 */
@Component
public class SecurityHeadersConfig implements GlobalFilter, Ordered {

    /**
     * Content-Security-Policy 响应头
     * 防止 XSS 和数据注入攻击
     */
    private static final String CONTENT_SECURITY_POLICY =
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: blob:; " +
            "font-src 'self' data:; " +
            "connect-src 'self' ws: wss:; " +
            "frame-ancestors 'self';";

    /**
     * X-Content-Type-Options 响应头
     * 防止 MIME 类型嗅探
     */
    private static final String X_CONTENT_TYPE_OPTIONS = "nosniff";

    /**
     * X-Frame-Options 响应头
     * 防止点击劫持攻击
     */
    private static final String X_FRAME_OPTIONS = "DENY";

    /**
     * X-XSS-Protection 响应头
     * 启用浏览器 XSS 防护
     */
    private static final String X_XSS_PROTECTION = "1; mode=block";

    /**
     * Strict-Transport-Security 响应头
     * 强制使用 HTTPS
     */
    private static final String STRICT_TRANSPORT_SECURITY =
            "max-age=31536000; includeSubDomains; preload";

    /**
     * Referrer-Policy 响应头
     * 控制引用来源信息
     */
    private static final String REFERRER_POLICY = "strict-origin-when-cross-origin";

    /**
     * Permissions-Policy 响应头
     * 控制浏览器功能权限
     */
    private static final String PERMISSIONS_POLICY =
            "accelerometer=(), camera=(), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), payment=(), usb=()";

    /**
     * 不需要添加安全头的路径列表
     */
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/actuator/health",
            "/actuator/prometheus",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 对排除路径直接放行
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        // 添加安全响应头
        headers.add("Content-Security-Policy", CONTENT_SECURITY_POLICY);
        headers.add("X-Content-Type-Options", X_CONTENT_TYPE_OPTIONS);
        headers.add("X-Frame-Options", X_FRAME_OPTIONS);
        headers.add("X-XSS-Protection", X_XSS_PROTECTION);
        headers.add("Strict-Transport-Security", STRICT_TRANSPORT_SECURITY);
        headers.add("Referrer-Policy", REFERRER_POLICY);
        headers.add("Permissions-Policy", PERMISSIONS_POLICY);
        headers.add("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * 检查路径是否在排除列表中
     */
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
