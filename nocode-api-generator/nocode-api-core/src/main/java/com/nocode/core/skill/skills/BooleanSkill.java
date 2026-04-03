package com.nocode.core.skill.skills;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.orchestrator.ConversionResult;
import com.nocode.core.skill.TypeConversionSkill;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 布尔类型转换技能
 * 处理 Boolean 类型，支持多种格式输入
 */
@Component
public class BooleanSkill implements TypeConversionSkill {
    
    private static final Set<String> SUPPORTED_TYPES = new HashSet<>(Arrays.asList(
            "Boolean",
            "boolean"
    ));
    
    /**
     * 表示 true 的字符串值
     */
    private static final Set<String> TRUE_VALUES = new HashSet<>(Arrays.asList(
            "true", "1", "yes", "y", "on", "是", "真", "enabled"
    ));
    
    /**
     * 表示 false 的字符串值
     */
    private static final Set<String> FALSE_VALUES = new HashSet<>(Arrays.asList(
            "false", "0", "no", "n", "off", "否", "假", "disabled"
    ));
    
    @Override
    public Set<String> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
    
    @Override
    public String getName() {
        return "BooleanSkill";
    }
    
    @Override
    public int getOrder() {
        return 20; // 较高优先级
    }
    
    @Override
    public ConversionResult tryConvert(Object value, ColumnInfo columnInfo) {
        if (value == null) {
            if (columnInfo.isNullable()) {
                return ConversionResult.success(null);
            }
            return ConversionResult.failed(columnInfo.getName(), "字段不能为空");
        }
        
        // 已经是布尔类型
        if (value instanceof Boolean) {
            return ConversionResult.success(value);
        }
        
        // 数值类型：非零为 true
        if (value instanceof Number) {
            return ConversionResult.success(((Number) value).doubleValue() != 0);
        }
        
        // 字符串处理
        if (value instanceof String) {
            String str = ((String) value).trim().toLowerCase();
            
            // 空字符串
            if (str.isEmpty()) {
                if (columnInfo.isNullable()) {
                    return ConversionResult.success(null);
                }
                return ConversionResult.failed(columnInfo.getName(), "字段不能为空");
            }
            
            // 检查是否是 true
            if (TRUE_VALUES.contains(str)) {
                return ConversionResult.success(true);
            }
            
            // 检查是否是 false
            if (FALSE_VALUES.contains(str)) {
                return ConversionResult.success(false);
            }
            
            // 无法识别的字符串 → 需要 Agent
            return ConversionResult.needAgent();
        }
        
        // 其他类型
        return ConversionResult.needAgent();
    }
}
