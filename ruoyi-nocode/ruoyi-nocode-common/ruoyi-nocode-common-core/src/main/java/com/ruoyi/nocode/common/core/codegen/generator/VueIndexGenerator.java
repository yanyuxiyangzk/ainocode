package com.ruoyi.nocode.common.core.codegen.generator;

import com.ruoyi.nocode.common.core.codegen.config.CodeGenConfig;
import com.ruoyi.nocode.common.core.codegen.model.ColumnInfo;
import com.ruoyi.nocode.common.core.codegen.model.TableInfo;

import java.util.List;

/**
 * Vue Index页面生成器
 *
 * @author ruoyi
 */
public class VueIndexGenerator extends BaseCodeGenerator {

    private static final String TEMPLATE_NAME = "vue/index.vue";

    @Override
    protected void initTemplates() {
        String template = """
                <template>
                  <div class="app-container">
                    <!-- 搜索工作栏 -->
                    #if($showSearch)
                    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch" label-width="68px">
                    #else
                    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
                    #end
                #foreach ($column in $queryColumns)
                #if($column.displayType == 'datetime' || $column.displayType == 'date')
                    <el-form-item label="$column.comment" prop="$column.javaFieldName">
                      <el-date-picker
                        v-model="daterange$column.capitalizedFieldName"
                        type="daterange"
                        value-format="YYYY-MM-DD"
                        range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        :style="{ width: '240px' }"
                      />
                    </el-form-item>
                #elseif($column.displayType == 'select' || $column.displayType == 'radio')
                    <el-form-item label="$column.comment" prop="$column.javaFieldName">
                      <el-select v-model="queryParams.$column.javaFieldName" placeholder="请选择$column.comment" clearable>
                        <el-option label="请选择" value="" />
                      </el-select>
                    </el-form-item>
                #elseif($column.displayType == 'checkbox')
                    <el-form-item label="$column.comment" prop="$column.javaFieldName">
                      <el-checkbox-group v-model="queryParams.$column.javaFieldName">
                        <el-checkbox label="启用">启用</el-checkbox>
                      </el-checkbox-group>
                    </el-form-item>
                #else
                    <el-form-item label="$column.comment" prop="$column.javaFieldName">
                      <el-input
                        v-model="queryParams.$column.javaFieldName"
                        placeholder="请输入$column.comment"
                        clearable
                        @keyup.enter="handleQuery"
                      />
                    </el-form-item>
                #end
                #end
                      <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                      </el-form-item>
                    </el-form>

                    <!-- 操作工具栏 -->
                    <el-row :gutter="10" class="mb8">
                      <el-col :span="1.5">
                        <el-button
                          type="primary"
                          plain
                          icon="Plus"
                          @click="handleAdd"
                          v-hasPermi="['$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end:add']"
                        >新增</el-button>
                      </el-col>
                      <el-col :span="1.5">
                        <el-button
                          type="success"
                          plain
                          icon="Edit"
                          :disabled="single"
                          @click="handleUpdate"
                          v-hasPermi="['$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end:edit']"
                        >修改</el-button>
                      </el-col>
                      <el-col :span="1.5">
                        <el-button
                          type="danger"
                          plain
                          icon="Delete"
                          :disabled="multiple"
                          @click="handleDelete"
                          v-hasPermi="['$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end:remove']"
                        >删除</el-button>
                      </el-col>
                      <el-col :span="1.5">
                        <el-button
                          type="warning"
                          plain
                          icon="Download"
                          @click="handleExport"
                          v-hasPermi="['$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end:export']"
                        >导出</el-button>
                      </el-col>
                      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
                    </el-row>

                    <!-- 列表数据 -->
                    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
                      <el-table-column type="selection" width="55" align="center" />
                #foreach ($column in $columns)
                #if($column.primaryKey)
                      <el-table-column label="$column.comment" align="center" prop="$column.javaFieldName" width="120" />
                #elseif($column.displayType == 'datetime' || $column.displayType == 'date')
                      <el-table-column label="$column.comment" align="center" prop="$column.javaFieldName" width="180">
                        <template #default="scope">
                          <span>{{ parseTime(scope.row.$column.javaFieldName) }}</span>
                        </template>
                      </el-table-column>
                #elseif($column.displayType == 'select' || $column.displayType == 'radio')
                      <el-table-column label="$column.comment" align="center" prop="$column.javaFieldName" width="120">
                        <template #default="scope">
                          <dict-tag :options="$column.dictTypeCode" :value="scope.row.$column.javaFieldName" />
                        </template>
                      </el-table-column>
                #elseif($column.displayType == 'checkbox')
                      <el-table-column label="$column.comment" align="center" prop="$column.javaFieldName" width="120">
                        <template #default="scope">
                          <el-tag type="success" v-if="scope.row.$column.javaFieldName">是</el-tag>
                          <el-tag type="danger" v-else>否</el-tag>
                        </template>
                      </el-table-column>
                #elseif($column.displayType == 'image')
                      <el-table-column label="$column.comment" align="center" prop="$column.javaFieldName" width="100">
                        <template #default="scope">
                          <image-preview :src="scope.row.$column.javaFieldName" />
                        </template>
                      </el-table-column>
                #else
                      <el-table-column label="$column.comment" align="center" prop="$column.javaFieldName" #if($column.columnSize > 200) show-overflow-tooltip #end width="#if($column.columnSize > 500)300#{elseif($column.columnSize > 100)180#{else}120#end" />
                #end
                #end
                      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
                        <template #default="scope">
                          <el-button
                            link
                            type="primary"
                            icon="Edit"
                            @click="handleUpdate(scope.row)"
                            v-hasPermi="['$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end:edit']"
                          >修改</el-button>
                          <el-button
                            link
                            type="danger"
                            icon="Delete"
                            @click="handleDelete(scope.row)"
                            v-hasPermi="['$config.moduleName:#if($config.businessName)$config.businessName#{else}${table.lowerCaseEntityName}#end:remove']"
                          >删除</el-button>
                        </template>
                      </el-table-column>
                    </el-table>

                    <!-- 分页组件 -->
                    <pagination
                      v-model:total="total"
                      v-model:page="queryParams.pageNum"
                      v-model:limit="queryParams.pageSize"
                      @pagination="getList"
                    />

                    <!-- 添加或修改对话框 -->
                    <el-dialog v-model="dialogVisible" :title="title" width="#if($dialogWidth > 800)800px#{else}${dialogWidth}px#end" append-to-body>
                      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                #foreach ($column in $editColumns)
                #if($column.displayType == 'datetime' || $column.displayType == 'date')
                        <el-form-item label="$column.comment" prop="$column.javaFieldName">
                          <el-date-picker
                            v-model="form.$column.javaFieldName"
                            type="datetime"
                            value-format="YYYY-MM-DD HH:mm:ss"
                            placeholder="请选择$column.comment"
                            :style="{ width: '100%' }"
                          />
                        </el-form-item>
                #elseif($column.displayType == 'select')
                        <el-form-item label="$column.comment" prop="$column.javaFieldName">
                          <el-select v-model="form.$column.javaFieldName" placeholder="请选择$column.comment">
                            <el-option label="请选择" value="" />
                          </el-select>
                        </el-form-item>
                #elseif($column.displayType == 'radio')
                        <el-form-item label="$column.comment" prop="$column.javaFieldName">
                          <el-radio-group v-model="form.$column.javaFieldName">
                            <el-radio label="1">启用</el-radio>
                            <el-radio label="0">停用</el-radio>
                          </el-radio-group>
                        </el-form-item>
                #elseif($column.displayType == 'checkbox')
                        <el-form-item label="$column.comment">
                          <el-checkbox v-model="form.$column.javaFieldName">是否启用</el-checkbox>
                        </el-form-item>
                #elseif($column.displayType == 'image')
                        <el-form-item label="$column.comment" prop="$column.javaFieldName">
                          <image-upload v-model="form.$column.javaFieldName" />
                        </el-form-item>
                #elseif($column.displayType == 'file')
                        <el-form-item label="$column.comment" prop="$column.javaFieldName">
                          <file-upload v-model="form.$column.javaFieldName" />
                        </el-form-item>
                #elseif($column.displayType == 'textarea')
                        <el-form-item label="$column.comment" prop="$column.javaFieldName">
                          <el-input v-model="form.$column.javaFieldName" type="textarea" :rows="3" placeholder="请输入$column.comment" />
                        </el-form-item>
                #else
                        <el-form-item label="$column.comment" prop="$column.javaFieldName">
                          <el-input v-model="form.$column.javaFieldName" placeholder="请输入$column.comment" />
                        </el-form-item>
                #end
                #end
                      </el-form>
                      <template #footer>
                        <el-button @click="dialogVisible = false">取 消</el-button>
                        <el-button type="primary" @click="submitForm">确 定</el-button>
                      </template>
                    </el-dialog>
                  </div>
                </template>

                <script setup name="$table.entityName">
                import { list${table.entityName}, get${table.entityName}, add${table.entityName}, update${table.entityName}, del${table.entityName} } from './api'
                #if($hasDateQuery)
                import { parseTime } from '@/utils/ruoyi'
                #end

                const { proxy } = getCurrentInstance()

                // 选中行ID数组
                const ids = ref([])
                // 非单个禁用
                const single = computed(() => ids.value.length !== 1)
                // 非多个禁用
                const multiple = computed(() => ids.value.length === 0)
                // 遮罩层
                const loading = ref(true)
                // 显示搜索条件
                const showSearch = ref(true)
                // 总条数
                const total = ref(0)
                // 表格数据
                const dataList = ref([])
                // 弹出层标题
                const title = ref('')
                // 对话框可见性
                const dialogVisible = ref(false)

                // 查询参数
                const queryParams = ref({
                  pageNum: 1,
                  pageSize: 10,
                #foreach ($column in $queryColumns)
                  $column.javaFieldName: null#if($foreach.count < $queryColumns.size()),#end
                #end
                })

                // 表单参数
                const form = ref({})
                // 表单引用
                const formRef = ref()
                // 表单校验
                const rules = ref({
                #foreach ($column in $requiredColumns)
                  $column.javaFieldName: [{ required: true, message: "$column.comment不能为空", trigger: #if($column.displayType == 'select')"change"#else"blur"#end }]#if($foreach.count < $requiredColumns.size()),#end
                #end
                })

                #foreach ($column in $queryDateColumns)
                // $column.comment时间范围
                const daterange$column.capitalizedFieldName = ref([])
                #end

                /** 查询列表 */
                function getList() {
                  loading.value = true
                  const params = {
                    ...queryParams.value,
                  }
                #foreach ($column in $queryDateColumns)
                  if (daterange$column.capitalizedFieldName.value) {
                    params.params['begin$column.capitalizedFieldName'] = daterange$column.capitalizedFieldName.value[0]
                    params.params['end$column.capitalizedFieldName'] = daterange$column.capitalizedFieldName.value[1]
                  }
                #end
                  list${table.entityName}(params).then(response => {
                    dataList.value = response.rows
                    total.value = response.total
                    loading.value = false
                  })
                }

                /** 搜索按钮操作 */
                function handleQuery() {
                  queryParams.value.pageNum = 1
                  getList()
                }

                /** 重置按钮操作 */
                function resetQuery() {
                #foreach ($column in $queryDateColumns)
                  daterange$column.capitalizedFieldName.value = []
                #end
                  proxy.resetForm('queryFormRef')
                  handleQuery()
                }

                /** 多选框选中数据 */
                function handleSelectionChange(selection) {
                  ids.value = selection.map(item => item.$pkFieldName)
                }

                /** 添加按钮操作 */
                function handleAdd() {
                  form.value = {
                #foreach ($column in $columns)
                  #if($column.javaType == 'Integer' || $column.javaType == 'Long' || $column.javaType == 'Short')
                    $column.javaFieldName: 0#if($foreach.count < $columns.size()),#end
                  #elseif($column.javaType == 'Boolean')
                    $column.javaFieldName: false#if($foreach.count < $columns.size()),#end
                  #else
                    $column.javaFieldName: null#if($foreach.count < $columns.size()),#end
                  #end
                #end
                  }
                  dialogVisible.value = true
                  title.value = '添加$table.tableComment'
                }

                /** 修改按钮操作 */
                function handleUpdate(row) {
                  const $pkJsFieldName = row.$pkFieldName || ids.value[0]
                  get${table.entityName}($pkJsFieldName).then(response => {
                    form.value = response.data
                    dialogVisible.value = true
                    title.value = '修改$table.tableComment'
                  })
                }

                /** 提交按钮 */
                function submitForm() {
                  formRef.value.validate(valid => {
                    if (valid) {
                      if (form.value.$pkFieldName) {
                        update${table.entityName}(form.value).then(response => {
                          proxy.\\$modal.msgSuccess('修改成功')
                          dialogVisible.value = false
                          getList()
                        })
                      } else {
                        add${table.entityName}(form.value).then(response => {
                          proxy.\\$modal.msgSuccess('新增成功')
                          dialogVisible.value = false
                          getList()
                        })
                      }
                    }
                  })
                }

                /** 删除按钮操作 */
                function handleDelete(row) {
                  const $pkJsFieldName = row.$pkFieldName || ids.value
                  proxy.\\$modal.confirm('是否确认删除编号为"' + $pkJsFieldName + '"的数据项？')
                    .then(() => del${table.entityName}($pkJsFieldName))
                    .then(() => {
                      getList()
                      proxy.\\$modal.msgSuccess('删除成功')
                    })
                }

                /** 导出按钮操作 */
                function handleExport() {
                  proxy.\\$modal.confirm('是否确认导出所有数据项？')
                    .then(() => {
                      // exportExcel(queryParams)
                    })
                }

                getList()
                </script>
                """;
        templateEngine.registerTemplate(TEMPLATE_NAME, template);
    }

    @Override
    public String getGeneratorType() {
        return "vueIndex";
    }

    @Override
    protected String doGenerate(TableInfo tableInfo, CodeGenConfig config) {
        List<ColumnInfo> queryColumns = tableInfo.getQueryColumns();
        List<ColumnInfo> editColumns = tableInfo.getEditableColumns();
        List<ColumnInfo> requiredColumns = editColumns.stream().filter(ColumnInfo::isRequired).toList();
        List<ColumnInfo> queryDateColumns = queryColumns.stream().filter(c -> "datetime".equals(c.getDisplayType()) || "date".equals(c.getDisplayType())).toList();

        boolean hasDateQuery = !queryDateColumns.isEmpty();
        boolean showSearch = !queryColumns.isEmpty();

        int dialogWidth = 600;
        for (ColumnInfo col : editColumns) {
            if ("textarea".equals(col.getDisplayType()) || col.getColumnSize() > 200) {
                dialogWidth = 800;
                break;
            }
        }

        String pkFieldName = tableInfo.getPrimaryKey() != null ? tableInfo.getPrimaryKey().getJavaFieldName() : "id";
        String lowerCaseEntityName = tableInfo.getEntityName().toLowerCase();

        String template = templateEngine.getTemplate(TEMPLATE_NAME);
        template = template.replace("$pkFieldName", pkFieldName);
        template = template.replace("$pkJsFieldName", pkFieldName);
        template = template.replace("$!{table.tableComment}", tableInfo.getTableComment() != null ? tableInfo.getTableComment() : tableInfo.getEntityName());
        template = template.replace("${table.lowerCaseEntityName}", lowerCaseEntityName);
        template = template.replace("$showSearch", String.valueOf(showSearch));
        template = template.replace("$hasDateQuery", String.valueOf(hasDateQuery));
        template = template.replace("$dialogWidth", String.valueOf(dialogWidth));
        template = template.replace("#if($dialogWidth > 800)800px#{else}${dialogWidth}px#end", dialogWidth > 800 ? "800px" : dialogWidth + "px");

        // 处理 capitalizedFieldName
        for (ColumnInfo col : tableInfo.getColumns()) {
            String capField = col.getCapitalizedFieldName();
            template = template.replace("$column.capitalizedFieldName", capField);
        }

        return templateEngine.render(template, tableInfo, config);
    }

    @Override
    public String getFileName(TableInfo tableInfo, CodeGenConfig config) {
        return "index.vue";
    }

    @Override
    public String getFilePath(TableInfo tableInfo, CodeGenConfig config) {
        String basePath = config.getVueOutputPath();
        if (basePath == null || basePath.isEmpty()) {
            basePath = System.getProperty("user.dir") + "/src/views";
        }
        String modulePath = config.getModuleName();
        if (config.getBusinessName() != null && !config.getBusinessName().isEmpty()) {
            modulePath = config.getBusinessName();
        }
        return basePath + "/" + modulePath + "/" + tableInfo.getEntityName().toLowerCase() + "/" + getFileName(tableInfo, config);
    }

    @Override
    protected String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    protected String getModulePackage() {
        return "vueIndex";
    }
}
