package com.ruoyi.nocode.common.plugin.extension;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.Map;

/**
 * 零代码平台插件基类
 * 
 * 所有插件都应该继承此类
 * 提供统一的插件生命周期管理
 * 
 * @author ruoyi-nocode
 */
public abstract class NocodePlugin extends Plugin {

    /**
     * 插件初始化上下文
     */
    protected Map<String, Object> context;

    public NocodePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * 插件启动
     */
    @Override
    public void start() {
        log.info("插件启动: {} ({})", getPluginName(), getPluginVersion());
        doStart();
    }

    /**
     * 插件停止
     */
    @Override
    public void stop() {
        log.info("插件停止: {} ({})", getPluginName(), getPluginVersion());
        doStop();
    }

    /**
     * 插件删除
     */
    @Override
    public void delete() {
        log.info("插件删除: {} ({})", getPluginName(), getPluginVersion());
        doDelete();
    }

    /**
     * 子类实现的启动逻辑
     */
    protected abstract void doStart();

    /**
     * 子类实现的停止逻辑
     */
    protected void doStop() {
        // 默认空实现
    }

    /**
     * 子类实现的删除逻辑
     */
    protected void doDelete() {
        // 默认空实现
    }

    /**
     * 获取插件名称
     */
    public String getPluginName() {
        return wrapper.getPluginId();
    }

    /**
     * 获取插件版本
     */
    public String getPluginVersion() {
        return wrapper.getDescriptor().getVersion();
    }

    /**
     * 获取插件描述
     */
    public String getPluginDescription() {
        return wrapper.getDescriptor().getPluginDescription();
    }

    /**
     * 获取插件提供者
     */
    public String getPluginProvider() {
        return wrapper.getDescriptor().getProvider();
    }

    /**
     * 设置插件上下文
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * 获取插件上下文
     */
    public Map<String, Object> getContext() {
        return context;
    }
}
