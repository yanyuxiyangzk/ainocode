package com.ruoyi.nocode.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 请求日志过滤器
 * 
 * 记录请求和响应信息
 * - 请求路径、方法、参数
 * - 响应状态码、耗时
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    private static final String START_TIME = "startTime";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String requestId = generateRequestId();
        String clientIp = getClientIp(request);

        // 记录请求开始时间
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());

        log.info("[{}] >>> 请求开始 - 时间: {}, IP: {}, 方法: {}, 路径: {}, 查询参数: {}",
                requestId,
                LocalDateTime.now().format(FORMATTER),
                clientIp,
                method,
                path,
                request.getURI().getQuery()
        );

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                int statusCode = exchange.getResponse().getStatusCode() != null 
                        ? exchange.getResponse().getStatusCode().value() 
                        : 0;

                log.info("[{}] <<< 请求结束 - 状态码: {}, 耗时: {}ms, 路径: {}",
                        requestId,
                        statusCode,
                        duration,
                        path
                );
            }
        }));
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress() != null 
                    ? request.getRemoteAddress().getAddress().getHostAddress() 
                    : "unknown";
        }
        // 多级代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 生成请求 ID
     */
    private String generateRequestId() {
        return String.format("%08x", System.nanoTime() & 0xFFFFFFFF);
    }

    @Override
    public int getOrder() {
        // 在认证过滤器之后执行
        return -90;
    }
}
