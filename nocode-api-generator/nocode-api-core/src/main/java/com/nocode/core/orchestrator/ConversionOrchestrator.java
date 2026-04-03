package com.nocode.core.orchestrator;

import com.nocode.core.agent.AgentContext;
import com.nocode.core.agent.TypeConversionAgent;
import com.nocode.core.entity.ColumnInfo;
import com.nocode.core.entity.TableInfo;
import com.nocode.core.skill.SkillRegistry;
import com.nocode.core.skill.TypeConversionSkill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 类型转换编排器
 * 协调 Skill 和 Agent 完成类型转换
 * 
 * Agent 开关通过配置 nocode.agent.enabled 控制：
 * - true: 启用 Agent，复杂类型转换由 LLM 智能处理
 * - false: 禁用 Agent（默认），Skill 无法处理时返回友好错误
 */
@Slf4j
@Component
public class ConversionOrchestrator {
    
    private final SkillRegistry skillRegistry;
    
    @Autowired(required = false)
    private TypeConversionAgent agent;
    
    @Value("${nocode.agent.enabled:false}")
    private boolean agentEnabled;
    
    public ConversionOrchestrator(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }
    
    /**
     * 转换单个字段值
     * @param value 输入值
     * @param columnInfo 字段信息
     * @return 转换结果
     */
    public ConversionResult convert(Object value, ColumnInfo columnInfo) {
        log.debug("转换字段 {}: {} ({})", columnInfo.getName(), value, columnInfo.getJavaType());
        
        // 1. 查找匹配的 Skill
        TypeConversionSkill skill = skillRegistry.getSkill(columnInfo);
        
        if (skill != null) {
            // 2. 尝试 Skill 转换
            String colName = columnInfo.getName();
            if (colName.equals("create_time")
                    || colName.equals("update_time")
                    ) {
                value = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            ConversionResult result = skill.tryConvert(value, columnInfo);
            
            switch (result.getStatus()) {
                case SUCCESS:
                case SKIP:
                    log.debug("Skill {} 处理成功: {} -> {}", 
                            skill.getName(), value, result.getConvertedValue());
                    return result;
                    
                case NEED_AGENT:
                    // 3. Skill 建议使用 Agent
                    log.debug("Skill {} 建议使用 Agent", skill.getName());
                    if (isAgentAvailable()) {
                        return invokeAgent(value, columnInfo, result);
                    }
                    // Agent 未启用，返回友好错误
                    return buildNoAgentResult(value, columnInfo);
                    
                case FAILED:
                    // 4. Skill 失败，尝试 Agent 恢复
                    if (isAgentAvailable()) {
                        return invokeAgent(value, columnInfo, result);
                    }
                    return result;
            }
        }
        
        // 5. 无匹配 Skill，尝试 Agent
        if (isAgentAvailable()) {
            return invokeAgent(value, columnInfo, null);
        }
        
        // 6. 兜底：返回原值（让数据库处理或报错）
        log.debug("无匹配 Skill，使用原值: {}", value);
        return ConversionResult.success(value);
    }
    
    /**
     * 转换整条记录
     * @param data 原始数据（key 为驼峰格式）
     * @param tableInfo 表信息
     * @return 转换后的数据（key 为数据库列名）
     * @throws ConversionException 转换失败时抛出
     */
    public Map<String, Object> convertAll(Map<String, Object> data, TableInfo tableInfo) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        
        for (ColumnInfo column : tableInfo.getColumns()) {
            String columnName = column.getName();
            String camelName = toCamelCase(columnName);
            
            // 获取输入值（支持驼峰和原始列名两种格式）
            Object value = getValue(data, camelName, columnName);
//            if(null == value){
//                continue;
//            }
            // 执行转换
            ConversionResult cr = convert(value, column);

            switch (cr.getStatus()) {
                case SUCCESS:
                    result.put(columnName, cr.getConvertedValue());
                    break;
                    
                case SKIP:
                    // 跳过该字段（如自增主键）
                    log.debug("跳过字段: {}", columnName);
                    break;
                    
                case FAILED:
                    String error = formatError(columnName, cr);
                    errors.add(error);
                    log.warn("字段转换失败: {}", error);
                    break;
                    
                default:
                    // 其他状态不应该出现在这里
                    log.warn("未处理的转换状态: {} - {}", columnName, cr.getStatus());
                    result.put(columnName, value);
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ConversionException(errors);
        }
        
        return result;
    }
    
    /**
     * 调用 Agent 处理
     */
    private ConversionResult invokeAgent(Object value, ColumnInfo columnInfo, ConversionResult skillResult) {
        if (!isAgentAvailable()) {
            return buildNoAgentResult(value, columnInfo);
        }
        
        // 构建 Agent 上下文
        AgentContext context = AgentContext.builder()
                .fieldName(columnInfo.getName())
                .dbType(columnInfo.getType())
                .javaType(columnInfo.getJavaType())
                .inputValue(value)
                .primaryKey(columnInfo.isPrimaryKey())
                .autoIncrement(columnInfo.isAutoIncrement())
                .nullable(columnInfo.isNullable())
                .length(columnInfo.getLength())
                .skillError(skillResult != null ? skillResult.getErrorMessage() : null)
                .build();
        
        log.info("调用 Agent 处理字段: {}", columnInfo.getName());
        return agent.handle(context);
    }
    
    /**
     * 构建 Agent 未启用时的错误结果
     */
    private ConversionResult buildNoAgentResult(Object value, ColumnInfo columnInfo) {
        String message = String.format(
                "字段 '%s' 类型转换失败：无法将 '%s' 转换为 %s 类型",
                columnInfo.getName(),
                value != null ? value.toString() : "null",
                columnInfo.getJavaType()
        );
        
        List<String> suggestions = new ArrayList<>();
        
        // 根据字段类型给出具体建议
        String javaType = columnInfo.getJavaType().toLowerCase();
        
        if (javaType.contains("int") || javaType.contains("long") || 
            javaType.contains("short") || javaType.contains("byte") ||
            javaType.contains("double") || javaType.contains("float")) {
            // 数值类型
            suggestions.add("请输入有效的数值类型");
            if (columnInfo.isPrimaryKey()) {
                suggestions.add("该表主键为数值类型，请传入数值主键或让数据库自增生成");
            }
        } else if (javaType.contains("boolean")) {
            suggestions.add("请输入 true/false 或 1/0");
        } else if (javaType.contains("date") || javaType.contains("time")) {
            suggestions.add("请输入有效的日期时间格式，如 '2024-01-01 00:00:00'");
        } else {
            suggestions.add("请检查输入值格式是否符合字段类型要求");
        }
        
        // 添加启用 Agent 的提示
        if (!agentEnabled) {
            suggestions.add("提示：可配置 nocode.agent.enabled=true 启用智能类型转换");
        }
        
        return ConversionResult.agentFailed(message, suggestions);
    }
    
    /**
     * 检查 Agent 是否可用
     */
    private boolean isAgentAvailable() {
        return agentEnabled && agent != null && agent.isEnabled();
    }
    
    /**
     * 获取值（支持多种 key 格式）
     */
    private Object getValue(Map<String, Object> data, String camelName, String columnName) {
        // 优先使用驼峰格式
        if (data.containsKey(camelName)) {
            return data.get(camelName);
        }
        // 其次使用原始列名
        if (data.containsKey(columnName)) {
            return data.get(columnName);
        }
        return null;
    }
    
    /**
     * 格式化错误信息
     */
    private String formatError(String columnName, ConversionResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("字段 '").append(columnName).append("': ").append(result.getErrorMessage());
        
        if (result.getSuggestions() != null && !result.getSuggestions().isEmpty()) {
            sb.append("。建议: ");
            sb.append(String.join("; ", result.getSuggestions()));
        }
        
        return sb.toString();
    }
    
    /**
     * 下划线转驼峰
     */
    private String toCamelCase(String name) {
        if (name == null) return null;
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }
}