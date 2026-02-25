package com.nocode.core.entity;

import lombok.Data;

/**
 * 数据库字段信息
 */
@Data
public class ColumnInfo {
    /** 字段名 */
    private String name;
    /** 字段类型 */
    private String type;
    /** Java类型 */
    private String javaType;
    /** 字段注释 */
    private String comment;
    /** 是否主键 */
    private boolean primaryKey;
    /** 是否可为空 */
    private boolean nullable;
    /** 字段长度 */
    private Integer length;
    /** 精度 */
    private Integer precision;
    /** 小数位数 */
    private Integer scale;
    /** 默认值 */
    private String defaultValue;
    /** 是否自增 */
    private boolean autoIncrement;
}
