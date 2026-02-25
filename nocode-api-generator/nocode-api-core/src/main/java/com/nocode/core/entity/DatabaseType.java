package com.nocode.core.entity;

/**
 * 数据库类型枚举
 */
public enum DatabaseType {
    MYSQL("mysql", "`", "LIMIT ?,?"),
    POSTGRESQL("postgresql", "\"", "LIMIT ? OFFSET ?"),
    ORACLE("oracle", "\"", ""),
    SQLSERVER("sqlserver", "[", "");

    private final String keyword;
    private final String identifierQuote;
    private final String paginationFormat;

    DatabaseType(String keyword, String identifierQuote, String paginationFormat) {
        this.keyword = keyword;
        this.identifierQuote = identifierQuote;
        this.paginationFormat = paginationFormat;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getIdentifierQuote() {
        return identifierQuote;
    }

    public String getPaginationFormat() {
        return paginationFormat;
    }

    /**
     * 根据 JDBC URL 或驱动类名检测数据库类型
     */
    public static DatabaseType detect(String jdbcUrl, String driverClassName) {
        // 优先从 JDBC URL 检测
        if (jdbcUrl != null) {
            String url = jdbcUrl.toLowerCase();
            if (url.contains("mysql:")) {
                return MYSQL;
            } else if (url.contains("postgresql:")) {
                return POSTGRESQL;
            } else if (url.contains("oracle:")) {
                return ORACLE;
            } else if (url.contains("sqlserver:") || url.contains("jtds:")) {
                return SQLSERVER;
            }
        }

        // 从驱动类名检测
        if (driverClassName != null) {
            String driver = driverClassName.toLowerCase();
            if (driver.contains("mysql")) {
                return MYSQL;
            } else if (driver.contains("postgresql")) {
                return POSTGRESQL;
            } else if (driver.contains("oracle")) {
                return ORACLE;
            } else if (driver.contains("sqlserver") || driver.contains("mssql")) {
                return SQLSERVER;
            }
        }

        // 默认返回 MySQL
        return MYSQL;
    }
}
