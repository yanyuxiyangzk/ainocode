package com.ruoyi.nocode.common.core.codegen.generator;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;

/**
 * Service接口生成器
 *
 * @author ruoyi
 */
public class ServiceGenerator extends BaseCodeGenerator {

    private static final String TEMPLATE_NAME = "service.java";

    @Override
    protected void initTemplates() {
        String template = """
                package #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.service;

                import com.baomidou.mybatisplus.core.metadata.IPage;
                import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
                import #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.domain.${table.entityName};

                import java.util.List;

                /**
                 * $!{table.tableComment}Service接口
                 *
                 * @author $!{config.author}
                 */
                public interface ${table.entityName}Service {

                    /**
                     * 查询$!{table.tableComment}
                     *
                     * @param $!{table.primaryKeyFieldName} 主键
                     * @return $!{table.tableComment}
                     */
                    ${table.entityName} select${table.entityName}ById($table.primaryKeyJavaType $table.primaryKeyFieldName);

                    /**
                     * 查询$!{table.tableComment}分页列表
                     *
                     * @param page 分页参数
                     * @param $lowerFirstEntityName $!{table.tableComment}信息
                     * @return 分页结果
                     */
                    IPage<${table.entityName}> select${table.entityName}Page(Page<${table.entityName}> page, ${table.entityName} $lowerFirstEntityName);

                    /**
                     * 查询$!{table.tableComment}列表
                     *
                     * @param $lowerFirstEntityName $!{table.tableComment}信息
                     * @return $!{table.tableComment}列表
                     */
                    List<${table.entityName}> select${table.entityName}List(${table.entityName} $lowerFirstEntityName);

                    /**
                     * 新增$!{table.tableComment}
                     *
                     * @param $lowerFirstEntityName $!{table.tableComment}信息
                     * @return 结果
                     */
                    int insert${table.entityName}(${table.entityName} $lowerFirstEntityName);

                    /**
                     * 修改$!{table.tableComment}
                     *
                     * @param $lowerFirstEntityName $!{table.tableComment}信息
                     * @return 结果
                     */
                    int update${table.entityName}(${table.entityName} $lowerFirstEntityName);

                    /**
                     * 删除$!{table.tableComment}
                     *
                     * @param $table.primaryKeyFieldName 主键
                     * @return 结果
                     */
                    int delete${table.entityName}ById($table.primaryKeyJavaType $table.primaryKeyFieldName);

                    /**
                     * 批量删除$!{table.tableComment}
                     *
                     * @param $table.primaryKeyFieldNames 主键数组
                     * @return 结果
                     */
                    int delete${table.entityName}ByIds(List<$table.primaryKeyJavaType> $table.primaryKeyFieldNames);

                    /**
                     * 逻辑删除$!{table.tableComment}
                     *
                     * @param $table.primaryKeyFieldName 主键
                     * @return 结果
                     */
                    int logicDelete${table.entityName}ById($table.primaryKeyJavaType $table.primaryKeyFieldName);
                }
                """;
        templateEngine.registerTemplate(TEMPLATE_NAME, template);
    }

    @Override
    public String getGeneratorType() {
        return "service";
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

        return templateEngine.render(template, tableInfo, config);
    }

    @Override
    public String getFileName(TableInfo tableInfo, CodeGenConfig config) {
        return tableInfo.getEntityName() + "Service.java";
    }

    @Override
    public String getFilePath(TableInfo tableInfo, CodeGenConfig config) {
        String basePath = config.getJavaOutputPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = System.getProperty("user.dir") + "/src/main/java";
        }
        return basePath + "/" + getPackageName(tableInfo, config).replace(".", "/") + "/service/" + getFileName(tableInfo, config);
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
