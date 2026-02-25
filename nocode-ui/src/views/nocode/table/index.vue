<template>
  <div class="table-manager card-box">
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入表名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
        <el-input
          v-model="queryParams.tableComment"
          placeholder="请输入表描述"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" icon="Plus" @click="handleCreate">新建表</el-button>
        <el-button type="success" icon="Download" @click="handleGenerate">生成代码</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="tableList" border stripe>
      <el-table-column type="selection" width="55" />
      <el-table-column label="表名称" prop="tableName" min-width="150" show-overflow-tooltip />
      <el-table-column label="表描述" prop="tableComment" min-width="150" show-overflow-tooltip />
      <el-table-column label="实体类名" prop="className" min-width="120" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link icon="Refresh" @click="handleSync(row)">同步</el-button>
          <el-button type="primary" link icon="View" @click="handlePreview(row)">预览</el-button>
          <el-button type="danger" link icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-show="total > 0"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </div>

    <!-- 新建/编辑表对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="900px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="表名称" prop="tableName">
              <el-input v-model="form.tableName" placeholder="请输入表名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="表描述" prop="tableComment">
              <el-input v-model="form.tableComment" placeholder="请输入表描述" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实体类名" prop="className">
              <el-input v-model="form.className" placeholder="请输入实体类名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="functionAuthor">
              <el-input v-model="form.functionAuthor" placeholder="请输入作者" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">字段配置</el-divider>

        <el-table :data="form.columns" border style="width: 100%">
          <el-table-column label="字段名" prop="columnName" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.columnName" placeholder="字段名" />
            </template>
          </el-table-column>
          <el-table-column label="字段描述" prop="columnComment" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.columnComment" placeholder="字段描述" />
            </template>
          </el-table-column>
          <el-table-column label="字段类型" prop="columnType" width="140">
            <template #default="{ row }">
              <el-select v-model="row.columnType" placeholder="类型">
                <el-option label="VARCHAR" value="varchar" />
                <el-option label="INT" value="int" />
                <el-option label="BIGINT" value="bigint" />
                <el-option label="DATETIME" value="datetime" />
                <el-option label="DECIMAL" value="decimal" />
                <el-option label="TEXT" value="text" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="Java类型" prop="javaType" width="120">
            <template #default="{ row }">
              <el-select v-model="row.javaType" placeholder="Java类型">
                <el-option label="String" value="String" />
                <el-option label="Integer" value="Integer" />
                <el-option label="Long" value="Long" />
                <el-option label="Double" value="Double" />
                <el-option label="BigDecimal" value="BigDecimal" />
                <el-option label="Date" value="Date" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="必填" prop="isRequired" width="60" align="center">
            <template #default="{ row }">
              <el-checkbox v-model="row.isRequired" true-value="1" false-value="0" />
            </template>
          </el-table-column>
          <el-table-column label="主键" prop="isPk" width="60" align="center">
            <template #default="{ row }">
              <el-checkbox v-model="row.isPk" true-value="1" false-value="0" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link icon="Delete" @click="handleDeleteColumn($index)" />
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" link icon="Plus" class="mt-10" @click="handleAddColumn">
          添加字段
        </el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

defineOptions({ name: 'NocodeTable' })

const loading = ref(false)
const total = ref(0)
const tableList = ref<any[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  tableName: '',
  tableComment: ''
})

const form = reactive({
  tableId: undefined as number | undefined,
  tableName: '',
  tableComment: '',
  className: '',
  functionAuthor: '',
  columns: [] as any[]
})

const rules: FormRules = {
  tableName: [{ required: true, message: '请输入表名称', trigger: 'blur' }],
  tableComment: [{ required: true, message: '请输入表描述', trigger: 'blur' }],
  className: [{ required: true, message: '请输入实体类名', trigger: 'blur' }]
}

const dialogTitle = computed(() => (form.tableId ? '编辑表' : '新建表'))

// 模拟数据
const mockData = [
  { tableId: 1, tableName: 'sys_user', tableComment: '用户信息表', className: 'SysUser', createTime: '2024-01-01 10:00:00' },
  { tableId: 2, tableName: 'sys_role', tableComment: '角色信息表', className: 'SysRole', createTime: '2024-01-01 10:00:00' },
  { tableId: 3, tableName: 'sys_menu', tableComment: '菜单权限表', className: 'SysMenu', createTime: '2024-01-01 10:00:00' },
  { tableId: 4, tableName: 'biz_order', tableComment: '订单信息表', className: 'BizOrder', createTime: '2024-01-02 14:30:00' }
]

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    tableList.value = mockData.filter(item => {
      if (queryParams.tableName && !item.tableName.includes(queryParams.tableName)) return false
      if (queryParams.tableComment && !item.tableComment.includes(queryParams.tableComment)) return false
      return true
    })
    total.value = tableList.value.length
    loading.value = false
  }, 300)
}

const resetQuery = () => {
  queryParams.tableName = ''
  queryParams.tableComment = ''
  handleQuery()
}

const handleCreate = () => {
  form.tableId = undefined
  form.tableName = ''
  form.tableComment = ''
  form.className = ''
  form.functionAuthor = ''
  form.columns = [
    { columnName: 'id', columnComment: '主键ID', columnType: 'bigint', javaType: 'Long', isRequired: '0', isPk: '1' },
    { columnName: 'create_time', columnComment: '创建时间', columnType: 'datetime', javaType: 'Date', isRequired: '0', isPk: '0' },
    { columnName: 'update_time', columnComment: '更新时间', columnType: 'datetime', javaType: 'Date', isRequired: '0', isPk: '0' }
  ]
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  form.tableId = row.tableId
  form.tableName = row.tableName
  form.tableComment = row.tableComment
  form.className = row.className
  form.functionAuthor = 'Nocode'
  form.columns = [
    { columnName: 'id', columnComment: '主键ID', columnType: 'bigint', javaType: 'Long', isRequired: '0', isPk: '1' }
  ]
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除表 ${row.tableName}?`, '警告', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleSync = (row: any) => {
  ElMessage.success(`同步表 ${row.tableName} 成功`)
}

const handlePreview = (row: any) => {
  ElMessage.info(`预览表 ${row.tableName} 的代码生成结果`)
}

const handleGenerate = () => {
  ElMessage.success('代码生成成功')
}

const handleAddColumn = () => {
  form.columns.push({
    columnName: '',
    columnComment: '',
    columnType: 'varchar',
    javaType: 'String',
    isRequired: '0',
    isPk: '0'
  })
}

const handleDeleteColumn = (index: number) => {
  form.columns.splice(index, 1)
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return
  
  ElMessage.success(form.tableId ? '修改成功' : '创建成功')
  dialogVisible.value = false
  handleQuery()
}

// 初始化
handleQuery()
</script>

<style lang="scss" scoped>
.table-manager {
  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    .toolbar-left {
      display: flex;
      gap: 10px;
    }

    .toolbar-right {
      display: flex;
      gap: 10px;
    }
  }
}
</style>
