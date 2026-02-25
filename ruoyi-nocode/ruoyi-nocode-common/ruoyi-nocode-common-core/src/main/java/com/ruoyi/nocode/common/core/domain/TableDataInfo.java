package com.ruoyi.nocode.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 表格数据响应
 * 
 * @author ruoyi-nocode
 */
@Data
@NoArgsConstructor
public class TableDataInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<?> rows;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }

    /**
     * 成功返回
     */
    public static TableDataInfo success() {
        return new TableDataInfo(null, 0);
    }

    /**
     * 成功返回（带数据）
     */
    public static TableDataInfo success(List<?> list) {
        return success(list, list.size());
    }

    /**
     * 成功返回（带数据总数）
     */
    public static TableDataInfo success(List<?> list, int total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
    }

    /**
     * 失败返回
     */
    public static TableDataInfo error() {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(500);
        rspData.setMsg("查询失败");
        return rspData;
    }

    /**
     * 失败返回（带消息）
     */
    public static TableDataInfo error(String msg) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(500);
        rspData.setMsg(msg);
        return rspData;
    }
}
