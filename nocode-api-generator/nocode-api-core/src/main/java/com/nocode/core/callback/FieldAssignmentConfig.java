package com.nocode.core.callback;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 字段赋值配置
 * 从 application.yml 读取 nocode.api.callbacks 配置
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "nocode.api.callbacks")
public class FieldAssignmentConfig {

    /** 是否启用配置式字段赋值 */
    private boolean enabled = true;

    /** 表级别的字段赋值配置 */
    private Map<String, TableFieldAssignment> tables;

    /** 缓存：按表名索引的字段赋值 */
    private Map<String, TableFieldAssignment> tableAssignmentCache;

    public FieldAssignmentConfig() {
        this.tables = new LinkedHashMap<>();
        this.tableAssignmentCache = new LinkedHashMap<>();
    }

    @PostConstruct
    public void init() {
        // 构建按表名索引的缓存
        if (tables != null) {
            for (Map.Entry<String, TableFieldAssignment> entry : tables.entrySet()) {
                String tableKey = entry.getKey();
                TableFieldAssignment assignment = entry.getValue();
                if (assignment == null) {
                    assignment = new TableFieldAssignment(tableKey);
                }
                assignment.setTableName(tableKey);
                tableAssignmentCache.put(tableKey, assignment);
                log.info("加载表字段赋值配置: {}", tableKey);
            }
        }
        log.info("FieldAssignmentConfig 初始化完成，共加载 {} 个表的字段赋值配置", tableAssignmentCache.size());
    }

    /**
     * 获取指定表的字段赋值配置
     * @param tableName 表名
     * @return 表字段赋值配置，不存在返回 null
     */
    public TableFieldAssignment getTableAssignment(String tableName) {
        return tableAssignmentCache.get(tableName);
    }

    /**
     * 解析指定表的字段赋值
     * @param tableName 表名
     * @param userContext 用户上下文对象
     * @return 字段名 -> 解析后值的映射，不存在配置返回空 Map
     */
    public Map<String, Object> resolveFields(String tableName, Object userContext) {
        TableFieldAssignment assignment = tableAssignmentCache.get(tableName);
        if (assignment == null || !assignment.hasAssignments()) {
            return new LinkedHashMap<>();
        }
        return assignment.resolveFields(userContext);
    }

    /**
     * 检查是否配置了指定表的字段赋值
     */
    public boolean hasTableAssignment(String tableName) {
        TableFieldAssignment assignment = tableAssignmentCache.get(tableName);
        return assignment != null && assignment.hasAssignments();
    }

    /**
     * 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }
}
