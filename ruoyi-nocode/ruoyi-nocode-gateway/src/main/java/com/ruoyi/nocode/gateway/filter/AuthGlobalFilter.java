package com.ruoyi.nocode.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.nocode.gateway.config.AuthWhiteListProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局认证过滤器
 * 
 * 基于 Sa-Token 实现统一认证鉴权
 * - 白名单路径放行
 * - Token 验证
 * - 用户信息传递
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthWhiteListProperties authWhiteListProperties;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        log.debug("请求路径: {}, 方法: {}", path, method);

        // 白名单放行
        if (isWhitePath(path)) {
            log.debug("白名单路径放行: {}", path);
            return chain.filter(exchange);
        }

        // 获取 Token
        String token = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (StrUtil.isEmpty(token)) {
            log.warn("请求未携带 Token, 路径: {}", path);
            return unauthorized(exchange, "未登录或登录已过期");
        }

        // 去除 Bearer 前缀
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }

        try {
            // Sa-Token 验证
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                log.warn("Token 无效, 路径: {}", path);
                return unauthorized(exchange, "Token 无效");
            }

            // 传递用户信息到下游服务
            ServerHttpRequest newRequest = request.mutate()
                    .header(USER_ID_HEADER, loginId.toString())
                    .header(USER_NAME_HEADER, getUserName(loginId.toString()))
                    .build();

            log.debug("认证成功, 用户ID: {}, 路径: {}", loginId, path);
            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (Exception e) {
            log.error("Token 验证异常: {}, 路径: {}", e.getMessage(), path);
            return unauthorized(exchange, "Token 验证失败: " + e.getMessage());
        }
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhitePath(String path) {
        if (authWhiteListProperties == null || authWhiteListProperties.getPaths() == null) {
            // 默认白名单
            return path.startsWith("/actuator/") 
                    || path.startsWith("/auth/login")
                    || path.startsWith("/auth/register")
                    || path.startsWith("/auth/captcha")
                    || path.startsWith("/swagger-ui")
                    || path.startsWith("/v3/api-docs");
        }
        
        return authWhiteListProperties.getPaths().stream()
                .anyMatch(whitePath -> {
                    // 支持通配符匹配: /actuator/** 匹配 /actuator/ 下所有路径
                    if (whitePath.endsWith("/**")) {
                        String prefix = whitePath.substring(0, whitePath.length() - 3);
                        return path.startsWith(prefix);
                    }
                    // 支持前缀匹配
                    if (whitePath.endsWith("/*")) {
                        String prefix = whitePath.substring(0, whitePath.length() - 2);
                        return path.startsWith(prefix) && path.indexOf('/', prefix.length()) == -1;
                    }
                    // 精确匹配
                    return path.equals(whitePath);
                });
    }

    /**
     * 获取用户名
     */
    private String getUserName(String userId) {
        try {
            // 从 Sa-Token Session 获取用户名
            return StpUtil.getSession().getString("userName");
        } catch (Exception e) {
            return userId;
        }
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"code\":401,\"msg\":\"%s\",\"data\":null}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 优先级最高
        return -100;
    }
}
