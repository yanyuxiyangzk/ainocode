package com.nocode.core.callback;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 回调组件接口
 * 用于在数据库操作前后执行自定义逻辑
 */
public interface CallbackComponent {

    /**
     * 组件名称
     */
    String getName();

    /**
     * 插入前回调
     * @param context 回调上下文
     * @return 返回需要合并到数据的字段（可返回 null）
     */
    Map<String, Object> beforeInsert(CallbackContext context);

    /**
     * 更新前回调（可选）
     * @param context 回调上下文
     * @return 返回需要合并到数据的字段（可返回 null）
     */
    default Map<String, Object> beforeUpdate(CallbackContext context) {
        return null;
    }

    /**
     * 查询后回调（可选）
     * @param context 回调上下文
     * @param data 查询结果数据
     * @return 处理后的数据
     */
    default List<Map<String, Object>> afterQuery(CallbackContext context, List<Map<String, Object>> data) {
        return data;
    }

    /**
     * 删除后回调（可选）
     * @param context 回调上下文
     */
    default void afterDelete(CallbackContext context) {
    }

    /**
     * 目标表列表，null 表示全局组件（对所有表生效）
     * @return 目标表名集合，null 为全局组件
     */
    default Set<String> getTargetTables() {
        return null;
    }
}
