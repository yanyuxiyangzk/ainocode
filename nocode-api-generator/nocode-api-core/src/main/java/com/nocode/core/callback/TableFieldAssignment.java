package com.nocode.core.callback;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 表级别的字段赋值配置
 */
@Data
public class TableFieldAssignment {

    /** 表名 */
    private String tableName;

    /** 字段赋值规则列表 */
    private List<FieldAssignmentRule> fields;

    public TableFieldAssignment() {
        this.fields = new ArrayList<>();
    }

    public TableFieldAssignment(String tableName) {
        this.tableName = tableName;
        this.fields = new ArrayList<>();
    }

    /**
     * 添加字段规则
     */
    public void addField(String fieldName, String valueSource) {
        this.fields.add(new FieldAssignmentRule(fieldName, valueSource));
    }

    /**
     * 解析该表所有字段赋值
     * @param userContext 用户上下文对象
     * @return 字段名 -> 解析后值的映射
     */
    public Map<String, Object> resolveFields(Object userContext) {
        Map<String, Object> resolved = new LinkedHashMap<>();
        for (FieldAssignmentRule rule : fields) {
            Object value = rule.resolveValue(userContext);
            if (value != null) {
                resolved.put(rule.getFieldName(), value);
            }
        }
        return resolved;
    }

    /**
     * 检查是否配置了该表的字段赋值
     */
    public boolean hasAssignments() {
        return fields != null && !fields.isEmpty();
    }
}
