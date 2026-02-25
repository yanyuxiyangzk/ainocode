package com.nocode.starter;

import com.nocode.core.config.NocodeApiProperties;
import com.nocode.core.datasource.DatasourceRegistry;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用NoCode API注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({NocodeApiConfiguration.class})
public @interface EnableNocodeApi {
    /**
     * 是否自动启动管理界面
     */
    boolean enableAdmin() default true;
}
