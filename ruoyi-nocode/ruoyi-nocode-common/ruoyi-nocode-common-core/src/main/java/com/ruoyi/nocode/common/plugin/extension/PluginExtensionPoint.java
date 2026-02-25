package com.ruoyi.nocode.common.plugin.extension;

import org.pf4j.ExtensionPoint;

import java.util.Map;

/**
 * 插件扩展点基类接口
 * 
 * 所有插件扩展点必须继承此接口
 * 
 * @author ruoyi-nocode
 */
public interface PluginExtensionPoint extends ExtensionPoint {

    /**
     * 获取扩展点名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 获取扩展点版本
     *
     * @return 版本
     */
    String getVersion();

    /**
     * 初始化扩展点
     *
     * @param context 初始化上下文
     */
    default void initialize(Map<String, Object> context) {
        // 默认空实现
    }

    /**
     * 销毁扩展点
     */
    default void destroy() {
        // 默认空实现
    }

    /**
     * 获取扩展点描述
     *
     * @return 描述
     */
    default String getDescription() {
        return "";
    }

    /**
     * 获取扩展点优先级
     *
     * @return 优先级 (数值越小优先级越高)
     */
    default int getPriority() {
        return 100;
    }
}
