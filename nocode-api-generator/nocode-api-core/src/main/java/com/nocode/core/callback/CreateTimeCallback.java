package com.nocode.core.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 时间戳自动填充回调
 * 自动给 create_time, update_time 等字段赋当前时间
 * 全局生效（对所有表生效）
 */
@Slf4j
@Component
public class CreateTimeCallback implements CallbackComponent {

    /**
     * 需要自动填充的字段名
     */
    private static final String[] TIME_FIELDS = {
            "create_time",
            "update_time",
            "createTime",
            "updateTime",
            "created_at",
            "updated_at",
            "createdAt",
            "updatedAt"
    };

    @Override
    public String getName() {
        return "CreateTimeCallback";
    }

    @Override
    public Set<String> getTargetTables() {
        // 全局组件 - 返回 null 表示对所有表生效
        return null;
    }

    @Override
    public Map<String, Object> beforeInsert(CallbackContext context) {
        Map<String, Object> result = new HashMap<>();
        Object dataObj = context.getData();
        if (!(dataObj instanceof Map)) {
            return result;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) dataObj;

        if (data == null) {
            return result;
        }

        LocalDateTime now = LocalDateTime.now();

        for (String field : TIME_FIELDS) {
            // 检查字段是否存在且当前值为空
            if (data.containsKey(field)) {
                Object currentValue = data.get(field);
                if (currentValue == null || (currentValue instanceof String && ((String) currentValue).isEmpty())) {
                    // 填充当前时间
                    if (isSqlTimestampField(field, context)) {
                        result.put(field, Timestamp.valueOf(now));
                    } else {
                        result.put(field, now);
                    }
                    log.debug("自动填充字段 {} 为当前时间", field);
                }
            }
        }

        return result;
    }

    @Override
    public Map<String, Object> beforeUpdate(CallbackContext context) {
        Map<String, Object> result = new HashMap<>();
        Object dataObj = context.getData();
        if (!(dataObj instanceof Map)) {
            return result;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) dataObj;

        if (data == null) {
            return result;
        }

        LocalDateTime now = LocalDateTime.now();

        // 更新时只处理 update_time 相关字段
        for (String field : TIME_FIELDS) {
            if (isUpdateTimeField(field)) {
                if (data.containsKey(field)) {
                    Object currentValue = data.get(field);
                    if (currentValue == null || (currentValue instanceof String && ((String) currentValue).isEmpty())) {
                        if (isSqlTimestampField(field, context)) {
                            result.put(field, Timestamp.valueOf(now));
                        } else {
                            result.put(field, now);
                        }
                        log.debug("自动填充更新字段 {} 为当前时间", field);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 检查是否为更新时间字段
     */
    private boolean isUpdateTimeField(String field) {
        return field.toLowerCase().contains("update") ||
               field.toLowerCase().contains("modified") ||
               field.equalsIgnoreCase("last_modified");
    }

    /**
     * 检查是否需要使用 SQL Timestamp 类型
     * PostgreSQL 等数据库对 java.sql.Timestamp 有更好的支持
     */
    private boolean isSqlTimestampField(String field, CallbackContext context) {
        // 根据数据库类型判断，这里简单处理
        // 实际项目中可以从 context 获取更多信息
        String datasourceName = context.getDatasourceName();
        if (datasourceName != null && datasourceName.toLowerCase().contains("postgres")) {
            return true;
        }
        return false;
    }
}
