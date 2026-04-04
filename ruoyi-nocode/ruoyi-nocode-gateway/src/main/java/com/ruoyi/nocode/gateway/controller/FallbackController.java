package com.ruoyi.nocode.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Gateway Fallback Controller
 *
 * Handles circuit breaker fallback requests
 */
@Tag(name = "Fallback API", description = "网关熔断降级接口")
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @GetMapping("/auth")
    @Operation(summary = "Auth service fallback", description = "认证服务降级处理")
    public ResponseEntity<Map<String, Object>> authFallback() {
        log.warn("Auth service circuit breaker triggered");
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("msg", "认证服务暂不可用，请稍后再试");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/system")
    @Operation(summary = "System service fallback", description = "系统服务降级处理")
    public ResponseEntity<Map<String, Object>> systemFallback() {
        log.warn("System service circuit breaker triggered");
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("msg", "系统服务暂不可用，请稍后再试");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/gen")
    @Operation(summary = "Code generation service fallback", description = "代码生成服务降级处理")
    public ResponseEntity<Map<String, Object>> genFallback() {
        log.warn("Code generation service circuit breaker triggered");
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("msg", "代码生成服务暂不可用，请稍后再试");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/global")
    @Operation(summary = "Global fallback", description = "全局降级处理")
    public ResponseEntity<Map<String, Object>> globalFallback() {
        log.error("Global fallback triggered - service unavailable");
        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("msg", "服务暂不可用，请稍后再试");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
