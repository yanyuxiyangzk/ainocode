package com.nocode.core.entity;

import lombok.Data;

/**
 * 外键关系信息
 */
@Data
public class ForeignKeyInfo {
    /** 外键约束名称 */
    private String fkName;
    /** 源表名（外键所在表） */
    private String tableName;
    /** 源列名（外键列） */
    private String columnName;
    /** 目标表名（被引用表） */
    private String referencedTableName;
    /** 目标列名（被引用列） */
    private String referencedColumnName;
}
