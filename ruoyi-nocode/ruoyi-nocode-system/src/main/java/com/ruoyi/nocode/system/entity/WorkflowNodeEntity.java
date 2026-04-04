package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流节点实体
 *
 * @author ruoyi-nocode
 */
@Data
@TableName("workflow_node")
public class WorkflowNodeEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    @TableId(value = "node_id", type = IdType.AUTO)
    private Long nodeId;

    /**
     * 所属定义ID
     */
    private Long definitionId;

    /**
     * 节点编码
     */
    private String nodeCode;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型（start/end/approve/condition/parallel/counterSign/subProcess）
     */
    private String nodeType;

    /**
     * 节点属性（JSON格式）
     */
    private String nodeProps;

    /**
     * 审批人类型（assignee/candidate/role/formUser）
     */
    private String assigneeType;

    /**
     * 审批人（用户ID、角色ID或表达式）
     */
    private String assigneeValue;

    /**
     * 审批人名称
     */
    private String assigneeName;

    /**
     * 会签类型（none/parallel/serial）
     */
    private String counterSignType;

    /**
     * 会签通过条件（all/any/custom）
     */
    private String counterSignRule;

    /**
     * 会签人数
     */
    private Integer counterSignCount;

    /**
     * 节点路由条件（JSON格式）
     */
    private String routingConditions;

    /**
     * 表单权限（JSON格式，定义各节点对表单字段的读写权限）
     */
    private String formPermissions;

    /**
     * 超时时间（分钟）
     */
    private Integer timeoutMinutes;

    /**
     * 超时处理方式（autoPass/autoReject/notify/remind）
     */
    private String timeoutAction;

    /**
     * 是否允许转交（0:否 1:是）
     */
    private String allowDelegate;

    /**
     * 是否允许驳回（0:否 1:是）
     */
    private String allowReject;

    /**
     * 驳回目标节点
     */
    private String rejectTargets;

    /**
     * 显示顺序
     */
    private Integer sort;

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