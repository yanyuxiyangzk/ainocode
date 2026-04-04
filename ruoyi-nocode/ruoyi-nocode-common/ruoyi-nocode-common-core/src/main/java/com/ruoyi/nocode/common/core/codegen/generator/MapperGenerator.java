package com.ruoyi.nocode.common.core.codegen.generator;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.model.ColumnInfo;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mapper接口生成器
 *
 * @author ruoyi
 */
public class MapperGenerator extends BaseCodeGenerator {

    private static final String TEMPLATE_NAME = "mapper.java";

    @Override
    protected void initTemplates() {
        String template = """
                package #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.mapper;

                #if($config.useSwagger)
                import io.swagger.v3.oas.annotations.Operation;
                #end
                import org.apache.ibatis.annotations.Param;
                import com.baomidou.mybatisplus.core.mapper.BaseMapper;
                import com.baomidou.mybatisplus.core.metadata.IPage;
                import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
                import #if(${config.packageName})#${config.packageName}#end.#if($config.moduleName)$config.moduleName#end.domain.${table.entityName};

                /**
                 * $!{table.tableComment}Mapper接口
                 *
                 * @author $!{config.author}
                 */
                public interface ${table.entityName}Mapper extends BaseMapper<${table.entityName}> {

                    /**
                     * 查询$!{table.tableComment}分页列表
                     *
                     * @param page 分页参数
                     * @param $!{table.lowerFirstEntityName} $!{table.tableComment}信息
                     * @return 分页结果
                     */
                    IPage<${table.entityName}> selectPage${table.entityName}List(Page<${table.entityName}> page, @Param("data") ${table.entityName} $!{table.lowerFirstEntityName});

                    /**
                     * 根据条件查询$!{table.tableComment}列表
                     *
                     * @param $!{table.lowerFirstEntityName} $!{table.tableComment}信息
                     * @return $!{table.tableComment}列表
                     */
                    List<${table.entityName}> select${table.entityName}List(@Param("data") ${table.entityName} $!{table.lowerFirstEntityName});

                    /**
                     * 根据主键查询$!{table.tableComment}
                     *
                     * @param $!{table.primaryKeyFieldName} 主键
                     * @return $!{table.tableComment}
                     */
                    ${table.entityName} select${table.entityName}By#foreach($pk in $table.primaryKey)#if($foreach.count > 1)And#end${pk.capitalizedFieldName}#end(#foreach($pk in $table.primaryKey)@Param("$pk.javaFieldName") $pk.javaType $pk.lowerFieldName#if($foreach.count < $table.primaryKey.size()), #end#end);

                    /**
                     * 新增$!{table.tableComment}
                     *
                     * @param $!{table.lowerFirstEntityName} $!{table.tableComment}信息
                     * @return 结果
                     */
                    int insert${table.entityName}(@Param("data") ${table.entityName} $!{table.lowerFirstEntityName});

                    /**
                     * 修改$!{table.tableComment}
                     *
                     * @param $!{table.lowerFirstEntityName} $!{table.tableComment}信息
                     * @return 结果
                     */
                    int update${table.entityName}(@Param("data") ${table.entityName} $!{table.lowerFirstEntityName});

                    /**
                     * 删除$!{table.tableComment}
                     *
                     * @param $!{table.primaryKeyFieldName} 主键
                     * @return 结果
                     */
                    int delete${table.entityName}By#foreach($pk in $table.primaryKey)#if($foreach.count > 1)And#end${pk.capitalizedFieldName}#end(#foreach($pk in $table.primaryKey)@Param("$pk.javaFieldName") $pk.javaType $pk.lowerFieldName#if($foreach.count < $table.primaryKey.size()), #end#end);

                    /**
                     * 批量删除$!{table.tableComment}
                     *
                     * @param $!{table.primaryKeyFieldName}s 主键数组
                     * @return 结果
                     */
                    int delete${table.entityName}By#foreach($pk in $table.primaryKey)#if($foreach.count > 1)And#end${pk.capitalizedFieldName}#end(@Param("$table.primaryKeyFieldName"s) List<$table.primaryKeyJavaType> $table.primaryKeyFieldName"s);
                }
                """;
        templateEngine.registerTemplate(TEMPLATE_NAME, template);
    }

    @Override
    public String getGeneratorType() {
        return "mapper";
    }

    @Override
    protected String doGenerate(TableInfo tableInfo, CodeGenConfig config) {
        // 添加额外的上下文
        String lowerFirstEntityName = lowerFirst(tableInfo.getEntityName());
        String pkFieldName = tableInfo.getPrimaryKey() != null ? tableInfo.getPrimaryKey().getJavaFieldName() : "id";
        String pkType = tableInfo.getPrimaryKey() != null ? tableInfo.getPrimaryKey().getJavaType() : "Long";

        String template = templateEngine.getTemplate(TEMPLATE_NAME);
        template = template.replace("$!{table.lowerFirstEntityName}", lowerFirstEntityName);
        template = template.replace("$!{table.primaryKeyFieldName}", pkFieldName);
        template = template.replace("$!{table.primaryKeyJavaType}", pkType);
        template = template.replace("$!{table.tableComment}", tableInfo.getTableComment() != null ? tableInfo.getTableComment() : tableInfo.getEntityName());

        // 处理主键相关
        template = processPrimaryKeyPlaceholders(template, tableInfo);

        return templateEngine.render(template, tableInfo, config);
    }

    private String processPrimaryKeyPlaceholders(String template, TableInfo tableInfo) {
        if (tableInfo.getPrimaryKey() == null) {
            return template;
        }

        String pk = tableInfo.getPrimaryKey().getJavaFieldName();
        String pkCap = capitalize(pk);
        String pkType = tableInfo.getPrimaryKey().getJavaType();

        template = template.replace("${pk.capitalizedFieldName}", pkCap);
        template = template.replace("${pk.lowerFieldName}", lowerFirst(pk));
        template = template.replace("${pk.javaType}", pkType);

        return template;
    }

    @Override
    public String getFileName(TableInfo tableInfo, CodeGenConfig config) {
        return tableInfo.getEntityName() + "Mapper.java";
    }

    @Override
    public String getFilePath(TableInfo tableInfo, CodeGenConfig config) {
        String basePath = config.getJavaOutputPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = System.getProperty("user.dir") + "/src/main/java";
        }
        return basePath + "/" + getPackageName(tableInfo, config).replace(".", "/") + "/mapper/" + getFileName(tableInfo, config);
    }

    @Override
    protected String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    protected String getModulePackage() {
        return "mapper";
    }

    private String lowerFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
