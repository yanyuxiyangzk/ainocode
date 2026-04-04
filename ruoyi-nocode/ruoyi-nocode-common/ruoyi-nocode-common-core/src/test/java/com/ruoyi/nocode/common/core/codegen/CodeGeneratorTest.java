package com.ruoyi.nocode.common.core.codegen;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.engine.CodeGenerator;
import com.ruoyi.nocode.common.core.codegen.engine.VelocityTemplateEngine;
import com.ruoyi.nocode.common.core.codegen.model.ColumnInfo;
import com.ruoyi.nocode.common.core.codegen.model.GeneratedFile;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器单元测试
 *
 * @author ruoyi
 */
@DisplayName("代码生成器测试")
class CodeGeneratorTest {

    private CodeGenConfig config;
    private TableInfo tableInfo;
    private CodeGenerator codeGenerator;

    @BeforeEach
    void setUp() {
        // 初始化配置
        config = new CodeGenConfig();
        config.setAuthor("test-author");
        config.setEmail("test@example.com");
        config.setPackageName("com.ruoyi.test");
        config.setModuleName("system");
        config.setTablePrefix("sys_");
        config.setUseLombok(true);
        config.setUseSwagger(true);
        config.setUseLogicDelete(true);
        config.setLogicDeleteField("del_flag");
        config.setLogicDeleteValue("2");
        config.setLogicNotDeleteValue("0");
        config.setGenerateEntity(true);
        config.setGenerateMapper(true);
        config.setGenerateService(true);
        config.setGenerateController(true);
        config.setGenerateVue(true);

        // 初始化表信息
        tableInfo = new TableInfo();
        tableInfo.setTableName("sys_user");
        tableInfo.setTableComment("用户表");
        tableInfo.setEntityName("User");
        tableInfo.setEntityComment("用户信息");
        tableInfo.setColumns(createMockColumns());
        tableInfo.setPrimaryKey(createMockPrimaryKey());
        tableInfo.setPrimaryKeyFieldName("userId");
        tableInfo.setPrimaryKeyJavaType("Long");

        // 初始化生成器
        codeGenerator = new CodeGenerator();
    }

    private List<ColumnInfo> createMockColumns() {
        List<ColumnInfo> columns = new ArrayList<>();

        ColumnInfo id = new ColumnInfo();
        id.setColumnName("user_id");
        id.setColumnType("BIGINT");
        id.setJavaType("Long");
        id.setJavaFieldName("userId");
        id.setPrimaryKey(true);
        id.setAutoIncrement(true);
        id.setNullable(false);
        id.setComment("用户ID");
        columns.add(id);

        ColumnInfo name = new ColumnInfo();
        name.setColumnName("user_name");
        name.setColumnType("VARCHAR");
        name.setJavaType("String");
        name.setJavaFieldName("userName");
        name.setPrimaryKey(false);
        name.setNullable(false);
        name.setColumnSize(100);
        name.setComment("用户名");
        name.setDisplayType("input");
        name.setRequired(true);
        columns.add(name);

        ColumnInfo email = new ColumnInfo();
        email.setColumnName("email");
        email.setColumnType("VARCHAR");
        email.setJavaType("String");
        email.setJavaFieldName("email");
        email.setPrimaryKey(false);
        email.setNullable(true);
        email.setColumnSize(200);
        email.setComment("邮箱");
        email.setDisplayType("input");
        columns.add(email);

        ColumnInfo status = new ColumnInfo();
        status.setColumnName("status");
        status.setColumnType("TINYINT");
        status.setJavaType("Integer");
        status.setJavaFieldName("status");
        status.setPrimaryKey(false);
        status.setNullable(false);
        status.setComment("状态:0正常 1停用");
        status.setDisplayType("select");
        columns.add(status);

        ColumnInfo createTime = new ColumnInfo();
        createTime.setColumnName("create_time");
        createTime.setColumnType("DATETIME");
        createTime.setJavaType("LocalDateTime");
        createTime.setJavaFieldName("createTime");
        createTime.setPrimaryKey(false);
        createTime.setNullable(true);
        createTime.setComment("创建时间");
        createTime.setDisplayType("datetime");
        columns.add(createTime);

        ColumnInfo delFlag = new ColumnInfo();
        delFlag.setColumnName("del_flag");
        delFlag.setColumnType("TINYINT");
        delFlag.setJavaType("Short");
        delFlag.setJavaFieldName("delFlag");
        delFlag.setPrimaryKey(false);
        delFlag.setNullable(false);
        delFlag.setComment("删除标志");
        delFlag.setLogicDeleteField(true);
        columns.add(delFlag);

        return columns;
    }

    private ColumnInfo createMockPrimaryKey() {
        ColumnInfo pk = new ColumnInfo();
        pk.setColumnName("user_id");
        pk.setColumnType("BIGINT");
        pk.setJavaType("Long");
        pk.setJavaFieldName("userId");
        pk.setPrimaryKey(true);
        pk.setAutoIncrement(true);
        pk.setNullable(false);
        return pk;
    }

    @Test
    @DisplayName("测试Entity生成")
    void testEntityGeneration() {
        GeneratedFile result = codeGenerator.generate(tableInfo, config, "entity");

        Assertions.assertTrue(result.isSuccess(), "Entity生成应该成功");
        Assertions.assertNotNull(result.getContent(), "内容不为空");
        Assertions.assertTrue(result.getContent().contains("class User"), "包含实体类名");
        Assertions.assertTrue(result.getContent().contains("package com.ruoyi.test.system.domain"), "包含包名");
        Assertions.assertTrue(result.getContent().contains("@Data"), "包含Lombok注解");
        Assertions.assertTrue(result.getContent().contains("private Long userId"), "包含主键字段");
        Assertions.assertTrue(result.getContent().contains("private String userName"), "包含用户名字段");
    }

    @Test
    @DisplayName("测试Mapper生成")
    void testMapperGeneration() {
        GeneratedFile result = codeGenerator.generate(tableInfo, config, "mapper");

        Assertions.assertTrue(result.isSuccess(), "Mapper生成应该成功");
        Assertions.assertNotNull(result.getContent(), "内容不为空");
        Assertions.assertTrue(result.getContent().contains("interface UserMapper"), "包含Mapper接口");
        Assertions.assertTrue(result.getContent().contains("BaseMapper<User>"), "继承BaseMapper");
        Assertions.assertTrue(result.getContent().contains("selectUserById"), "包含查询方法");
    }

    @Test
    @DisplayName("测试Service生成")
    void testServiceGeneration() {
        GeneratedFile result = codeGenerator.generate(tableInfo, config, "service");

        Assertions.assertTrue(result.isSuccess(), "Service生成应该成功");
        Assertions.assertNotNull(result.getContent(), "内容不为空");
        Assertions.assertTrue(result.getContent().contains("interface UserService"), "包含Service接口");
        Assertions.assertTrue(result.getContent().contains("selectUserById"), "包含查询方法");
        Assertions.assertTrue(result.getContent().contains("insertUser"), "包含插入方法");
    }

    @Test
    @DisplayName("测试Controller生成")
    void testControllerGeneration() {
        GeneratedFile result = codeGenerator.generate(tableInfo, config, "controller");

        Assertions.assertTrue(result.isSuccess(), "Controller生成应该成功");
        Assertions.assertNotNull(result.getContent(), "内容不为空");
        Assertions.assertTrue(result.getContent().contains("class UserController"), "包含Controller类");
        Assertions.assertTrue(result.getContent().contains("@RequestMapping"), "包含请求映射");
        Assertions.assertTrue(result.getContent().contains("/system/user"), "包含路由路径");
        Assertions.assertTrue(result.getContent().contains("@PreAuthorize"), "包含权限注解");
    }

    @Test
    @DisplayName("测试Vue API生成")
    void testVueApiGeneration() {
        GeneratedFile result = codeGenerator.generate(tableInfo, config, "vueApi");

        Assertions.assertTrue(result.isSuccess(), "Vue API生成应该成功");
        Assertions.assertNotNull(result.getContent(), "内容不为空");
        Assertions.assertTrue(result.getContent().contains("listUser"), "包含列表方法");
        Assertions.assertTrue(result.getContent().contains("getUser"), "包含获取方法");
        Assertions.assertTrue(result.getContent().contains("addUser"), "包含新增方法");
        Assertions.assertTrue(result.getContent().contains("export 'axios'"), "包含导出方法");
    }

    @Test
    @DisplayName("测试批量生成")
    void testBatchGeneration() {
        List<GeneratedFile> results = codeGenerator.generate(tableInfo, config);

        Assertions.assertFalse(results.isEmpty(), "生成结果不为空");
        Assertions.assertTrue(results.size() >= 5, "至少生成5个文件");

        // 验证所有文件都生成成功
        for (GeneratedFile file : results) {
            Assertions.assertTrue(file.isSuccess(), file.getFileName() + "生成应该成功");
            Assertions.assertNotNull(file.getContent(), file.getFileName() + "内容不为空");
        }
    }

    @Test
    @DisplayName("测试禁用生成")
    void testDisableGeneration() {
        config.setGenerateEntity(false);
        config.setGenerateVue(false);

        GeneratedFile entityResult = codeGenerator.generate(tableInfo, config, "entity");
        Assertions.assertFalse(entityResult.isSuccess() && entityResult.getContent() != null,
                "Entity生成应该被禁用");

        config.setGenerateEntity(true);
    }

    @Test
    @DisplayName("测试生成器类型列表")
    void testGeneratorTypes() {
        List<String> types = codeGenerator.getGeneratorTypes();

        Assertions.assertFalse(types.isEmpty(), "生成器类型列表不为空");
        Assertions.assertTrue(types.contains("entity"), "包含entity");
        Assertions.assertTrue(types.contains("mapper"), "包含mapper");
        Assertions.assertTrue(types.contains("controller"), "包含controller");
    }

    @Test
    @DisplayName("测试ColumnInfo工具方法")
    void testColumnInfoUtils() {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName("user_name");
        column.setJavaFieldName("userName");
        column.setJavaType("String");

        // 测试下划线转驼峰
        Assertions.assertEquals("userName", ColumnInfo.toCamelCase("user_name"));
        Assertions.assertEquals("userName", ColumnInfo.toCamelCase("USER_NAME"));
        Assertions.assertEquals("username", ColumnInfo.toCamelCase("username"));

        // 测试首字母大写
        Assertions.assertEquals("UserName", column.toCapitalizedFieldName());

        // 测试类型判断
        Assertions.assertTrue(column.isStringType());
        Assertions.assertFalse(column.isNumericType());
        Assertions.assertFalse(column.isDateType());
    }

    @Test
    @DisplayName("测试TableInfo辅助方法")
    void testTableInfoUtils() {
        List<ColumnInfo> columns = tableInfo.getColumns();

        // 测试获取非主键列
        List<ColumnInfo> nonPkColumns = tableInfo.getNonPrimaryColumns();
        Assertions.assertEquals(columns.size() - 1, nonPkColumns.size());

        // 测试获取可编辑列
        List<ColumnInfo> editableColumns = tableInfo.getEditableColumns();
        Assertions.assertTrue(editableColumns.stream().noneMatch(ColumnInfo::isPrimaryKey));
        Assertions.assertTrue(editableColumns.stream().noneMatch(ColumnInfo::isLogicDeleteField));

        // 测试列名获取
        String columnNames = tableInfo.getColumnNames();
        Assertions.assertNotNull(columnNames);
        Assertions.assertTrue(columnNames.contains("user_id"));
    }

    @Test
    @DisplayName("测试GeneratedFile")
    void testGeneratedFile() {
        GeneratedFile file = GeneratedFile.success("Test.java", "/path/Test.java", "public class Test {}", "java");

        Assertions.assertTrue(file.isSuccess());
        Assertions.assertEquals("Test.java", file.getFileName());
        Assertions.assertEquals("/path/Test.java", file.getFilePath());
        Assertions.assertEquals("public class Test {}", file.getContent());
        Assertions.assertEquals("java", file.getFileType());
        Assertions.assertTrue(file.getFileSize() > 0);

        GeneratedFile errorFile = GeneratedFile.failure("Error.java", "Some error");
        Assertions.assertFalse(errorFile.isSuccess());
        Assertions.assertEquals("Some error", errorFile.getErrorMessage());
    }

    @Test
    @DisplayName("测试VelocityTemplateEngine")
    void testVelocityEngine() {
        VelocityTemplateEngine engine = new VelocityTemplateEngine();

        String template = "Hello $name! You have $count messages.";
        Map<String, Object> context = Map.of("name", "World", "count", 5);

        String result = engine.render(template, context);
        Assertions.assertEquals("Hello World! You have 5 messages.", result);

        // 测试空模板
        Assertions.assertEquals("", engine.render("", context));
        Assertions.assertEquals("", engine.render(null, context));
    }

    @Test
    @DisplayName("测试TableInfo记录类型")
    void testTableInfoRecord() {
        TableInfo table = new TableInfo();
        table.setTableName("test_table");
        table.setEntityName("TestTable");

        // 测试TreeTable字段
        table.setTreeParentField("parentId");
        table.setTreeNameField("name");
        Assertions.assertEquals("parentId", table.getTreeParentField());
        Assertions.assertEquals("name", table.getTreeNameField());

        // 测试JoinTable
        table.setJoinTableName("join_table");
        table.setJoinForeignKey("test_id");
        Assertions.assertEquals("join_table", table.getJoinTableName());
        Assertions.assertEquals("test_id", table.getJoinForeignKey());
    }
}
