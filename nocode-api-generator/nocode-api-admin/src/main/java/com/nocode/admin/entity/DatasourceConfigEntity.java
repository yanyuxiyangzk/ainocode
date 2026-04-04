package com.nocode.admin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 数据源配置实体类
 */
@Entity
@Table(name = "nocode_datasource_config")
public class DatasourceConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 数据源名称（唯一标识） */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /** JDBC URL */
    @Column(name = "jdbc_url", nullable = false, length = 500)
    private String jdbcUrl;

    /** 用户名 */
    @Column(name = "username", length = 100)
    private String username;

    /** 密码（加密存储） */
    @Column(name = "password", length = 255)
    private String password;

    /** 驱动类名 */
    @Column(name = "driver_class_name", length = 100)
    private String driverClassName;

    /** 数据库类型 */
    @Column(name = "database_type", length = 20)
    private String databaseType;

    /** 初始连接数 */
    @Column(name = "initial_size")
    private Integer initialSize = 5;

    /** 最小连接数 */
    @Column(name = "min_idle")
    private Integer minIdle = 5;

    /** 最大连接数 */
    @Column(name = "max_active")
    private Integer maxActive = 20;

    /** 连接最大等待时间（毫秒） */
    @Column(name = "max_wait")
    private Long maxWait = 60000L;

    /** 是否启用 */
    @Column(name = "enabled")
    private Boolean enabled = true;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 更新时间 */
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
    public String getJdbcUrl() { return jdbcUrl; }
    public void setJdbcUrl(String jdbcUrl) { this.jdbcUrl = jdbcUrl; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDriverClassName() { return driverClassName; }
    public void setDriverClassName(String driverClassName) { this.driverClassName = driverClassName; }
    public String getDatabaseType() { return databaseType; }
    public void setDatabaseType(String databaseType) { this.databaseType = databaseType; }
    public Integer getInitialSize() { return initialSize; }
    public void setInitialSize(Integer initialSize) { this.initialSize = initialSize; }
    public Integer getMinIdle() { return minIdle; }
    public void setMinIdle(Integer minIdle) { this.minIdle = minIdle; }
    public Integer getMaxActive() { return maxActive; }
    public void setMaxActive(Integer maxActive) { this.maxActive = maxActive; }
    public Long getMaxWait() { return maxWait; }
    public void setMaxWait(Long maxWait) { this.maxWait = maxWait; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}