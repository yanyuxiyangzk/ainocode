package com.nocode.core.skill;

import com.nocode.core.entity.ColumnInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Skill 注册器
 */
@Slf4j
@Component
public class SkillRegistry {
    
    /**
     * 按类型索引的 Skill 列表
     */
    private final Map<String, List<TypeConversionSkill>> skillByType = new HashMap<>();
    
    /**
     * 所有已注册的 Skill（按优先级排序）
     */
    private final List<TypeConversionSkill> allSkills = new ArrayList<>();
    
    /**
     * 自动注入所有 TypeConversionSkill 实现
     */
    @Autowired(required = false)
    private List<TypeConversionSkill> injectedSkills;
    
    @PostConstruct
    public void init() {
        if (injectedSkills != null) {
            for (TypeConversionSkill skill : injectedSkills) {
                register(skill);
            }
        }
        log.info("SkillRegistry 初始化完成，已注册 {} 个 Skill", allSkills.size());
    }
    
    /**
     * 注册 Skill
     * @param skill 技能实例
     */
    public void register(TypeConversionSkill skill) {
        // 添加到全局列表
        allSkills.add(skill);
        // 按优先级排序
        allSkills.sort(Comparator.comparingInt(TypeConversionSkill::getOrder));
        
        // 按类型索引
        for (String type : skill.getSupportedTypes()) {
            skillByType.computeIfAbsent(type.toLowerCase(), k -> new ArrayList<>())
                    .add(skill);
            // 保持优先级排序
            skillByType.get(type.toLowerCase())
                    .sort(Comparator.comparingInt(TypeConversionSkill::getOrder));
        }
        
        log.debug("注册 Skill: {} (优先级: {}, 支持类型: {})", 
                skill.getName(), skill.getOrder(), skill.getSupportedTypes());
    }
    
    /**
     * 根据类型获取 Skill
     * @param javaType Java 类型名
     * @return 匹配的 Skill，如果没有则返回 null
     */
    public TypeConversionSkill getSkill(String javaType) {
        if (javaType == null) {
            return null;
        }
        
        List<TypeConversionSkill> skills = skillByType.get(javaType.toLowerCase());
        if (skills != null && !skills.isEmpty()) {
            return skills.get(0); // 返回优先级最高的
        }
        
        return null;
    }
    
    /**
     * 根据字段信息获取最匹配的 Skill
     * @param columnInfo 字段信息
     * @return 匹配的 Skill，如果没有则返回 null
     */
    public TypeConversionSkill getSkill(ColumnInfo columnInfo) {
        if (columnInfo == null) {
            return null;
        }
        
        // 遍历所有 Skill，找到第一个匹配的
        for (TypeConversionSkill skill : allSkills) {
            if (skill.matches(columnInfo)) {
                return skill;
            }
        }
        
        // 兜底：按类型查找
        return getSkill(columnInfo.getJavaType());
    }
    
    /**
     * 获取所有已注册的 Skill
     * @return Skill 列表
     */
    public List<TypeConversionSkill> getAllSkills() {
        return Collections.unmodifiableList(allSkills);
    }
}
