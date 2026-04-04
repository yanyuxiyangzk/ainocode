package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.nocode.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 表单配置实体
 *
 * @author ruoyi-nocode
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("form_config")
public class FormConfigEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表单ID
     */
    @TableId(value = "form_id", type = IdType.AUTO)
    private Long formId;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单编码（唯一标识）
     */
    private String formCode;

    /**
     * 表单类型（1:动态表单 2:自定义表单）
     */
    private String formType;

    /**
     * 表单描述
     */
    private String description;

    /**
     * 表单配置（JSON格式）
     */
    private String formConfig;

    /**
     * 表单样式（JSON格式）
     */
    private String formStyle;

    /**
     * 数据源ID
     */
    private Long datasourceId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 关联表名
     */
    private String tableName;

    /**
     * 状态（0:禁用 1:启用）
     */
    private String status;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标志（0:存在 1:删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;
}