package com.nocode.core.skill.skills;

import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.orchestrator.ConversionResult;
import com.nocode.core.skill.TypeConversionSkill;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * 自增主键处理技能
 * 跳过自增字段，让数据库自动生成
 * 
 * 注意：此 Skill 只处理非主键的自增字段
 * 主键字段由 PrimaryKeySkill 统一处理
 */
@Component
public class AutoIncrementSkill implements TypeConversionSkill {
    
    private static final Set<String> SUPPORTED_TYPES = Collections.emptySet();
    
    @Override
    public Set<String> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }
    
    @Override
    public String getName() {
        return "AutoIncrementSkill";
    }
    
    @Override
    public int getOrder() {
        return 1; // 高优先级，但低于 PrimaryKeySkill
    }
    
    /**
     * 匹配规则：自增字段且非主键
     * 主键字段由 PrimaryKeySkill 处理
     */
    @Override
    public boolean matches(ColumnInfo columnInfo) {
        return columnInfo.isAutoIncrement() && !columnInfo.isPrimaryKey();
    }
    
    @Override
    public ConversionResult tryConvert(Object value, ColumnInfo columnInfo) {
        // 自增字段直接跳过，让数据库生成
        return ConversionResult.skip();
    }
}