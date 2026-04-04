package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表单组件实体
 *
 * @author ruoyi-nocode
 */
@Data
@TableName("form_component")
public class FormComponentEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 组件ID
     */
    @TableId(value = "component_id", type = IdType.AUTO)
    private Long componentId;

    /**
     * 所属表单ID
     */
    private Long formId;

    /**
     * 组件编码
     */
    private String componentCode;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 组件类型（input/select/date/radio/checkbox/textarea/upload/editor等）
     */
    private String componentType;

    /**
     * 组件属性（JSON格式）
     */
    private String componentProps;

    /**
     * 组件样式（JSON格式）
     */
    private String componentStyle;

    /**
     * 组件默认值
     */
    private String defaultValue;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 验证规则（JSON格式）
     */
    private String validationRules;

    /**
     * 是否必填（0:否 1:是）
     */
    private String required;

    /**
     * 是否禁用（0:否 1:是）
     */
    private String disabled;

    /**
     * 是否隐藏（0:否 1:是）
     */
    private String hidden;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 所属分组
     */
    private String groupName;

    /**
     * 占位提示文本
     */
    private String placeholder;

    /**
     * 帮助文本
     */
    private String helpText;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}