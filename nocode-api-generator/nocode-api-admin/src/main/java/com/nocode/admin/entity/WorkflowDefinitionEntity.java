package com.nocode.admin.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 流程定义实体类
 */
@Data
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
}
