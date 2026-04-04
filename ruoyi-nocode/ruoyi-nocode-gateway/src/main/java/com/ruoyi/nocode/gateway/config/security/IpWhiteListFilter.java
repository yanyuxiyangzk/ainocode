package com.ruoyi.nocode.gateway.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * IP 白名单过滤器
 *
 * 基于 IpWhiteListConfig 配置对请求进行 IP 过滤
 *
 * @author ruoyi-nocode
 */
@Slf4j
@Component
public class IpWhiteListFilter implements GlobalFilter, Ordered {

    private static final String RESPONSE_BODY = "{\"code\":403,\"msg\":\"访问被拒绝，您的IP不在允许范围内\",\"data\":null}";

    private final IpWhiteListConfig ipWhiteListConfig;
    private final List<ViewResolver> viewResolvers;

    public IpWhiteListFilter(IpWhiteListConfig ipWhiteListConfig,
                            ObjectProvider<List<ViewResolver>> viewResolversProvider) {
        this.ipWhiteListConfig = ipWhiteListConfig;
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 如果路径在排除列表中，则直接放行
        if (ipWhiteListConfig.isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        // 获取客户端 IP 地址
        String clientIp = getClientIp(request);

        // 检查 IP 是否在白名单中
        if (!ipWhiteListConfig.isAllowed(clientIp)) {
            log.warn("IP {} attempted to access {} but is not in whitelist", clientIp, path);
            return forbiddenResponse(exchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -90;
    }

    /**
     * 获取客户端真实 IP 地址
     * 支持代理转发场景
     */
    private String getClientIp(ServerHttpRequest request) {
        // 优先从 X-Forwarded-For 头获取
        String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            // X-Forwarded-For 可能包含多个 IP，取第一个
            return forwardedFor.split(",")[0].trim();
        }

        // 其次从 X-Real-IP 头获取
        String realIp = request.getHeaders().getFirst("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp.trim();
        }

        // 最后使用远程地址
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }

    /**
     * 返回 403 Forbidden 响应
     */
    private Mono<Void> forbiddenResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(RESPONSE_BODY.getBytes())));
    }
}
