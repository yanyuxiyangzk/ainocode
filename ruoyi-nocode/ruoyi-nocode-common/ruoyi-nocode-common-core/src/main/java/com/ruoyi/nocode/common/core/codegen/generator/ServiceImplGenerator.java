package com.ruoyi.nocode.common.core.codegen.generator;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;

/**
 * ServiceImpl实现类生成器
 *
 * @author ruoyi
 */
public class ServiceImplGenerator extends BaseCodeGenerator {

    private static final String TEMPLATE_NAME = "serviceImpl.java";

    @Override
    protected void initTemplates() {
        String template = """
                package #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.service.impl;

                import com.baomidou.mybatisplus.core.metadata.IPage;
                import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
                import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
                import #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.domain.${table.entityName};
                import #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.mapper.${table.entityName}Mapper;
                import #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.service.I${table.entityName}Service;
                import lombok.RequiredArgsConstructor;
                import org.springframework.stereotype.Service;
                import org.springframework.transaction.annotation.Transactional;

                import java.util.List;

                /**
                 * $!{table.tableComment}Service实现
                 *
                 * @author $!{config.author}
                 */
                @Service
                @RequiredArgsConstructor
                public class ${table.entityName}ServiceImpl extends ServiceImpl<${table.entityName}Mapper, ${table.entityName}> implements I${table.entityName}Service {

                    private final ${table.entityName}Mapper ${lowerFirstEntityName}Mapper;

                    @Override
                    public ${table.entityName} select${table.entityName}ById($table.primaryKeyJavaType $table.primaryKeyFieldName) {
                        return this.getById($table.primaryKeyFieldName);
                    }

                    @Override
                    public IPage<${table.entityName}> select${table.entityName}Page(Page<${table.entityName}> page, ${table.entityName} $lowerFirstEntityName) {
                        return this.page(page, getQueryWrapper($lowerFirstEntityName));
                    }

                    @Override
                    public List<${table.entityName}> select${table.entityName}List(${table.entityName} $lowerFirstEntityName) {
                        return this.list(getQueryWrapper($lowerFirstEntityName));
                    }

                    #if($config.useLogicDelete)
                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int insert${table.entityName}(${table.entityName} $lowerFirstEntityName) {
                        #if($config.useTenant)
                        // 设置租户ID
                        // $lowerFirstEntityName.set$tenantFieldUpper(${table.entityName}.getClass());
                        #end
                        return this.save($lowerFirstEntityName) ? 1 : 0;
                    }

                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int update${table.entityName}(${table.entityName} $lowerFirstEntityName) {
                        return this.updateById($lowerFirstEntityName) ? 1 : 0;
                    }

                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int delete${table.entityName}ById($table.primaryKeyJavaType $table.primaryKeyFieldName) {
                        return this.removeById($table.primaryKeyFieldName) ? 1 : 0;
                    }

                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int delete${table.entityName}ByIds(List<$table.primaryKeyJavaType> $table.primaryKeyFieldName$s) {
                        return this.removeByIds($table.primaryKeyFieldName$s) ? 1 : 0;
                    }

                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int logicDelete${table.entityName}ById($table.primaryKeyJavaType $table.primaryKeyFieldName) {
                        ${table.entityName} entity = new ${table.entityName}();
                        entity.set${capitalizePrimaryKey}($table.primaryKeyFieldName);
                        entity.set${logicDeleteFieldUpper}(Short.parseShort("$logicDeleteValue"));
                        return this.updateById(entity) ? 1 : 0;
                    }
                    #else
                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int insert${table.entityName}(${table.entityName} $lowerFirstEntityName) {
                        return this.save($lowerFirstEntityName) ? 1 : 0;
                    }

                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int update${table.entityName}(${table.entityName} $lowerFirstEntityName) {
                        return this.updateById($lowerFirstEntityName) ? 1 : 0;
                    }

                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int delete${table.entityName}ById($table.primaryKeyJavaType $table.primaryKeyFieldName) {
                        return this.removeById($table.primaryKeyFieldName) ? 1 : 0;
                    }

                    @Override
                    @Transactional(rollbackFor = Exception.class)
                    public int delete${table.entityName}ByIds(List<$table.primaryKeyJavaType> $table.primaryKeyFieldName$s) {
                        return this.removeByIds($table.primaryKeyFieldName$s) ? 1 : 0;
                    }
                    #end

                    /**
                     * 构建查询条件
                     */
                    private LambdaQueryWrapper<${table.entityName}> getQueryWrapper(${table.entityName} $lowerFirstEntityName) {
                        LambdaQueryWrapper<${table.entityName}> wrapper = new LambdaQueryWrapper<>();
                        #foreach ($column in $columns)
                        #if($column.javaType == 'String' && !$column.required)
                        if (StrUtil.isNotBlank($lowerFirstEntityName.get${column.capitalizedFieldName}())) {
                            wrapper.like(${table.entityName}::get${column.capitalizedFieldName}, $lowerFirstEntityName.get${column.capitalizedFieldName}());
                        }
                        #end
                        #end
                        #if($config.useLogicDelete)
                        wrapper.eq(${table.entityName}::get${logicDeleteFieldUpper}, Short.parseShort("$logicNotDeleteValue"));
                        #end
                        wrapper.orderByDesc(${table.entityName}::getCreateTime);
                        return wrapper;
                    }
                }
                """;
        templateEngine.registerTemplate(TEMPLATE_NAME, template);
    }

    @Override
    public String getGeneratorType() {
        return "serviceImpl";
    }

    @Override
    protected String doGenerate(TableInfo tableInfo, CodeGenConfig config) {
        String lowerFirstEntityName = lowerFirst(tableInfo.getEntityName());
        String pkFieldName = tableInfo.getPrimaryKey() != null ? tableInfo.getPrimaryKey().getJavaFieldName() : "id";
        String pkType = tableInfo.getPrimaryKey() != null ? tableInfo.getPrimaryKey().getJavaType() : "Long";
        String pkCap = capitalize(pkFieldName);
        String logicDeleteField = config.getLogicDeleteField();
        String logicDeleteFieldUpper = capitalize(logicDeleteField);
        String logicDeleteValue = config.getLogicDeleteValue();
        String logicNotDeleteValue = config.getLogicNotDeleteValue();

        String template = templateEngine.getTemplate(TEMPLATE_NAME);
        template = template.replace("$lowerFirstEntityName", lowerFirstEntityName);
        template = template.replace("$table.primaryKeyFieldName", pkFieldName);
        template = template.replace("$table.primaryKeyJavaType", pkType);
        template = template.replace("$table.primaryKeyFieldName", pkFieldName + "s");
        template = template.replace("$!{table.tableComment}", tableInfo.getTableComment() != null ? tableInfo.getTableComment() : tableInfo.getEntityName());
        template = template.replace("${capitalizePrimaryKey}", pkCap);
        template = template.replace("${logicDeleteFieldUpper}", logicDeleteFieldUpper);
        template = template.replace("$logicDeleteValue", logicDeleteValue);
        template = template.replace("$logicNotDeleteValue", logicNotDeleteValue);

        return templateEngine.render(template, tableInfo, config);
    }

    @Override
    public String getFileName(TableInfo tableInfo, CodeGenConfig config) {
        return tableInfo.getEntityName() + "ServiceImpl.java";
    }

    @Override
    public String getFilePath(TableInfo tableInfo, CodeGenConfig config) {
        String basePath = config.getJavaOutputPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = System.getProperty("user.dir") + "/src/main/java";
        }
        return basePath + "/" + getPackageName(tableInfo, config).replace(".", "/") + "/service/impl/" + getFileName(tableInfo, config);
    }

    @Override
    protected String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    protected String getModulePackage() {
        return "service";
    }
}
