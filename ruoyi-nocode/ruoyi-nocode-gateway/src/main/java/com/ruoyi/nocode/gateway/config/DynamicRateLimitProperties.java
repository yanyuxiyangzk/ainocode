package com.ruoyi.nocode.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态刷新配置属性类
 * 
 * 使用 @RefreshScope 注解支持 Nacos 配置动态刷新
 * 当 Nacos 配置发生变化时，此配置会自动更新
 * 
 * @author ruoyi-nocode
 */
@Component
@RefreshScope
@ConfigurationProperties(prefix = "gateway.rate-limit")
public class DynamicRateLimitProperties {

    /**
     * 是否启用限流
     */
    private boolean enabled = true;

    /**
     * 每秒请求数
     */
    private int requestsPerSecond = 100;

    /**
     * 令牌桶容量
     */
    private int burstCapacity = 200;

    /**
     * 白名单 IP 列表
     */
    private List<String> whiteList = new ArrayList<>();

    // Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public void setRequestsPerSecond(int requestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(int burstCapacity) {
        this.burstCapacity = burstCapacity;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }
}
