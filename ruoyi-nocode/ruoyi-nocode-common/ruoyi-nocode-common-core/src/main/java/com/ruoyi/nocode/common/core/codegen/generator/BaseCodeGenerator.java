package com.ruoyi.nocode.common.core.codegen.generator;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.engine.VelocityTemplateEngine;
import com.ruoyi.nocode.common.core.codegen.model.GeneratedFile;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;

/**
 * 代码生成器基类
 *
 * @author ruoyi
 */
public abstract class BaseCodeGenerator {

    protected final VelocityTemplateEngine templateEngine;

    protected BaseCodeGenerator() {
        this.templateEngine = new VelocityTemplateEngine();
        initTemplates();
    }

    /**
     * 初始化模板
     */
    protected abstract void initTemplates();

    /**
     * 获取生成器类型
     */
    public abstract String getGeneratorType();

    /**
     * 生成代码
     */
    public GeneratedFile generate(TableInfo tableInfo, CodeGenConfig config) {
        try {
            String content = doGenerate(tableInfo, config);
            String fileName = getFileName(tableInfo, config);
            String filePath = getFilePath(tableInfo, config);
            return GeneratedFile.success(fileName, filePath, content, getGeneratorType());
        } catch (Exception e) {
            return GeneratedFile.failure(getFileName(tableInfo, config), e.getMessage());
        }
    }

    /**
     * 执行生成
     */
    protected abstract String doGenerate(TableInfo tableInfo, CodeGenConfig config);

    /**
     * 获取文件名
     */
    public abstract String getFileName(TableInfo tableInfo, CodeGenConfig config);

    /**
     * 获取文件路径
     */
    public abstract String getFilePath(TableInfo tableInfo, CodeGenConfig config);

    /**
     * 渲染模板
     */
    protected String renderTemplate(TableInfo tableInfo, CodeGenConfig config) {
        String templateName = getTemplateName();
        String template = templateEngine.getTemplate(templateName);
        if (template == null) {
            throw new RuntimeException("Template not found: " + templateName);
        }
        return templateEngine.render(template, tableInfo, config);
    }

    /**
     * 获取模板名称
     */
    protected abstract String getTemplateName();

    /**
     * 获取包名
     */
    protected String getPackageName(TableInfo tableInfo, CodeGenConfig config) {
        return config.getPackageName() + "." + getModulePackage();
    }

    /**
     * 获取模块包名
     */
    protected abstract String getModulePackage();

    /**
     * 首字母大写
     */
    protected String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 首字母小写
     */
    protected String lowerFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
