package com.nocode.admin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 流程实例实体类
 */
@Entity
@Table(name = "nocode_workflow_instance")
public class WorkflowInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 流程定义ID */
    @Column(name = "definition_id", nullable = false)
    private Long definitionId;

    /** 业务key（关联业务表ID） */
    @Column(name = "business_key", length = 100)
    private String businessKey;

    /** 申请人 */
    @Column(name = "applicant", length = 64)
    private String applicant;

    /** 当前节点ID */
    @Column(name = "current_node_id", length = 100)
    private String currentNodeId;

    /** 当前节点名称 */
    @Column(name = "current_node_name", length = 100)
    private String currentNodeName;

    /** 实例状态：RUNNING-运行中，COMPLETED-已完成，CANCELLED-已取消，REJECTED-已驳回
     */
    @Column(name = "instance_status", length = 20)
    private String instanceStatus = "RUNNING";

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 完成时间 */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }

    // Manual getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDefinitionId() { return definitionId; }
    public void setDefinitionId(Long definitionId) { this.definitionId = definitionId; }
    public String getBusinessKey() { return businessKey; }
    public void setBusinessKey(String businessKey) { this.businessKey = businessKey; }
    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }
    public String getCurrentNodeId() { return currentNodeId; }
    public void setCurrentNodeId(String currentNodeId) { this.currentNodeId = currentNodeId; }
    public String getCurrentNodeName() { return currentNodeName; }
    public void setCurrentNodeName(String currentNodeName) { this.currentNodeName = currentNodeName; }
    public String getInstanceStatus() { return instanceStatus; }
    public void setInstanceStatus(String instanceStatus) { this.instanceStatus = instanceStatus; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
