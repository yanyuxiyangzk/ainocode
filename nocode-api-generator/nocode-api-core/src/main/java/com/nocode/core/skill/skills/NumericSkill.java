package com.nocode.core.skill.skills;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.orchestrator.ConversionResult;
import com.nocode.core.skill.TypeConversionSkill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * 数值类型转换技能
 * 处理 Long, Integer, Short, Byte, Double, Float, BigDecimal 等类型
 */
@Slf4j
@Component
public class NumericSkill implements TypeConversionSkill {

    private static final Set<String> SUPPORTED_TYPES = new HashSet<>(Arrays.asList(
            "Long", "long",
            "Integer", "int", "Int",
            "Short", "short",
            "Byte", "byte",
            "Double", "double",
            "Float", "float",
            "BigDecimal"
    ));
    
    /**
     * 有效数字格式的正则表达式
     */
    private static final java.util.regex.Pattern NUMBER_PATTERN = 
            java.util.regex.Pattern.compile("^-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?$");
    
    @Override
    public Set<String> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
    
    @Override
    public String getName() {
        return "NumericSkill";
    }
    
    @Override
    public int getOrder() {
        return 10; // 高优先级
    }
    
    @Override
    public ConversionResult tryConvert(Object value, ColumnInfo columnInfo) {
        if (value == null) {
            // 空值处理：如果字段可为空则返回 null，否则返回失败
            if (columnInfo.isNullable()) {
                return ConversionResult.success(null);
            }
            return ConversionResult.failed(columnInfo.getName(), "字段不能为空");
        }
        
        String javaType = columnInfo.getJavaType();
        
        // 已经是数值类型
        if (value instanceof Number) {
            Number num = (Number) value;
            Object converted = convertNumberToType(num, javaType);
            return ConversionResult.success(converted);
        }
        
        // 字符串处理
        if (value instanceof String) {
            String str = ((String) value).trim();
            
            // 空字符串
            if (str.isEmpty()) {
                if (columnInfo.isNullable()) {
                    return ConversionResult.success(null);
                }
                return ConversionResult.failed(columnInfo.getName(), "字段不能为空");
            }
            
            // 检查是否是有效数字格式
            if (!isValidNumber(str)) {
                // 关键：非数字字符串无法转换为数值类型 → 需要 Agent 处理
                log.debug("数值类型字段 {} 收到非数字值: {}", columnInfo.getName(), str);
                return ConversionResult.needAgent();
            }
            
            try {
                Number num = parseNumber(str, javaType);
                return ConversionResult.success(num);
            } catch (NumberFormatException e) {
                log.warn("数值解析失败: {} -> {}", str, javaType, e);
                return ConversionResult.failed(columnInfo.getName(), 
                        "数值格式错误: '" + str + "' 无法转换为 " + javaType);
            }
        }
        
        // 其他类型（如 Boolean）→ 需要 Agent 判断
        log.debug("数值类型字段 {} 收到非预期类型: {}", columnInfo.getName(), value.getClass().getSimpleName());
        return ConversionResult.needAgent();
    }
    
    /**
     * 检查字符串是否是有效的数字格式
     */
    private boolean isValidNumber(String str) {
        return NUMBER_PATTERN.matcher(str).matches();
    }
    
    /**
     * 解析字符串为数字
     */
    private Number parseNumber(String str, String javaType) throws NumberFormatException {
        String type = javaType.toLowerCase();
        
        if (type.equals("long")) {
            return Long.parseLong(str);
        } else if (type.equals("int") || type.equals("integer")) {
            // 对于大数，先转 Long 再截断
            long longVal = new BigDecimal(str).longValue();
            return (int) longVal;
        } else if (type.equals("short")) {
            return Short.parseShort(str);
        } else if (type.equals("byte")) {
            return Byte.parseByte(str);
        } else if (type.equals("double")) {
            return Double.parseDouble(str);
        } else if (type.equals("float")) {
            return Float.parseFloat(str);
        } else if (type.equals("bigdecimal")) {
            return new BigDecimal(str);
        }
        
        // 默认返回 Long
        return Long.parseLong(str);
    }
    
    /**
     * 将 Number 转换为目标类型
     */
    private Object convertNumberToType(Number num, String javaType) {
        String type = javaType.toLowerCase();
        
        if (type.equals("long")) {
            return num.longValue();
        } else if (type.equals("int") || type.equals("integer")) {
            return num.intValue();
        } else if (type.equals("short")) {
            return num.shortValue();
        } else if (type.equals("byte")) {
            return num.byteValue();
        } else if (type.equals("double")) {
            return num.doubleValue();
        } else if (type.equals("float")) {
            return num.floatValue();
        } else if (type.equals("bigdecimal")) {
            if (num instanceof BigDecimal) {
                return num;
            }
            return new BigDecimal(num.toString());
        }
        
        return num;
    }
}
