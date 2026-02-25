package com.nocode.core.parser;

import com.nocode.core.entity.ForeignKeyInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL表解析器
 */
public class MySqlTableParser extends AbstractTableParser {

    @Override
    public List<String> getTables(Connection conn) {
        List<String> tables = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT TABLE_NAME FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE() ORDER BY TABLE_NAME")) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            throw new RuntimeException("获取表列表失败", e);
        }
        return tables;
    }

    @Override
    protected String getTableComment(Connection conn, String tableName) {
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT TABLE_COMMENT FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + tableName + "'")) {
            if (rs.next()) {
                return rs.getString("TABLE_COMMENT");
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "";
    }

    @Override
    protected String getColumnsQuery(String tableName) {
        return "SELECT * FROM information_schema.COLUMNS " +
               "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + tableName + "' " +
               "ORDER BY ORDINAL_POSITION";
    }

    @Override
    protected String getTableCommentQuery(String tableName) {
        return "SELECT TABLE_COMMENT FROM information_schema.TABLES " +
               "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + tableName + "'";
    }

    @Override
    protected String getTablesQuery() {
        return "SELECT TABLE_NAME FROM information_schema.TABLES " +
               "WHERE TABLE_SCHEMA = DATABASE() ORDER BY TABLE_NAME";
    }

    @Override
    public String getDatabaseType() {
        return "MySQL";
    }

    @Override
    public List<ForeignKeyInfo> getForeignKeys(Connection conn, String tableName) {
        List<ForeignKeyInfo> fkList = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT CONSTRAINT_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME " +
                "FROM information_schema.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = '" + tableName + "' " +
                "AND REFERENCED_TABLE_NAME IS NOT NULL")) {
            while (rs.next()) {
                ForeignKeyInfo fk = new ForeignKeyInfo();
                fk.setFkName(rs.getString("CONSTRAINT_NAME"));
                fk.setTableName(tableName);
                fk.setColumnName(rs.getString("COLUMN_NAME"));
                fk.setReferencedTableName(rs.getString("REFERENCED_TABLE_NAME"));
                fk.setReferencedColumnName(rs.getString("REFERENCED_COLUMN_NAME"));
                fkList.add(fk);
            }
        } catch (Exception e) {
            throw new RuntimeException("获取外键信息失败: " + tableName, e);
        }
        return fkList;
    }

    @Override
    public List<ForeignKeyInfo> getAllForeignKeys(Connection conn) {
        List<ForeignKeyInfo> fkList = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME " +
                "FROM information_schema.KEY_COLUMN_USAGE " +
                "WHERE TABLE_SCHEMA = DATABASE() AND REFERENCED_TABLE_NAME IS NOT NULL " +
                "ORDER BY TABLE_NAME")) {
            while (rs.next()) {
                ForeignKeyInfo fk = new ForeignKeyInfo();
                fk.setFkName(rs.getString("CONSTRAINT_NAME"));
                fk.setTableName(rs.getString("TABLE_NAME"));
                fk.setColumnName(rs.getString("COLUMN_NAME"));
                fk.setReferencedTableName(rs.getString("REFERENCED_TABLE_NAME"));
                fk.setReferencedColumnName(rs.getString("REFERENCED_COLUMN_NAME"));
                fkList.add(fk);
            }
        } catch (Exception e) {
            throw new RuntimeException("获取所有外键信息失败", e);
        }
        return fkList;
    }
}
