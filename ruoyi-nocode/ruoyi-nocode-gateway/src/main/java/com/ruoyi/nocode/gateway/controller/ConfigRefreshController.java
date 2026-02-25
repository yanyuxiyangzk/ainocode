package com.ruoyi.nocode.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.nocode.gateway.config.DynamicRateLimitProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Nacos 配置刷新控制器
 * 
 * 提供配置刷新和状态查看接口
 * 
 * @author ruoyi-nocode
 */
@RestController
@RequestMapping("/config")
public class ConfigRefreshController {

    @Autowired
    private DynamicRateLimitProperties rateLimitProperties;

    @Autowired(required = false)
    private ContextRefresher contextRefresher;

    /**
     * 获取当前限流配置
     * 用于验证配置是否已从 Nacos 刷新
     */
    @GetMapping("/rate-limit")
    public Map<String, Object> getRateLimitConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", rateLimitProperties.isEnabled());
        result.put("requestsPerSecond", rateLimitProperties.getRequestsPerSecond());
        result.put("burstCapacity", rateLimitProperties.getBurstCapacity());
        result.put("whiteList", rateLimitProperties.getWhiteList());
        return result;
    }

    /**
     * 手动触发配置刷新
     * 用于测试动态配置刷新功能
     */
    @PostMapping("/refresh")
    public Map<String, Object> refreshConfig() {
        Map<String, Object> result = new HashMap<>();
        try {
            if (contextRefresher != null) {
                Set<String> refreshed = contextRefresher.refresh();
                result.put("success", true);
                result.put("refreshed", refreshed);
                result.put("message", "配置刷新成功");
            } else {
                result.put("success", false);
                result.put("message", "ContextRefresher 未注入，请检查配置");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "配置刷新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "ruoyi-nocode-gateway");
        return result;
    }
}
