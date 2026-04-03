package com.nocode.core.skill.skills;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.orchestrator.ConversionResult;
import com.nocode.core.skill.TypeConversionSkill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 主键处理技能
 * 专门处理主键字段的各种场景，不依赖 Agent
 * 
 * 处理逻辑：
 * 1. 自增主键：跳过，让数据库生成
 * 2. 数值类型主键：
 *    - 有效数值：转换
 *    - 空/null：返回错误，提示需要指定值或使用自增
 *    - 非数字字符串：返回错误，建议修改字段类型或使用数值
 * 3. 字符串类型主键：
 *    - 有值：直接使用
 *    - 空/null：自动生成 UUID+MD5
 */
@Slf4j
@Component
public class PrimaryKeySkill implements TypeConversionSkill {
    
    private static final Set<String> SUPPORTED_TYPES = Collections.emptySet();
    
    @Override
    public Set<String> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
    
    @Override
    public String getName() {
        return "PrimaryKeySkill";
    }
    
    @Override
    public int getOrder() {
        return 0; // 最高优先级，最先处理主键
    }
    
    /**
     * 只匹配主键字段
     */
    @Override
    public boolean matches(ColumnInfo columnInfo) {
        return columnInfo.isPrimaryKey();
    }
    
    @Override
    public ConversionResult tryConvert(Object value, ColumnInfo columnInfo) {
        String fieldName = columnInfo.getName();
        String javaType = columnInfo.getJavaType();
        
        log.debug("PrimaryKeySkill 处理主键字段: {} (类型: {}, 自增: {})", 
                fieldName, javaType, columnInfo.isAutoIncrement());
        
        // 1. 自增主键：跳过
        if (columnInfo.isAutoIncrement()) {
            log.debug("主键 {} 是自增字段，跳过", fieldName);
            return ConversionResult.skip();
        }
        
        // 2. 判断主键类型
        if (isNumericType(javaType)) {
            return handleNumericPrimaryKey(value, columnInfo);
        } else if (isStringType(javaType)) {
            return handleStringPrimaryKey(value, columnInfo);
        } else {
            // 其他类型（如 UUID、自定义类型）
            return handleOtherPrimaryKey(value, columnInfo);
        }
    }
    
    /**
     * 处理数值类型主键
     */
    private ConversionResult handleNumericPrimaryKey(Object value, ColumnInfo columnInfo) {
        String fieldName = columnInfo.getName();
        String javaType = columnInfo.getJavaType();
        
        // 值为空或空字符串
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            String message = String.format("主键 '%s' 为数值类型，必须指定有效的主键值", fieldName);
            List<String> suggestions = new ArrayList<>();
            suggestions.add("请传入数值类型的主键值");
            suggestions.add("或将主键字段设置为自增 (SERIAL/AUTO_INCREMENT)");
            suggestions.add("或将主键字段改为 varchar 类型以支持字符串主键");
            return ConversionResult.agentFailed(message, suggestions);
        }
        
        // 已经是数值类型
        if (value instanceof Number) {
            Number num = (Number) value;
            Object converted = convertNumberToType(num, javaType);
            return ConversionResult.success(converted);
        }
        
        // 字符串类型
        if (value instanceof String) {
            String str = ((String) value).trim();
            
            // 检查是否是有效数字
            if (isValidNumber(str)) {
                try {
                    Number num = parseNumber(str, javaType);
                    return ConversionResult.success(num);
                } catch (NumberFormatException e) {
                    // 数字格式错误（如溢出）
                    String message = String.format("主键 '%s' 数值超出范围: %s", fieldName, str);
                    List<String> suggestions = new ArrayList<>();
                    suggestions.add("请传入有效的数值范围");
                    if (javaType.toLowerCase().contains("int")) {
                        suggestions.add("Integer 范围: -2147483648 ~ 2147483647");
                    } else if (javaType.toLowerCase().contains("long")) {
                        suggestions.add("Long 范围: -9223372036854775808 ~ 9223372036854775807");
                    }
                    return ConversionResult.agentFailed(message, suggestions);
                }
            }
            
            // 非数字字符串
            String message = String.format("主键 '%s' 为数值类型，但传入的是非数字字符串: %s", fieldName, str);
            List<String> suggestions = new ArrayList<>();
            suggestions.add("请传入数值类型的主键值");
            suggestions.add("或将数据库主键字段改为 varchar 类型");
            
            // 如果是32位十六进制字符串（类似 MD5），给出更具体建议
            if (str.length() == 32 && str.matches("[0-9a-fA-F]+")) {
                suggestions.clear();
                suggestions.add("当前传入的是32位MD5字符串，但主键字段是数值类型");
                suggestions.add("建议：将数据库主键字段改为 varchar(32) 类型");
                suggestions.add("或：使用数据库自增主键，让系统自动生成数值ID");
            }
            
            return ConversionResult.agentFailed(message, suggestions);
        }
        
        // 其他类型（如 Boolean）
        String message = String.format("主键 '%s' 类型不匹配: 期望 %s, 实际 %s", 
                fieldName, javaType, value.getClass().getSimpleName());
        List<String> suggestions = new ArrayList<>();
        suggestions.add("请传入正确类型的主键值");
        return ConversionResult.agentFailed(message, suggestions);
    }
    
    /**
     * 处理字符串类型主键
     */
    private ConversionResult handleStringPrimaryKey(Object value, ColumnInfo columnInfo) {
        String fieldName = columnInfo.getName();
        
        // 值为空或空字符串：自动生成 UUID+MD5
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            String generatedId = generateMD5UUID();
            log.info("主键 {} 为空，自动生成: {}", fieldName, generatedId);
            return ConversionResult.success(generatedId);
        }
        
        // 字符串类型：直接使用
        if (value instanceof String) {
            String str = ((String) value).trim();
            
            // 检查长度限制
            if (columnInfo.getLength() != null && str.length() > columnInfo.getLength()) {
                String message = String.format("主键 '%s' 长度超出限制: %d > %d", 
                        fieldName, str.length(), columnInfo.getLength());
                List<String> suggestions = new ArrayList<>();
                suggestions.add("请缩短主键值长度");
                suggestions.add(String.format("或将数据库字段长度改为 %d", str.length()));
                return ConversionResult.agentFailed(message, suggestions);
            }
            
            return ConversionResult.success(str);
        }
        
        // 其他类型转字符串
        String strValue = value.toString();
        return ConversionResult.success(strValue);
    }
    
    /**
     * 处理其他类型主键
     */
    private ConversionResult handleOtherPrimaryKey(Object value, ColumnInfo columnInfo) {
        String fieldName = columnInfo.getName();
        String javaType = columnInfo.getJavaType();
        
        // 值为空
        if (value == null) {
            String message = String.format("主键 '%s' 必须指定值", fieldName);
            List<String> suggestions = new ArrayList<>();
            suggestions.add("请提供主键值");
            return ConversionResult.agentFailed(message, suggestions);
        }
        
        // 直接使用原值
        log.debug("主键 {} 类型 {} 使用原值: {}", fieldName, javaType, value);
        return ConversionResult.success(value);
    }
    
    // ========== 工具方法 ==========
    
    /**
     * 判断是否是数值类型
     */
    private boolean isNumericType(String javaType) {
        if (javaType == null) return false;
        String type = javaType.toLowerCase();
        return type.equals("long") || type.equals("integer") || type.equals("int") ||
               type.equals("short") || type.equals("byte") || type.equals("bigint") ||
               type.equals("smallint") || type.equals("tinyint");
    }
    
    /**
     * 判断是否是字符串类型
     */
    private boolean isStringType(String javaType) {
        if (javaType == null) return false;
        String type = javaType.toLowerCase();
        return type.equals("string") || type.contains("varchar") || 
               type.contains("char") || type.contains("text");
    }
    
    /**
     * 检查是否是有效数字格式
     */
    private boolean isValidNumber(String str) {
        return str.matches("-?\\d+");
    }
    
    /**
     * 解析字符串为数字
     */
    private Number parseNumber(String str, String javaType) throws NumberFormatException {
        String type = javaType.toLowerCase();
        
        if (type.equals("long") || type.equals("bigint")) {
            return Long.parseLong(str);
        } else if (type.equals("int") || type.equals("integer")) {
            return Integer.parseInt(str);
        } else if (type.equals("short") || type.equals("smallint")) {
            return Short.parseShort(str);
        } else if (type.equals("byte") || type.equals("tinyint")) {
            return Byte.parseByte(str);
        }
        
        // 默认 Long
        return Long.parseLong(str);
    }
    
    /**
     * 将 Number 转换为目标类型
     */
    private Object convertNumberToType(Number num, String javaType) {
        String type = javaType.toLowerCase();
        
        if (type.equals("long") || type.equals("bigint")) {
            return num.longValue();
        } else if (type.equals("int") || type.equals("integer")) {
            return num.intValue();
        } else if (type.equals("short") || type.equals("smallint")) {
            return num.shortValue();
        } else if (type.equals("byte") || type.equals("tinyint")) {
            return num.byteValue();
        }
        
        return num;
    }
    
    /**
     * 生成 UUID + MD5
     */
    private String generateMD5UUID() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(uuid.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // MD5 不可用，直接返回 UUID
            return uuid;
        }
    }
}
