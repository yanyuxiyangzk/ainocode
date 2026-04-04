package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.nocode.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 工作流定义实体
 *
 * @author ruoyi-nocode
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("workflow_definition")
public class WorkflowDefinitionEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 工作流定义ID
     */
    @TableId(value = "definition_id", type = IdType.AUTO)
    private Long definitionId;

    /**
     * 工作流名称
     */
    private String workflowName;

    /**
     * 工作流编码（唯一标识）
     */
    private String workflowCode;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 工作流类型
     */
    private String workflowType;

    /**
     * 工作流描述
     */
    private String description;

    /**
     * 表单ID
     */
    private Long formId;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 流程定义（JSON格式）
     */
    private String processDefinition;

    /**
     * 流程图定义（JSON格式）
     */
    private String flowGraphic;

    /**
     * 状态（0:禁用 1:启用 2:归档）
     */
    private String status;

    /**
     * 是否最新版本（0:否 1:是）
     */
    private String isLatest;

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