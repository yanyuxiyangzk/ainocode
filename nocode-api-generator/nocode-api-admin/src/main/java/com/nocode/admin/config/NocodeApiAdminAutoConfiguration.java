package com.nocode.admin.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * NoCode API Admin 自动配置类
 * 
 * 当引入 nocode-api-admin 依赖并配置 nocode.api.admin.enabled=true 时自动生效
 * 
 * =============================================================================
 * RuoYi 微服务集成指南
 * =============================================================================
 * 
 * 1. 添加 Maven 依赖：
 *    <dependency>
 *        <groupId>com.nocode</groupId>
 *        <artifactId>nocode-api-admin</artifactId>
 *        <version>1.0.0</version>
 *    </dependency>
 * 
 * 2. 在 Nacos 配置中心添加配置（Data ID: ruoyi-system.yml）：
 *    nocode:
 *      api:
 *        enabled: true
 *        admin:
 *          enabled: true
 *          # 前端管理界面路径前缀，访问地址：http://host:port/nocode-admin/index.html
 *          context-path: /nocode-admin
 *        datasources:
 *          - name: business
 *            jdbc-url: jdbc:postgresql://127.0.0.1:15432/cost
 *            username: ods
 *            password: Test@123
 *            driver-class-name: org.postgresql.Driver
 * 
 * 3. 启动类添加注解：
 *    @SpringBootApplication
 *    @EnableNocodeApi(enableAdmin = true)
 *    @EnableDiscoveryClient
 *    public class RuoYiSystemApplication {
 *        public static void main(String[] args) {
 *            SpringApplication.run(RuoYiSystemApplication.class, args);
 *        }
 *    }
 * 
 * 4. 访问管理界面：
 *    http://localhost:9201/nocode-admin/index.html
 *    （假设服务端口为 9201）
 * 
 * =============================================================================
 * 前端资源配置说明
 * =============================================================================
 * 
 * 前端资源已打包在 jar 的 static/ 目录下，无需额外配置。
 * 
 * 访问路径：
 * - 管理界面：/nocode-admin/index.html
 * - API 接口：/api/admin/*
 * - 动态 API：/api/datasource/{datasource}/{table}
 * 
 * =============================================================================
 */
@Configuration
@ComponentScan(basePackages = {
    "com.nocode.admin.controller",
    "com.nocode.admin.service",
    "com.nocode.admin.entity",
    "com.nocode.admin.repository",
    "com.nocode.admin.config",
    "com.nocode.core",
    "com.nocode.starter"
})
@EnableJpaRepositories(basePackages = "com.nocode.admin.repository")
@EntityScan(basePackages = "com.nocode.admin.entity")
@ConditionalOnProperty(name = "nocode.api.admin.enabled", havingValue = "true", matchIfMissing = false)
public class NocodeApiAdminAutoConfiguration {
    // 通过 @ComponentScan 自动扫描并注册所有组件
    // 前端资源在 jar 包的 static/ 目录下，Spring Boot 自动提供静态资源服务
}
