package com.nocode.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nocode.admin.entity.CodeGeneratorConfigEntity;
import com.nocode.admin.exception.CodeGenerateException;
import com.nocode.admin.exception.ResourceNotFoundException;
import com.nocode.admin.repository.CodeGeneratorConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 代码生成器Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGeneratorService {

    private final CodeGeneratorConfigRepository codeGeneratorConfigRepository;
    private final ObjectMapper objectMapper;

    /**
     * 创建配置
     */
    @Transactional
    public CodeGeneratorConfigEntity create(CodeGeneratorConfigEntity entity) {
        return codeGeneratorConfigRepository.save(entity);
    }

    /**
     * 更新配置
     */
    @Transactional
    public CodeGeneratorConfigEntity update(Long id, CodeGeneratorConfigEntity entity) {
        Optional<CodeGeneratorConfigEntity> existing = codeGeneratorConfigRepository.findById(id);
        if (existing.isPresent()) {
            CodeGeneratorConfigEntity config = existing.get();
            config.setName(entity.getName());
            config.setTableName(entity.getTableName());
            config.setEntityName(entity.getEntityName());
            config.setPackageName(entity.getPackageName());
            config.setModuleName(entity.getModuleName());
            config.setGenerateType(entity.getGenerateType());
            config.setTemplateConfig(entity.getTemplateConfig());
            config.setFieldConfig(entity.getFieldConfig());
            config.setStatus(entity.getStatus());
            return codeGeneratorConfigRepository.save(config);
        }
        throw new ResourceNotFoundException("Code generator config", id);
    }

    /**
     * 获取配置
     */
    public Optional<CodeGeneratorConfigEntity> getById(Long id) {
        return codeGeneratorConfigRepository.findById(id);
    }

    /**
     * 查询所有配置
     */
    public List<CodeGeneratorConfigEntity> findAll() {
        return codeGeneratorConfigRepository.findAll();
    }

    /**
     * 根据状态查询
     */
    public List<CodeGeneratorConfigEntity> findByStatus(String status) {
        return codeGeneratorConfigRepository.findByStatus(status);
    }

    /**
     * 生成代码
     */
    public String generateCode(Long configId, String outputPath) {
        CodeGeneratorConfigEntity config = codeGeneratorConfigRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("Code generator config", configId));

        try {
            Map<String, Object> fieldConfig = objectMapper.readValue(config.getFieldConfig(), Map.class);
            Map<String, Object> templateConfig = objectMapper.readValue(config.getTemplateConfig(), Map.class);

            String entityContent = generateEntity(config, fieldConfig);
            String serviceContent = generateService(config);
            String controllerContent = generateController(config);
            String repositoryContent = generateRepository(config);
            String mapperContent = generateMapper(config);

            // 写入文件
            Path basePath = Paths.get(outputPath, config.getModuleName());
            Files.createDirectories(basePath);

            writeFile(basePath.resolve("Entity.java"), entityContent);
            writeFile(basePath.resolve("Repository.java"), repositoryContent);
            writeFile(basePath.resolve("Service.java"), serviceContent);
            writeFile(basePath.resolve("ServiceImpl.java"), generateServiceImpl(config));
            writeFile(basePath.resolve("Controller.java"), controllerContent);
            writeFile(basePath.resolve("Mapper.java"), mapperContent);

            return "Code generated successfully at: " + basePath;
        } catch (Exception e) {
            log.error("Code generation failed", e);
            throw new CodeGenerateException("Code generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * 生成实体类
     */
    private String generateEntity(CodeGeneratorConfigEntity config, Map<String, Object> fieldConfig) {
        StringBuilder sb = new StringBuilder();
        String packageName = config.getPackageName();
        String entityName = config.getEntityName();

        sb.append("package ").append(packageName).append(".entity;\n\n");
        sb.append("import lombok.Data;\n");
        sb.append("import javax.persistence.*;\n");
        sb.append("import java.time.LocalDateTime;\n\n");
        sb.append("@Data\n@Entity\n@Table(name = \"").append(config.getTableName()).append("\")\n");
        sb.append("public class ").append(entityName).append("Entity {\n\n");

        // ID field - only generated once, not inside the loop
        sb.append("    @Id\n");
        sb.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
        sb.append("    private Long id;\n\n");

        @SuppressWarnings("unchecked")
        List<Map<String, String>> fields = (List<Map<String, String>>) fieldConfig.get("fields");
        if (fields != null) {
            for (Map<String, String> field : fields) {
                String fieldName = field.get("name");
                String fieldType = field.getOrDefault("type", "String");
                String columnName = field.get("column");
                String columnDefinition = field.getOrDefault("definition", "VARCHAR(100)");

                sb.append("    @Column(name = \"").append(columnName).append("\"");
                if (columnDefinition != null) {
                    sb.append(", columnDefinition = \"").append(columnDefinition).append("\"");
                }
                sb.append(")\n");
                sb.append("    private ").append(fieldType).append(" ").append(toCamelCase(fieldName)).append(";\n\n");
            }
        }

        sb.append("    @Column(name = \"create_time\")\n");
        sb.append("    private LocalDateTime createTime;\n\n");
        sb.append("    @Column(name = \"update_time\")\n");
        sb.append("    private LocalDateTime updateTime;\n\n");
        sb.append("    @PrePersist\n");
        sb.append("    protected void onCreate() {\n");
        sb.append("        createTime = LocalDateTime.now();\n");
        sb.append("        updateTime = LocalDateTime.now();\n");
        sb.append("    }\n\n");
        sb.append("    @PreUpdate\n");
        sb.append("    protected void onUpdate() {\n");
        sb.append("        updateTime = LocalDateTime.now();\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 生成Repository
     */
    private String generateRepository(CodeGeneratorConfigEntity config) {
        StringBuilder sb = new StringBuilder();
        String packageName = config.getPackageName();
        String entityName = config.getEntityName();

        sb.append("package ").append(packageName).append(".repository;\n\n");
        sb.append("import ").append(packageName).append(".entity.").append(entityName).append("Entity;\n");
        sb.append("import org.springframework.data.jpa.repository.JpaRepository;\n");
        sb.append("import org.springframework.stereotype.Repository;\n\n");
        sb.append("@Repository\n");
        sb.append("public interface ").append(entityName).append("Repository extends JpaRepository<")
                .append(entityName).append("Entity, Long> {\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 生成Service接口
     */
    private String generateService(CodeGeneratorConfigEntity config) {
        StringBuilder sb = new StringBuilder();
        String packageName = config.getPackageName();
        String entityName = config.getEntityName();

        sb.append("package ").append(packageName).append(".service;\n\n");
        sb.append("import ").append(packageName).append(".entity.").append(entityName).append("Entity;\n");
        sb.append("import java.util.List;\n\n");
        sb.append("public interface ").append(entityName).append("Service {\n\n");
        sb.append("    ").append(entityName).append("Entity create(").append(entityName).append("Entity entity);\n\n");
        sb.append("    ").append(entityName).append("Entity update(Long id, ").append(entityName).append("Entity entity);\n\n");
        sb.append("    void delete(Long id);\n\n");
        sb.append("    ").append(entityName).append("Entity getById(Long id);\n\n");
        sb.append("    List<").append(entityName).append("Entity> findAll();\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 生成Service实现
     */
    private String generateServiceImpl(CodeGeneratorConfigEntity config) {
        StringBuilder sb = new StringBuilder();
        String packageName = config.getPackageName();
        String entityName = config.getEntityName();

        sb.append("package ").append(packageName).append(".service;\n\n");
        sb.append("import ").append(packageName).append(".entity.").append(entityName).append("Entity;\n");
        sb.append("import ").append(packageName).append(".repository.").append(entityName).append("Repository;\n");
        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import org.springframework.transaction.annotation.Transactional;\n\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Optional;\n\n");
        sb.append("@Service\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(entityName).append("ServiceImpl implements ").append(entityName).append("Service {\n\n");
        sb.append("    private final ").append(entityName).append("Repository repository;\n\n");
        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public ").append(entityName).append("Entity create(").append(entityName).append("Entity entity) {\n");
        sb.append("        return repository.save(entity);\n");
        sb.append("    }\n\n");
        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public ").append(entityName).append("Entity update(Long id, ").append(entityName).append("Entity entity) {\n");
        sb.append("        Optional<").append(entityName).append("Entity> existing = repository.findById(id);\n");
        sb.append("        if (existing.isPresent()) {\n");
        sb.append("            return repository.save(entity);\n");
        sb.append("        }\n");
        sb.append("        throw new RuntimeException(\"").append(entityName).append(" not found: \" + id);\n");
        sb.append("    }\n\n");
        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public void delete(Long id) {\n");
        sb.append("        repository.deleteById(id);\n");
        sb.append("    }\n\n");
        sb.append("    @Override\n");
        sb.append("    public Optional<").append(entityName).append("Entity> getById(Long id) {\n");
        sb.append("        return repository.findById(id);\n");
        sb.append("    }\n\n");
        sb.append("    @Override\n");
        sb.append("    public List<").append(entityName).append("Entity> findAll() {\n");
        sb.append("        return repository.findAll();\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 生成Controller
     */
    private String generateController(CodeGeneratorConfigEntity config) {
        StringBuilder sb = new StringBuilder();
        String packageName = config.getPackageName();
        String entityName = config.getEntityName();

        sb.append("package ").append(packageName).append(".controller;\n\n");
        sb.append("import ").append(packageName).append(".entity.").append(entityName).append("Entity;\n");
        sb.append("import ").append(packageName).append(".service.").append(entityName).append("Service;\n");
        sb.append("import com.nocode.core.entity.ApiResult;\n");
        sb.append("import lombok.RequiredArgsConstructor;\n");
        sb.append("import org.springframework.web.bind.annotation.*;\n\n");
        sb.append("import java.util.List;\n\n");
        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/api/").append(toKebabCase(entityName)).append("\")\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(entityName).append("Controller {\n\n");
        sb.append("    private final ").append(entityName).append("Service service;\n\n");
        sb.append("    @PostMapping\n");
        sb.append("    public ApiResult<").append(entityName).append("Entity> create(@RequestBody ").append(entityName).append("Entity entity) {\n");
        sb.append("        return ApiResult.success(service.create(entity));\n");
        sb.append("    }\n\n");
        sb.append("    @PutMapping(\"/{id}\")\n");
        sb.append("    public ApiResult<").append(entityName).append("Entity> update(@PathVariable Long id, @RequestBody ").append(entityName).append("Entity entity) {\n");
        sb.append("        entity.setId(id);\n");
        sb.append("        return ApiResult.success(service.update(id, entity));\n");
        sb.append("    }\n\n");
        sb.append("    @GetMapping(\"/{id}\")\n");
        sb.append("    public ApiResult<").append(entityName).append("Entity> getById(@PathVariable Long id) {\n");
        sb.append("        return service.getById(id).map(ApiResult::success).orElse(ApiResult.error(\"Not found\"));\n");
        sb.append("    }\n\n");
        sb.append("    @GetMapping(\"/list\")\n");
        sb.append("    public ApiResult<List<").append(entityName).append("Entity>> list() {\n");
        sb.append("        return ApiResult.success(service.findAll());\n");
        sb.append("    }\n\n");
        sb.append("    @DeleteMapping(\"/{id}\")\n");
        sb.append("    public ApiResult<Void> delete(@PathVariable Long id) {\n");
        sb.append("        service.delete(id);\n");
        sb.append("        return ApiResult.success();\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 生成Mapper
     */
    private String generateMapper(CodeGeneratorConfigEntity config) {
        StringBuilder sb = new StringBuilder();
        String packageName = config.getPackageName();
        String entityName = config.getEntityName();

        sb.append("package ").append(packageName).append(".mapper;\n\n");
        sb.append("import ").append(packageName).append(".entity.").append(entityName).append("Entity;\n");
        sb.append("import org.apache.ibatis.annotations.Mapper;\n");
        sb.append("import org.apache.ibatis.annotations.Param;\n\n");
        sb.append("@Mapper\n");
        sb.append("public interface ").append(entityName).append("Mapper {\n\n");
        sb.append("    int insert(").append(entityName).append("Entity entity);\n\n");
        sb.append("    int update(").append(entityName).append("Entity entity);\n\n");
        sb.append("    int deleteById(@Param(\"id\") Long id);\n\n");
        sb.append("    ").append(entityName).append("Entity selectById(@Param(\"id\") Long id);\n\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 生成Vue列表页
     *
     * @param config      代码生成配置
     * @param fieldConfig 字段配置
     * @return Vue列表页代码
     */
    @SuppressWarnings("unchecked")
    public String generateVueIndex(CodeGeneratorConfigEntity config, Map<String, Object> fieldConfig) {
        StringBuilder sb = new StringBuilder();
        String entityName = config.getEntityName();
        String moduleName = toKebabCase(entityName);
        List<Map<String, String>> fields = (List<Map<String, String>>) fieldConfig.get("fields");

        sb.append("<template>\n");
        sb.append("  <div class=\"app-container\">\n");
        sb.append("    <!-- 查询表单 -->\n");
        sb.append("    <el-form :model=\"queryParams\" ref=\"queryForm\" :inline=\"true\" v-show=\"showSearch\">\n");

        if (fields != null) {
            for (Map<String, String> field : fields) {
                String fieldName = field.get("name");
                String label = field.getOrDefault("label", fieldName);
                String componentType = field.getOrDefault("component", "input");

                if ("date".equals(componentType) || "datetime".equals(componentType)) {
                    sb.append("      <el-form-item label=\"").append(label).append("\" prop=\"").append(toCamelCase(fieldName)).append("\">\n");
                    sb.append("        <el-date-picker\n");
                    sb.append("          v-model=\"queryParams.").append(toCamelCase(fieldName)).append("\"\n");
                    sb.append("          type=\"date\"\n");
                    sb.append("          placeholder=\"选择日期\"\n");
                    sb.append("        />\n");
                    sb.append("      </el-form-item>\n");
                } else if ("select".equals(componentType)) {
                    sb.append("      <el-form-item label=\"").append(label).append("\" prop=\"").append(toCamelCase(fieldName)).append("\">\n");
                    sb.append("        <el-select v-model=\"queryParams.").append(toCamelCase(fieldName)).append("\" placeholder=\"请选择\" clearable>\n");
                    sb.append("          <el-option label=\"是\" value=\"1\" />\n");
                    sb.append("          <el-option label=\"否\" value=\"0\" />\n");
                    sb.append("        </el-select>\n");
                    sb.append("      </el-form-item>\n");
                } else {
                    sb.append("      <el-form-item label=\"").append(label).append("\" prop=\"").append(toCamelCase(fieldName)).append("\">\n");
                    sb.append("        <el-input\n");
                    sb.append("          v-model=\"queryParams.").append(toCamelCase(fieldName)).append("\"\n");
                    sb.append("          placeholder=\"请输入").append(label).append("\"\n");
                    sb.append("          clearable\n");
                    sb.append("        />\n");
                    sb.append("      </el-form-item>\n");
                }
            }
        }

        sb.append("      <el-form-item>\n");
        sb.append("        <el-button type=\"primary\" icon=\"Search\" @click=\"handleQuery\">搜索</el-button>\n");
        sb.append("        <el-button icon=\"Refresh\" @click=\"resetQuery\">重置</el-button>\n");
        sb.append("      </el-form-item>\n");
        sb.append("    </el-form>\n\n");

        sb.append("    <!-- 工具栏 -->\n");
        sb.append("    <el-row :gutter=\"10\" class=\"mb8\">\n");
        sb.append("      <el-col :span=\"1.5\">\n");
        sb.append("        <el-button\n");
        sb.append("          type=\"primary\"\n");
        sb.append("          plain\n");
        sb.append("          icon=\"Plus\"\n");
        sb.append("          @click=\"handleAdd\"\n");
        sb.append("        >新增</el-button>\n");
        sb.append("      </el-col>\n");
        sb.append("      <el-col :span=\"1.5\">\n");
        sb.append("        <el-button\n");
        sb.append("          type=\"success\"\n");
        sb.append("          plain\n");
        sb.append("          icon=\"Edit\"\n");
        sb.append("          :disabled=\"single\"\n");
        sb.append("          @click=\"handleUpdate\"\n");
        sb.append("        >修改</el-button>\n");
        sb.append("      </el-col>\n");
        sb.append("      <el-col :span=\"1.5\">\n");
        sb.append("        <el-button\n");
        sb.append("          type=\"danger\"\n");
        sb.append("          plain\n");
        sb.append("          icon=\"Delete\"\n");
        sb.append("          :disabled=\"multiple\"\n");
        sb.append("          @click=\"handleDelete\"\n");
        sb.append("        >删除</el-button>\n");
        sb.append("      </el-col>\n");
        sb.append("      <el-col :span=\"1.5\">\n");
        sb.append("        <el-button\n");
        sb.append("          type=\"warning\"\n");
        sb.append("          plain\n");
        sb.append("          icon=\"Download\"\n");
        sb.append("          @click=\"handleExport\"\n");
        sb.append("        >导出</el-button>\n");
        sb.append("      </el-col>\n");
        sb.append("      <right-toolbar v-model:showSearch=\"showSearch\" @queryTable=\"getList\"></right-toolbar>\n");
        sb.append("    </el-row>\n\n");

        sb.append("    <!-- 数据表格 -->\n");
        sb.append("    <el-table v-loading=\"loading\" :data=\"dataList\" @selection-change=\"handleSelectionChange\">\n");
        sb.append("      <el-table-column type=\"selection\" width=\"50\" align=\"center\" />\n");

        if (fields != null) {
            int idx = 1;
            for (Map<String, String> field : fields) {
                String fieldName = field.get("name");
                String label = field.getOrDefault("label", fieldName);
                sb.append("      <el-table-column label=\"").append(label).append("\" align=\"center\" prop=\"").append(toCamelCase(fieldName)).append("\" />\n");
                if (idx++ > 5) break; // Limit columns shown
            }
        }

        sb.append("      <el-table-column label=\"操作\" align=\"center\" class-name=\"small-padding fixed-width\">\n");
        sb.append("        <template #default=\"scope\">\n");
        sb.append("          <el-button\n");
        sb.append("            link\n");
        sb.append("            type=\"primary\"\n");
        sb.append("            icon=\"Edit\"\n");
        sb.append("            @click=\"handleUpdate(scope.row)\"\n");
        sb.append("          >修改</el-button>\n");
        sb.append("          <el-button\n");
        sb.append("            link\n");
        sb.append("            type=\"danger\"\n");
        sb.append("            icon=\"Delete\"\n");
        sb.append("            @click=\"handleDelete(scope.row)\"\n");
        sb.append("          >删除</el-button>\n");
        sb.append("        </template>\n");
        sb.append("      </el-table-column>\n");
        sb.append("    </el-table>\n\n");

        sb.append("    <!-- 分页 -->\n");
        sb.append("    <pagination\n");
        sb.append("      v-show=\"total > 0\"\n");
        sb.append("      :total=\"total\"\n");
        sb.append("      v-model:page=\"queryParams.pageNum\"\n");
        sb.append("      v-model:limit=\"queryParams.pageSize\"\n");
        sb.append("      @pagination=\"getList\"\n");
        sb.append("    />\n");

        sb.append("    <!-- 添加或修改对话框 -->\n");
        sb.append("    <el-dialog v-model=\"dialogVisible\" :title=\"dialogTitle\" width=\"600px\" append-to-body>\n");
        sb.append("      <el-form ref=\"formRef\" :model=\"form\" :rules=\"rules\" label-width=\"80px\">\n");

        if (fields != null) {
            for (Map<String, String> field : fields) {
                String fieldName = field.get("name");
                String label = field.getOrDefault("label", fieldName);
                String required = field.getOrDefault("required", "false");
                sb.append("        <el-form-item label=\"").append(label).append("\" prop=\"").append(toCamelCase(fieldName)).append("\">\n");
                sb.append("          <el-input v-model=\"form.").append(toCamelCase(fieldName)).append("\" placeholder=\"请输入").append(label).append("\" />\n");
                sb.append("        </el-form-item>\n");
            }
        }

        sb.append("      </el-form>\n");
        sb.append("      <template #footer>\n");
        sb.append("        <el-button @click=\"dialogVisible = false\">取消</el-button>\n");
        sb.append("        <el-button type=\"primary\" @click=\"submitForm\">确定</el-button>\n");
        sb.append("      </template>\n");
        sb.append("    </el-dialog>\n");
        sb.append("  </div>\n");
        sb.append("</template>\n\n");

        sb.append("<script setup name=\"").append(entityName).append("Page\">\n");
        sb.append("import { list").append(entityName).append(", get").append(entityName).append(", add").append(entityName)
                .append(", update").append(entityName).append(", del").append(entityName).append(" } from '@/api/").append(moduleName).append("'\n\n");
        sb.append("const dataList = ref([])\n");
        sb.append("const loading = ref(true)\n");
        sb.append("const showSearch = ref(true)\n");
        sb.append("const ids = ref([])\n");
        sb.append("const single = ref(true)\n");
        sb.append("const multiple = ref(true)\n");
        sb.append("const total = ref(0)\n");
        sb.append("const dialogVisible = ref(false)\n");
        sb.append("const dialogTitle = ref('')\n\n");

        sb.append("const queryParams = reactive({\n");
        sb.append("  pageNum: 1,\n");
        sb.append("  pageSize: 10,\n");
        if (fields != null) {
            for (Map<String, String> field : fields) {
                String fieldName = field.get("name");
                sb.append("  ").append(toCamelCase(fieldName)).append(": null,\n");
            }
        }
        sb.append("})\n\n");

        sb.append("const form = ref({\n");
        if (fields != null) {
            for (Map<String, String> field : fields) {
                String fieldName = field.get("name");
                sb.append("  ").append(toCamelCase(fieldName)).append(": null,\n");
            }
        }
        sb.append("})\n\n");

        sb.append("const rules = ref({\n");
        if (fields != null) {
            for (Map<String, String> field : fields) {
                String fieldName = field.get("name");
                String required = field.getOrDefault("required", "false");
                if ("true".equals(required)) {
                    sb.append("  ").append(toCamelCase(fieldName)).append(": [{ required: true, message: '不能为空', trigger: 'blur' }],\n");
                }
            }
        }
        sb.append("})\n\n");

        sb.append("/** 查询列表 */\n");
        sb.append("const getList = async () => {\n");
        sb.append("  loading.value = true\n");
        sb.append("  try {\n");
        sb.append("    const res = await list").append(entityName).append("(queryParams)\n");
        sb.append("    dataList.value = res.rows\n");
        sb.append("    total.value = res.total\n");
        sb.append("  } finally {\n");
        sb.append("    loading.value = false\n");
        sb.append("  }\n");
        sb.append("}\n\n");

        sb.append("/** 搜索按钮 */\n");
        sb.append("const handleQuery = () => {\n");
        sb.append("  queryParams.pageNum = 1\n");
        sb.append("  getList()\n");
        sb.append("}\n\n");

        sb.append("/** 重置按钮 */\n");
        sb.append("const resetQuery = () => {\n");
        sb.append("  queryParams.pageNum = 1\n");
        sb.append("  queryParams.pageSize = 10\n");
        sb.append("  getList()\n");
        sb.append("}\n\n");

        sb.append("/** 新增按钮 */\n");
        sb.append("const handleAdd = () => {\n");
        sb.append("  dialogTitle.value = '添加'\n");
        sb.append("  dialogVisible.value = true\n");
        sb.append("}\n\n");

        sb.append("/** 修改按钮 */\n");
        sb.append("const handleUpdate = async (row) => {\n");
        sb.append("  const id = row.id || ids.value[0]\n");
        sb.append("  dialogTitle.value = '修改'\n");
        sb.append("  dialogVisible.value = true\n");
        sb.append("  const res = await get").append(entityName).append("(id)\n");
        sb.append("  Object.assign(form.value, res.data)\n");
        sb.append("}\n\n");

        sb.append("/** 提交按钮 */\n");
        sb.append("const submitForm = async () => {\n");
        sb.append("  if (form.value.id) {\n");
        sb.append("    await update").append(entityName).append("(form.value)\n");
        sb.append("  } else {\n");
        sb.append("    await add").append(entityName).append("(form.value)\n");
        sb.append("  }\n");
        sb.append("  dialogVisible.value = false\n");
        sb.append("  getList()\n");
        sb.append("}\n\n");

        sb.append("/** 删除按钮 */\n");
        sb.append("const handleDelete = async (row) => {\n");
        sb.append("  const deleteIds = row.id || ids.value\n");
        sb.append("  await del").append(entityName).append("(deleteIds)\n");
        sb.append("  getList()\n");
        sb.append("}\n\n");

        sb.append("/** 导出按钮 */\n");
        sb.append("const handleExport = () => {\n");
        sb.append("  ElMessageBox.confirm('是否确认导出所有数据?', '警告', {\n");
        sb.append("    confirmButtonText: '确定',\n");
        sb.append("    cancelButtonText: '取消',\n");
        sb.append("    type: 'warning'\n");
        sb.append("  }).then(async () => {\n");
        sb.append("    await export").append(entityName).append("(queryParams)\n");
        sb.append("    ElMessage.success('导出成功')\n");
        sb.append("  })\n");
        sb.append("}\n\n");

        sb.append("onMounted(() => {\n");
        sb.append("  getList()\n");
        sb.append("})\n");
        sb.append("</script>\n");

        return sb.toString();
    }

    /**
     * 生成Vue导出功能
     *
     * @param config 代码生成配置
     * @return Vue导出API代码
     */
    public String generateVueExportApi(CodeGeneratorConfigEntity config) {
        StringBuilder sb = new StringBuilder();
        String entityName = config.getEntityName();
        String moduleName = toKebabCase(entityName);

        sb.append("import request from '@/utils/request'\n\n");

        sb.append("/**\n");
        sb.append(" * 导出").append(entityName).append("列表\n");
        sb.append(" */\n");
        sb.append("export function export").append(entityName).append("(query) {\n");
        sb.append("  return request({\n");
        sb.append("    url: '/api/").append(moduleName).append("/export',\n");
        sb.append("    method: 'get',\n");
        sb.append("    params: query,\n");
        sb.append("    responseType: 'blob'\n");
        sb.append("  })\n");
        sb.append("}\n\n");

        sb.append("/**\n");
        sb.append(" * 获取导入模板\n");
        sb.append(" */\n");
        sb.append("export function getImportTemplate() {\n");
        sb.append("  return request({\n");
        sb.append("    url: '/api/").append(moduleName).append("/importTemplate',\n");
        sb.append("    method: 'get',\n");
        sb.append("    responseType: 'blob'\n");
        sb.append("  })\n");
        sb.append("}\n\n");

        sb.append("/**\n");
        sb.append(" * 导入").append(entityName).append("列表\n");
        sb.append(" */\n");
        sb.append("export function import").append(entityName).append("(file) {\n");
        sb.append("  const formData = new FormData()\n");
        sb.append("  formData.append('file', file)\n");
        sb.append("  return request({\n");
        sb.append("    url: '/api/").append(moduleName).append("/import',\n");
        sb.append("    method: 'post',\n");
        sb.append("    data: formData,\n");
        sb.append("    headers: {\n");
        sb.append("      'Content-Type': 'multipart/form-data'\n");
        sb.append("    }\n");
        sb.append("  })\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 生成Vue API文件
     *
     * @param config 代码生成配置
     * @return Vue API代码
     */
    public String generateVueApi(CodeGeneratorConfigEntity config) {
        StringBuilder sb = new StringBuilder();
        String entityName = config.getEntityName();
        String moduleName = toKebabCase(entityName);

        sb.append("import request from '@/utils/request'\n\n");

        sb.append("/**\n");
        sb.append(" * 获取").append(entityName).append("列表\n");
        sb.append(" */\n");
        sb.append("export function list").append(entityName).append("(query) {\n");
        sb.append("  return request({\n");
        sb.append("    url: '/api/").append(moduleName).append("/list',\n");
        sb.append("    method: 'get',\n");
        sb.append("    params: query\n");
        sb.append("  })\n");
        sb.append("}\n\n");

        sb.append("/**\n");
        sb.append(" * 查询").append(entityName).append("详细\n");
        sb.append(" */\n");
        sb.append("export function get").append(entityName).append("(id) {\n");
        sb.append("  return request({\n");
        sb.append("    url: '/api/").append(moduleName).append("/' + id,\n");
        sb.append("    method: 'get'\n");
        sb.append("  })\n");
        sb.append("}\n\n");

        sb.append("/**\n");
        sb.append(" * 新增").append(entityName).append("\n");
        sb.append(" */\n");
        sb.append("export function add").append(entityName).append("(data) {\n");
        sb.append("  return request({\n");
        sb.append("    url: '/api/").append(moduleName).append("/',\n");
        sb.append("    method: 'post',\n");
        sb.append("    data: data\n");
        sb.append("  })\n");
        sb.append("}\n\n");

        sb.append("/**\n");
        sb.append(" * 修改").append(entityName).append("\n");
        sb.append(" */\n");
        sb.append("export function update").append(entityName).append("(data) {\n");
        sb.append("  return request({\n");
        sb.append("    url: '/api/").append(moduleName).append("/',\n");
        sb.append("    method: 'put',\n");
        sb.append("    data: data\n");
        sb.append("  })\n");
        sb.append("}\n\n");

        sb.append("/**\n");
        sb.append(" * 删除").append(entityName).append("\n");
        sb.append(" */\n");
        sb.append("export function del").append(entityName).append("(id) {\n");
        sb.append("  return request({\n");
        sb.append("    url: '/api/").append(moduleName).append("/' + id,\n");
        sb.append("    method: 'delete'\n");
        sb.append("  })\n");
        sb.append("}\n");

        // Add export API methods
        sb.append("\n");
        sb.append(generateVueExportApi(config));

        return sb.toString();
    }

    /**
     * 生成完整代码（包含Vue）
     *
     * @param configId  配置ID
     * @param outputPath 输出路径
     * @param includeVue 是否包含Vue文件
     * @return 生成结果
     */
    public String generateFullCode(Long configId, String outputPath, boolean includeVue) {
        CodeGeneratorConfigEntity config = codeGeneratorConfigRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("Code generator config", configId));

        try {
            Map<String, Object> fieldConfig = objectMapper.readValue(config.getFieldConfig(), Map.class);
            Map<String, Object> templateConfig = objectMapper.readValue(config.getTemplateConfig(), Map.class);

            String entityContent = generateEntity(config, fieldConfig);
            String serviceContent = generateService(config);
            String controllerContent = generateController(config);
            String repositoryContent = generateRepository(config);
            String mapperContent = generateMapper(config);

            // 写入后端文件
            Path basePath = Paths.get(outputPath, config.getModuleName());
            Files.createDirectories(basePath);

            writeFile(basePath.resolve("entity/" + config.getEntityName() + "Entity.java"), entityContent);
            writeFile(basePath.resolve("repository/" + config.getEntityName() + "Repository.java"), repositoryContent);
            writeFile(basePath.resolve("service/" + config.getEntityName() + "Service.java"), serviceContent);
            writeFile(basePath.resolve("service/impl/" + config.getEntityName() + "ServiceImpl.java"), generateServiceImpl(config));
            writeFile(basePath.resolve("controller/" + config.getEntityName() + "Controller.java"), controllerContent);
            writeFile(basePath.resolve("mapper/" + config.getEntityName() + "Mapper.java"), mapperContent);

            // 生成Vue文件
            if (includeVue) {
                String vueApiContent = generateVueApi(config);
                String vueIndexContent = generateVueIndex(config, fieldConfig);

                Path vuePath = Paths.get(outputPath, "vue", config.getModuleName());
                Files.createDirectories(vuePath);

                writeFile(vuePath.resolve("api.js"), vueApiContent);
                writeFile(vuePath.resolve("index.vue"), vueIndexContent);
            }

            return "Code generated successfully at: " + basePath;
        } catch (Exception e) {
            log.error("Code generation failed", e);
            throw new CodeGenerateException("Code generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * 写入文件
     */
    private void writeFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(content);
        }
        log.info("Generated file: {}", path);
    }

    /**
     * 删除配置
     */
    @Transactional
    public void delete(Long id) {
        codeGeneratorConfigRepository.deleteById(id);
    }

    /**
     * 工具方法：下划线转驼峰
     */
    private String toCamelCase(String str) {
        if (str == null) return null;
        StringBuilder result = new StringBuilder();
        boolean upperNext = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else {
                result.append(upperNext ? Character.toUpperCase(c) : c);
                upperNext = false;
            }
        }
        return result.toString();
    }

    /**
     * 工具方法：转kebab-case
     */
    private String toKebabCase(String str) {
        if (str == null) return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append('-');
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }
}