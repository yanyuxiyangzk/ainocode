package com.nocode.core.entity;

import lombok.Data;

import java.util.List;

/**
 * 数据库表信息
 */
@Data
public class TableInfo {
    /** 表名 */
    private String tableName;
    /** 表注释 */
    private String comment;
    /** 字段列表 */
    private List<ColumnInfo> columns;
    /** 主键字段名 */
    private String primaryKeyColumn;
    /** 主键Java类型 */
    private String primaryKeyType;
    /** 序号名称 */
    private String snakeName;
    /** 驼峰名称 */
    private String camelName;
    /** 关联的数据源配置 */
    private DatasourceConfig datasourceConfig;
}
