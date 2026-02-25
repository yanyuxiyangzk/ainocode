package com.ruoyi.nocode.gateway.filter;

import com.ruoyi.nocode.gateway.config.AuthWhiteListProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * AuthGlobalFilter 单元测试
 * 
 * @author ruoyi-nocode
 */
@ExtendWith(MockitoExtension.class)
class AuthGlobalFilterTest {

    @Mock
    private AuthWhiteListProperties authWhiteListProperties;

    @Mock
    private GatewayFilterChain chain;

    private AuthGlobalFilter authGlobalFilter;

    @BeforeEach
    void setUp() {
        // 配置白名单 - 使用 lenient 避免不必要的 stubbing 警告
        lenient().when(authWhiteListProperties.getPaths())
                .thenReturn(Arrays.asList("/auth/login", "/auth/captcha", "/actuator/**"));
        lenient().when(authWhiteListProperties.isEnabled())
                .thenReturn(true);
        
        authGlobalFilter = new AuthGlobalFilter(authWhiteListProperties);
    }

    @Test
    @DisplayName("测试过滤器优先级")
    void testGetOrder() {
        assertEquals(-100, authGlobalFilter.getOrder());
    }

    @Test
    @DisplayName("白名单路径直接放行")
    void testWhiteListPathShouldPass() {
        // 准备白名单请求
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/auth/login")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();
        
        when(chain.filter(any())).thenReturn(Mono.empty());

        // 执行过滤
        StepVerifier.create(authGlobalFilter.filter(exchange, chain))
                .verifyComplete();

        // 验证调用了 chain.filter
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("无Token返回401")
    void testNoTokenShouldReturn401() {
        // 准备需要认证的请求（无Token）
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/system/user/list")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        // 执行过滤
        StepVerifier.create(authGlobalFilter.filter(exchange, chain))
                .verifyComplete();

        // 验证响应状态为401
        ServerHttpResponse response = exchange.getResponse();
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        // 验证未调用 chain.filter
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("无效Token返回401")
    void testInvalidTokenShouldReturn401() {
        // 准备带Token的请求（Token无效，因为没有Sa-Token环境）
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/system/user/list")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token-123")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        // 执行过滤 - 由于没有Sa-Token环境，Token验证会失败
        StepVerifier.create(authGlobalFilter.filter(exchange, chain))
                .verifyComplete();

        // 验证响应状态为401（Token验证失败）
        ServerHttpResponse response = exchange.getResponse();
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        // 验证未调用 chain.filter
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("Actuator端点放行")
    void testActuatorEndpointShouldPass() {
        // 准备 Actuator 请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/actuator/health")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();
        
        when(chain.filter(any())).thenReturn(Mono.empty());

        // 执行过滤
        StepVerifier.create(authGlobalFilter.filter(exchange, chain))
                .verifyComplete();

        // 验证调用了 chain.filter
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("验证码接口放行")
    void testCaptchaEndpointShouldPass() {
        // 准备验证码请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/auth/captcha")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();
        
        when(chain.filter(any())).thenReturn(Mono.empty());

        // 执行过滤
        StepVerifier.create(authGlobalFilter.filter(exchange, chain))
                .verifyComplete();

        // 验证调用了 chain.filter
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("空Token返回401")
    void testEmptyTokenShouldReturn401() {
        // 准备带空Token的请求
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/system/user/list")
                .header(HttpHeaders.AUTHORIZATION, "")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();

        // 执行过滤
        StepVerifier.create(authGlobalFilter.filter(exchange, chain))
                .verifyComplete();

        // 验证响应状态为401
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
}
