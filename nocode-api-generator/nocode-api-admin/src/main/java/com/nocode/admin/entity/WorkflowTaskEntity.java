package com.nocode.admin.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 工作流任务实体类
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Data
@Entity
@Table(name = "nocode_workflow_task")
public class WorkflowTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 实例ID */
    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    /** 节点ID */
    @Column(name = "node_id", nullable = false, length = 100)
    private String nodeId;

    /** 节点名称 */
    @Column(name = "node_name", length = 100)
    private String nodeName;

    /** 办理人 */
    @Column(name = "assignee", length = 64)
    private String assignee;

    /** 候选人 */
    @Column(name = "candidate", length = 500)
    private String candidate;

    /** 任务状态：PENDING-待签收，CLAIMED-已签收，COMPLETED-已完成，REJECTED-已驳回，CANCELLED-已取消，TRANSFERRED-已转交 */
    @Column(name = "task_status", length = 20)
    private String taskStatus = "PENDING";

    /** 会签人数 */
    @Column(name = "countersign_count")
    private Integer countersignCount = 0;

    /** 已会签人数 */
    @Column(name = "countersigned_count")
    private Integer countersignedCount = 0;

    /** 会签结果：AGREE-同意，REJECT-驳回 */
    @Column(name = "countersign_result", length = 20)
    private String countersignResult;

    /** 是否会签任务 */
    @Column(name = "is_countersign")
    private Boolean isCounterSign = false;

    /** 父任务ID（用于转交） */
    @Column(name = "parent_task_id")
    private Long parentTaskId;

    /** 优先级：LOW-低，NORMAL-普通，HIGH-高 */
    @Column(name = "priority", length = 20)
    private String priority = "NORMAL";

    /** 截止日期 */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /** 批注 */
    @Column(name = "comment", length = 500)
    private String comment;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 签收时间 */
    @Column(name = "claim_time")
    private LocalDateTime claimTime;

    /** 完成时间 */
    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}