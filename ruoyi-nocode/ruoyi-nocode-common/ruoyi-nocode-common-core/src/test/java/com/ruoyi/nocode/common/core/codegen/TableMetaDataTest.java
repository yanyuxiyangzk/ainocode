package com.ruoyi.nocode.common.core.codegen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TableMetaData 单元测试
 *
 * @author ruoyi
 */
@DisplayName("表元数据测试")
class TableMetaDataTest {

    @Test
    @DisplayName("测试构建模拟表元数据")
    void testBuildMockTable() {
        List<ColumnMetaData> columns = new ArrayList<>();

        ColumnMetaData idCol = new ColumnMetaData();
        idCol.setColumnName("user_id");
        idCol.setColumnType("bigint");
        idCol.setJavaType("Long");
        idCol.setJdbcType("BIGINT");
        idCol.setColumnComment("用户ID");
        idCol.setPrimaryKey(true);
        idCol.setAutoIncrement(true);
        idCol.setNullable(false);
        idCol.setFieldName("userId");
        columns.add(idCol);

        ColumnMetaData nameCol = new ColumnMetaData();
        nameCol.setColumnName("user_name");
        nameCol.setColumnType("varchar(50)");
        nameCol.setJavaType("String");
        nameCol.setJdbcType("VARCHAR");
        nameCol.setColumnComment("用户名");
        nameCol.setPrimaryKey(false);
        nameCol.setNullable(false);
        nameCol.setFieldName("userName");
        columns.add(nameCol);

        ColumnMetaData emailCol = new ColumnMetaData();
        emailCol.setColumnName("email");
        emailCol.setColumnType("varchar(100)");
        emailCol.setJavaType("String");
        emailCol.setJdbcType("VARCHAR");
        emailCol.setColumnComment("邮箱");
        emailCol.setPrimaryKey(false);
        emailCol.setNullable(true);
        emailCol.setFieldName("email");
        columns.add(emailCol);

        ColumnMetaData createTimeCol = new ColumnMetaData();
        createTimeCol.setColumnName("create_time");
        createTimeCol.setColumnType("datetime");
        createTimeCol.setJavaType("LocalDateTime");
        createTimeCol.setJdbcType("DATETIME");
        createTimeCol.setColumnComment("创建时间");
        createTimeCol.setPrimaryKey(false);
        createTimeCol.setNullable(false);
        createTimeCol.setFieldName("createTime");
        columns.add(createTimeCol);

        TableMetaData tableMeta = TableParserService.buildMockTable(
                "sys_user", "系统用户表", columns);

        Assertions.assertEquals("sys_user", tableMeta.getTableName());
        Assertions.assertEquals("系统用户表", tableMeta.getTableComment());
        Assertions.assertEquals("SysUser", tableMeta.getEntityName());
        Assertions.assertEquals("sysUser", tableMeta.getVariableName());
        Assertions.assertEquals(4, tableMeta.getColumns().size());
        Assertions.assertNotNull(tableMeta.getPrimaryKeyColumn());
        Assertions.assertEquals("user_id", tableMeta.getPrimaryKeyColumn().getColumnName());
    }

    @Test
    @DisplayName("测试主键Java类型获取")
    void testGetPrimaryKeyJavaType() {
        List<ColumnMetaData> columns = new ArrayList<>();

        ColumnMetaData idCol = new ColumnMetaData();
        idCol.setColumnName("id");
        idCol.setColumnType("bigint");
        idCol.setJavaType("Long");
        idCol.setPrimaryKey(true);
        idCol.setFieldName("id");
        columns.add(idCol);

        TableMetaData tableMeta = TableParserService.buildMockTable("test_table", "测试表", columns);

        Assertions.assertEquals("Long", tableMeta.getPrimaryKeyJavaType());
        Assertions.assertEquals("id", tableMeta.getPrimaryKeyFieldName());
    }

    @Test
    @DisplayName("测试无主键时默认主键类型")
    void testDefaultPrimaryKeyType() {
        List<ColumnMetaData> columns = new ArrayList<>();

        ColumnMetaData nameCol = new ColumnMetaData();
        nameCol.setColumnName("user_name");
        nameCol.setColumnType("varchar");
        nameCol.setJavaType("String");
        nameCol.setPrimaryKey(false);
        nameCol.setFieldName("userName");
        columns.add(nameCol);

        TableMetaData tableMeta = TableParserService.buildMockTable("test_table", "测试表", columns);

        Assertions.assertEquals("Long", tableMeta.getPrimaryKeyJavaType());
        Assertions.assertEquals("id", tableMeta.getPrimaryKeyFieldName());
    }

    @Test
    @DisplayName("测试查询列过滤")
    void testGetQueryColumns() {
        List<ColumnMetaData> columns = new ArrayList<>();

        ColumnMetaData idCol = new ColumnMetaData();
        idCol.setColumnName("id");
        idCol.setPrimaryKey(true);
        idCol.setFieldName("id");
        columns.add(idCol);

        ColumnMetaData nameCol = new ColumnMetaData();
        nameCol.setColumnName("user_name");
        nameCol.setPrimaryKey(false);
        nameCol.setFieldName("userName");
        columns.add(nameCol);

        ColumnMetaData createTimeCol = new ColumnMetaData();
        createTimeCol.setColumnName("create_time");
        createTimeCol.setPrimaryKey(false);
        createTimeCol.setFieldName("createTime");
        columns.add(createTimeCol);

        ColumnMetaData updateTimeCol = new ColumnMetaData();
        updateTimeCol.setColumnName("update_time");
        updateTimeCol.setPrimaryKey(false);
        updateTimeCol.setFieldName("updateTime");
        columns.add(updateTimeCol);

        TableMetaData tableMeta = TableParserService.buildMockTable("test_table", "测试表", columns);

        List<ColumnMetaData> queryColumns = tableMeta.getQueryColumns();

        Assertions.assertEquals(1, queryColumns.size());
        Assertions.assertEquals("user_name", queryColumns.get(0).getColumnName());
    }

    @Test
    @DisplayName("测试字典字段检测")
    void testHasDictField() {
        List<ColumnMetaData> columns = new ArrayList<>();

        ColumnMetaData statusCol = new ColumnMetaData();
        statusCol.setColumnName("status");
        statusCol.setDictField(true);
        statusCol.setDictType("sys_status");
        statusCol.setFieldName("status");
        columns.add(statusCol);

        TableMetaData tableMeta = TableParserService.buildMockTable("test_table", "测试表", columns);

        Assertions.assertTrue(tableMeta.hasDictField());
        Assertions.assertEquals(1, tableMeta.getDictColumns().size());
    }

    @Test
    @DisplayName("测试无字典字段")
    void testNoDictField() {
        List<ColumnMetaData> columns = new ArrayList<>();

        ColumnMetaData nameCol = new ColumnMetaData();
        nameCol.setColumnName("user_name");
        nameCol.setDictField(false);
        nameCol.setFieldName("userName");
        columns.add(nameCol);

        TableMetaData tableMeta = TableParserService.buildMockTable("test_table", "测试表", columns);

        Assertions.assertFalse(tableMeta.hasDictField());
        Assertions.assertTrue(tableMeta.getDictColumns().isEmpty());
    }
}
