package com.ruoyi.nocode.common.core.codegen;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 表元数据
 *
 * @author ruoyi
 */
@Data
public class TableMetaData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 实体的类名（首字母大写）
     */
    private String entityName;

    /**
     * 实体的变量名（首字母小写）
     */
    private String variableName;

    /**
     * 包名（如：com.ruoyi.system）
     */
    private String packageName;

    /**
     * 模块名（如：user）
     */
    private String moduleName;

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
     * 列元数据列表
     */
    private List<ColumnMetaData> columns = new ArrayList<>();

    /**
     * 主键列
     */
    private ColumnMetaData primaryKeyColumn;

    /**
     * 业务主键列（非自增主键）
     */
    private ColumnMetaData bizKeyColumn;

    /**
     * 模板类型：CRUD, TREE, MASTER_DETAIL
     */
    private String templateType = "CRUD";

    /**
     * 是否移除表前缀
     */
    private boolean removeTablePrefix = true;

    /**
     * 表前缀
     */
    private String tablePrefix = "";

    /**
     * 基础路径
     */
    private String basePath = "";

    /**
     * 获取主键的Java类型
     */
    public String getPrimaryKeyJavaType() {
        if (primaryKeyColumn != null) {
            return primaryKeyColumn.getJavaType();
        }
        return "Long";
    }

    /**
     * 获取主键的字段名（驼峰）
     */
    public String getPrimaryKeyFieldName() {
        if (primaryKeyColumn != null) {
            return primaryKeyColumn.getFieldName();
        }
        return "id";
    }

    /**
     * 获取主键的JdbcType
     */
    public String getPrimaryKeyJdbcType() {
        if (primaryKeyColumn != null) {
            return primaryKeyColumn.getJdbcType();
        }
        return "BIGINT";
    }

    /**
     * 获取查询条件字段列表
     */
    public List<ColumnMetaData> getQueryColumns() {
        List<ColumnMetaData> queryColumns = new ArrayList<>();
        for (ColumnMetaData column : columns) {
            // 排除主键、自增、创建时间、更新时间等
            if (!column.isPrimaryKey() && !"create_time".equalsIgnoreCase(column.getColumnName())
                    && !"update_time".equalsIgnoreCase(column.getColumnName())
                    && !"create_by".equalsIgnoreCase(column.getColumnName())
                    && !"update_by".equalsIgnoreCase(column.getColumnName())) {
                queryColumns.add(column);
            }
        }
        return queryColumns;
    }

    /**
     * 获取需要验证的字段列表
     */
    public List<ColumnMetaData> getValidateColumns() {
        List<ColumnMetaData> validateColumns = new ArrayList<>();
        for (ColumnMetaData column : columns) {
            if (!column.isPrimaryKey() && !"create_time".equalsIgnoreCase(column.getColumnName())
                    && !"update_time".equalsIgnoreCase(column.getColumnName())
                    && !"create_by".equalsIgnoreCase(column.getColumnName())
                    && !"update_by".equalsIgnoreCase(column.getColumnName())) {
                validateColumns.add(column);
            }
        }
        return validateColumns;
    }

    /**
     * 是否包含字典字段
     */
    public boolean hasDictField() {
        for (ColumnMetaData column : columns) {
            if (column.isDictField()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字典字段列表
     */
    public List<ColumnMetaData> getDictColumns() {
        List<ColumnMetaData> dictColumns = new ArrayList<>();
        for (ColumnMetaData column : columns) {
            if (column.isDictField()) {
                dictColumns.add(column);
            }
        }
        return dictColumns;
    }
}
