package com.nocode.starter;

import com.nocode.core.config.NocodeApiProperties;
import com.nocode.core.datasource.DatasourceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * NoCode API自动配置类
 */
@Configuration
@EnableConfigurationProperties(NocodeApiProperties.class)
@ConditionalOnProperty(prefix = "nocode.api", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.nocode.core")
public class NocodeApiConfiguration {

    @Autowired
    private NocodeApiProperties properties;

    /**
     * 数据源注册中心
     */
    @Bean
    @ConditionalOnMissingBean
    public DatasourceRegistry datasourceRegistry() {
        return new DatasourceRegistry(properties);
    }

    /**
     * CORS配置
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(),
                HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
