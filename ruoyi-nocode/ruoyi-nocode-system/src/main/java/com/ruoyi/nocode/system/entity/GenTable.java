package com.ruoyi.nocode.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.nocode.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 代码生成表实体
 * 
 * @author ruoyi-nocode
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gen_table")
public class GenTable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表ID
     */
    @TableId(value = "table_id", type = IdType.AUTO)
    private Long tableId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 实体类名称
     */
    private String className;

    /**
     * 包名称
     */
    private String packageName;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 业务名称
     */
    private String businessName;

    /**
     * 功能名称
     */
    private String functionName;

    /**
     * 生成方式（0zip压缩包 1自定义路径）
     */
    private String genType;

    /**
     * 生成路径
     */
    private String genPath;

    /**
     * 主键列
     */
    private String pkColumn;

    /**
     * 树编码字段
     */
    private String treeCode;

    /**
     * 树父编码字段
     */
    private String treeParentCode;

    /**
     * 树名称字段
     */
    private String treeName;

    /**
     * 上级菜单ID
     */
    private Long parentMenuId;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 表字段列表
     */
    @TableField(exist = false)
    private java.util.List<GenTableColumn> columns;

    /**
     * 主键列信息
     */
    @TableField(exist = false)
    private GenTableColumn pkColumnObj;
}
