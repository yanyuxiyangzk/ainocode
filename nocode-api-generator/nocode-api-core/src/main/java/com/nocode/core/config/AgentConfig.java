package com.nocode.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Agent 配置类
 */
@Configuration
public class AgentConfig {
    
    @Value("${nocode.agent.timeout:5000}")
    private int timeout;
    
    /**
     * RestTemplate 用于调用 LLM API
     * 仅在 Agent 启用时创建
     */
    @Bean
    @ConditionalOnProperty(name = "nocode.agent.enabled", havingValue = "true")
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }
}
