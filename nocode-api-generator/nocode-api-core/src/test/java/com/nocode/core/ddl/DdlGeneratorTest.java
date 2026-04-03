package com.nocode.core.ddl;

import com.nocode.core.entity.DatabaseType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DdlGenerator 单元测试
 *
 * @author auto-dev
 * @since 2026-04-03
 */
class DdlGeneratorTest {

    @Test
    void testGenerateDdl_MySQL() {
        // Given
        Map<String, Object> formConfig = createCustomerFormConfig();

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, DatabaseType.MYSQL);

        // Then
        assertNotNull(ddl);
        assertTrue(ddl.contains("CREATE TABLE t_customer"));
        assertTrue(ddl.contains("id BIGINT PRIMARY KEY AUTO_INCREMENT"));
        assertTrue(ddl.contains("customer_name VARCHAR(255) NOT NULL"));
        assertTrue(ddl.contains("phone VARCHAR(255)"));
        assertTrue(ddl.contains("email VARCHAR(255)"));
        assertTrue(ddl.contains("status TINYINT(1)"));
        assertTrue(ddl.contains("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"));
        assertTrue(ddl.contains("COMMENT='客户管理'"));
    }

    @Test
    void testGenerateDdl_PostgreSQL() {
        // Given
        Map<String, Object> formConfig = createCustomerFormConfig();

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, DatabaseType.POSTGRESQL);

        // Then
        assertNotNull(ddl);
        assertTrue(ddl.contains("CREATE TABLE t_customer"));
        assertTrue(ddl.contains("id BIGSERIAL PRIMARY KEY"));
        assertTrue(ddl.contains("customer_name VARCHAR(255)"));
        assertTrue(ddl.contains("status BOOLEAN"));
    }

    @Test
    void testGenerateDdl_Oracle() {
        // Given
        Map<String, Object> formConfig = createCustomerFormConfig();

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, DatabaseType.ORACLE);

        // Then
        assertNotNull(ddl);
        assertTrue(ddl.contains("CREATE TABLE t_customer"));
        assertTrue(ddl.contains("id NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY"));
        assertTrue(ddl.contains("customer_name VARCHAR2(255)"));
        assertTrue(ddl.contains("status NUMBER(1)"));
    }

    @Test
    void testGenerateDdl_SQLServer() {
        // Given
        Map<String, Object> formConfig = createCustomerFormConfig();

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, DatabaseType.SQLSERVER);

        // Then
        assertNotNull(ddl);
        assertTrue(ddl.contains("CREATE TABLE t_customer"));
        assertTrue(ddl.contains("id BIGINT IDENTITY(1,1) PRIMARY KEY"));
        assertTrue(ddl.contains("customer_name NVARCHAR(255)"));
        assertTrue(ddl.contains("status BIT"));
    }

    @Test
    void testGenerateDdl_WithTableNameFromConfig() {
        // Given - formConfig contains tableName
        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "customer");
        formConfig.put("formName", "客户管理");
        formConfig.put("tableName", "t_customer");
        formConfig.put("components", createCustomerComponents());

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, DatabaseType.MYSQL);

        // Then
        assertNotNull(ddl);
        assertTrue(ddl.contains("CREATE TABLE t_customer"));
    }

    @Test
    void testGenerateDdl_WithoutTableName_UsesFormId() {
        // Given - formConfig without tableName, should use formId
        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "customer");
        formConfig.put("formName", "客户管理");
        formConfig.put("components", createCustomerComponents());

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, DatabaseType.MYSQL);

        // Then
        assertNotNull(ddl);
        assertTrue(ddl.contains("CREATE TABLE t_customer"));
    }

    @Test
    void testGenerateDdl_WithRequiredField() {
        // Given
        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", List.of(
            createComponent("name", "input", "名称", true)
        ));

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("name VARCHAR(255) NOT NULL"));
    }

    @Test
    void testGenerateDdl_WithDefaultValue() {
        // Given
        Map<String, Object> component = createComponent("status", "switch", "状态", false);
        component.put("defaultValue", "1");

        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", List.of(component));

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("DEFAULT 1"));
    }

    @Test
    void testGenerateDdl_WithMaxLength() {
        // Given
        Map<String, Object> component = createComponent("code", "input", "编码", false);
        component.put("maxLength", 50);

        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", List.of(component));

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("VARCHAR(50)"));
    }

    @Test
    void testGenerateDdl_Textarea() {
        // Given
        Map<String, Object> component = createComponent("description", "textarea", "描述", false);

        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", List.of(component));

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("description TEXT"));
    }

    @Test
    void testGenerateDdl_DateField() {
        // Given
        Map<String, Object> component = createComponent("birthday", "date", "生日", false);

        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", List.of(component));

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("birthday DATE"));
    }

    @Test
    void testGenerateDdl_DateTimeField() {
        // Given
        Map<String, Object> component = createComponent("createTime", "datetime", "创建时间", false);

        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", List.of(component));

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("createTime DATETIME"));
    }

    @Test
    void testGenerateDdl_AuditColumns() {
        // Given
        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", new ArrayList<>());

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("create_time DATETIME"));
        assertTrue(ddl.contains("update_time DATETIME"));
        assertTrue(ddl.contains("create_by VARCHAR(64)"));
        assertTrue(ddl.contains("update_by VARCHAR(64)"));
        assertTrue(ddl.contains("remark VARCHAR(500)"));
    }

    @Test
    void testGenerateDropTable_MySQL() {
        // When
        String sql = DdlGenerator.generateDropTable("t_customer", DatabaseType.MYSQL);

        // Then
        assertEquals("DROP TABLE IF EXISTS t_customer", sql);
    }

    @Test
    void testGenerateDropTable_PostgreSQL() {
        // When
        String sql = DdlGenerator.generateDropTable("t_customer", DatabaseType.POSTGRESQL);

        // Then
        assertEquals("DROP TABLE IF EXISTS t_customer", sql);
    }

    @Test
    void testGenerateDropTable_Oracle() {
        // When
        String sql = DdlGenerator.generateDropTable("t_customer", DatabaseType.ORACLE);

        // Then
        assertEquals("IF OBJECT_ID('t_customer', 'U') IS NOT NULL DROP TABLE t_customer", sql);
    }

    @Test
    void testGenerateAddColumn() {
        // Given
        Map<String, Object> component = createComponent("newField", "input", "新字段", false);

        // When
        String sql = DdlGenerator.generateAddColumn("t_customer", component, DatabaseType.MYSQL);

        // Then
        assertNotNull(sql);
        assertTrue(sql.contains("ALTER TABLE t_customer ADD COLUMN"));
        assertTrue(sql.contains("newField"));
    }

    @Test
    void testGenerateDdl_EmptyComponents() {
        // Given
        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("formName", "测试");
        formConfig.put("components", new ArrayList<>());

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertNotNull(ddl);
        assertTrue(ddl.contains("CREATE TABLE"));
        assertTrue(ddl.contains("PRIMARY KEY"));
    }

    @Test
    void testGenerateDdl_EscapeComment() {
        // Given
        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("formName", "客户'信息");
        formConfig.put("components", new ArrayList<>());

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "客户'信息", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("COMMENT='客户''信息'"));
    }

    @Test
    void testGenerateDdl_NumberField() {
        // Given
        Map<String, Object> component = createComponent("age", "number", "年龄", false);

        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", List.of(component));

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("age BIGINT"));
    }

    @Test
    void testGenerateDdl_SelectField() {
        // Given
        Map<String, Object> component = createComponent("gender", "select", "性别", false);

        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "test");
        formConfig.put("components", List.of(component));

        // When
        String ddl = DdlGenerator.generateDdl(formConfig, "t_test", "测试", DatabaseType.MYSQL);

        // Then
        assertTrue(ddl.contains("gender VARCHAR(100)"));
    }

    /**
     * 创建客户表单配置
     */
    private Map<String, Object> createCustomerFormConfig() {
        Map<String, Object> formConfig = new HashMap<>();
        formConfig.put("formId", "customer");
        formConfig.put("formName", "客户管理");
        formConfig.put("components", createCustomerComponents());
        return formConfig;
    }

    /**
     * 创建客户表单组件列表
     */
    private List<Map<String, Object>> createCustomerComponents() {
        List<Map<String, Object>> components = new ArrayList<>();
        components.add(createComponent("customer_name", "input", "客户姓名", true));
        components.add(createComponent("phone", "input", "联系电话", false));
        components.add(createComponent("email", "input", "邮箱", false));
        components.add(createComponent("status", "switch", "状态", false));
        return components;
    }

    /**
     * 创建组件配置
     */
    private Map<String, Object> createComponent(String field, String type, String label, boolean required) {
        Map<String, Object> component = new HashMap<>();
        component.put("field", field);
        component.put("type", type);
        component.put("label", label);
        component.put("required", required);
        return component;
    }
}