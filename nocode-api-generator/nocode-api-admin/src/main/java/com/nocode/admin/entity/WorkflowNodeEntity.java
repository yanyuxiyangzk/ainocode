package com.nocode.admin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 工作流节点实体类
 *
 * @author auto-dev
 * @since 2026-04-03
 */
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

    // Manual getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDefinitionId() { return definitionId; }
    public void setDefinitionId(Long definitionId) { this.definitionId = definitionId; }
    public String getNodeKey() { return nodeKey; }
    public void setNodeKey(String nodeKey) { this.nodeKey = nodeKey; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }
    public String getAssigneeType() { return assigneeType; }
    public void setAssigneeType(String assigneeType) { this.assigneeType = assigneeType; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public String getCandidates() { return candidates; }
    public void setCandidates(String candidates) { this.candidates = candidates; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getCounterSignConfig() { return counterSignConfig; }
    public void setCounterSignConfig(String counterSignConfig) { this.counterSignConfig = counterSignConfig; }
    public Boolean getIsCounterSign() { return isCounterSign; }
    public void setIsCounterSign(Boolean isCounterSign) { this.isCounterSign = isCounterSign; }
    public String getNodeProps() { return nodeProps; }
    public void setNodeProps(String nodeProps) { this.nodeProps = nodeProps; }
    public String getNodePosition() { return nodePosition; }
    public void setNodePosition(String nodePosition) { this.nodePosition = nodePosition; }
    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }
    public String getOverdueAction() { return overdueAction; }
    public void setOverdueAction(String overdueAction) { this.overdueAction = overdueAction; }
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}