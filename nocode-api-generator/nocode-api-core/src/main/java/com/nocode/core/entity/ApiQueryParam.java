package com.nocode.core.entity;

import lombok.Data;

/**
 * API查询参数
 */
@Data
public class ApiQueryParam {
    /** 页码，从1开始 */
    private Integer page = 1;
    /** 每页大小 */
    private Integer size = 10;
    /** 排序字段 */
    private String orderBy;
    /** 排序方向：asc/desc */
    private String orderDirection = "asc";
    /** 查询条件 */
    private String where;
    /** 查询参数值 */
    private Object[] params;
    /** 返回字段 */
    private String[] fields;
}
