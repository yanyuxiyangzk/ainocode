package com.nocode.core.skill.skills;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.orchestrator.ConversionResult;
import com.nocode.core.skill.TypeConversionSkill;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 字符串类型转换技能
 * 处理 String, varchar, text 等类型
 */
@Component
public class StringSkill implements TypeConversionSkill {
    
    private static final Set<String> SUPPORTED_TYPES = new HashSet<>(Arrays.asList(
            "String",
            "char",
            "Character"
    ));
    
    @Override
    public Set<String> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
    
    @Override
    public String getName() {
        return "StringSkill";
    }
    
    @Override
    public int getOrder() {
        return 50; // 中等优先级
    }
    
    @Override
    public ConversionResult tryConvert(Object value, ColumnInfo columnInfo) {
        if (value == null) {
            // 空值处理
            if (columnInfo.isNullable()) {
                return ConversionResult.success(null);
            }
            return ConversionResult.failed(columnInfo.getName(), "字段不能为空");
        }
        
        // 已经是字符串
        if (value instanceof String) {
            String str = (String) value;
            
            // 检查长度限制
            if (columnInfo.getLength() != null && str.length() > columnInfo.getLength()) {
                return ConversionResult.failed(columnInfo.getName(), 
                        "字符串长度超出限制: " + str.length() + " > " + columnInfo.getLength());
            }
            
            return ConversionResult.success(str);
        }
        
        // 其他类型转字符串
        String strValue = value.toString();
        
        // 检查长度限制
        if (columnInfo.getLength() != null && strValue.length() > columnInfo.getLength()) {
            return ConversionResult.failed(columnInfo.getName(), 
                    "字符串长度超出限制: " + strValue.length() + " > " + columnInfo.getLength());
        }
        
        return ConversionResult.success(strValue);
    }
}
