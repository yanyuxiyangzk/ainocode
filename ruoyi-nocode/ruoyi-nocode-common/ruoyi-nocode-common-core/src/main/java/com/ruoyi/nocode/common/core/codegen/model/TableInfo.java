package com.ruoyi.nocode.common.core.codegen.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 表信息
 *
 * @author ruoyi
 */
@Data
public class TableInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 表前缀（生成实体类时会去掉前缀）
     */
    private String tablePrefix;

    /**
     * 实体类名（首字母大写）
     */
    private String entityName;

    /**
     * 实体类描述
     */
    private String entityComment;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 功能名
     */
    private String functionName;

    /**
     * 作者
     */
    private String author;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 列信息列表
     */
    private List<ColumnInfo> columns = new ArrayList<>();

    /**
     * 主键列
     */
    private ColumnInfo primaryKey;

    /**
     * 主键Java类型
     */
    private String primaryKeyJavaType;

    /**
     * 主键Java字段名
     */
    private String primaryKeyFieldName;

    /**
     * 业务主键列（用于一般查询）
     */
    private ColumnInfo bizKey;

    /**
     * 是否使用逻辑删除
     */
    private boolean useLogicDelete;

    /**
     * 逻辑删除字段名
     */
    private String logicDeleteField;

    /**
     * 逻辑删除值
     */
    private String logicDeleteValue;

    /**
     * 未删除值
     */
    private String logicNotDeleteValue;

    /**
     * 是否租户隔离
     */
    private boolean useTenant;

    /**
     * 租户字段名
     */
    private String tenantField;

    /**
     * 关联查询表名
     */
    private String joinTableName;

    /**
     * 关联查询外键
     */
    private String joinForeignKey;

    /**
     * 树表父ID字段
     */
    private String treeParentField;

    /**
     * 树表名称字段
     */
    private String treeNameField;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 生成代码类型：single/all
     * single-生成单个文件，all-生成所有文件
     */
    private String generateType;

    /**
     * 前端路由路径
     */
    private String routePath;

    /**
     * Vue文件路径
     */
    private String vuePath;

    /**
     * 移除表前缀后的名称（用于生成类名等）
     */
    private String removalPrefixName;

    /**
     * 获取所有非主键列
     */
    public List<ColumnInfo> getNonPrimaryColumns() {
        return columns.stream()
                .filter(c -> !c.isPrimaryKey())
                .toList();
    }

    /**
     * 获取所有可编辑列（排除主键、逻辑删除、租户字段）
     */
    public List<ColumnInfo> getEditableColumns() {
        return columns.stream()
                .filter(c -> !c.isPrimaryKey())
                .filter(c -> !c.isLogicDeleteField())
                .filter(c -> !c.isTenantField())
                .filter(c -> !c.isExportIgnore())
                .toList();
    }

    /**
     * 获取查询列（排除文本类型的大字段）
     */
    public List<ColumnInfo> getQueryColumns() {
        return columns.stream()
                .filter(c -> !c.isPrimaryKey())
                .filter(c -> !c.isLogicDeleteField())
                .filter(c -> !c.isTenantField())
                .filter(c -> !("String".equals(c.getJavaType()) && c.getColumnSize() > 500))
                .toList();
    }

    /**
     * 获取导入列
     */
    public List<ColumnInfo> getImportColumns() {
        return columns.stream()
                .filter(c -> !c.isPrimaryKey())
                .filter(c -> !c.isImportIgnore())
                .filter(c -> c.isRequired())
                .toList();
    }

    /**
     * 获取导出列
     */
    public List<ColumnInfo> getExportColumns() {
        return columns.stream()
                .filter(c -> !c.isExportIgnore())
                .toList();
    }

    /**
     * 判断是否有日期类型列
     */
    public boolean hasDateColumn() {
        return columns.stream().anyMatch(ColumnInfo::isDateType);
    }

    /**
     * 判断是否有数字类型列
     */
    public boolean hasNumericColumn() {
        return columns.stream().anyMatch(ColumnInfo::isNumericType);
    }

    /**
     * 获取列名列表（逗号分隔）
     */
    public String getColumnNames() {
        return String.join(", ", columns.stream()
                .map(ColumnInfo::getColumnName)
                .toList());
    }

    /**
     * 获取Java字段名列表（逗号分隔）
     */
    public String getJavaFieldNames() {
        return String.join(", ", columns.stream()
                .map(ColumnInfo::getJavaFieldName)
                .toList());
    }
}
