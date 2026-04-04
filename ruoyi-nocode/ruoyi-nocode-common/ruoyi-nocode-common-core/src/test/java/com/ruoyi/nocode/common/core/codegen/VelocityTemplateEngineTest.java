package com.ruoyi.nocode.common.core.codegen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VelocityTemplateEngine 单元测试
 *
 * @author ruoyi
 */
@DisplayName("Velocity模板引擎测试")
class VelocityTemplateEngineTest {

    private VelocityTemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        templateEngine = VelocityTemplateEngine.getInstance();
        templateEngine.clearCache();
    }

    @Test
    @DisplayName("测试简单变量替换")
    void testSimpleVariableReplacement() {
        String template = "Hello ${name}!";
        Map<String, Object> context = new HashMap<>();
        context.put("name", "World");

        String result = templateEngine.render(template, context);

        Assertions.assertEquals("Hello World!", result);
    }

    @Test
    @DisplayName("测试多变量替换")
    void testMultipleVariables() {
        String template = "Package: ${package}, Class: ${className}";
        Map<String, Object> context = new HashMap<>();
        context.put("package", "com.ruoyi.system");
        context.put("className", "User");

        String result = templateEngine.render(template, context);

        Assertions.assertEquals("Package: com.ruoyi.system, Class: User", result);
    }

    @Test
    @DisplayName("测试foreach循环")
    void testForeachLoop() {
        String template = "#foreach($item in $items)$item#end";
        Map<String, Object> context = new HashMap<>();
        context.put("items", List.of("A", "B", "C"));

        String result = templateEngine.render(template, context);

        Assertions.assertEquals("ABC", result);
    }

    @Test
    @DisplayName("测试if条件")
    void testIfCondition() {
        String template = "#if($show)visible#end";
        Map<String, Object> contextTrue = new HashMap<>();
        contextTrue.put("show", true);

        Map<String, Object> contextFalse = new HashMap<>();
        contextFalse.put("show", false);

        Assertions.assertEquals("visible", templateEngine.render(template, contextTrue));
        Assertions.assertEquals("", templateEngine.render(template, contextFalse));
    }

    @Test
    @DisplayName("测试空模板")
    void testEmptyTemplate() {
        Assertions.assertEquals("", templateEngine.render("", new HashMap<>()));
        Assertions.assertEquals("", templateEngine.render(null, new HashMap<>()));
    }

    @Test
    @DisplayName("测试模板缓存")
    void testTemplateCache() {
        String key = "test_template";
        String content = "Hello ${name}";

        templateEngine.addTemplate(key, content);

        Assertions.assertTrue(templateEngine.hasTemplate(key));
        Assertions.assertEquals(1, templateEngine.getCacheSize());
    }

    @Test
    @DisplayName("测试清除缓存")
    void testClearCache() {
        templateEngine.addTemplate("t1", "content1");
        templateEngine.addTemplate("t2", "content2");

        Assertions.assertEquals(2, templateEngine.getCacheSize());

        templateEngine.clearCache();

        Assertions.assertEquals(0, templateEngine.getCacheSize());
    }

    @Test
    @DisplayName("测试模板不存在")
    void testTemplateNotExists() {
        Assertions.assertFalse(templateEngine.hasTemplate("non_existent"));
    }

    @Test
    @DisplayName("测试移除模板")
    void testRemoveTemplate() {
        String key = "to_remove";
        templateEngine.addTemplate(key, "content");

        Assertions.assertTrue(templateEngine.hasTemplate(key));

        templateEngine.removeTemplate(key);

        Assertions.assertFalse(templateEngine.hasTemplate(key));
    }

    @Test
    @DisplayName("测试TableMetaData渲染")
    void testTableMetaDataRender() {
        // Create mock table metadata
        java.util.List<ColumnMetaData> columns = new java.util.ArrayList<>();

        ColumnMetaData idCol = new ColumnMetaData();
        idCol.setColumnName("id");
        idCol.setColumnType("bigint");
        idCol.setJavaType("Long");
        idCol.setPrimaryKey(true);
        idCol.setFieldName("id");
        columns.add(idCol);

        ColumnMetaData nameCol = new ColumnMetaData();
        nameCol.setColumnName("user_name");
        nameCol.setColumnType("varchar(50)");
        nameCol.setJavaType("String");
        nameCol.setPrimaryKey(false);
        nameCol.setFieldName("userName");
        columns.add(nameCol);

        TableMetaData tableMeta = TableParserService.buildMockTable(
                "sys_user", "系统用户表", columns);

        String template = "Entity: ${table.entityName}, Table: ${table.tableName}, Columns: ${table.columns.size()}";
        String result = templateEngine.render(template, tableMeta);

        Assertions.assertEquals("Entity: SysUser, Table: sys_user, Columns: 2", result);
    }
}
