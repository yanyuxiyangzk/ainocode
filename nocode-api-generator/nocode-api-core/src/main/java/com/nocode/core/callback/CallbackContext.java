package com.nocode.core.callback;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 回调上下文
 * 在执行数据库操作前后传递上下文信息
 */
@Data
public class CallbackContext {
    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 操作类型：INSERT / UPDATE / QUERY_LIST / QUERY_ONE / DELETE
     */
    private String operationType;

    /**
     * 当前数据（插入或更新时使用）
     * 对于查询操作，此字段可能包含 List<Map<String, Object>> 类型
     */
    private Object data;

    /**
     * 用户上下文（可自定义传递额外信息）
     */
    private Object userContext;
}
