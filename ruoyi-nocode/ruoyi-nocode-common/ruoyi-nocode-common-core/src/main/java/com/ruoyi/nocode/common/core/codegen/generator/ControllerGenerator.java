package com.ruoyi.nocode.common.core.codegen.generator;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;

/**
 * Controller生成器
 *
 * @author ruoyi
 */
public class ControllerGenerator extends BaseCodeGenerator {

    private static final String TEMPLATE_NAME = "controller.java";

    @Override
    protected void initTemplates() {
        String template = """
                package #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.controller;

                #if($config.useSwagger)
                import io.swagger.v3.oas.annotations.Operation;
                import io.swagger.v3.oas.annotations.tags.Tag;
                #end
                import lombok.RequiredArgsConstructor;
                import com.ruoyi.nocode.common.core.domain.AjaxResult;
                import com.ruoyi.nocode.common.core.domain.PageQuery;
                import com.ruoyi.nocode.common.core.domain.TableDataInfo;
                #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.domain.${table.entityName};
                #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.service.I${table.entityName}Service;
                import org.springframework.security.access.prepost.PreAuthorize;
                import org.springframework.validation.annotation.Validated;
                import org.springframework.web.bind.annotation.*;

                #if($table.hasDateColumn)
                import java.time.LocalDateTime;
                #end
                import java.util.List;

                /**
                 * $!{table.tableComment}控制器
                 *
                 * @author $!{config.author}
                 */
                #if($config.useSwagger)
                @Tag(name = "$!{table.tableComment}管理")
                #end
                @RestController
                @RequestMapping("/$config.moduleName/#if($config.businessName)$config.businessName#{else}${table.lowerFirstEntityName}#end")
                @RequiredArgsConstructor
                public class ${table.entityName}Controller {

                    private final I${table.entityName}Service ${lowerFirstEntityName}Service;

                    /**
                     * 分页查询$!{table.tableComment}列表
                     */
                    @Operation(summary = "分页查询$!{table.tableComment}")
                    @PreAuthorize('@ss.hasPermi("$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerFirstEntityName}#end:list')")
                    @GetMapping("/list")
                    public TableDataInfo list(PageQuery pageQuery, ${table.entityName} $lowerFirstEntityName) {
                        return ${lowerFirstEntityName}Service.select${table.entityName}Page(pageQuery.toPage(), $lowerFirstEntityName).into(TableDataInfo::new);
                    }

                    /**
                     * 导出$!{table.tableComment}列表
                     */
                    @Operation(summary = "导出$!{table.tableComment}")
                    @PreAuthorize('@ss.hasPermi("$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerFirstEntityName}#end:export")')
                    @PostMapping("/export")
                    public void export(${table.entityName} $lowerFirstEntityName) {
                        List<${table.entityName}> list = ${lowerFirstEntityName}Service.select${table.entityName}List($lowerFirstEntityName);
                        // ExcelUtils.exportExcel(list, ${table.entityName}.class);
                    }

                    /**
                     * 获取$!{table.tableComment}详细信息
                     */
                    @Operation(summary = "获取$!{table.tableComment}详情")
                    @PreAuthorize('@ss.hasPermi("$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerFirstEntityName}#end:query")')
                    @GetMapping("/{$table.primaryKeyFieldName}")
                    public AjaxResult getInfo(@PathVariable("$table.primaryKeyFieldName") $table.primaryKeyJavaType $table.primaryKeyFieldName) {
                        return AjaxResult.success(${lowerFirstEntityName}Service.select${table.entityName}ById($table.primaryKeyFieldName));
                    }

                    /**
                     * 新增$!{table.tableComment}
                     */
                    @Operation(summary = "新增$!{table.tableComment}")
                    @PreAuthorize('@ss.hasPermi("$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerFirstEntityName}#end:add")')
                    @PostMapping
                    public AjaxResult add(@Validated @RequestBody ${table.entityName} $lowerFirstEntityName) {
                        return AjaxResult.success(${lowerFirstEntityName}Service.insert${table.entityName}($lowerFirstEntityName));
                    }

                    /**
                     * 修改$!{table.tableComment}
                     */
                    @Operation(summary = "修改$!{table.tableComment}")
                    @PreAuthorize('@ss.hasPermi("$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerFirstEntityName}#end:edit")')
                    @PutMapping
                    public AjaxResult edit(@Validated @RequestBody ${table.entityName} $lowerFirstEntityName) {
                        return AjaxResult.success(${lowerFirstEntityName}Service.update${table.entityName}($lowerFirstEntityName));
                    }

                    /**
                     * 删除$!{table.tableComment}
                     */
                    @Operation(summary = "删除$!{table.tableComment}")
                    @PreAuthorize('@ss.hasPermi("$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerFirstEntityName}#end:remove")')
                    @DeleteMapping("/{$table.primaryKeyFieldName$s}")
                    public AjaxResult remove(@PathVariable List<$table.primaryKeyJavaType> $table.primaryKeyFieldName$s) {
                        return AjaxResult.success(${lowerFirstEntityName}Service.delete${table.entityName}ByIds($table.primaryKeyFieldName$s));
                    }
                }
                """;
        templateEngine.registerTemplate(TEMPLATE_NAME, template);
    }

    @Override
    public String getGeneratorType() {
        return "controller";
    }

    @Override
    protected String doGenerate(TableInfo tableInfo, CodeGenConfig config) {
        String lowerFirstEntityName = lowerFirst(tableInfo.getEntityName());
        String pkFieldName = tableInfo.getPrimaryKey() != null ? tableInfo.getPrimaryKey().getJavaFieldName() : "id";
        String pkType = tableInfo.getPrimaryKey() != null ? tableInfo.getPrimaryKey().getJavaType() : "Long";

        String template = templateEngine.getTemplate(TEMPLATE_NAME);
        template = template.replace("$lowerFirstEntityName", lowerFirstEntityName);
        template = template.replace("$table.primaryKeyFieldName", pkFieldName);
        template = template.replace("$table.primaryKeyJavaType", pkType);
        template = template.replace("$table.primaryKeyFieldName", pkFieldName + "s");
        template = template.replace("$!{table.tableComment}", tableInfo.getTableComment() != null ? tableInfo.getTableComment() : tableInfo.getEntityName());
        template = template.replace("$!{table.lowerFirstEntityName}", lowerFirstEntityName);

        return templateEngine.render(template, tableInfo, config);
    }

    @Override
    public String getFileName(TableInfo tableInfo, CodeGenConfig config) {
        return tableInfo.getEntityName() + "Controller.java";
    }

    @Override
    public String getFilePath(TableInfo tableInfo, CodeGenConfig config) {
        String basePath = config.getJavaOutputPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = System.getProperty("user.dir") + "/src/main/java";
        }
        return basePath + "/" + getPackageName(tableInfo, config).replace(".", "/") + "/controller/" + getFileName(tableInfo, config);
    }

    @Override
    protected String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    protected String getModulePackage() {
        return "controller";
    }
}
