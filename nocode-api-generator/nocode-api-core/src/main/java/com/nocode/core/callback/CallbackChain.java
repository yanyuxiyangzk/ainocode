package com.nocode.core.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回调链执行器
 * 管理组件式回调和配置式字段赋值
 */
@Slf4j
@Component
public class CallbackChain {

    private CallbackRegistry callbackRegistry;

    @Autowired(required = false)
    private FieldAssignmentConfig fieldAssignmentConfig;

    public CallbackChain(CallbackRegistry callbackRegistry) {
        this.callbackRegistry = callbackRegistry;
    }

    @PostConstruct
    public void init() {
        log.info("CallbackChain 初始化完成");
    }

    // ==================== 组件式回调方法 ====================

    /**
     * 执行插入前回调
     * 先执行全局组件，再执行表专属组件，最后执行配置式字段赋值
     * @param context 回调上下文
     * @return 合并后的数据
     */
    public Map<String, Object> executeBeforeInsert(CallbackContext context) {
        Map<String, Object> result = new HashMap<>();
        List<CallbackComponent> callbacks = callbackRegistry.getAllApplicableCallbacks(context.getTableName());

        for (CallbackComponent callback : callbacks) {
            try {
                Map<String, Object> additionalData = callback.beforeInsert(context);
                if (additionalData != null && !additionalData.isEmpty()) {
                    result.putAll(additionalData);
                    log.debug("回调组件 {} 执行 beforeInsert，添加字段: {}", callback.getName(), additionalData.keySet());
                }
            } catch (Exception e) {
                log.warn("回调组件 {} 执行 beforeInsert 失败: {}", callback.getName(), e.getMessage());
            }
        }

        // 配置式字段赋值：在组件回调之后执行
        Object dataObj = context.getData();
        if (dataObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            applyConfigBasedAssignment(context.getTableName(), data, context.getUserContext());
            if (!data.isEmpty()) {
                result.putAll(data);
            }
        }

        return result;
    }

    /**
     * 执行更新前回调
     * 先执行全局组件，再执行表专属组件，最后执行配置式字段赋值
     * @param context 回调上下文
     * @return 合并后的数据
     */
    public Map<String, Object> executeBeforeUpdate(CallbackContext context) {
        Map<String, Object> result = new HashMap<>();
        List<CallbackComponent> callbacks = callbackRegistry.getAllApplicableCallbacks(context.getTableName());

        for (CallbackComponent callback : callbacks) {
            try {
                Map<String, Object> additionalData = callback.beforeUpdate(context);
                if (additionalData != null && !additionalData.isEmpty()) {
                    result.putAll(additionalData);
                    log.debug("回调组件 {} 执行 beforeUpdate，添加字段: {}", callback.getName(), additionalData.keySet());
                }
            } catch (Exception e) {
                log.warn("回调组件 {} 执行 beforeUpdate 失败: {}", callback.getName(), e.getMessage());
            }
        }

        // 配置式字段赋值：在组件回调之后执行
        Object dataObj = context.getData();
        if (dataObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            applyConfigBasedAssignment(context.getTableName(), data, context.getUserContext());
            if (!data.isEmpty()) {
                result.putAll(data);
            }
        }

        return result;
    }

    /**
     * 执行查询后回调
     * 先执行全局组件，再执行表专属组件，处理结果
     * @param context 回调上下文
     * @return 处理后的数据（List 或 Map，取决于查询类型）
     */
    @SuppressWarnings("unchecked")
    public Object executeAfterQuery(CallbackContext context) {
        List<CallbackComponent> callbacks = callbackRegistry.getAllApplicableCallbacks(context.getTableName());

        // 从上下文中获取查询结果数据
        Object dataObj = context.getData();
        Object result = dataObj;

        // 处理列表查询
        if (dataObj instanceof List) {
            List<Map<String, Object>> listResult = (List<Map<String, Object>>) dataObj;
            for (CallbackComponent callback : callbacks) {
                try {
                    listResult = callback.afterQuery(context, listResult);
                    log.debug("回调组件 {} 执行 afterQuery，处理 {} 条记录", callback.getName(),
                            listResult != null ? listResult.size() : 0);
                } catch (Exception e) {
                    log.warn("回调组件 {} 执行 afterQuery 失败: {}", callback.getName(), e.getMessage());
                }
            }
            result = listResult;
        } else if (dataObj instanceof Map) {
            // 处理单条查询 - 包装成列表再处理，然后返回单条
            Map<String, Object> mapResult = (Map<String, Object>) dataObj;
            List<Map<String, Object>> wrappedList = new java.util.ArrayList<>();
            wrappedList.add(mapResult);
            for (CallbackComponent callback : callbacks) {
                try {
                    List<Map<String, Object>> processed = callback.afterQuery(context, wrappedList);
                    if (processed != null && !processed.isEmpty()) {
                        wrappedList = processed;
                    }
                    log.debug("回调组件 {} 执行 QUERY_ONE afterQuery", callback.getName());
                } catch (Exception e) {
                    log.warn("回调组件 {} 执行 QUERY_ONE afterQuery 失败: {}", callback.getName(), e.getMessage());
                }
            }
            // 返回处理后的单条记录
            result = wrappedList.isEmpty() ? mapResult : wrappedList.get(0);
        }

        return result;
    }

    /**
     * 执行删除后回调
     * @param context 回调上下文
     */
    public void executeAfterDelete(CallbackContext context) {
        List<CallbackComponent> callbacks = callbackRegistry.getAllApplicableCallbacks(context.getTableName());

        for (CallbackComponent callback : callbacks) {
            try {
                callback.afterDelete(context);
                log.debug("回调组件 {} 执行 afterDelete", callback.getName());
            } catch (Exception e) {
                log.warn("回调组件 {} 执行 afterDelete 失败: {}", callback.getName(), e.getMessage());
            }
        }
    }

    // ==================== 配置式字段赋值方法 ====================

    /**
     * 在数据转换后应用配置式字段赋值
     * 配置式赋值在组件式回调之后执行
     *
     * @param tableName 表名
     * @param data 当前数据（会被直接修改）
     * @param userContext 用户上下文对象
     */
    public void applyConfigBasedAssignment(String tableName, Map<String, Object> data, Object userContext) {
        if (fieldAssignmentConfig == null || !fieldAssignmentConfig.isEnabled()) {
            return;
        }

        if (!fieldAssignmentConfig.hasTableAssignment(tableName)) {
            return;
        }

        Map<String, Object> resolvedFields = fieldAssignmentConfig.resolveFields(tableName, userContext);
        if (resolvedFields.isEmpty()) {
            return;
        }

        log.debug("应用配置式字段赋值到表 {}: {}", tableName, resolvedFields);

        // 将解析后的值合并到数据中
        for (Map.Entry<String, Object> entry : resolvedFields.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            // 只有当字段不存在或值为null时才赋值
            if (!data.containsKey(fieldName) || data.get(fieldName) == null) {
                data.put(fieldName, value);
                log.debug("字段赋值: {} = {}", fieldName, value);
            } else {
                log.debug("跳过字段 {}，已有值: {}", fieldName, data.get(fieldName));
            }
        }
    }

    /**
     * 执行完整的回调链
     *
     * @param tableName 表名
     * @param data 数据
     * @param userContext 用户上下文
     * @param stage 回调阶段（BEFORE/AFTER）
     */
    public void executeChain(String tableName, Map<String, Object> data, Object userContext, CallbackStage stage) {
        if (CallbackStage.BEFORE == stage) {
            // BEFORE 阶段：可以执行预处理
            log.debug("CallbackChain BEFORE 阶段: {}", tableName);
        } else if (CallbackStage.AFTER == stage) {
            // AFTER 阶段：执行配置式字段赋值
            log.debug("CallbackChain AFTER 阶段: {}", tableName);
            applyConfigBasedAssignment(tableName, data, userContext);
        }
    }

    /**
     * 回调阶段枚举
     */
    public enum CallbackStage {
        /** 数据处理前 */
        BEFORE,
        /** 数据处理后 */
        AFTER
    }
}
