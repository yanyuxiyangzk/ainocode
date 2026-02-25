package com.nocode.core.config;

import com.nocode.core.entity.DatasourceConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * NoCode API配置属性
 */
@Data
@ConfigurationProperties(prefix = "nocode.api")
public class NocodeApiProperties {
    /** 是否启用 */
    private boolean enabled = true;
    /** 是否启用管理界面 */
    private boolean adminEnabled = true;
    /** 管理界面端口 */
    private int adminPort = 8081;
    /** API前缀 */
    private String apiPrefix = "/api";
    /** 管理界面路径 */
    private String adminPath = "/nocode-admin";
    /** 数据源配置列表 */
    private List<DatasourceConfig> datasources = new ArrayList<>();
    /** 默认每页大小 */
    private int defaultPageSize = 10;
    /** 最大每页大小 */
    private int maxPageSize = 1000;
    /** 是否允许跨域 */
    private boolean corsEnabled = true;
}
