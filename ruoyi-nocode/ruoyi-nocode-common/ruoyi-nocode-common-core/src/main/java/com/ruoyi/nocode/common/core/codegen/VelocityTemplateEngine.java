package com.ruoyi.nocode.common.core.codegen;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Velocity模板引擎封装
 *
 * @author ruoyi
 */
public class VelocityTemplateEngine {

    private static final Logger log = LoggerFactory.getLogger(VelocityTemplateEngine.class);

    private final VelocityEngine velocityEngine;

    /**
     * 缓存的模板内容
     */
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    /**
     * 单例实例
     */
    private static volatile VelocityTemplateEngine instance;

    private VelocityTemplateEngine() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        props.setProperty("input.encoding", "UTF-8");
        props.setProperty("output.encoding", "UTF-8");
        props.setProperty("runtime.log.invalid.references", "false");
        props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");

        this.velocityEngine = new VelocityEngine(props);
        this.velocityEngine.init();
    }

    /**
     * 获取单例实例
     */
    public static VelocityTemplateEngine getInstance() {
        if (instance == null) {
            synchronized (VelocityTemplateEngine.class) {
                if (instance == null) {
                    instance = new VelocityTemplateEngine();
                }
            }
        }
        return instance;
    }

    /**
     * 渲染模板
     *
     * @param templateContent 模板内容
     * @param context          上下文数据
     * @return 渲染后的内容
     */
    public String render(String templateContent, Map<String, Object> context) {
        if (templateContent == null || templateContent.isBlank()) {
            return "";
        }

        VelocityContext velocityContext = new VelocityContext(context);

        try (StringWriter writer = new StringWriter()) {
            velocityEngine.evaluate(velocityContext, writer, "template", templateContent);
            return writer.toString();
        } catch (Exception e) {
            log.error("Failed to render template", e);
            throw new RuntimeException("Failed to render template: " + e.getMessage(), e);
        }
    }

    /**
     * 渲染模板（快捷方法，使用TableMetaData）
     *
     * @param templateContent 模板内容
     * @param tableMeta       表元数据
     * @return 渲染后的内容
     */
    public String render(String templateContent, TableMetaData tableMeta) {
        VelocityContext context = new VelocityContext();
        context.put("table", tableMeta);
        context.put("columns", tableMeta.getColumns());
        context.put("pk", tableMeta.getPrimaryKeyColumn());
        context.put("pkType", tableMeta.getPrimaryKeyJavaType());
        context.put("pkField", tableMeta.getPrimaryKeyFieldName());
        context.put("entityName", tableMeta.getEntityName());
        context.put("variableName", tableMeta.getVariableName());
        context.put("packageName", tableMeta.getPackageName());
        context.put("author", tableMeta.getAuthor());
        context.put("email", tableMeta.getEmail());
        context.put("createTime", tableMeta.getCreateTime());
        context.put("tableComment", tableMeta.getTableComment());
        context.put("tableName", tableMeta.getTableName());
        context.put("moduleName", tableMeta.getModuleName());
        context.put("basePath", tableMeta.getBasePath());
        context.put("templateType", tableMeta.getTemplateType());

        try (StringWriter writer = new StringWriter()) {
            velocityEngine.evaluate(context, writer, "template", templateContent);
            return writer.toString();
        } catch (Exception e) {
            log.error("Failed to render template for table: {}", tableMeta.getTableName(), e);
            throw new RuntimeException("Failed to render template: " + e.getMessage(), e);
        }
    }

    /**
     * 缓存并渲染模板
     *
     * @param templateKey  模板键
     * @param tableMeta    表元数据
     * @return 渲染后的内容
     */
    public String renderWithCache(String templateKey, TableMetaData tableMeta) {
        String templateContent = templateCache.computeIfAbsent(templateKey, k -> {
            throw new IllegalStateException("Template not found in cache: " + templateKey);
        });
        return render(templateContent, tableMeta);
    }

    /**
     * 添加模板到缓存
     *
     * @param key    模板键
     * @param content 模板内容
     */
    public void addTemplate(String key, String content) {
        templateCache.put(key, content);
        log.debug("Added template to cache: {}", key);
    }

    /**
     * 从类路径加载模板
     *
     * @param path 类路径下的模板位置
     * @return 模板内容
     */
    public String loadTemplate(String path) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("Template not found on classpath: " + path);
            }
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to load template from path: {}", path, e);
            throw new RuntimeException("Failed to load template: " + path, e);
        }
    }

    /**
     * 加载并缓存模板
     *
     * @param key  模板键
     * @param path 类路径下的模板位置
     */
    public void loadAndCacheTemplate(String key, String path) {
        String content = loadTemplate(path);
        templateCache.put(key, content);
        log.debug("Loaded and cached template: {} from {}", key, path);
    }

    /**
     * 清除模板缓存
     */
    public void clearCache() {
        templateCache.clear();
        log.info("Template cache cleared");
    }

    /**
     * 获取缓存的模板数量
     */
    public int getCacheSize() {
        return templateCache.size();
    }

    /**
     * 检查模板是否存在
     */
    public boolean hasTemplate(String key) {
        return templateCache.containsKey(key);
    }

    /**
     * 移除指定模板
     */
    public void removeTemplate(String key) {
        templateCache.remove(key);
    }
}
