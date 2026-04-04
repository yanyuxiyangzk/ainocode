package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.nocode.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 工作流实例实体
 *
 * @author ruoyi-nocode
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("workflow_instance")
public class WorkflowInstanceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 实例ID
     */
    @TableId(value = "instance_id", type = IdType.AUTO)
    private Long instanceId;

    /**
     * 流程定义ID
     */
    private Long definitionId;

    /**
     * 实例标题
     */
    private String instanceTitle;

    /**
     * 实例编码
     */
    private String instanceCode;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人名称
     */
    private String applicantName;

    /**
     * 所属部门ID
     */
    private Long deptId;

    /**
     * 所属部门名称
     */
    private String deptName;

    /**
     * 当前节点ID
     */
    private Long currentNodeId;

    /**
     * 当前节点名称
     */
    private String currentNodeName;

    /**
     * 当前节点类型
     */
    private String currentNodeType;

    /**
     * 实例状态（0:草稿 1:审批中 2:已完成 3:已驳回 4:已撤回 5:已取消）
     */
    private String instanceStatus;

    /**
     * 业务数据ID
     */
    private String businessKey;

    /**
     * 业务数据JSON
     */
    private String businessData;

    /**
     * 审批历史JSON
     */
    private String approvalHistory;

    /**
     * 发起时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime endTime;

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