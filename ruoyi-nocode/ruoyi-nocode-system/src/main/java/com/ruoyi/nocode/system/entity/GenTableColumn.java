package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 代码生成列实体
 * 
 * @author ruoyi-nocode
 */
@Data
@TableName("gen_table_column")
public class GenTableColumn implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 列ID
     */
    @TableId(value = "column_id", type = IdType.AUTO)
    private Long columnId;

    /**
     * 表ID
     */
    private Long tableId;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列描述
     */
    private String columnComment;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * Java类型
     */
    private String javaType;

    /**
     * Java字段名
     */
    private String javaField;

    /**
     * 是否主键（1是）
     */
    private String isPk;

    /**
     * 是否自增（1是）
     */
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    private String isQuery;

    /**
     * 查询方式
     */
    private String queryType;

    /**
     * 显示类型
     */
    private String htmlType;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 排序
     */
    private Integer sort;

    // ==================== 查询方式常量 ====================

    public static final String QUERY_EQ = "EQ";       // 等于
    public static final String QUERY_NE = "NE";       // 不等于
    public static final String QUERY_GT = "GT";       // 大于
    public static final String QUERY_GTE = "GTE";     // 大于等于
    public static final String QUERY_LT = "LT";       // 小于
    public static final String QUERY_LTE = "LTE";     // 小于等于
    public static final String QUERY_LIKE = "LIKE";   // 模糊
    public static final String QUERY_BETWEEN = "BETWEEN"; // 范围

    // ==================== 显示类型常量 ====================

    public static final String HTML_INPUT = "input";         // 输入框
    public static final String HTML_TEXTAREA = "textarea";   // 文本域
    public static final String HTML_SELECT = "select";       // 下拉框
    public static final String HTML_RADIO = "radio";         // 单选框
    public static final String HTML_CHECKBOX = "checkbox";   // 复选框
    public static final String HTML_DATE = "datetime";       // 日期
    public static final String HTML_IMAGE = "imageUpload";   // 图片上传
    public static final String HTML_FILE = "fileUpload";     // 文件上传
    public static final String HTML_EDITOR = "editor";       // 富文本

    // ==================== Java类型常量 ====================

    public static final String TYPE_STRING = "String";
    public static final String TYPE_LONG = "Long";
    public static final String TYPE_INTEGER = "Integer";
    public static final String TYPE_DOUBLE = "Double";
    public static final String TYPE_DECIMAL = "BigDecimal";
    public static final String TYPE_DATE = "Date";
    public static final String TYPE_BOOLEAN = "Boolean";
}
