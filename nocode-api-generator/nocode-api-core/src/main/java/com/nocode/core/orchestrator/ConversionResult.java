package com.nocode.core.orchestrator;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 类型转换结果
 */
@Data
public class ConversionResult {
    
    /**
     * 转换状态
     */
    public enum Status {
        /** 转换成功 */
        SUCCESS,
        /** 转换失败，无法处理 */
        FAILED,
        /** 需要 Agent 智能处理 */
        NEED_AGENT,
        /** 跳过该字段（如自增主键） */
        SKIP
    }
    
    /**
     * 转换状态
     */
    private Status status;
    
    /**
     * 转换后的值
     */
    private Object convertedValue;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 建议列表（Agent 生成的解决方案）
     */
    private List<String> suggestions;
    
    /**
     * 字段名（用于错误提示）
     */
    private String fieldName;
    
    // ========== 静态工厂方法 ==========
    
    /**
     * 转换成功
     */
    public static ConversionResult success(Object value) {
        ConversionResult result = new ConversionResult();
        result.setStatus(Status.SUCCESS);
        result.setConvertedValue(value);
        return result;
    }
    
    /**
     * 转换失败
     */
    public static ConversionResult failed(String errorMessage) {
        ConversionResult result = new ConversionResult();
        result.setStatus(Status.FAILED);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    /**
     * 转换失败（带字段名）
     */
    public static ConversionResult failed(String fieldName, String errorMessage) {
        ConversionResult result = new ConversionResult();
        result.setStatus(Status.FAILED);
        result.setFieldName(fieldName);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    /**
     * 需要 Agent 处理
     */
    public static ConversionResult needAgent() {
        ConversionResult result = new ConversionResult();
        result.setStatus(Status.NEED_AGENT);
        return result;
    }
    
    /**
     * 跳过该字段
     */
    public static ConversionResult skip() {
        ConversionResult result = new ConversionResult();
        result.setStatus(Status.SKIP);
        return result;
    }
    
    /**
     * Agent 处理失败（带建议）
     */
    public static ConversionResult agentFailed(String errorMessage, List<String> suggestions) {
        ConversionResult result = new ConversionResult();
        result.setStatus(Status.FAILED);
        result.setErrorMessage(errorMessage);
        result.setSuggestions(suggestions != null ? suggestions : Collections.emptyList());
        return result;
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
    
    /**
     * 是否跳过
     */
    public boolean isSkip() {
        return status == Status.SKIP;
    }
    
    /**
     * 是否失败
     */
    public boolean isFailed() {
        return status == Status.FAILED;
    }
    
    /**
     * 是否需要 Agent
     */
    public boolean needsAgent() {
        return status == Status.NEED_AGENT;
    }
    
    /**
     * 添加建议
     */
    public void addSuggestion(String suggestion) {
        if (suggestions == null) {
            suggestions = new ArrayList<>();
        }
        suggestions.add(suggestion);
    }
}
