package com.nocode.admin;

import com.nocode.starter.EnableNocodeApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * NoCode API Admin 启动类
 * 注意：使用 scanBasePackages 替代 @ComponentScan，避免覆盖默认扫描
 */
@SpringBootApplication(scanBasePackages = {"com.nocode.admin", "com.nocode.admin.controller", "com.nocode.core", "com.nocode.starter"})
@EnableNocodeApi(enableAdmin = true)
@RestController
public class NocodeApiAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NocodeApiAdminApplication.class, args);
    }

    /**
     * 首页 - 重定向到静态页面
     */
    @GetMapping("/")
    public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/index.html");
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    /**
     * 测试端点 - 验证 POST 是否正常工作
     */
    @PostMapping("/api/test-post")
    public Map<String, Object> testPost(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "POST 请求成功");
        result.put("receivedData", data);
        return result;
    }

    /**
     * 测试端点 - 验证 GET 是否正常工作
     */
    @GetMapping("/api/test-get")
    public Map<String, Object> testGet() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "GET 请求成功");
        return result;
    }
}
