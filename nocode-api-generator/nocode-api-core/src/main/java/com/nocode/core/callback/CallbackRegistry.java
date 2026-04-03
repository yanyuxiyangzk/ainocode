package com.nocode.core.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 回调组件注册中心
 * 管理所有 CallbackComponent 实现，按表分类
 */
@Slf4j
@Component
public class CallbackRegistry {

    /**
     * 全局组件列表（对所有表生效）
     */
    private final List<CallbackComponent> globalCallbacks = new ArrayList<>();

    /**
     * 表级别组件映射：表名 -> 组件列表
     */
    private final Map<String, List<CallbackComponent>> tableCallbacks = new ConcurrentHashMap<>();

    /**
     * 自动注入所有 CallbackComponent 实现
     */
    @Autowired(required = false)
    private List<CallbackComponent> injectedCallbacks;

    @PostConstruct
    public void init() {
        if (injectedCallbacks != null) {
            for (CallbackComponent callback : injectedCallbacks) {
                register(callback);
            }
        }
        log.info("CallbackRegistry 初始化完成，已注册 {} 个全局组件，{} 个表级别组件",
                globalCallbacks.size(), tableCallbacks.size());
    }

    /**
     * 注册回调组件
     * @param callback 回调组件实例
     */
    public void register(CallbackComponent callback) {
        Set<String> targetTables = callback.getTargetTables();

        if (targetTables == null || targetTables.isEmpty()) {
            // 全局组件
            globalCallbacks.add(callback);
            log.debug("注册全局回调组件: {}", callback.getName());
        } else {
            // 表级别组件
            for (String tableName : targetTables) {
                tableCallbacks.computeIfAbsent(tableName.toLowerCase(), k -> new ArrayList<>())
                        .add(callback);
            }
            log.debug("注册表级别回调组件: {} -> {}", callback.getName(), targetTables);
        }
    }

    /**
     * 获取全局回调组件列表
     * @return 全局组件列表（不可变）
     */
    public List<CallbackComponent> getGlobalCallbacks() {
        return Collections.unmodifiableList(globalCallbacks);
    }

    /**
     * 获取指定表的回调组件列表
     * @param tableName 表名
     * @return 该表的回调组件列表（不可变）
     */
    public List<CallbackComponent> getTableCallbacks(String tableName) {
        if (tableName == null) {
            return Collections.emptyList();
        }
        List<CallbackComponent> callbacks = tableCallbacks.get(tableName.toLowerCase());
        return callbacks != null ? Collections.unmodifiableList(callbacks) : Collections.emptyList();
    }

    /**
     * 获取指定表的所有适用回调组件（全局 + 表级别）
     * @param tableName 表名
     * @return 所有适用的回调组件列表
     */
    public List<CallbackComponent> getAllApplicableCallbacks(String tableName) {
        List<CallbackComponent> result = new ArrayList<>(globalCallbacks);
        result.addAll(getTableCallbacks(tableName));
        return result;
    }
}
