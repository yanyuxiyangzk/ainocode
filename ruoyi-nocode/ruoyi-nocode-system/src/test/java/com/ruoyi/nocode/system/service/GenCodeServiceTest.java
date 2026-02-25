package com.ruoyi.nocode.system.service;

import com.ruoyi.nocode.system.entity.GenTable;
import com.ruoyi.nocode.system.entity.GenTableColumn;
import com.ruoyi.nocode.system.mapper.GenTableColumnMapper;
import com.ruoyi.nocode.system.mapper.GenTableMapper;
import com.ruoyi.nocode.system.service.impl.GenCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * GenCodeService 单元测试
 * 
 * @author ruoyi-nocode
 */
@ExtendWith(MockitoExtension.class)
class GenCodeServiceTest {

    @Mock
    private GenTableMapper genTableMapper;

    @Mock
    private GenTableColumnMapper genTableColumnMapper;

    @InjectMocks
    private GenCodeServiceImpl genCodeService;

    private GenTable testTable;
    private List<GenTableColumn> testColumns;

    @BeforeEach
    void setUp() {
        // 创建测试表
        testTable = new GenTable();
        testTable.setTableId(1L);
        testTable.setTableName("sys_user");
        testTable.setTableComment("用户表");
        testTable.setClassName("SysUser");
        testTable.setPackageName("com.ruoyi.nocode.system");
        testTable.setModuleName("system");
        testTable.setBusinessName("user");
        testTable.setFunctionName("用户");
        testTable.setPkColumn("user_id");

        // 创建测试列
        testColumns = new ArrayList<>();
        
        GenTableColumn pkColumn = new GenTableColumn();
        pkColumn.setColumnId(1L);
        pkColumn.setTableId(1L);
        pkColumn.setColumnName("user_id");
        pkColumn.setColumnComment("用户ID");
        pkColumn.setColumnType("BIGINT");
        pkColumn.setJavaType("Long");
        pkColumn.setJavaField("userId");
        pkColumn.setIsPk("1");
        pkColumn.setIsIncrement("1");
        pkColumn.setSort(0);
        testColumns.add(pkColumn);

        GenTableColumn nameColumn = new GenTableColumn();
        nameColumn.setColumnId(2L);
        nameColumn.setTableId(1L);
        nameColumn.setColumnName("user_name");
        nameColumn.setColumnComment("用户名");
        nameColumn.setColumnType("VARCHAR");
        nameColumn.setJavaType("String");
        nameColumn.setJavaField("userName");
        nameColumn.setIsPk("0");
        nameColumn.setIsInsert("1");
        nameColumn.setIsEdit("1");
        nameColumn.setIsList("1");
        nameColumn.setIsQuery("1");
        nameColumn.setSort(1);
        testColumns.add(nameColumn);

        testTable.setColumns(testColumns);
    }

    @Test
    @DisplayName("查询生成表列表")
    void testSelectGenTableList() {
        List<GenTable> mockList = new ArrayList<>();
        mockList.add(testTable);

        when(genTableMapper.selectGenTableList(any())).thenReturn(mockList);

        List<GenTable> result = genCodeService.selectGenTableList(new GenTable());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("sys_user", result.get(0).getTableName());
    }

    @Test
    @DisplayName("根据ID查询生成表")
    void testSelectGenTableById() {
        when(genTableMapper.selectById(1L)).thenReturn(testTable);
        when(genTableColumnMapper.selectGenTableColumnByTableId(1L)).thenReturn(testColumns);

        GenTable result = genCodeService.selectGenTableById(1L);

        assertNotNull(result);
        assertEquals("sys_user", result.getTableName());
        assertNotNull(result.getColumns());
        assertEquals(2, result.getColumns().size());
    }

    @Test
    @DisplayName("更新生成表")
    void testUpdateGenTable() {
        when(genTableMapper.updateById(any(GenTable.class))).thenReturn(1);

        int result = genCodeService.updateGenTable(testTable);

        assertEquals(1, result);
        verify(genTableMapper, times(1)).updateById(any(GenTable.class));
    }

    @Test
    @DisplayName("删除生成表")
    void testDeleteGenTableByIds() {
        when(genTableColumnMapper.deleteGenTableColumnByTableId(any())).thenReturn(1);
        when(genTableMapper.deleteById(any())).thenReturn(1);

        int result = genCodeService.deleteGenTableByIds(new Long[]{1L});

        assertTrue(result > 0);
    }

    @Test
    @DisplayName("预览代码")
    void testPreviewCode() {
        when(genTableMapper.selectById(1L)).thenReturn(testTable);
        when(genTableColumnMapper.selectGenTableColumnByTableId(1L)).thenReturn(testColumns);

        Map<String, String> result = genCodeService.previewCode(1L);

        // 验证生成了所有模板代码
        assertNotNull(result);
        assertTrue(result.containsKey("entity.java"));
        assertTrue(result.containsKey("mapper.java"));
        assertTrue(result.containsKey("service.java"));
        assertTrue(result.containsKey("serviceImpl.java"));
        assertTrue(result.containsKey("controller.java"));

        // 验证代码内容
        String entityCode = result.get("entity.java");
        assertNotNull(entityCode);
        assertTrue(entityCode.contains("class"));
    }

    @Test
    @DisplayName("下载代码生成ZIP")
    void testDownloadCode() {
        when(genTableMapper.selectById(1L)).thenReturn(testTable);
        when(genTableColumnMapper.selectGenTableColumnByTableId(1L)).thenReturn(testColumns);

        byte[] result = genCodeService.downloadCode(1L);

        assertNotNull(result);
        assertTrue(result.length > 0);
        // ZIP文件魔数
        assertEquals(0x50, result[0] & 0xFF); // 'P'
        assertEquals(0x4B, result[1] & 0xFF); // 'K'
    }

    @Test
    @DisplayName("批量下载代码")
    void testDownloadCodeBatch() {
        when(genTableMapper.selectById(1L)).thenReturn(testTable);
        when(genTableColumnMapper.selectGenTableColumnByTableId(1L)).thenReturn(testColumns);

        byte[] result = genCodeService.downloadCodeBatch(new Long[]{1L});

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    @DisplayName("查询数据库表列表")
    void testSelectDbTableList() {
        List<Map<String, Object>> mockTables = new ArrayList<>();
        Map<String, Object> table = new HashMap<>();
        table.put("tableName", "sys_dept");
        table.put("tableComment", "部门表");
        mockTables.add(table);

        when(genTableMapper.selectDbTableList(any())).thenReturn(mockTables);

        List<Map<String, Object>> result = genCodeService.selectDbTableList(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("sys_dept", result.get(0).get("tableName"));
    }

    @Test
    @DisplayName("导入表结构")
    void testImportGenTable() {
        // 模拟数据库表信息
        Map<String, Object> tableInfo = new HashMap<>();
        tableInfo.put("tableName", "sys_post");
        tableInfo.put("tableComment", "岗位表");

        List<Map<String, Object>> columns = new ArrayList<>();
        Map<String, Object> column = new HashMap<>();
        column.put("columnName", "post_id");
        column.put("columnComment", "岗位ID");
        column.put("columnType", "BIGINT");
        column.put("isPk", "1");
        column.put("isIncrement", "1");
        column.put("isNullable", "NO");
        columns.add(column);

        when(genTableMapper.selectDbTableByName("sys_post")).thenReturn(tableInfo);
        when(genTableMapper.selectTableColumns("sys_post")).thenReturn(columns);
        when(genTableMapper.insert(any(GenTable.class))).thenAnswer(inv -> {
            GenTable t = inv.getArgument(0);
            t.setTableId(100L);
            return 1;
        });
        when(genTableColumnMapper.batchInsertGenTableColumn(any())).thenReturn(1);

        assertDoesNotThrow(() -> {
            genCodeService.importGenTable(Arrays.asList("sys_post"));
        });
    }
}
