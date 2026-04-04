package com.ruoyi.nocode.common.core.codegen.generator;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;

/**
 * Entity实体类生成器
 *
 * @author ruoyi
 */
public class EntityGenerator extends BaseCodeGenerator {

    private static final String TEMPLATE_NAME = "entity.java";

    @Override
    protected void initTemplates() {
        String template = """
                package #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.domain;

                #if($config.useSwagger)
                import io.swagger.v3.oas.annotations.media.Schema;
                #if($config.useLombok)
                import io.swagger.v3.oas.annotations.media.Schema;
                #end
                #end
                #if($config.useLombok)
                import lombok.Data;
                import lombok.NoArgsConstructor;
                import lombok.AllArgsConstructor;
                import lombok.Builder;
                #end
                import java.io.Serial;
                import java.io.Serializable;
                #foreach ($import in $importList)
                import $import;
                #end

                /**
                 * $!{table.tableComment}实体
                 *
                 * @author $!{config.author}
                 */
                #if($config.useLombok)
                @Data
                @NoArgsConstructor
                @AllArgsConstructor
                @Builder
                #end
                public class ${table.entityName} implements Serializable {

                    @Serial
                    private static final long serialVersionUID = 1L;

                #foreach ($column in $columns)
                    #if($column.comment && $column.comment != '')
                    /** $column.comment */
                    #end
                    #if($config.useSwagger)
                    @Schema(description = "$column.comment")
                    #end
                    #if($config.useLombok && $column.javaType == 'LocalDateTime')
                    #set($addTransient = true)
                    #end
                    private $column.javaType#if($column.javaType == 'String' && !$column.required)## end#if($column.defaultValue) = $column.defaultValue;#else ;#end

                #end
                }
                """;
        templateEngine.registerTemplate(TEMPLATE_NAME, template);
    }

    @Override
    public String getGeneratorType() {
        return "entity";
    }

    @Override
    protected String doGenerate(TableInfo tableInfo, CodeGenConfig config) {
        return renderTemplate(tableInfo, config);
    }

    @Override
    public String getFileName(TableInfo tableInfo, CodeGenConfig config) {
        return tableInfo.getEntityName() + ".java";
    }

    @Override
    public String getFilePath(TableInfo tableInfo, CodeGenConfig config) {
        String basePath = config.getJavaOutputPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = System.getProperty("user.dir") + "/src/main/java";
        }
        return basePath + "/" + getPackageName(tableInfo, config).replace(".", "/") + "/domain/" + getFileName(tableInfo, config);
    }

    @Override
    protected String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    protected String getModulePackage() {
        return "domain";
    }
}
