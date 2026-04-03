package com.nocode.admin.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 表单配置实体类
 */
@Data
@Entity
@Table(name = "nocode_form_config")
public class FormConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 表单名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 表单描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 表单配置JSON */
    @Column(name = "form_config", columnDefinition = "TEXT")
    private String formConfig;

    /** 对应数据表名 */
    @Column(name = "table_name", length = 100)
    private String tableName;

    /** 表单状态：DRAFT-草稿，PUBLISHED-已发布 */
    @Column(name = "status", length = 20)
    private String status = "DRAFT";

    /** 版本号 */
    @Column(name = "version")
    private Integer version = 1;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /** 创建人 */
    @Column(name = "create_by", length = 64)
    private String createBy;

    /** 更新人 */
    @Column(name = "update_by", length = 64)
    private String updateBy;

    /** 删除标志（0-未删除，1-已删除） */
    @Column(name = "del_flag", length = 1)
    private String delFlag = "0";

    /** 备注 */
    @Column(name = "remark", length = 500)
    private String remark;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (delFlag == null) {
            delFlag = "0";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
