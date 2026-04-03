package com.nocode.core.ddl;

import com.nocode.core.entity.DatabaseType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DDL生成器 - 根据表单JSON配置自动生成建表SQL
 *
 * @author auto-dev
 * @since 2026-04-03
 */
@Slf4j
public class DdlGenerator {

    /**
     * 根据表单配置生成DDL
     *
     * @param formConfig 表单配置JSON
     * @param tableName 表名
     * @param tableComment 表注释
     * @param databaseType 数据库类型
     * @return DDL语句
     */
    public static String generateDdl(Map<String, Object> formConfig, String tableName, String tableComment, DatabaseType databaseType) {
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> components = getComponents(formConfig);

        // 生成主键字段
        String primaryKey = generatePrimaryKey(databaseType);
        columns.add(primaryKey);

        // 生成业务字段
        for (Map<String, Object> component : components) {
            String column = generateColumn(component, databaseType);
            if (column != null) {
                columns.add(column);
            }
        }

        // 生成审计字段
        columns.addAll(generateAuditColumns(databaseType));

        // 组装DDL
        return buildCreateTableSql(tableName, columns, tableComment, databaseType);
    }

    /**
     * 简化版本：根据表单配置生成DDL（表名从配置中获取）
     */
    public static String generateDdl(Map<String, Object> formConfig, DatabaseType databaseType) {
        String tableName = getTableName(formConfig);
        String tableComment = (String) formConfig.getOrDefault("formName", "");
        return generateDdl(formConfig, tableName, tableComment, databaseType);
    }

    /**
     * 获取表单配置中的表名
     */
    private static String getTableName(Map<String, Object> formConfig) {
        // 优先使用tableName字段
        Object tableName = formConfig.get("tableName");
        if (tableName != null && !tableName.toString().isEmpty()) {
            return tableName.toString();
        }

        // 其次使用formId字段，自动添加t_前缀
        Object formId = formConfig.get("formId");
        if (formId != null && !formId.toString().isEmpty()) {
            return "t_" + formId.toString();
        }

        // 默认表名
        return "t_dynamic_table";
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> getComponents(Map<String, Object> formConfig) {
        Object components = formConfig.get("components");
        if (components instanceof List) {
            return (List<Map<String, Object>>) components;
        }
        return new ArrayList<>();
    }

    /**
     * 生成主键字段
     */
    private static String generatePrimaryKey(DatabaseType databaseType) {
        switch (databaseType) {
            case MYSQL:
                return "id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID'";
            case POSTGRESQL:
                return "id BIGSERIAL PRIMARY KEY";
            case ORACLE:
                return "id NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY";
            case SQLSERVER:
                return "id BIGINT IDENTITY(1,1) PRIMARY KEY";
            default:
                return "id BIGINT PRIMARY KEY AUTO_INCREMENT";
        }
    }

    /**
     * 根据组件配置生成列定义
     */
    private static String generateColumn(Map<String, Object> component, DatabaseType databaseType) {
        String field = (String) component.get("field");
        String type = (String) component.getOrDefault("type", "input");
        String label = (String) component.getOrDefault("label", field);
        Boolean required = (Boolean) component.getOrDefault("required", false);

        if (field == null || field.isEmpty()) {
            return null;
        }

        // 根据组件类型确定SQL类型
        String sqlType = getSqlType(type, component);
        String nullable = required ? "NOT NULL" : "NULL";
        String comment = "COMMENT '" + escapeComment(label) + "'";

        // 处理默认值
        String defaultValue = getDefaultValue(type, component, databaseType);

        return field + " " + sqlType + " " + nullable + " " + defaultValue + " " + comment;
    }

    /**
     * 根据组件类型获取SQL类型
     */
    private static String getSqlType(String componentType, Map<String, Object> component) {
        switch (componentType) {
            case "number":
            case "input-number":
                return "BIGINT";
            case "textarea":
                return "TEXT";
            case "select":
            case "radio":
            case "checkbox":
                return "VARCHAR(100)";
            case "date":
                return "DATE";
            case "datetime":
            case "time":
                return "DATETIME";
            case "switch":
            case "checkbox":
                return "TINYINT(1)";
            case "slider":
                return "INT";
            case "cascader":
            case "tree-select":
                return "VARCHAR(500)";
            case "upload":
            case "image-upload":
                return "VARCHAR(1000)";
            case "editor":
            case "rich-text":
                return "LONGTEXT";
            default:
                // input 和其他默认类型
                Object maxLength = component.get("maxLength");
                if (maxLength != null) {
                    int len = Integer.parseInt(maxLength.toString());
                    if (len > 500) {
                        return "TEXT";
                    }
                    return "VARCHAR(" + len + ")";
                }
                return "VARCHAR(255)";
        }
    }

    /**
     * 获取默认值
     */
    private static String getDefaultValue(String type, Map<String, Object> component, DatabaseType databaseType) {
        Object defaultVal = component.get("defaultValue");
        if (defaultVal != null && !defaultVal.toString().isEmpty()) {
            String val = defaultVal.toString();
            // 字符串需要加引号
            if ("input".equals(type) || "textarea".equals(type) || "select".equals(type)) {
                return "DEFAULT '" + escapeString(val) + "'";
            }
            return "DEFAULT " + val;
        }

        // 根据类型设置合理的默认值
        switch (type) {
            case "switch":
                if (databaseType == DatabaseType.POSTGRESQL) {
                    return "DEFAULT false";
                }
                return "DEFAULT 0";
            case "date":
                if (databaseType == DatabaseType.ORACLE) {
                    return "DEFAULT SYSDATE";
                }
                return "DEFAULT CURRENT_DATE";
            case "datetime":
                if (databaseType == DatabaseType.ORACLE) {
                    return "DEFAULT SYSTIMESTAMP";
                }
                return "DEFAULT CURRENT_TIMESTAMP";
            default:
                return "";
        }
    }

    /**
     * 生成审计字段
     */
    private static List<String> generateAuditColumns(DatabaseType databaseType) {
        List<String> columns = new ArrayList<>();

        String createBy = "create_by VARCHAR(64) DEFAULT '' COMMENT '创建者'";
        String updateBy = "update_by VARCHAR(64) DEFAULT '' COMMENT '更新者'";
        String remark = "remark VARCHAR(500) COMMENT '备注'";

        switch (databaseType) {
            case MYSQL:
                columns.add("create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'");
                columns.add("update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'");
                break;
            case POSTGRESQL:
                columns.add("create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
                columns.add("update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
                break;
            case ORACLE:
                columns.add("create_time DATE DEFAULT SYSDATE");
                columns.add("update_time DATE DEFAULT SYSDATE");
                break;
            case SQLSERVER:
                columns.add("create_time DATETIME DEFAULT GETDATE()");
                columns.add("update_time DATETIME DEFAULT GETDATE()");
                break;
            default:
                columns.add("create_time DATETIME DEFAULT CURRENT_TIMESTAMP");
                columns.add("update_time DATETIME DEFAULT CURRENT_TIMESTAMP");
        }

        columns.add(createBy);
        columns.add(updateBy);
        columns.add(remark);

        return columns;
    }

    /**
     * 构建CREATE TABLE SQL
     */
    private static String buildCreateTableSql(String tableName, List<String> columns, String tableComment, DatabaseType databaseType) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (\n");

        for (int i = 0; i < columns.size(); i++) {
            sql.append("  ").append(columns.get(i));
            if (i < columns.size() - 1) {
                sql.append(",");
            }
            sql.append("\n");
        }

        sql.append(")");

        // MySQL表选项
        if (databaseType == DatabaseType.MYSQL) {
            sql.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        }

        // 表注释
        if (tableComment != null && !tableComment.isEmpty()) {
            sql.append(" COMMENT='").append(escapeComment(tableComment)).append("'");
        }

        return sql.toString();
    }

    /**
     * 转义注释中的特殊字符
     */
    private static String escapeComment(String comment) {
        if (comment == null) {
            return "";
        }
        return comment.replace("'", "''");
    }

    /**
     * 转义字符串中的单引号
     */
    private static String escapeString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("'", "''");
    }

    /**
     * 生成DROP TABLE IF EXISTS语句
     */
    public static String generateDropTable(String tableName, DatabaseType databaseType) {
        switch (databaseType) {
            case MYSQL:
            case POSTGRESQL:
                return "DROP TABLE IF EXISTS " + tableName;
            case ORACLE:
            case SQLSERVER:
                return "IF OBJECT_ID('" + tableName + "', 'U') IS NOT NULL DROP TABLE " + tableName;
            default:
                return "DROP TABLE IF EXISTS " + tableName;
        }
    }

    /**
     * 生成添加字段的ALTER TABLE语句
     */
    public static String generateAddColumn(String tableName, Map<String, Object> component, DatabaseType databaseType) {
        String column = generateColumn(component, databaseType);
        if (column == null) {
            return null;
        }

        // 提取列定义部分（去掉字段名）
        String[] parts = column.split(" ", 2);
        if (parts.length < 2) {
            return null;
        }

        switch (databaseType) {
            case MYSQL:
                return "ALTER TABLE " + tableName + " ADD COLUMN " + column;
            case POSTGRESQL:
                return "ALTER TABLE " + tableName + " ADD COLUMN " + column;
            case ORACLE:
                return "ALTER TABLE " + tableName + " ADD (" + column + ")";
            case SQLSERVER:
                return "ALTER TABLE " + tableName + " ADD " + column;
            default:
                return "ALTER TABLE " + tableName + " ADD COLUMN " + column;
        }
    }
}
