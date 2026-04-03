package com.nocode.core.skill.skills;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.orchestrator.ConversionResult;
import com.nocode.core.skill.TypeConversionSkill;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * UUID 生成技能
 * 
 * 注意：此 Skill 已禁用，字符串字段统一由 StringSkill 处理
 * StringSkill 会处理空值情况（返回 null 或报错）
 * 
 * 保留此类是为了将来可能的扩展需求
 */
@Component
public class UUIDSkill implements TypeConversionSkill {
    
    private static final Set<String> SUPPORTED_TYPES = Collections.singleton("String");
    
    @Override
    public Set<String> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
    
    @Override
    public String getName() {
        return "UUIDSkill";
    }
    
    @Override
    public int getOrder() {
        return 100; // 低优先级，不干扰其他 Skill
    }
    
    /**
     * 不匹配任何字段，让 StringSkill 处理所有字符串字段
     */
    @Override
    public boolean matches(ColumnInfo columnInfo) {
        // 禁用此 Skill，不匹配任何字段
        // 所有字符串字段由 StringSkill 处理
        return false;
    }
    
    @Override
    public ConversionResult tryConvert(Object value, ColumnInfo columnInfo) {
        // 永远不会被调用，因为 matches() 返回 false
        return ConversionResult.needAgent();
    }
    
    /**
     * 判断是否是字符串类型
     */
    private boolean isStringType(String javaType) {
        if (javaType == null) {
            return false;
        }
        String type = javaType.toLowerCase();
        return type.equals("string") || type.contains("varchar") || 
               type.contains("text") || type.contains("char");
    }
    
    /**
     * MD5 加密
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // MD5 不可用，直接返回 UUID
            return input;
        }
    }
}
