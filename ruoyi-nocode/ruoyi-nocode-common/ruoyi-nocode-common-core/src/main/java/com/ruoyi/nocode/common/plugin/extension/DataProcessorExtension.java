package com.ruoyi.nocode.common.plugin.extension;

import java.util.Map;

/**
 * 数据处理扩展点
 * 
 * 用于扩展数据处理能力
 * 插件可以通过此扩展点实现自定义数据处理逻辑
 * 
 * @author ruoyi-nocode
 */
public interface DataProcessorExtension extends PluginExtensionPoint {

    /**
     * 获取处理器支持的类型
     *
     * @return 类型标识
     */
    String getProcessorType();

    /**
     * 处理数据
     *
     * @param data      输入数据
     * @param context   处理上下文
     * @return 处理结果
     */
    Object process(Object data, Map<String, Object> context);

    /**
     * 验证数据
     *
     * @param data      输入数据
     * @param context   验证上下文
     * @return 验证结果
     */
    default ValidationResult validate(Object data, Map<String, Object> context) {
        return ValidationResult.success();
    }

    /**
     * 数据转换
     *
     * @param data      输入数据
     * @param targetClass 目标类型
     * @param context   转换上下文
     * @return 转换结果
     */
    default Object transform(Object data, Class<?> targetClass, Map<String, Object> context) {
        return data;
    }

    /**
     * 验证结果
     */
    class ValidationResult {
        private final boolean success;
        private final String message;
        private final String field;

        public ValidationResult(boolean success, String message, String field) {
            this.success = success;
            this.message = message;
            this.field = field;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message, null);
        }

        public static ValidationResult failure(String field, String message) {
            return new ValidationResult(false, message, field);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getField() {
            return field;
        }
    }
}
