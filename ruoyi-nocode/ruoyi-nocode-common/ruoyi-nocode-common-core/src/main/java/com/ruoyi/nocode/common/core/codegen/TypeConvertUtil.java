package com.ruoyi.nocode.common.core.codegen;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库类型到Java类型的转换工具
 *
 * @author ruoyi
 */
public class TypeConvertUtil {

    private static final Map<String, String> TYPE_MAPPING = new HashMap<>();
    private static final Map<String, String> JDBC_MAPPING = new HashMap<>();

    static {
        // MySQL 类型映射
        TYPE_MAPPING.put("varchar", "String");
        TYPE_MAPPING.put("char", "String");
        TYPE_MAPPING.put("text", "String");
        TYPE_MAPPING.put("tinytext", "String");
        TYPE_MAPPING.put("mediumtext", "String");
        TYPE_MAPPING.put("longtext", "String");
        TYPE_MAPPING.put("blob", "byte[]");
        TYPE_MAPPING.put("tinyblob", "byte[]");
        TYPE_MAPPING.put("mediumblob", "byte[]");
        TYPE_MAPPING.put("longblob", "byte[]");

        TYPE_MAPPING.put("bit", "Boolean");
        TYPE_MAPPING.put("tinyint", "Boolean");
        TYPE_MAPPING.put("boolean", "Boolean");
        TYPE_MAPPING.put("smallint", "Integer");
        TYPE_MAPPING.put("int", "Integer");
        TYPE_MAPPING.put("integer", "Integer");
        TYPE_MAPPING.put("bigint", "Long");
        TYPE_MAPPING.put("float", "Float");
        TYPE_MAPPING.put("double", "Double");
        TYPE_MAPPING.put("decimal", "BigDecimal");
        TYPE_MAPPING.put("numeric", "BigDecimal");
        TYPE_MAPPING.put("real", "BigDecimal");

        TYPE_MAPPING.put("date", "LocalDate");
        TYPE_MAPPING.put("datetime", "LocalDateTime");
        TYPE_MAPPING.put("timestamp", "LocalDateTime");
        TYPE_MAPPING.put("time", "LocalTime");
        TYPE_MAPPING.put("year", "Short");

        // JDBC类型映射
        JDBC_MAPPING.put("varchar", "VARCHAR");
        JDBC_MAPPING.put("char", "CHAR");
        JDBC_MAPPING.put("text", "VARCHAR");
        JDBC_MAPPING.put("tinytext", "VARCHAR");
        JDBC_MAPPING.put("mediumtext", "VARCHAR");
        JDBC_MAPPING.put("longtext", "VARCHAR");
        JDBC_MAPPING.put("blob", "BLOB");
        JDBC_MAPPING.put("tinyblob", "BLOB");
        JDBC_MAPPING.put("mediumblob", "BLOB");
        JDBC_MAPPING.put("longblob", "BLOB");

        JDBC_MAPPING.put("bit", "BIT");
        JDBC_MAPPING.put("tinyint", "TINYINT");
        JDBC_MAPPING.put("boolean", "BOOLEAN");
        JDBC_MAPPING.put("smallint", "SMALLINT");
        JDBC_MAPPING.put("int", "INTEGER");
        JDBC_MAPPING.put("integer", "INTEGER");
        JDBC_MAPPING.put("bigint", "BIGINT");
        JDBC_MAPPING.put("float", "FLOAT");
        JDBC_MAPPING.put("double", "DOUBLE");
        JDBC_MAPPING.put("decimal", "DECIMAL");
        JDBC_MAPPING.put("numeric", "DECIMAL");
        JDBC_MAPPING.put("real", "REAL");

        JDBC_MAPPING.put("date", "DATE");
        JDBC_MAPPING.put("datetime", "DATETIME");
        JDBC_MAPPING.put("timestamp", "TIMESTAMP");
        JDBC_MAPPING.put("time", "TIME");
        JDBC_MAPPING.put("year", "SMALLINT");
    }

    private TypeConvertUtil() {
    }

    /**
     * 将数据库类型转换为Java类型
     *
     * @param columnType 数据库列类型
     * @return Java类型
     */
    public static String toJavaType(String columnType) {
        if (columnType == null || columnType.isBlank()) {
            return "String";
        }

        String type = columnType.toLowerCase().trim();

        // 处理带长度修饰的类型，如 varchar(255)
        if (type.contains("(")) {
            type = type.substring(0, type.indexOf("("));
        }

        // 处理 unsigned 类型
        if (type.contains("unsigned")) {
            type = type.replace("unsigned", "").trim();
        }

        String javaType = TYPE_MAPPING.get(type);
        return javaType != null ? javaType : "String";
    }

    /**
     * 将数据库类型转换为JDBC类型
     *
     * @param columnType 数据库列类型
     * @return JDBC类型
     */
    public static String toJdbcType(String columnType) {
        if (columnType == null || columnType.isBlank()) {
            return "VARCHAR";
        }

        String type = columnType.toLowerCase().trim();

        // 处理带长度修饰的类型
        if (type.contains("(")) {
            type = type.substring(0, type.indexOf("("));
        }

        // 处理 unsigned 类型
        if (type.contains("unsigned")) {
            type = type.replace("unsigned", "").trim();
        }

        String jdbcType = JDBC_MAPPING.get(type);
        return jdbcType != null ? jdbcType : "VARCHAR";
    }

    /**
     * 判断是否为字符串类型
     */
    public static boolean isStringType(String columnType) {
        String javaType = toJavaType(columnType);
        return "String".equals(javaType);
    }

    /**
     * 判断是否为数值类型
     */
    public static boolean isNumericType(String columnType) {
        String javaType = toJavaType(columnType);
        return "Integer".equals(javaType) || "Long".equals(javaType)
                || "Float".equals(javaType) || "Double".equals(javaType)
                || "BigDecimal".equals(javaType);
    }

    /**
     * 判断是否为日期类型
     */
    public static boolean isDateType(String columnType) {
        String javaType = toJavaType(columnType);
        return "LocalDate".equals(javaType) || "LocalDateTime".equals(javaType)
                || "LocalTime".equals(javaType);
    }

    /**
     * 判断是否为主键自增类型
     */
    public static boolean isAutoIncrement(String columnType, String extra) {
        if (extra != null && extra.toLowerCase().contains("auto_increment")) {
            return true;
        }
        String type = columnType.toLowerCase();
        return type.contains("int") || type.contains("bigint");
    }

    /**
     * 判断是否为主键列
     */
    public static boolean isPrimaryKey(String columnName, String columnKey) {
        return "pri".equalsIgnoreCase(columnKey) || "id".equalsIgnoreCase(columnName);
    }

    /**
     * 将表名转换为驼峰命名的类名
     *
     * @param tableName 表名
     * @param prefix    要移除的前缀
     * @return 类名
     */
    public static String toClassName(String tableName, String prefix) {
        if (tableName == null || tableName.isBlank()) {
            return "Unknown";
        }

        String name = tableName;

        // 移除前缀
        if (prefix != null && !prefix.isBlank()) {
            if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
                name = name.substring(prefix.length());
            }
        }

        // 转驼峰
        return toCamelCase(name);
    }

    /**
     * 将字符串转换为驼峰命名
     */
    public static String toCamelCase(String str) {
        if (str == null || str.isBlank()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_' || c == '-' || c == ' ') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                // 首字母大写
                if (result.length() == 0) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    /**
     * 将字符串转换为驼峰命名的变量名（首字母小写）
     */
    public static String toVariableName(String str) {
        String className = toCamelCase(str);
        if (className.isEmpty()) {
            return "unknown";
        }
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    /**
     * 将字符串转换为大写下划线命名
     */
    public static String toUpperUnderline(String str) {
        if (str == null || str.isBlank()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && result.length() > 0) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }
        return result.toString();
    }
}
