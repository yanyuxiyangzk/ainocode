package com.nocode.core.agent;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Agent 上下文
 * 包含类型转换所需的所有信息
 */
@Data
@Builder
public class AgentContext {
    
    /**
     * 字段名
     */
    private String fieldName;
    
    /**
     * 数据库类型（如 int4, varchar, timestamp）
     */
    private String dbType;
    
    /**
     * Java 类型（如 Long, String, Timestamp）
     */
    private String javaType;
    
    /**
     * 输入值
     */
    private Object inputValue;
    
    /**
     * 是否主键
     */
    private boolean primaryKey;
    
    /**
     * 是否自增
     */
    private boolean autoIncrement;
    
    /**
     * 是否可为空
     */
    private boolean nullable;
    
    /**
     * 字段长度
     */
    private Integer length;
    
    /**
     * Skill 失败原因
     */
    private String skillError;
    
    /**
     * 扩展信息
     */
    @Builder.Default
    private Map<String, Object> extra = new HashMap<>();
    
    /**
     * 获取输入值的类型名
     */
    public String getInputValueType() {
        if (inputValue == null) {
            return "null";
        }
        return inputValue.getClass().getSimpleName();
    }
    
    /**
     * 获取输入值的字符串表示
     */
    public String getInputValueString() {
        if (inputValue == null) {
            return "null";
        }
        return inputValue.toString();
    }
    
    /**
     * 添加扩展信息
     */
    public void addExtra(String key, Object value) {
        if (extra == null) {
            extra = new HashMap<>();
        }
        extra.put(key, value);
    }
    
    /**
     * 获取扩展信息
     */
    public Object getExtra(String key) {
        if (extra == null) {
            return null;
        }
        return extra.get(key);
    }
}
