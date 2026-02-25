package com.ruoyi.nocode.gateway.filter;

import com.ruoyi.nocode.gateway.config.DynamicRateLimitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RequestLogFilter 单元测试
 * 
 * @author ruoyi-nocode
 */
@ExtendWith(MockitoExtension.class)
class RequestLogFilterTest {

    @Mock
    private GatewayFilterChain chain;

    private RequestLogFilter requestLogFilter;

    @BeforeEach
    void setUp() {
        requestLogFilter = new RequestLogFilter();
    }

    @Test
    @DisplayName("测试过滤器优先级")
    void testGetOrder() {
        assertEquals(-90, requestLogFilter.getOrder());
    }

    @Test
    @DisplayName("记录GET请求日志")
    void testLogGetRequest() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/system/user/list")
                .queryParam("name", "test")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();
        
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(requestLogFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("记录POST请求日志")
    void testLogPostRequest() {
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/auth/login")
                .body("{\"username\":\"admin\",\"password\":\"123456\"}");
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();
        
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(requestLogFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("记录响应时间")
    void testLogResponseTime() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/system/user/1")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();
        
        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(requestLogFilter.filter(exchange, chain))
                .verifyComplete();

        // 验证过滤器正常完成
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("异常请求处理")
    void testHandleException() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/error/path")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.builder(request).build();
        
        // 模拟链式调用抛出异常
        when(chain.filter(any())).thenReturn(Mono.error(new RuntimeException("Test exception")));

        // 过滤器应该记录日志，但异常会继续传播
        StepVerifier.create(requestLogFilter.filter(exchange, chain))
                .expectError(RuntimeException.class)
                .verify();
    }
}
