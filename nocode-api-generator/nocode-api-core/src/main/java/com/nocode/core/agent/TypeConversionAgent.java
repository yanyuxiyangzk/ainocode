package com.nocode.core.agent;

import com.nocode.core.orchestrator.ConversionResult;

/**
 * 类型转换 Agent 接口
 * 用于智能处理 Skill 无法处理的复杂场景
 */
public interface TypeConversionAgent {
    
    /**
     * 智能处理类型转换
     * @param context 上下文信息
     * @return 转换结果
     */
    ConversionResult handle(AgentContext context);
    
    /**
     * 是否启用 Agent
     * @return true 表示启用
     */
    default boolean isEnabled() {
        return true;
    }
    
    /**
     * Agent 名称
     * @return 名称
     */
    default String getName() {
        return "TypeConversionAgent";
    }
}
