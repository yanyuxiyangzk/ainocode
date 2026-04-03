package com.nocode.core.skill;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.orchestrator.ConversionResult;

/**
 * 类型转换技能接口
 */
public interface TypeConversionSkill {
    
    /**
     * 获取支持的 Java 类型列表
     * @return 支持的类型名称集合（如 "Long", "Integer", "String"）
     */
    java.util.Set<String> getSupportedTypes();
    
    /**
     * 尝试转换值
     * @param value 输入值
     * @param columnInfo 字段信息
     * @return 转换结果
     */
    ConversionResult tryConvert(Object value, ColumnInfo columnInfo);
    
    /**
     * 优先级（数值越小优先级越高）
     * @return 优先级值
     */
    default int getOrder() {
        return 100;
    }
    
    /**
     * 技能名称
     * @return 名称
     */
    String getName();
    
    /**
     * 是否匹配该字段（用于特殊匹配逻辑，如自增字段）
     * @param columnInfo 字段信息
     * @return 是否匹配
     */
    default boolean matches(ColumnInfo columnInfo) {
        return getSupportedTypes().stream()
                .anyMatch(type -> type.equalsIgnoreCase(columnInfo.getJavaType()));
    }
}
