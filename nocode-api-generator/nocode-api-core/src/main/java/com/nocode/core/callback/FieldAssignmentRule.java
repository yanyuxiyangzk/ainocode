package com.nocode.core.callback;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 字段赋值规则
 * 支持三种值来源：
 * - $CURRENT_TIMESTAMP - 当前时间戳
 * - $SECURITY_CONTEXT.xxx - 从安全上下文获取属性
 * - $固定值 - 直接使用配置的值
 */
@Data
public class FieldAssignmentRule {

    private static final String CURRENT_TIMESTAMP = "$CURRENT_TIMESTAMP";
    private static final String SECURITY_CONTEXT_PREFIX = "$SECURITY_CONTEXT.";

    /** 字段名 */
    private String fieldName;

    /** 值来源，如 $CURRENT_TIMESTAMP, $SECURITY_CONTEXT.orgId */
    private String valueSource;

    /** 固定值（如果是固定值类型，值就是配置的字符串本身） */
    private String fixedValue;

    /** 是否是固定值类型 */
    private boolean fixedValueType;

    public FieldAssignmentRule() {
    }

    public FieldAssignmentRule(String fieldName, String valueSource) {
        this.fieldName = fieldName;
        this.valueSource = valueSource;
        this.fixedValueType = !valueSource.startsWith("$");
        if (this.fixedValueType) {
            this.fixedValue = valueSource;
        }
    }

    /**
     * 解析实际值
     * @param userContext 用户上下文对象（用于获取安全上下文属性）
     * @return 解析后的值
     */
    public Object resolveValue(Object userContext) {
        if (valueSource == null) {
            return fixedValue;
        }

        // 处理 $CURRENT_TIMESTAMP
        if (CURRENT_TIMESTAMP.equals(valueSource)) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // 处理 $SECURITY_CONTEXT.xxx
        if (valueSource.startsWith(SECURITY_CONTEXT_PREFIX)) {
            String attributeName = valueSource.substring(SECURITY_CONTEXT_PREFIX.length());
            return getSecurityContextAttribute(userContext, attributeName);
        }

        // 固定值类型
        if (fixedValueType) {
            return fixedValue;
        }

        // 其他情况返回配置的原值
        return valueSource;
    }

    /**
     * 从安全上下文获取属性
     */
    @SuppressWarnings("unchecked")
    private Object getSecurityContextAttribute(Object userContext, String attributeName) {
        if (userContext == null) {
            return null;
        }

        // 支持 Map 类型的用户上下文
        if (userContext instanceof Map) {
            Map<String, Object> contextMap = (Map<String, Object>) userContext;
            return contextMap.get(attributeName);
        }

        // 支持带 getter 方法的对象
        try {
            String getterName = "get" + capitalize(attributeName);
            return userContext.getClass().getMethod(getterName).invoke(userContext);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 首字母大写
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
