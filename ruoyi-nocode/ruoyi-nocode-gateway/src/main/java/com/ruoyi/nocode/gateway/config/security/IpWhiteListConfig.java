package com.ruoyi.nocode.gateway.config.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * IP 白名单配置
 *
 * 支持 IP 地址和 CIDR 网段配置
 *
 * @author ruoyi-nocode
 */
@Slf4j
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "gateway.security.ip-whitelist")
public class IpWhiteListConfig {

    /**
     * 是否启用 IP 白名单
     */
    private boolean enabled = false;

    /**
     * IP 白名单列表
     * 支持单个 IP 地址和 CIDR 网段格式
     * 例如: 192.168.1.1, 10.0.0.0/8, 172.16.0.0/16
     */
    private List<String> addresses = new ArrayList<>();

    /**
     * 默认放行的路径（这些路径不受 IP 白名单限制）
     */
    private List<String> excludedPaths = new ArrayList<>();

    /**
     * 编译后的 IP 模式列表
     */
    private transient List<IpPattern> compiledPatterns = new ArrayList<>();

    @PostConstruct
    public void init() {
        compilePatterns();
        log.info("IP WhiteList initialized with {} patterns", compiledPatterns.size());
    }

    /**
     * 编译 IP 地址和网段为可匹配的模式
     */
    private void compilePatterns() {
        compiledPatterns.clear();
        for (String address : addresses) {
            if (StringUtils.hasText(address)) {
                try {
                    compiledPatterns.add(new IpPattern(address.trim()));
                } catch (Exception e) {
                    log.warn("Invalid IP pattern: {}", address);
                }
            }
        }
    }

    /**
     * 检查指定 IP 是否在白名单中
     *
     * @param ipAddress IP 地址
     * @return true 表示在白名单中
     */
    public boolean isAllowed(String ipAddress) {
        // 如果未启用白名单，则允许所有 IP
        if (!enabled) {
            return true;
        }

        // 如果白名单为空，则允许所有 IP
        if (compiledPatterns.isEmpty()) {
            return true;
        }

        // 检查是否匹配白名单
        for (IpPattern pattern : compiledPatterns) {
            if (pattern.matches(ipAddress)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查指定路径是否在排除列表中
     *
     * @param path 请求路径
     * @return true 表示在排除列表中
     */
    public boolean isExcludedPath(String path) {
        if (excludedPaths.isEmpty()) {
            return false;
        }
        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    /**
     * IP 模式匹配类
     * 支持单个 IP 地址和 CIDR 网段格式
     */
    private static class IpPattern {
        private final Pattern pattern;
        private final String cidrMask;
        private final boolean isCidr;

        public IpPattern(String address) {
            if (address.contains("/")) {
                // CIDR 网段格式
                this.isCidr = true;
                this.cidrMask = address;
                this.pattern = null;
            } else {
                // 单个 IP 地址
                this.isCidr = false;
                this.cidrMask = null;
                this.pattern = Pattern.compile("^" + address.replace(".", "\\.") + "$");
            }
        }

        public boolean matches(String ipAddress) {
            if (isCidr) {
                return matchesCidr(ipAddress);
            }
            return pattern.matcher(ipAddress).matches();
        }

        private boolean matchesCidr(String ipAddress) {
            try {
                String[] parts = cidrMask.split("/");
                String networkAddress = parts[0];
                int prefixLength = Integer.parseInt(parts[1]);

                long networkLong = ipToLong(networkAddress);
                long ipLong = ipToLong(ipAddress);
                long mask = (-1L << (32 - prefixLength));

                return (networkLong & mask) == (ipLong & mask);
            } catch (Exception e) {
                return false;
            }
        }

        private long ipToLong(String ipAddress) {
            String[] octets = ipAddress.split("\\.");
            long result = 0;
            for (int i = 0; i < 4; i++) {
                result = (result << 8) | Integer.parseInt(octets[i]);
            }
            return result;
        }
    }
}
