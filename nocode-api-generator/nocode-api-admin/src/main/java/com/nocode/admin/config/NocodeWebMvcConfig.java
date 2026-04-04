package com.nocode.admin.config;

import com.nocode.core.config.NocodeApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * NoCode API Admin WebMvc 配置
 * 
 * 处理前端静态资源和路径映射
 * 
 * 集成到其他项目时，前端访问路径：
 * - 管理界面：http://host:port/{adminPath}/index.html
 * - 默认路径：http://host:port/nocode-admin/index.html
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "nocode.api.admin.enabled", havingValue = "true", matchIfMissing = false)
public class NocodeWebMvcConfig implements WebMvcConfigurer {

    private final NocodeApiProperties properties;

    /**
     * 配置静态资源映射
     * 
     * 将 /nocode-admin/** 映射到 classpath:/static/ 目录
     * 这样前端资源可以正确访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String adminPath = properties.getAdminPath();
        
        // 确保路径格式正确
        String pathPattern = adminPath.endsWith("/") ? adminPath + "**" : adminPath + "/**";
        
        // 映射前端静态资源
        registry.addResourceHandler(pathPattern)
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
        
        // 同时支持根路径访问
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }

    /**
     * 配置视图控制器
     * 
     * 访问管理界面根路径时重定向到 index.html
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String adminPath = properties.getAdminPath();
        
        // 访问 /nocode-admin 重定向到 /nocode-admin/index.html
        registry.addRedirectViewController(adminPath, adminPath + "/index.html");
    }
}
