package com.ruoyi.nocode.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.nocode.common.core.exception.ServiceException;
import com.ruoyi.nocode.system.entity.GenTable;
import com.ruoyi.nocode.system.entity.GenTableColumn;
import com.ruoyi.nocode.system.mapper.GenTableColumnMapper;
import com.ruoyi.nocode.system.mapper.GenTableMapper;
import com.ruoyi.nocode.system.service.IGenCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成服务实现
 * 
 * @author ruoyi-nocode
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenCodeServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements IGenCodeService {

    private final GenTableColumnMapper genTableColumnMapper;
    private final GenTableMapper genTableMapper;

    // 模板路径
    private static final String TPL_ENTITY = "templates/velocity/entity.java.vm";
    private static final String TPL_MAPPER = "templates/velocity/mapper.java.vm";
    private static final String TPL_SERVICE = "templates/velocity/service.java.vm";
    private static final String TPL_SERVICE_IMPL = "templates/velocity/serviceImpl.java.vm";
    private static final String TPL_CONTROLLER = "templates/velocity/controller.java.vm";

    // 默认配置
    private static final String DEFAULT_PACKAGE = "com.ruoyi.nocode.system";
    private static final String DEFAULT_AUTHOR = "ruoyi-nocode";
    private static final String DEFAULT_MODULE = "system";

    @Override
    public List<GenTable> selectGenTableList(GenTable genTable) {
        return genTableMapper.selectGenTableList(genTable);
    }

    @Override
    public List<Map<String, Object>> selectDbTableList(String tableName) {
        return genTableMapper.selectDbTableList(tableName);
    }

    @Override
    public Map<String, Object> selectDbTableByName(String tableName) {
        return genTableMapper.selectDbTableByName(tableName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importGenTable(List<String> tableNames) {
        if (tableNames == null || tableNames.isEmpty()) {
            throw new ServiceException("请选择要导入的表");
        }

        for (String tableName : tableNames) {
            Map<String, Object> tableInfo = genTableMapper.selectDbTableByName(tableName);
            if (tableInfo == null) {
                continue;
            }

            // 创建生成表记录
            GenTable genTable = new GenTable();
            genTable.setTableName(tableName);
            genTable.setTableComment((String) tableInfo.get("tableComment"));
            genTable.setClassName(toCamelCase(tableName, true));
            genTable.setPackageName(DEFAULT_PACKAGE);
            genTable.setModuleName(DEFAULT_MODULE);
            genTable.setBusinessName(toCamelCase(tableName, false));
            genTable.setFunctionName((String) tableInfo.get("tableComment"));
            genTable.setGenType("0");
            genTable.setGenPath("/");
            genTable.setDbType("PostgreSQL");
            genTable.setCreateTime(LocalDateTime.now());
            genTable.setUpdateTime(LocalDateTime.now());

            save(genTable);

            // 导入字段信息
            List<Map<String, Object>> columns = genTableMapper.selectTableColumns(tableName);
            List<GenTableColumn> genColumns = new ArrayList<>();
            int sort = 0;

            for (Map<String, Object> column : columns) {
                GenTableColumn genColumn = new GenTableColumn();
                genColumn.setTableId(genTable.getTableId());
                genColumn.setColumnName((String) column.get("columnName"));
                genColumn.setColumnComment((String) column.get("columnComment"));
                genColumn.setColumnType((String) column.get("columnType"));
                genColumn.setJavaType(getJavaType((String) column.get("columnType")));
                genColumn.setJavaField(toCamelCase((String) column.get("columnName"), false));
                genColumn.setIsPk("1".equals(column.get("isPk")) ? "1" : "0");
                genColumn.setIsIncrement("1".equals(column.get("isIncrement")) ? "1" : "0");
                genColumn.setIsRequired("NO".equals(column.get("isNullable")) ? "1" : "0");
                genColumn.setIsInsert("1".equals(column.get("isPk")) ? "0" : "1");
                genColumn.setIsEdit("1".equals(column.get("isPk")) ? "0" : "1");
                genColumn.setIsList("1".equals(column.get("isPk")) ? "0" : "1");
                genColumn.setIsQuery("0");
                genColumn.setQueryType(GenTableColumn.QUERY_EQ);
                genColumn.setHtmlType(GenTableColumn.HTML_INPUT);
                genColumn.setSort(sort++);

                genColumns.add(genColumn);

                // 记录主键列
                if ("1".equals(genColumn.getIsPk())) {
                    genTable.setPkColumn(genColumn.getColumnName());
                }
            }

            genTableColumnMapper.batchInsertGenTableColumn(genColumns);
            updateById(genTable);
        }
    }

    @Override
    public GenTable selectGenTableById(Long tableId) {
        GenTable genTable = getById(tableId);
        if (genTable != null) {
            List<GenTableColumn> columns = genTableColumnMapper.selectGenTableColumnByTableId(tableId);
            genTable.setColumns(columns);

            // 设置主键列对象
            for (GenTableColumn column : columns) {
                if ("1".equals(column.getIsPk())) {
                    genTable.setPkColumnObj(column);
                    break;
                }
            }
        }
        return genTable;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateGenTable(GenTable genTable) {
        genTable.setUpdateTime(LocalDateTime.now());
        int result = genTableMapper.updateById(genTable);

        // 更新字段信息
        if (genTable.getColumns() != null) {
            for (GenTableColumn column : genTable.getColumns()) {
                genTableColumnMapper.updateById(column);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteGenTableByIds(Long[] tableIds) {
        int result = 0;
        for (Long tableId : tableIds) {
            genTableColumnMapper.deleteGenTableColumnByTableId(tableId);
            result += genTableMapper.deleteById(tableId);
        }
        return result;
    }

    @Override
    public Map<String, String> previewCode(Long tableId) {
        GenTable genTable = selectGenTableById(tableId);
        if (genTable == null) {
            throw new ServiceException("表信息不存在");
        }

        Map<String, String> dataMap = new LinkedHashMap<>();
        VelocityContext context = prepareContext(genTable);

        // 生成各层代码
        dataMap.put("entity.java", renderTemplate(TPL_ENTITY, context));
        dataMap.put("mapper.java", renderTemplate(TPL_MAPPER, context));
        dataMap.put("service.java", renderTemplate(TPL_SERVICE, context));
        dataMap.put("serviceImpl.java", renderTemplate(TPL_SERVICE_IMPL, context));
        dataMap.put("controller.java", renderTemplate(TPL_CONTROLLER, context));

        return dataMap;
    }

    @Override
    public byte[] downloadCode(Long tableId) {
        Map<String, String> dataMap = previewCode(tableId);
        return createZip(dataMap);
    }

    @Override
    public void generatorCode(Long tableId) {
        GenTable genTable = selectGenTableById(tableId);
        if (genTable == null) {
            throw new ServiceException("表信息不存在");
        }

        String genPath = genTable.getGenPath();
        if (StringUtils.isBlank(genPath)) {
            genPath = System.getProperty("user.dir") + "/generated";
        }

        Map<String, String> dataMap = previewCode(tableId);

        // 写入文件
        String packageName = genTable.getPackageName();
        String className = genTable.getClassName();

        try {
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                String fileName = entry.getKey();
                String content = entry.getValue();
                String filePath = buildFilePath(genPath, packageName, className, fileName);
                java.nio.file.Path path = java.nio.file.Paths.get(filePath);
                java.nio.file.Files.createDirectories(path.getParent());
                java.nio.file.Files.writeString(path, content);
            }
            log.info("代码生成成功: {}", genPath);
        } catch (IOException e) {
            log.error("代码生成失败", e);
            throw new ServiceException("代码生成失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] downloadCodeBatch(Long[] tableIds) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            for (Long tableId : tableIds) {
                GenTable genTable = selectGenTableById(tableId);
                if (genTable == null) continue;

                String className = genTable.getClassName();
                Map<String, String> dataMap = previewCode(tableId);

                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    String fileName = entry.getKey();
                    String content = entry.getValue();
                    ZipEntry zipEntry = new ZipEntry(className + "/" + fileName);
                    zip.putNextEntry(zipEntry);
                    zip.write(content.getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
            }
        } catch (IOException e) {
            log.error("批量生成代码失败", e);
            throw new ServiceException("批量生成代码失败: " + e.getMessage());
        }
        return outputStream.toByteArray();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchDbTable(Long tableId) {
        GenTable genTable = getById(tableId);
        if (genTable == null) {
            throw new ServiceException("表信息不存在");
        }

        String tableName = genTable.getTableName();
        List<Map<String, Object>> dbColumns = genTableMapper.selectTableColumns(tableName);
        List<GenTableColumn> existColumns = genTableColumnMapper.selectGenTableColumnByTableId(tableId);

        // 同步字段
        int sort = existColumns.size();
        for (Map<String, Object> dbColumn : dbColumns) {
            String columnName = (String) dbColumn.get("columnName");
            boolean exists = existColumns.stream()
                    .anyMatch(c -> columnName.equals(c.getColumnName()));

            if (!exists) {
                GenTableColumn genColumn = new GenTableColumn();
                genColumn.setTableId(tableId);
                genColumn.setColumnName(columnName);
                genColumn.setColumnComment((String) dbColumn.get("columnComment"));
                genColumn.setColumnType((String) dbColumn.get("columnType"));
                genColumn.setJavaType(getJavaType((String) dbColumn.get("columnType")));
                genColumn.setJavaField(toCamelCase(columnName, false));
                genColumn.setIsPk("1".equals(dbColumn.get("isPk")) ? "1" : "0");
                genColumn.setIsInsert("1");
                genColumn.setIsEdit("1");
                genColumn.setIsList("1");
                genColumn.setIsQuery("0");
                genColumn.setQueryType(GenTableColumn.QUERY_EQ);
                genColumn.setHtmlType(GenTableColumn.HTML_INPUT);
                genColumn.setSort(sort++);

                genTableColumnMapper.insert(genColumn);
            }
        }

        genTable.setUpdateTime(LocalDateTime.now());
        updateById(genTable);
    }

    @Override
    public List<Map<String, Object>> selectTableColumns(String tableName) {
        return genTableMapper.selectTableColumns(tableName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTableColumns(Long tableId, List<GenTableColumn> columns) {
        for (GenTableColumn column : columns) {
            genTableColumnMapper.updateById(column);
        }
    }

    /**
     * 准备模板上下文
     */
    private VelocityContext prepareContext(GenTable genTable) {
        VelocityContext context = new VelocityContext();

        context.put("packageName", genTable.getPackageName());
        context.put("moduleName", genTable.getModuleName());
        context.put("businessName", genTable.getBusinessName());
        context.put("className", genTable.getClassName());
        context.put("tableName", genTable.getTableName());
        context.put("functionName", genTable.getFunctionName());
        context.put("author", DEFAULT_AUTHOR);
        context.put("columns", genTable.getColumns());
        context.put("pkColumn", genTable.getPkColumnObj());
        context.put("table", genTable);

        // 导入列表
        Set<String> importList = new TreeSet<>();
        for (GenTableColumn column : genTable.getColumns()) {
            String javaType = column.getJavaType();
            if ("BigDecimal".equals(javaType)) {
                importList.add("java.math.BigDecimal");
            } else if ("Date".equals(javaType)) {
                importList.add("java.util.Date");
            }
        }
        context.put("importList", importList);

        return context;
    }

    /**
     * 渲染模板
     */
    private String renderTemplate(String templatePath, VelocityContext context) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", 
                org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader.class);
        engine.init();

        Template template = engine.getTemplate(templatePath, "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    /**
     * 创建 ZIP 压缩包
     */
    private byte[] createZip(Map<String, String> dataMap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zip.putNextEntry(zipEntry);
                zip.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
        } catch (IOException e) {
            log.error("创建ZIP失败", e);
            throw new ServiceException("创建ZIP失败: " + e.getMessage());
        }
        return outputStream.toByteArray();
    }

    /**
     * 构建文件路径
     */
    private String buildFilePath(String basePath, String packageName, String className, String fileName) {
        String packagePath = packageName.replace(".", "/");
        String fileType = fileName.substring(0, fileName.lastIndexOf("."));
        String subPath;

        switch (fileType) {
            case "entity":
                subPath = "/entity/" + className + ".java";
                break;
            case "mapper":
                subPath = "/mapper/" + className + "Mapper.java";
                break;
            case "service":
                subPath = "/service/I" + className + "Service.java";
                break;
            case "serviceImpl":
                subPath = "/service/impl/" + className + "ServiceImpl.java";
                break;
            case "controller":
                subPath = "/controller/" + className + "Controller.java";
                break;
            default:
                subPath = "/" + fileName;
        }

        return basePath + "/" + packagePath + subPath;
    }

    /**
     * 下划线转驼峰
     */
    private String toCamelCase(String str, boolean firstUpper) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpper = firstUpper;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }

        return result.toString();
    }

    /**
     * 获取 Java 类型
     */
    private String getJavaType(String columnType) {
        if (StringUtils.isBlank(columnType)) {
            return GenTableColumn.TYPE_STRING;
        }

        columnType = columnType.toLowerCase();

        if (columnType.contains("char") || columnType.contains("text")) {
            return GenTableColumn.TYPE_STRING;
        } else if (columnType.contains("bigint")) {
            return GenTableColumn.TYPE_LONG;
        } else if (columnType.contains("int") || columnType.contains("smallint") || columnType.contains("tinyint")) {
            return GenTableColumn.TYPE_INTEGER;
        } else if (columnType.contains("decimal") || columnType.contains("numeric")) {
            return GenTableColumn.TYPE_DECIMAL;
        } else if (columnType.contains("float") || columnType.contains("double")) {
            return GenTableColumn.TYPE_DOUBLE;
        } else if (columnType.contains("datetime") || columnType.contains("timestamp") || columnType.contains("date")) {
            return GenTableColumn.TYPE_DATE;
        } else if (columnType.contains("bit") || columnType.contains("boolean")) {
            return GenTableColumn.TYPE_BOOLEAN;
        } else {
            return GenTableColumn.TYPE_STRING;
        }
    }
}
