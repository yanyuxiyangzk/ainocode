package com.ruoyi.nocode.common.core.codegen.config;

/**
 * 数据库类型到Java类型的转换器
 *
 * @author ruoyi
 */
public class DbTypeConverter {

    /**
     * MySQL类型映射
     */
    private static final String[] MYSQL_STRING_TYPES = {
            "char", "varchar", "nvarchar", "tinytext", "text", "mediumtext", "longtext",
            "enum", "set"
    };

    private static final String[] MYSQL_NUMBER_TYPES = {
            "bit", "tinyint", "smallint", "mediumint", "int", "integer", "bigint",
            "float", "double", "decimal", "numeric", "real"
    };

    private static final String[] MYSQL_DATE_TYPES = {
            "date", "datetime", "timestamp", "time", "year"
    };

    private static final String[] MYSQL_BLOB_TYPES = {
            "bit", "tinyint", "smallint", "mediumint", "int", "bigint"
    };

    /**
     * PostgreSQL类型映射
     */
    private static final String[] PGSQL_STRING_TYPES = {
            "char", "varchar", "text", "character", "character varying"
    };

    private static final String[] PGSQL_NUMBER_TYPES = {
            "smallint", "integer", "bigint", "real", "double precision",
            "numeric", "decimal", "smallserial", "serial", "bigserial"
    };

    private static final String[] PGSQL_DATE_TYPES = {
            "date", "time", "timestamp", "timestamptz", "timetz", "interval"
    };

    /**
     * Oracle类型映射
     */
    private static final String[] ORACLE_STRING_TYPES = {
            "char", "varchar2", "nvarchar2", "clob", "nclob"
    };

    private static final String[] ORACLE_NUMBER_TYPES = {
            "number", "integer", "float", "binary_float", "binary_double"
    };

    private static final String[] ORACLE_DATE_TYPES = {
            "date", "timestamp", "timestamp with time zone", "timestamp with local time zone"
    };

    /**
     * SQLServer类型映射
     */
    private static final String[] SQLSERVER_STRING_TYPES = {
            "char", "varchar", "nchar", "nvarchar", "ntext", "text"
    };

    private static final String[] SQLSERVER_NUMBER_TYPES = {
            "bit", "tinyint", "smallint", "int", "bigint", "float", "real", "decimal", "numeric", "money"
    };

    private static final String[] SQLSERVER_DATE_TYPES = {
            "date", "datetime", "datetime2", "datetimeoffset", "smalldatetime", "time", "timestamp"
    };

    /**
     * 根据数据库类型和列大小获取Java类型
     */
    public static String toJavaType(String dbType, int columnSize, int decimalDigits, String dbName) {
        if (dbType == null) {
            return "String";
        }
        dbType = dbType.toLowerCase();

        String lowerDbName = dbName != null ? dbName.toLowerCase() : "mysql";

        return switch (lowerDbName) {
            case "mysql", "mariadb" -> getMySQLJavaType(dbType, columnSize, decimalDigits);
            case "postgresql", "pgsql" -> getPostgreSQLJavaType(dbType, columnSize, decimalDigits);
            case "oracle" -> getOracleJavaType(dbType, columnSize, decimalDigits);
            case "sqlserver", "mssql" -> getSQLServerJavaType(dbType, columnSize, decimalDigits);
            default -> getMySQLJavaType(dbType, columnSize, decimalDigits);
        };
    }

    private static String getMySQLJavaType(String dbType, int columnSize, int decimalDigits) {
        // 特殊处理自增主键
        if (contains(dbType, MYSQL_BLOB_TYPES) && columnSize == 1) {
            return "Boolean";
        }

        if (contains(dbType, MYSQL_STRING_TYPES)) {
            if (columnSize > 2000) {
                return "String";
            }
            return "String";
        }

        if (contains(dbType, MYSQL_NUMBER_TYPES)) {
            if (decimalDigits > 0) {
                if (columnSize > 10) {
                    return "Double";
                }
                return "Double";
            }
            if (columnSize <= 3) {
                return "Integer";
            } else if (columnSize <= 10) {
                return "Integer";
            } else if (columnSize <= 20) {
                return "Long";
            } else {
                return "BigDecimal";
            }
        }

        if (contains(dbType, MYSQL_DATE_TYPES)) {
            if (dbType.contains("time")) {
                return "LocalTime";
            }
            if (dbType.contains("date") && !dbType.contains("datetime") && !dbType.contains("timestamp")) {
                return "LocalDate";
            }
            return "LocalDateTime";
        }

        // 二进制
        if (dbType.contains("blob")) {
            return "byte[]";
        }

        return "String";
    }

    private static String getPostgreSQLJavaType(String dbType, int columnSize, int decimalDigits) {
        if (contains(dbType, PGSQL_STRING_TYPES)) {
            return "String";
        }

        if (contains(dbType, PGSQL_NUMBER_TYPES)) {
            if (decimalDigits > 0) {
                return "Double";
            }
            if (columnSize <= 4) {
                return "Integer";
            } else if (columnSize <= 10) {
                return "Integer";
            } else if (columnSize <= 20) {
                return "Long";
            } else {
                return "BigDecimal";
            }
        }

        if (contains(dbType, PGSQL_DATE_TYPES)) {
            if (dbType.contains("time") && !dbType.contains("timestamp")) {
                return "LocalTime";
            }
            if (dbType.equals("date")) {
                return "LocalDate";
            }
            return "LocalDateTime";
        }

        // UUID
        if (dbType.contains("uuid")) {
            return "String";
        }

        return "String";
    }

    private static String getOracleJavaType(String dbType, int columnSize, int decimalDigits) {
        if (contains(dbType, ORACLE_STRING_TYPES)) {
            return "String";
        }

        if (contains(dbType, ORACLE_NUMBER_TYPES)) {
            if (decimalDigits > 0) {
                return "Double";
            }
            if (columnSize <= 4) {
                return "Integer";
            } else if (columnSize <= 10) {
                return "Integer";
            } else if (columnSize <= 20) {
                return "Long";
            } else {
                return "BigDecimal";
            }
        }

        if (contains(dbType, ORACLE_DATE_TYPES)) {
            return "LocalDateTime";
        }

        // CLOB/BLOB
        if (dbType.contains("clob") || dbType.contains("blob")) {
            return "byte[]";
        }

        return "String";
    }

    private static String getSQLServerJavaType(String dbType, int columnSize, int decimalDigits) {
        if (contains(dbType, SQLSERVER_STRING_TYPES)) {
            return "String";
        }

        if (contains(dbType, SQLSERVER_NUMBER_TYPES)) {
            if (decimalDigits > 0) {
                return "Double";
            }
            if (columnSize <= 4) {
                return "Integer";
            } else if (columnSize <= 10) {
                return "Integer";
            } else if (columnSize <= 20) {
                return "Long";
            } else {
                return "BigDecimal";
            }
        }

        if (contains(dbType, SQLSERVER_DATE_TYPES)) {
            if (dbType.equals("date")) {
                return "LocalDate";
            }
            if (dbType.equals("time")) {
                return "LocalTime";
            }
            return "LocalDateTime";
        }

        return "String";
    }

    private static boolean contains(String dbType, String[] types) {
        for (String type : types) {
            if (dbType.contains(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取JDBC类型代码
     */
    public static int getJdbcTypeCode(String javaType) {
        return switch (javaType) {
            case "Integer", "Long", "Short", "Byte" -> java.sql.Types.INTEGER;
            case "Double", "Float", "BigDecimal" -> java.sql.Types.DECIMAL;
            case "Boolean" -> java.sql.Types.BIT;
            case "LocalDateTime", "LocalDate", "LocalTime", "Timestamp" -> java.sql.Types.TIMESTAMP;
            case "byte[]" -> java.sql.Types.BLOB;
            default -> java.sql.Types.VARCHAR;
        };
    }
}
