package com.ruoyi.nocode.common.core.codegen.engine;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Velocity模板引擎
 *
 * @author ruoyi
 */
public class VelocityTemplateEngine {

    private static final Logger log = LoggerFactory.getLogger(VelocityTemplateEngine.class);

    private final VelocityEngine velocityEngine;

    private final Map<String, String> templates = new ConcurrentHashMap<>();

    public VelocityTemplateEngine() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "string");
        props.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        props.setProperty("string.resource.loader.repository.static", "false");
        props.setProperty("input.encoding", "UTF-8");
        props.setProperty("output.encoding", "UTF-8");
        props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");

        this.velocityEngine = new VelocityEngine(props);
        this.velocityEngine.init();
    }

    /**
     * 渲染模板
     *
     * @param templateContent 模板内容
     * @param context         上下文数据
     * @return 渲染结果
     */
    public String render(String templateContent, Map<String, Object> context) {
        if (templateContent == null || templateContent.isEmpty()) {
            return "";
        }

        VelocityContext velocityContext = new VelocityContext(context);
        StringWriter writer = new StringWriter();

        try {
            velocityEngine.evaluate(velocityContext, writer, "template", templateContent);
            return writer.toString();
        } catch (Exception e) {
            log.error("Template rendering failed", e);
            throw new RuntimeException("Template rendering failed: " + e.getMessage(), e);
        }
    }

    /**
     * 渲染表信息
     *
     * @param templateContent 模板内容
     * @param tableInfo        表信息
     * @param config           配置
     * @return 渲染结果
     */
    public String render(String templateContent, TableInfo tableInfo, CodeGenConfig config) {
        VelocityContext context = new VelocityContext();
        context.put("table", tableInfo);
        context.put("config", config);
        context.put("columns", tableInfo.getColumns());
        context.put("primaryKey", tableInfo.getPrimaryKey());
        context.put("pkField", tableInfo.getPrimaryKeyFieldName());
        context.put("pkType", tableInfo.getPrimaryKeyJavaType());
        context.put("entityName", tableInfo.getEntityName());
        context.put("packageName", config.getPackageName());
        context.put("moduleName", config.getModuleName());
        context.put("businessName", config.getBusinessName());
        context.put("functionName", config.getFunctionName());
        context.put("author", config.getAuthor());
        context.put("email", config.getEmail());
        context.put("useLombok", config.isUseLombok());
        context.put("useSwagger", config.isUseSwagger());
        context.put("useLogicDelete", config.isUseLogicDelete());
        context.put("useTenant", config.isUseTenant());
        context.put("logicDeleteField", config.getLogicDeleteField());
        context.put("logicDeleteValue", config.getLogicDeleteValue());
        context.put("logicNotDeleteValue", config.getLogicNotDeleteValue());
        context.put("tableName", tableInfo.getTableName());
        context.put("tableComment", tableInfo.getTableComment());
        context.put("entityComment", tableInfo.getEntityComment());
        context.put("javaDateType", "java.time.LocalDateTime");
        context.put("stringUtils", new StringUtils());

        StringWriter writer = new StringWriter();
        try {
            velocityEngine.evaluate(context, writer, "template", templateContent);
            return writer.toString();
        } catch (Exception e) {
            log.error("Table template rendering failed", e);
            throw new RuntimeException("Table template rendering failed: " + e.getMessage(), e);
        }
    }

    /**
     * 注册模板
     */
    public void registerTemplate(String name, String content) {
        templates.put(name, content);
    }

    /**
     * 获取模板
     */
    public String getTemplate(String name) {
        return templates.get(name);
    }

    /**
     * 判断模板是否存在
     */
    public boolean hasTemplate(String name) {
        return templates.containsKey(name);
    }

    /**
     * String工具类（用于模板中调用）
     */
    public static class StringUtils {

        public boolean isEmpty(String str) {
            return str == null || str.isEmpty();
        }

        public boolean isNotEmpty(String str) {
            return !isEmpty(str);
        }

        public String capitalize(String str) {
            if (isEmpty(str)) {
                return str;
            }
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }

        public String lowerFirst(String str) {
            if (isEmpty(str)) {
                return str;
            }
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }

        public String upperFirst(String str) {
            if (isEmpty(str)) {
                return str;
            }
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }

        public String format(String template, Object... args) {
            if (isEmpty(template)) {
                return template;
            }
            return String.format(template, args);
        }

        public boolean equals(String a, String b) {
            return a == null ? b == null : a.equals(b);
        }

        public boolean equalsIgnoreCase(String a, String b) {
            return a == null ? b == null : a.equalsIgnoreCase(b);
        }

        public String defaultString(String str) {
            return str == null ? "" : str;
        }

        public String defaultString(String str, String defaultStr) {
            return str == null ? defaultStr : str;
        }
    }
}
