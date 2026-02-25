package com.nocode.core.parser;

import com.nocode.core.entity.ForeignKeyInfo;
import com.nocode.core.entity.TableInfo;

import java.sql.Connection;
import java.util.List;

/**
 * 表解析器接口
 */
public interface TableParser {
    /**
     * 获取所有表名
     */
    List<String> getTables(Connection conn);

    /**
     * 解析表结构
     */
    TableInfo parseTable(Connection conn, String tableName);

    /**
     * 获取数据库类型
     */
    String getDatabaseType();

    /**
     * 获取所有模式名
     */
    List<String> getSchemas(Connection conn);

    /**
     * 获取指定模式下的所有表名
     */
    List<String> getTablesBySchema(Connection conn, String schema);

    /**
     * 获取指定表的外键关系
     */
    List<ForeignKeyInfo> getForeignKeys(Connection conn, String tableName);

    /**
     * 获取所有外键关系
     */
    List<ForeignKeyInfo> getAllForeignKeys(Connection conn);
}
