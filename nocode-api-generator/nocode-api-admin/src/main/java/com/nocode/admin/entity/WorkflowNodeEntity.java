package com.nocode.admin.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 工作流节点实体类
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Data
@Entity
@Table(name = "nocode_workflow_node")
public class WorkflowNodeEntity {

    /** 节点ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属流程定义ID */
    @Column(name = "definition_id", nullable = false)
    private Long definitionId;

    /** 节点Key */
    @Column(name = "node_key", nullable = false, length = 100)
    private String nodeKey;

    /** 节点名称 */
    @Column(name = "node_name", length = 100)
    private String nodeName;

    /** 节点类型：START-开始节点，END-结束节点，TASK-任务节点，GATEWAY-网关节点 */
    @Column(name = "node_type", length = 20)
    private String nodeType;

    /** 节点角色/处理人类型：ASSIGNEE-指定人，CANDIDATE-候选人，ROLE-角色 */
    @Column(name = "assignee_type", length = 20)
    private String assigneeType;

    /** 指定处理人 */
    @Column(name = "assignee", length = 64)
    private String assignee;

    /** 候选处理人（多个用逗号分隔） */
    @Column(name = "candidates", length = 500)
    private String candidates;

    /** 角色ID */
    @Column(name = "role_id")
    private Long roleId;

    /** 会签配置JSON */
    @Column(name = "counter_sign_config", columnDefinition = "TEXT")
    private String counterSignConfig;

    /** 是否会签节点 */
    @Column(name = "is_counter_sign")
    private Boolean isCounterSign = false;

    /** 节点属性JSON */
    @Column(name = "node_props", columnDefinition = "TEXT")
    private String nodeProps;

    /** 节点位置JSON */
    @Column(name = "node_position", columnDefinition = "TEXT")
    private String nodePosition;

    /** 期限（小时） */
    @Column(name = "duration_hours")
    private Integer durationHours;

    /** 逾期处理方式 */
    @Column(name = "overdue_action", length = 50)
    private String overdueAction;

    /** 表单ID */
    @Column(name = "form_id")
    private Long formId;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}