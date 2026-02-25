package com.nocode.core.entity;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * 数据源配置
 */
@Data
public class DatasourceConfig {
    /** 数据源名称（唯一标识） */
    private String name;
    /** JDBC URL */
    private String jdbcUrl;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
    /** 驱动类名 */
    private String driverClassName;
    /** 数据库类型（自动检测） */
    private DatabaseType databaseType;
    /** 初始连接数 */
    private int initialSize = 5;
    /** 最小连接数 */
    private int minIdle = 5;
    /** 最大连接数 */
    private int maxActive = 20;
    /** 连接最大等待时间（毫秒） */
    private long maxWait = 60000;
    /** 连接最小生存时间（毫秒） */
    private long minEvictableIdleTimeMillis = 300000;
    /** 是否启用 */
    private boolean enabled = true;

    /**
     * 获取数据库类型（懒加载检测）
     */
    public DatabaseType getDatabaseType() {
        if (databaseType == null) {
            databaseType = DatabaseType.detect(jdbcUrl, driverClassName);
        }
        return databaseType;
    }

    /**
     * 创建DruidDataSource
     */
    public DruidDataSource createDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);

        // 自动检测并设置驱动类名
        String driver = driverClassName;
        if (driver == null || driver.isEmpty()) {
            driver = detectDriverClassName();
            ds.setDriverClassName(driver);
        } else {
            ds.setDriverClassName(driver);
        }

        ds.setInitialSize(initialSize);
        ds.setMinIdle(minIdle);
        ds.setMaxActive(maxActive);
        ds.setMaxWait(maxWait);
        ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);
        ds.setTestWhileIdle(true);
        ds.setPoolPreparedStatements(true);
        ds.setMaxPoolPreparedStatementPerConnectionSize(20);
        return ds;
    }

    /**
     * 根据 JDBC URL 自动检测驱动类名
     */
    private String detectDriverClassName() {
        if (jdbcUrl == null) {
            return "com.mysql.cj.jdbc.Driver"; // 默认
        }

        if (jdbcUrl.contains("mysql:")) {
            return "com.mysql.cj.jdbc.Driver";
        } else if (jdbcUrl.contains("postgresql:")) {
            return "org.postgresql.Driver";
        } else if (jdbcUrl.contains("oracle:")) {
            return "oracle.jdbc.OracleDriver";
        } else if (jdbcUrl.contains("sqlserver:") || jdbcUrl.contains("jtds:")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }

        return "com.mysql.cj.jdbc.Driver"; // 默认
    }
}
