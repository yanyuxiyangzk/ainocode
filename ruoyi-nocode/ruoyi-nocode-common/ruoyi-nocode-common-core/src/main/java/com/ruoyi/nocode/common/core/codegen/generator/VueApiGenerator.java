package com.ruoyi.nocode.common.core.codegen.generator;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;

/**
 * Vue API模块生成器
 *
 * @author ruoyi
 */
public class VueApiGenerator extends BaseCodeGenerator {

    private static final String TEMPLATE_NAME = "vue/api.js";

    @Override
    protected void initTemplates() {
        String template = """
                import request from '@/utils/request'

                // 查询${table.tableComment}分页列表
                export function list${table.entityName}(query) {
                  return request({
                    url: '/$config.moduleName/#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end/list',
                    method: 'get',
                    params: query
                  })
                }

                // 查询${table.tableComment}详细
                export function get${table.entityName}($pkJsFieldName) {
                  return request({
                    url: '/$config.moduleName/#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end/' + $pkJsFieldName,
                    method: 'get'
                  })
                }

                // 新增${table.tableComment}
                export function add${table.entityName}(data) {
                  return request({
                    url: '/$config.moduleName/#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end',
                    method: 'post',
                    data: data
                  })
                }

                // 修改${table.tableComment}
                export function update${table.entityName}(data) {
                  return request({
                    url: '/$config.moduleName/#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end',
                    method: 'put',
                    data: data
                  })
                }

                // 删除${table.tableComment}
                export function del${table.entityName}($pkJsFieldName) {
                  return request({
                    url: '/$config.moduleName/#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end/' + $pkJsFieldName,
                    method: 'delete'
                  })
                }

                // 导出${table.tableComment}
                export function export${table.entityName}(query) {
                  return request({
                    url: '/$config.moduleName/#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end/export',
                    method: 'post',
                    params: query
                  })
                }
                """;
        templateEngine.registerTemplate(TEMPLATE_NAME, template);
    }

    @Override
    public String getGeneratorType() {
        return "vueApi";
    }

    @Override
    protected String doGenerate(TableInfo tableInfo, CodeGenConfig config) {
        String pkJsFieldName = tableInfo.getPrimaryKey() != null ?
                tableInfo.getPrimaryKey().getJavaFieldName() : "id";
        String lowerCaseEntityName = tableInfo.getEntityName().toLowerCase();

        String template = templateEngine.getTemplate(TEMPLATE_NAME);
        template = template.replace("$pkJsFieldName", pkJsFieldName);
        template = template.replace("${table.lowerCaseEntityName}", lowerCaseEntityName);
        template = template.replace("$!{table.tableComment}", tableInfo.getTableComment() != null ? tableInfo.getTableComment() : tableInfo.getEntityName());

        return templateEngine.render(template, tableInfo, config);
    }

    @Override
    public String getFileName(TableInfo tableInfo, CodeGenConfig config) {
        return tableInfo.getEntityName().toLowerCase() + ".js";
    }

    @Override
    public String getFilePath(TableInfo tableInfo, CodeGenConfig config) {
        String basePath = config.getVueOutputPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = System.getProperty("user.dir") + "/src/api";
        }
        String modulePath = config.getModuleName();
        if (config.getBusinessName() != null && !config.getBusinessName().isEmpty()) {
            modulePath = config.getBusinessName();
        }
        return basePath + "/" + modulePath + "/" + getFileName(tableInfo, config);
    }

    @Override
    protected String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    protected String getModulePackage() {
        return "vueApi";
    }
}
