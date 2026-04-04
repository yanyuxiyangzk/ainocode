package com.nocode.admin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 流程定义实体类
 */
@Entity
@Table(name = "nocode_workflow_definition")
public class WorkflowDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 流程名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 流程描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 流程标识（唯一） */
    @Column(name = "process_key", nullable = false, unique = true, length = 100)
    private String processKey;

    /** 版本号 */
    @Column(name = "version")
    private Integer version = 1;

    /** 流程图JSON */
    @Column(name = "diagram_json", columnDefinition = "TEXT")
    private String diagramJson;

    /** 节点定义JSON */
    @Column(name = "node_definition", columnDefinition = "TEXT")
    private String nodeDefinition;

    /** 流转定义JSON */
    @Column(name = "sequence_flow", columnDefinition = "TEXT")
    private String sequenceFlow;

    /** 状态：DRAFT-草稿，DEPLOYED-已部署，SUSPENDED-挂起
     */
    @Column(name = "status", length = 20)
    private String status = "DRAFT";

    /** 是否挂起 */
    @Column(name = "suspended")
    private Boolean suspended = false;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**更新时间 */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    // Manual getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getProcessKey() { return processKey; }
    public void setProcessKey(String processKey) { this.processKey = processKey; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getDiagramJson() { return diagramJson; }
    public void setDiagramJson(String diagramJson) { this.diagramJson = diagramJson; }
    public String getNodeDefinition() { return nodeDefinition; }
    public void setNodeDefinition(String nodeDefinition) { this.nodeDefinition = nodeDefinition; }
    public String getSequenceFlow() { return sequenceFlow; }
    public void setSequenceFlow(String sequenceFlow) { this.sequenceFlow = sequenceFlow; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getSuspended() { return suspended; }
    public void setSuspended(Boolean suspended) { this.suspended = suspended; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}