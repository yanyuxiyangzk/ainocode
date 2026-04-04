package com.ruoyi.nocode.common.core.codegen.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 列信息
 *
 * @author ruoyi
 */
@Data
public class ColumnInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列类型（数据库类型）
     */
    private String columnType;

    /**
     * Java类型
     */
    private String javaType;

    /**
     * Java字段名（下划线转驼峰）
     */
    private String javaFieldName;

    /**
     * 是否主键
     */
    private boolean primaryKey;

    /**
     * 是否自增
     */
    private boolean autoIncrement;

    /**
     * 是否可为空
     */
    private boolean nullable;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 注释
     */
    private String comment;

    /**
     * 字段长度
     */
    private int columnSize;

    /**
     * 小数位数
     */
    private int decimalDigits;

    /**
     * 是否为逻辑删除字段
     */
    private boolean logicDeleteField;

    /**
     * 是否为租户字段
     */
    private boolean tenantField;

    /**
     * 枚举类名（如果有）
     */
    private String enumClassName;

    /**
     * 字典类型编码（如果有）
     */
    private String dictTypeCode;

    /**
     * 是否必填（用于表单验证）
     */
    private boolean required;

    /**
     * 展示类型：input/select/date/datetime/textarea/radio/checkbox
     */
    private String displayType;

    /**
     * 导出时是否忽略
     */
    private boolean exportIgnore;

    /**
     * 导入时是否忽略
     */
    private boolean importIgnore;

    /**
     * 获取驼峰命名的Java字段名
     */
    public String toCamelCase() {
        return toCamelCase(this.columnName);
    }

    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    /**
     * 获取首字母大写的Java字段名
     */
    public String toCapitalizedFieldName() {
        if (javaFieldName == null || javaFieldName.isEmpty()) {
            return javaFieldName;
        }
        return Character.toUpperCase(javaFieldName.charAt(0)) + javaFieldName.substring(1);
    }

    /**
     * 判断是否为String类型
     */
    public boolean isStringType() {
        return "String".equals(javaType) || "string".equalsIgnoreCase(javaType);
    }

    /**
     * 判断是否为数字类型
     */
    public boolean isNumericType() {
        return "Integer".equals(javaType) ||
                "Long".equals(javaType) ||
                "Double".equals(javaType) ||
                "Float".equals(javaType) ||
                "BigDecimal".equals(javaType) ||
                "Short".equals(javaType) ||
                "Byte".equals(javaType);
    }

    /**
     * 判断是否为日期类型
     */
    public boolean isDateType() {
        return "Date".equals(javaType) ||
                "LocalDate".equals(javaType) ||
                "LocalDateTime".equals(javaType) ||
                "Timestamp".equals(javaType) ||
                "LocalTime".equals(javaType);
    }
}
