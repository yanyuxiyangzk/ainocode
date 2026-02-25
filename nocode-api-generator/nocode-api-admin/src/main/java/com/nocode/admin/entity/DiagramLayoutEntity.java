package com.nocode.admin.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * ER图布局存储实体
 */
@Data
@Entity
@Table(name = "diagram_layout", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"datasource_name", "schema_name"})
})
public class DiagramLayoutEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 数据源名称
     */
    @Column(name = "datasource_name", nullable = false, length = 100)
    private String datasourceName;
    
    /**
     * Schema名称（PostgreSQL，MySQL为空）
     */
    @Column(name = "schema_name", length = 100)
    private String schemaName;
    
    /**
     * 布局数据（JSON格式）
     * 包含每个表节点的位置信息
     */
    @Column(name = "layout_data", columnDefinition = "TEXT")
    private String layoutData;
    
    /**
     * 视图配置（JSON格式）
     * 包含缩放比例、画布偏移等
     */
    @Column(name = "view_config", columnDefinition = "TEXT")
    private String viewConfig;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
