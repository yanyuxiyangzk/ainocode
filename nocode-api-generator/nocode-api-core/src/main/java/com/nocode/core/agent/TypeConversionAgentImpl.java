package com.nocode.core.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nocode.core.orchestrator.ConversionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换 Agent 实现
 * 通过调用 LLM 实现智能类型转换
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "nocode.agent.enabled", havingValue = "true", matchIfMissing = false)
public class TypeConversionAgentImpl implements TypeConversionAgent {
    
    @Value("${nocode.agent.llm-endpoint:http://localhost:11434/api/generate}")
    private String llmEndpoint;
    
    @Value("${nocode.agent.model:qwen2.5:7b}")
    private String model;
    
    @Value("${nocode.agent.timeout:5000}")
    private int timeout;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Override
    public String getName() {
        return "TypeConversionAgentImpl";
    }
    
    @Override
    public ConversionResult handle(AgentContext context) {
        log.info("Agent 开始处理字段: {} (类型: {} -> {})", 
                context.getFieldName(), context.getInputValueType(), context.getJavaType());
        
        // 如果没有配置 RestTemplate，返回失败
        if (restTemplate == null) {
            log.warn("RestTemplate 未配置，Agent 无法工作");
            return buildFallbackResult(context);
        }
        
        try {
            // 构建 Prompt
            String prompt = buildPrompt(context);
            log.debug("Agent Prompt:\n{}", prompt);
            
            // 调用 LLM
            String response = callLLM(prompt);
            log.debug("Agent Response:\n{}", response);
            
            // 解析响应
            return parseResponse(response, context);
            
        } catch (Exception e) {
            log.error("Agent 处理失败: {}", e.getMessage(), e);
            return buildFallbackResult(context);
        }
    }
    
    /**
     * 构建 Prompt
     */
    private String buildPrompt(AgentContext ctx) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个数据类型转换专家。分析以下情况并给出处理建议。\n\n");
        
        prompt.append("## 字段信息\n");
        prompt.append("- 字段名: ").append(ctx.getFieldName()).append("\n");
        prompt.append("- 数据库类型: ").append(ctx.getDbType() != null ? ctx.getDbType() : "未知").append("\n");
        prompt.append("- Java类型: ").append(ctx.getJavaType()).append("\n");
        prompt.append("- 是否主键: ").append(ctx.isPrimaryKey() ? "是" : "否").append("\n");
        prompt.append("- 是否自增: ").append(ctx.isAutoIncrement() ? "是" : "否").append("\n");
        prompt.append("- 是否可空: ").append(ctx.isNullable() ? "是" : "否").append("\n");
        if (ctx.getLength() != null) {
            prompt.append("- 字段长度: ").append(ctx.getLength()).append("\n");
        }
        
        prompt.append("\n## 输入数据\n");
        prompt.append("- 输入值: ").append(ctx.getInputValueString()).append("\n");
        prompt.append("- 输入值类型: ").append(ctx.getInputValueType()).append("\n");
        
        if (ctx.getSkillError() != null) {
            prompt.append("\n## Skill 转换失败原因\n");
            prompt.append(ctx.getSkillError()).append("\n");
        }
        
        prompt.append("\n## 分析要求\n");
        prompt.append("1. 分析输入值的实际含义\n");
        prompt.append("2. 判断能否转换为目标类型\n");
        prompt.append("3. 如果能转换，给出转换后的值\n");
        prompt.append("4. 如果不能转换，给出用户友好的错误提示\n");
        prompt.append("5. 给出解决方案建议（如修改表结构、使用其他值等）\n");
        
        prompt.append("\n## 输出格式（必须是有效的 JSON）\n");
        prompt.append("```json\n");
        prompt.append("{\n");
        prompt.append("  \"canConvert\": true或false,\n");
        prompt.append("  \"convertedValue\": \"转换后的值（字符串格式）\",\n");
        prompt.append("  \"userMessage\": \"给用户的友好错误提示\",\n");
        prompt.append("  \"suggestions\": [\"建议1\", \"建议2\"]\n");
        prompt.append("}\n");
        prompt.append("```\n");
        prompt.append("\n请只输出 JSON，不要输出其他内容。");
        
        return prompt.toString();
    }
    
    /**
     * 调用 LLM
     */
    private String callLLM(String prompt) {
        // Ollama API 格式
        JSONObject request = new JSONObject();
        request.put("model", model);
        request.put("prompt", prompt);
        request.put("stream", false);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> entity = new HttpEntity<>(request.toJSONString(), headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    llmEndpoint, 
                    HttpMethod.POST, 
                    entity, 
                    String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 解析 Ollama 响应
                JSONObject root = JSON.parseObject(response.getBody());
                return root.getString("response");
            }
        } catch (Exception e) {
            log.error("调用 LLM 失败: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 解析 LLM 响应
     */
    private ConversionResult parseResponse(String response, AgentContext context) {
        if (response == null || response.isEmpty()) {
            return buildFallbackResult(context);
        }
        
        try {
            // 提取 JSON 部分
            String json = extractJson(response);
            if (json == null) {
                log.warn("无法从响应中提取 JSON: {}", response);
                return buildFallbackResult(context);
            }
            
            JSONObject root = JSON.parseObject(json);
            
            boolean canConvert = root.getBooleanValue("canConvert");
            String userMessage = root.getString("userMessage");
            List<String> suggestions = new ArrayList<>();
            JSONArray suggestionsArray = root.getJSONArray("suggestions");
            if (suggestionsArray != null) {
                for (int i = 0; i < suggestionsArray.size(); i++) {
                    suggestions.add(suggestionsArray.getString(i));
                }
            }
            
            if (canConvert) {
                String convertedValueStr = root.getString("convertedValue");
                Object convertedValue = convertToType(convertedValueStr, context.getJavaType());
                return ConversionResult.success(convertedValue);
            }
            
            return ConversionResult.agentFailed(userMessage, suggestions);
            
        } catch (Exception e) {
            log.error("解析 LLM 响应失败: {}", e.getMessage());
            return buildFallbackResult(context);
        }
    }
    
    /**
     * 从响应中提取 JSON
     */
    private String extractJson(String response) {
        // 尝试找到 ```json ... ``` 块
        int start = response.indexOf("```json");
        if (start >= 0) {
            start += 7;
            int end = response.indexOf("```", start);
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        
        // 尝试找到 { ... } 块
        start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        
        return null;
    }
    
    /**
     * 将字符串转换为目标类型
     */
    private Object convertToType(String value, String javaType) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        String type = javaType.toLowerCase();
        
        try {
            if (type.equals("long")) {
                return Long.parseLong(value);
            } else if (type.equals("int") || type.equals("integer")) {
                return Integer.parseInt(value);
            } else if (type.equals("double")) {
                return Double.parseDouble(value);
            } else if (type.equals("float")) {
                return Float.parseFloat(value);
            } else if (type.equals("boolean")) {
                return Boolean.parseBoolean(value);
            }
        } catch (NumberFormatException e) {
            log.debug("转换失败: {} -> {}", value, javaType);
        }
        
        // 默认返回字符串
        return value;
    }
    
    /**
     * 构建降级结果
     */
    private ConversionResult buildFallbackResult(AgentContext context) {
        String message = String.format(
                "字段 '%s' 类型转换失败：无法将 '%s' (%s) 转换为 %s 类型",
                context.getFieldName(),
                context.getInputValueString(),
                context.getInputValueType(),
                context.getJavaType()
        );
        
        List<String> suggestions = new ArrayList<>();
        
        if (context.isPrimaryKey()) {
            if (context.getJavaType().toLowerCase().contains("int") || 
                context.getJavaType().toLowerCase().contains("long")) {
                suggestions.add("该表主键为数值类型，请传入数值类型的主键值");
                suggestions.add("或者将表主键字段改为 varchar 类型以支持字符串主键");
            } else {
                suggestions.add("请提供有效的主键值");
            }
        } else {
            suggestions.add("请检查输入值格式是否正确");
            suggestions.add("或联系管理员检查表结构设计");
        }
        
        return ConversionResult.agentFailed(message, suggestions);
    }
}