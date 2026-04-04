package com.ruoyi.nocode.common.core.codegen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TypeConvertUtil 单元测试
 *
 * @author ruoyi
 */
@DisplayName("类型转换工具测试")
class TypeConvertUtilTest {

    @Test
    @DisplayName("测试varchar转String")
    void testVarcharToString() {
        Assertions.assertEquals("String", TypeConvertUtil.toJavaType("varchar"));
        Assertions.assertEquals("String", TypeConvertUtil.toJavaType("varchar(255)"));
        Assertions.assertEquals("String", TypeConvertUtil.toJavaType("char"));
    }

    @Test
    @DisplayName("测试int类型转Integer")
    void testIntToInteger() {
        Assertions.assertEquals("Integer", TypeConvertUtil.toJavaType("int"));
        Assertions.assertEquals("Integer", TypeConvertUtil.toJavaType("integer"));
        Assertions.assertEquals("Integer", TypeConvertUtil.toJavaType("smallint"));
    }

    @Test
    @DisplayName("测试bigint转Long")
    void testBigintToLong() {
        Assertions.assertEquals("Long", TypeConvertUtil.toJavaType("bigint"));
        Assertions.assertEquals("Long", TypeConvertUtil.toJavaType("bigint unsigned"));
    }

    @Test
    @DisplayName("测试datetime转LocalDateTime")
    void testDatetimeToLocalDateTime() {
        Assertions.assertEquals("LocalDateTime", TypeConvertUtil.toJavaType("datetime"));
        Assertions.assertEquals("LocalDateTime", TypeConvertUtil.toJavaType("timestamp"));
    }

    @Test
    @DisplayName("测试decimal转BigDecimal")
    void testDecimalToBigDecimal() {
        Assertions.assertEquals("BigDecimal", TypeConvertUtil.toJavaType("decimal"));
        Assertions.assertEquals("BigDecimal", TypeConvertUtil.toJavaType("numeric"));
        Assertions.assertEquals("BigDecimal", TypeConvertUtil.toJavaType("decimal(10,2)"));
    }

    @Test
    @DisplayName("测试text类型")
    void testTextTypes() {
        Assertions.assertEquals("String", TypeConvertUtil.toJavaType("text"));
        Assertions.assertEquals("String", TypeConvertUtil.toJavaType("mediumtext"));
        Assertions.assertEquals("String", TypeConvertUtil.toJavaType("longtext"));
    }

    @Test
    @DisplayName("测试blob类型")
    void testBlobTypes() {
        Assertions.assertEquals("byte[]", TypeConvertUtil.toJavaType("blob"));
        Assertions.assertEquals("byte[]", TypeConvertUtil.toJavaType("tinyblob"));
    }

    @Test
    @DisplayName("测试转驼峰命名")
    void testToCamelCase() {
        Assertions.assertEquals("UserName", TypeConvertUtil.toCamelCase("user_name"));
        Assertions.assertEquals("UserName", TypeConvertUtil.toCamelCase("userName"));
        Assertions.assertEquals("UserName", TypeConvertUtil.toCamelCase("USER_NAME"));
        Assertions.assertEquals("UserName", TypeConvertUtil.toCamelCase("user-name"));
        Assertions.assertEquals("", TypeConvertUtil.toCamelCase(""));
        Assertions.assertEquals("", TypeConvertUtil.toCamelCase(null));
    }

    @Test
    @DisplayName("测试转变量名（首字母小写）")
    void testToVariableName() {
        Assertions.assertEquals("userName", TypeConvertUtil.toVariableName("user_name"));
        Assertions.assertEquals("userName", TypeConvertUtil.toVariableName("UserName"));
        Assertions.assertEquals("userName", TypeConvertUtil.toVariableName("USER_NAME"));
    }

    @Test
    @DisplayName("测试转大写下划线")
    void testToUpperUnderline() {
        Assertions.assertEquals("USER_NAME", TypeConvertUtil.toUpperUnderline("userName"));
        Assertions.assertEquals("USER_NAME", TypeConvertUtil.toUpperUnderline("UserName"));
        Assertions.assertEquals("USER_NAME", TypeConvertUtil.toUpperUnderline("user_name"));
    }

    @Test
    @DisplayName("测试类名转换")
    void testToClassName() {
        Assertions.assertEquals("SysUser", TypeConvertUtil.toClassName("sys_user", "sys_"));
        Assertions.assertEquals("User", TypeConvertUtil.toClassName("sys_user", "sys_"));
        Assertions.assertEquals("User", TypeConvertUtil.toClassName("user", ""));
        Assertions.assertEquals("Unknown", TypeConvertUtil.toClassName("", ""));
    }

    @Test
    @DisplayName("测试JDBC类型转换")
    void testToJdbcType() {
        Assertions.assertEquals("VARCHAR", TypeConvertUtil.toJdbcType("varchar"));
        Assertions.assertEquals("INTEGER", TypeConvertUtil.toJdbcType("int"));
        Assertions.assertEquals("BIGINT", TypeConvertUtil.toJdbcType("bigint"));
        Assertions.assertEquals("DATETIME", TypeConvertUtil.toJdbcType("datetime"));
    }

    @Test
    @DisplayName("测试是否为主键判断")
    void testIsPrimaryKey() {
        Assertions.assertTrue(TypeConvertUtil.isPrimaryKey("id", "pri"));
        Assertions.assertTrue(TypeConvertUtil.isPrimaryKey("user_id", "pri"));
        Assertions.assertTrue(TypeConvertUtil.isPrimaryKey("id", ""));
        Assertions.assertFalse(TypeConvertUtil.isPrimaryKey("user_name", ""));
        Assertions.assertFalse(TypeConvertUtil.isPrimaryKey("user_name", "uni"));
    }

    @Test
    @DisplayName("测试自增判断")
    void testIsAutoIncrement() {
        Assertions.assertTrue(TypeConvertUtil.isAutoIncrement("int", "auto_increment"));
        Assertions.assertTrue(TypeConvertUtil.isAutoIncrement("bigint", "auto_increment"));
        Assertions.assertFalse(TypeConvertUtil.isAutoIncrement("varchar", "auto_increment"));
        Assertions.assertFalse(TypeConvertUtil.isAutoIncrement("int", ""));
    }

    @Test
    @DisplayName("测试字符串类型判断")
    void testIsStringType() {
        Assertions.assertTrue(TypeConvertUtil.isStringType("varchar"));
        Assertions.assertTrue(TypeConvertUtil.isStringType("text"));
        Assertions.assertFalse(TypeConvertUtil.isStringType("int"));
    }

    @Test
    @DisplayName("测试数值类型判断")
    void testIsNumericType() {
        Assertions.assertTrue(TypeConvertUtil.isNumericType("int"));
        Assertions.assertTrue(TypeConvertUtil.isNumericType("bigint"));
        Assertions.assertTrue(TypeConvertUtil.isNumericType("decimal"));
        Assertions.assertFalse(TypeConvertUtil.isNumericType("varchar"));
    }

    @Test
    @DisplayName("测试日期类型判断")
    void testIsDateType() {
        Assertions.assertTrue(TypeConvertUtil.isDateType("date"));
        Assertions.assertTrue(TypeConvertUtil.isDateType("datetime"));
        Assertions.assertTrue(TypeConvertUtil.isDateType("timestamp"));
        Assertions.assertFalse(TypeConvertUtil.isDateType("varchar"));
    }
}
