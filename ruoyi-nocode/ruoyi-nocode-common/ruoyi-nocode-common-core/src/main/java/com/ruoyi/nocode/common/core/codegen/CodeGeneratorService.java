package com.ruoyi.nocode.common.core.codegen;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 代码生成服务
 * <p>
 * 完整的代码生成管道：
 * 1. 解析表结构 -> TableMetaData
 * 2. 加载Velocity模板
 * 3. 渲染生成代码
 * 4. 输出到指定目录
 *
 * @author ruoyi
 */
public class CodeGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(CodeGeneratorService.class);

    /**
     * 单例实例
     */
    private static volatile CodeGeneratorService instance;

    /**
     * 表解析服务
     */
    private final TableParserService tableParserService;

    /**
     * 模板引擎
     */
    private final VelocityTemplateEngine templateEngine;

    /**
     * 生成结果缓存
     */
    private final Map<String, GeneratedFile> generatedFiles = new ConcurrentHashMap<>();

    /**
     * 计数器
     */
    private final AtomicInteger fileCounter = new AtomicInteger(0);

    /**
     * 模板键定义
     */
    public static final String TEMPLATE_ENTITY = "entity";
    public static final String TEMPLATE_MAPPER = "mapper";
    public static final String TEMPLATE_MAPPER_XML = "mapper_xml";
    public static final String TEMPLATE_SERVICE = "service";
    public static final String TEMPLATE_SERVICE_IMPL = "service_impl";
    public static final String TEMPLATE_CONTROLLER = "controller";
    public static final String TEMPLATE_VUE_INDEX = "vue_index";
    public static final String TEMPLATE_VUE_API = "vue_api";
    public static final String TEMPLATE_VUE_ADD = "vue_add";
    public static final String TEMPLATE_VUE_EDIT = "vue_edit";

    private CodeGeneratorService() {
        this.tableParserService = TableParserService.getInstance();
        this.templateEngine = VelocityTemplateEngine.getInstance();
    }

    /**
     * 获取单例实例
     */
    public static CodeGeneratorService getInstance() {
        if (instance == null) {
            synchronized (CodeGeneratorService.class) {
                if (instance == null) {
                    instance = new CodeGeneratorService();
                }
            }
        }
        return instance;
    }

    /**
     * 设置数据源
     */
    public void setDataSource(javax.sql.DataSource dataSource) {
        tableParserService.setDataSource(dataSource);
    }

    /**
     * 设置配置
     */
    public void setConfig(String tablePrefix, String author, String email, String packageName, String basePath) {
        tableParserService.setConfig(tablePrefix, author, email, packageName, basePath);
    }

    /**
     * 添加模板
     */
    public void addTemplate(String key, String content) {
        templateEngine.addTemplate(key, content);
    }

    /**
     * 生成单个表的代码
     *
     * @param tableName 表名
     * @return 生成结果
     */
    public CodeGenResult generate(String tableName) {
        return generate(tableName, null);
    }

    /**
     * 生成单个表的代码（使用模拟表结构）
     *
     * @param tableMeta 表元数据
     * @return 生成结果
     */
    public CodeGenResult generate(TableMetaData tableMeta) {
        return generate(tableMeta.getTableName(), tableMeta);
    }

    /**
     * 生成单个表的代码
     *
     * @param tableName 表名（如果tableMeta不为null则忽略）
     * @param tableMeta 表元数据（可选，如果为null则从数据库读取）
     * @return 生成结果
     */
    public CodeGenResult generate(String tableName, TableMetaData tableMeta) {
        log.info("Starting code generation for table: {}", tableName);

        try {
            // 解析或使用提供的表元数据
            if (tableMeta == null) {
                tableMeta = tableParserService.parseTable(tableName);
            }

            // 检查模板是否已加载
            if (!templateEngine.hasTemplate(TEMPLATE_ENTITY)) {
                throw new IllegalStateException("Templates not loaded. Call loadTemplates() first.");
            }

            // 生成各类文件
            List<GeneratedFile> files = new ArrayList<>();

            // Java后端代码
            files.add(generateFile(TEMPLATE_ENTITY, tableMeta, getEntityPath(tableMeta)));
            files.add(generateFile(TEMPLATE_MAPPER, tableMeta, getMapperPath(tableMeta)));
            files.add(generateFile(TEMPLATE_MAPPER_XML, tableMeta, getMapperXmlPath(tableMeta)));
            files.add(generateFile(TEMPLATE_SERVICE, tableMeta, getServicePath(tableMeta)));
            files.add(generateFile(TEMPLATE_SERVICE_IMPL, tableMeta, getServiceImplPath(tableMeta)));
            files.add(generateFile(TEMPLATE_CONTROLLER, tableMeta, getControllerPath(tableMeta)));

            // Vue前端代码
            files.add(generateFile(TEMPLATE_VUE_INDEX, tableMeta, getVueIndexPath(tableMeta)));
            files.add(generateFile(TEMPLATE_VUE_API, tableMeta, getVueApiPath(tableMeta)));
            files.add(generateFile(TEMPLATE_VUE_ADD, tableMeta, getVueAddPath(tableMeta)));
            files.add(generateFile(TEMPLATE_VUE_EDIT, tableMeta, getVueEditPath(tableMeta)));

            // 写入文件
            for (GeneratedFile file : files) {
                if (file.isSuccess()) {
                    writeFile(file);
                }
            }

            // 统计结果
            long successCount = files.stream().filter(GeneratedFile::isSuccess).count();
            long failCount = files.stream().filter(f -> !f.isSuccess()).count();

            log.info("Code generation completed for table '{}': {} success, {} failed",
                    tableName, successCount, failCount);

            return new CodeGenResult(true, tableMeta, files);

        } catch (Exception e) {
            log.error("Code generation failed for table: {}", tableName, e);
            return new CodeGenResult(false, null, List.of(), e.getMessage());
        }
    }

    /**
     * 批量生成代码
     *
     * @param tableNames 表名列表
     * @return 生成结果列表
     */
    public List<CodeGenResult> generateBatch(List<String> tableNames) {
        List<CodeGenResult> results = new ArrayList<>();
        for (String tableName : tableNames) {
            results.add(generate(tableName));
        }
        return results;
    }

    /**
     * 生成文件
     */
    private GeneratedFile generateFile(String templateKey, TableMetaData tableMeta, String outputPath) {
        try {
            String content = templateEngine.renderWithCache(templateKey, tableMeta);
            return GeneratedFile.success(templateKey, outputPath, content);
        } catch (Exception e) {
            log.error("Failed to generate file for template: {}", templateKey, e);
            return GeneratedFile.failure(templateKey, outputPath, e.getMessage());
        }
    }

    /**
     * 写入文件
     */
    private void writeFile(GeneratedFile file) {
        if (!file.isSuccess() || file.getContent() == null) {
            return;
        }

        try {
            Path path = Paths.get(file.getFilePath());
            Files.createDirectories(path.getParent());
            Files.writeString(path, file.getContent());
            log.debug("Written file: {}", file.getFilePath());
        } catch (IOException e) {
            log.error("Failed to write file: {}", file.getFilePath(), e);
            file.setError("Failed to write file: " + e.getMessage());
            file.setSuccess(false);
        }
    }

    // ==================== 路径生成方法 ====================

    private String getEntityPath(TableMetaData table) {
        return table.getBasePath() + "/" + table.getPackageName().replace('.', '/')
                + "/entity/" + table.getEntityName() + ".java";
    }

    private String getMapperPath(TableMetaData table) {
        return table.getBasePath() + "/" + table.getPackageName().replace('.', '/')
                + "/mapper/" + table.getEntityName() + "Mapper.java";
    }

    private String getMapperXmlPath(TableMetaData table) {
        return table.getBasePath() + "/mapper-xml/"
                + table.getEntityName() + "Mapper.xml";
    }

    private String getServicePath(TableMetaData table) {
        return table.getBasePath() + "/" + table.getPackageName().replace('.', '/')
                + "/service/" + table.getEntityName() + "Service.java";
    }

    private String getServiceImplPath(TableMetaData table) {
        return table.getBasePath() + "/" + table.getPackageName().replace('.', '/')
                + "/service/impl/" + table.getEntityName() + "ServiceImpl.java";
    }

    private String getControllerPath(TableMetaData table) {
        return table.getBasePath() + "/" + table.getPackageName().replace('.', '/')
                + "/controller/" + table.getEntityName() + "Controller.java";
    }

    private String getVueIndexPath(TableMetaData table) {
        return table.getBasePath() + "/vue/"
                + table.getModuleName() + "/" + table.getVariableName() + "/index.vue";
    }

    private String getVueApiPath(TableMetaData table) {
        return table.getBasePath() + "/vue/api/"
                + table.getModuleName() + "/" + table.getVariableName() + ".js";
    }

    private String getVueAddPath(TableMetaData table) {
        return table.getBasePath() + "/vue/"
                + table.getModuleName() + "/" + table.getVariableName() + "/add.vue";
    }

    private String getVueEditPath(TableMetaData table) {
        return table.getBasePath() + "/vue/"
                + table.getModuleName() + "/" + table.getVariableName() + "/edit.vue";
    }

    /**
     * 获取生成的文件数量
     */
    public int getGeneratedFileCount() {
        return fileCounter.get();
    }

    /**
     * 清除生成的缓存
     */
    public void clearCache() {
        generatedFiles.clear();
        fileCounter.set(0);
    }

    // ==================== 内部类 ====================

    /**
     * 生成的文件
     */
    @Data
    public static class GeneratedFile {
        private String templateKey;
        private String filePath;
        private String content;
        private boolean success;
        private String error;

        public static GeneratedFile success(String templateKey, String filePath, String content) {
            GeneratedFile file = new GeneratedFile();
            file.templateKey = templateKey;
            file.filePath = filePath;
            file.content = content;
            file.success = true;
            return file;
        }

        public static GeneratedFile failure(String templateKey, String filePath, String error) {
            GeneratedFile file = new GeneratedFile();
            file.templateKey = templateKey;
            file.filePath = filePath;
            file.success = false;
            file.error = error;
            return file;
        }
    }

    /**
     * 代码生成结果
     */
    @Data
    public static class CodeGenResult {
        private boolean success;
        private TableMetaData tableMeta;
        private List<GeneratedFile> files;
        private String errorMessage;
        private long timestamp;

        public CodeGenResult(boolean success, TableMetaData tableMeta, List<GeneratedFile> files) {
            this.success = success;
            this.tableMeta = tableMeta;
            this.files = files;
            this.timestamp = System.currentTimeMillis();
        }

        public CodeGenResult(boolean success, TableMetaData tableMeta, List<GeneratedFile> files, String errorMessage) {
            this.success = success;
            this.tableMeta = tableMeta;
            this.files = files;
            this.errorMessage = errorMessage;
            this.timestamp = System.currentTimeMillis();
        }

        public long getSuccessCount() {
            return files.stream().filter(GeneratedFile::isSuccess).count();
        }

        public long getFailCount() {
            return files.stream().filter(f -> !f.isSuccess()).count();
        }
    }
}
