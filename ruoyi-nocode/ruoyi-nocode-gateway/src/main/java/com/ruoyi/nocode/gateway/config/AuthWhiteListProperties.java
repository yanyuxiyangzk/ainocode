package com.ruoyi.nocode.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证白名单配置
 * 
 * 支持从 Nacos 动态刷新
 * 
 * @author ruoyi-nocode
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "gateway.auth.whitelist")
public class AuthWhiteListProperties {

    /**
     * 白名单路径列表
     * 支持前缀匹配和正则表达式
     */
    private List<String> paths = new ArrayList<>();

    /**
     * 是否启用认证
     */
    private boolean enabled = true;
}
