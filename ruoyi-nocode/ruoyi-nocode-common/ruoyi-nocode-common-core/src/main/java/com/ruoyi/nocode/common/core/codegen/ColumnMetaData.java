package com.ruoyi.nocode.common.core.codegen;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 列元数据
 *
 * @author ruoyi
 */
@Data
public class ColumnMetaData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列类型（如：varchar, int, bigint, datetime）
     */
    private String columnType;

    /**
     * Java类型（如：String, Integer, Long, LocalDateTime）
     */
    private String javaType;

    /**
     * JDBC类型
     */
    private String jdbcType;

    /**
     * 列注释
     */
    private String columnComment;

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
     * 字段名称（驼峰命名）
     */
    private String fieldName;

    /**
     * 字段名称（大写下划线，如：USER_NAME）
     */
    private String fieldNameUpper;

    /**
     * 是否为字典字段
     */
    private boolean dictField;

    /**
     * 字典类型编码
     */
    private String dictType;

    /**
     * 获取MyBatis-Plus的字段注解
     */
    public String getMyBatisPlusFieldAnnotation() {
        StringBuilder annotation = new StringBuilder();

        if (primaryKey) {
            annotation.append("@TableId");
            if (autoIncrement) {
                annotation.append("(type = IdType.AUTO)");
            }
            annotation.append("\n    ");
        }

        annotation.append("@TableField(\"").append(columnName).append("\")");

        if (!nullable && !primaryKey) {
            annotation.append("\n    @NotNull");
        }

        return annotation.toString();
    }

    /**
     * 获取Lombok注解
     */
    public String getLombokAnnotations() {
        if ("String".equals(javaType)) {
            return "@TableField(fill = FieldFill.INSERT)\n    private String " + fieldName + ";";
        }
        return "private " + javaType + " " + fieldName + ";";
    }
}
