package com.nocode.admin.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 代码生成器配置实体类
 */
@Data
@Entity
@Table(name = "nocode_code_generator_config")
public class CodeGeneratorConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 配置名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 表名 */
    @Column(name = "table_name", length = 100)
    private String tableName;

    /** 实体类名 */
    @Column(name = "entity_name", length = 100)
    private String entityName;

    /** 包名 */
    @Column(name = "package_name", length = 200)
    private String packageName;

    /** 模块名 */
    @Column(name = "module_name", length = 100)
    private String moduleName;

    /** 生成方式：SINGLE-单体，TEMPLATE-模板 */
    @Column(name = "generate_type", length = 20)
    private String generateType = "TEMPLATE";

    /** 模板配置JSON */
    @Column(name = "template_config", columnDefinition = "TEXT")
    private String templateConfig;

    /** 字段配置JSON */
    @Column(name = "field_config", columnDefinition = "TEXT")
    private String fieldConfig;

    /** 状态：ENABLED-启用，DISABLED-禁用 */
    @Column(name = "status", length = 20)
    private String status = "ENABLED";

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}