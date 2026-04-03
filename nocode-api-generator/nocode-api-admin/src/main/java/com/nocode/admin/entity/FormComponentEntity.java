package com.nocode.admin.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 表单组件实体类
 */
@Data
@Entity
@Table(name = "nocode_form_component")
public class FormComponentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 组件类型 */
    @Column(name = "component_type", nullable = false, length = 50)
    private String componentType;

    /** 组件标签 */
    @Column(name = "label", nullable = false, length = 100)
    private String label;

    /** 字段名 */
    @Column(name = "field_name", length = 100)
    private String fieldName;

    /** 占位符 */
    @Column(name = "placeholder", length = 200)
    private String placeholder;

    /** 默认值 */
    @Column(name = "default_value", length = 500)
    private String defaultValue;

    /** 是否必填 */
    @Column(name = "required")
    private Boolean required = false;

    /** 验证规则JSON */
    @Column(name = "validation_rules", columnDefinition = "TEXT")
    private String validationRules;

    /** 组件属性JSON */
    @Column(name = "component_props", columnDefinition = "TEXT")
    private String componentProps;

    /** 排序号 */
    @Column(name = "sort")
    private Integer sort = 0;

    /** 所属表单ID */
    @Column(name = "form_id")
    private Long formId;

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
