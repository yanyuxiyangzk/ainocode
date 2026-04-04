package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流任务实体
 *
 * @author ruoyi-nocode
 */
@Data
@TableName("workflow_task")
public class WorkflowTaskEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    /**
     * 实例ID
     */
    private Long instanceId;

    /**
     * 节点ID
     */
    private Long nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 任务类型（approve/delegate/reject/counterSign）
     */
    private String taskType;

    /**
     * 任务状态（0:待处理 1:处理中 2:已完成 3:已取消 4:已转交）
     */
    private String taskStatus;

    /**
     * 任务办理人ID
     */
    private Long assigneeId;

    /**
     * 任务办理人名称
     */
    private String assigneeName;

    /**
     * 原办理人ID（用于转交记录）
     */
    private Long originalAssigneeId;

    /**
     * 原办理人名称
     */
    private String originalAssigneeName;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 审批结果（agree/reject/redirect/cancel）
     */
    private String result;

    /**
     * 任务接收时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime receiveTime;

    /**
     * 任务完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 任务截止时间
     */
    private LocalDateTime dueTime;

    /**
     * 是否超时（0:否 1:是）
     */
    private String isTimeout;

    /**
     * 外部任务ID（第三方系统集成用）
     */
    private String externalTaskId;

    /**
     * 任务变量（JSON格式）
     */
    private String taskVariables;

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