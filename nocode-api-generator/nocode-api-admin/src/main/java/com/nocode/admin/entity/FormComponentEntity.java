package com.nocode.admin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 表单组件实体类
 */
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

    // Manual getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getComponentType() { return componentType; }
    public void setComponentType(String componentType) { this.componentType = componentType; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    public Boolean getRequired() { return required; }
    public void setRequired(Boolean required) { this.required = required; }
    public String getValidationRules() { return validationRules; }
    public void setValidationRules(String validationRules) { this.validationRules = validationRules; }
    public String getComponentProps() { return componentProps; }
    public void setComponentProps(String componentProps) { this.componentProps = componentProps; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}