<template>
  <div class="entity-manager card-box">
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.entityName"
          placeholder="请输入实体名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" icon="Plus" @click="handleCreate">新建实体</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="entityList" border stripe>
      <el-table-column label="实体名称" prop="entityName" min-width="120" />
      <el-table-column label="实体描述" prop="entityComment" min-width="150" />
      <el-table-column label="关联表名" prop="tableName" min-width="120" />
      <el-table-column label="字段数量" prop="fieldCount" width="100" align="center" />
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '1' ? 'success' : 'info'">
            {{ row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link icon="Setting" @click="handleConfig(row)">配置字段</el-button>
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

    <!-- 实体配置对话框 -->
    <el-dialog v-model="configVisible" title="字段配置" width="1000px" append-to-body>
      <div class="config-container">
        <div class="field-list">
          <el-table :data="configFields" border style="width: 100%">
            <el-table-column label="字段名" prop="fieldName" min-width="100">
              <template #default="{ row }">
                <el-input v-model="row.fieldName" />
              </template>
            </el-table-column>
            <el-table-column label="字段类型" prop="fieldType" width="120">
              <template #default="{ row }">
                <el-select v-model="row.fieldType">
                  <el-option label="字符串" value="string" />
                  <el-option label="整数" value="integer" />
                  <el-option label="长整型" value="long" />
                  <el-option label="小数" value="decimal" />
                  <el-option label="日期" value="date" />
                  <el-option label="布尔" value="boolean" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="描述" prop="fieldComment" min-width="120">
              <template #default="{ row }">
                <el-input v-model="row.fieldComment" />
              </template>
            </el-table-column>
            <el-table-column label="必填" width="60" align="center">
              <template #default="{ row }">
                <el-checkbox v-model="row.isRequired" />
              </template>
            </el-table-column>
            <el-table-column label="唯一" width="60" align="center">
              <template #default="{ row }">
                <el-checkbox v-model="row.isUnique" />
              </template>
            </el-table-column>
            <el-table-column label="验证规则" min-width="150">
              <template #default="{ row }">
                <el-select v-model="row.validateRule" clearable placeholder="选择验证">
                  <el-option label="邮箱" value="email" />
                  <el-option label="手机号" value="phone" />
                  <el-option label="身份证" value="idcard" />
                  <el-option label="URL" value="url" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="{ $index }">
                <el-button type="danger" link icon="Delete" @click="handleDeleteField($index)" />
              </template>
            </el-table-column>
          </el-table>
          <el-button type="primary" link icon="Plus" class="mt-10" @click="handleAddField">
            添加字段
          </el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="configVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveConfig">保存配置</el-button>
      </template>
    </el-dialog>

    <!-- 新建/编辑实体对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="实体名称" prop="entityName">
          <el-input v-model="form.entityName" placeholder="请输入实体名称" />
        </el-form-item>
        <el-form-item label="实体描述" prop="entityComment">
          <el-input v-model="form.entityComment" placeholder="请输入实体描述" />
        </el-form-item>
        <el-form-item label="关联表名" prop="tableName">
          <el-input v-model="form.tableName" placeholder="请输入关联表名" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
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

defineOptions({ name: 'NocodeEntity' })

const loading = ref(false)
const total = ref(0)
const entityList = ref<any[]>([])
const dialogVisible = ref(false)
const configVisible = ref(false)
const formRef = ref<FormInstance>()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  entityName: ''
})

const form = reactive({
  entityId: undefined as number | undefined,
  entityName: '',
  entityComment: '',
  tableName: '',
  status: '1'
})

const configFields = ref<any[]>([])

const rules: FormRules = {
  entityName: [{ required: true, message: '请输入实体名称', trigger: 'blur' }],
  entityComment: [{ required: true, message: '请输入实体描述', trigger: 'blur' }]
}

const dialogTitle = computed(() => (form.entityId ? '编辑实体' : '新建实体'))

// 模拟数据
const mockData = [
  { entityId: 1, entityName: 'User', entityComment: '用户实体', tableName: 'sys_user', fieldCount: 10, status: '1', createTime: '2024-01-01' },
  { entityId: 2, entityName: 'Order', entityComment: '订单实体', tableName: 'biz_order', fieldCount: 15, status: '1', createTime: '2024-01-02' },
  { entityId: 3, entityName: 'Product', entityComment: '商品实体', tableName: 'biz_product', fieldCount: 20, status: '1', createTime: '2024-01-03' },
  { entityId: 4, entityName: 'Category', entityComment: '分类实体', tableName: 'biz_category', fieldCount: 8, status: '0', createTime: '2024-01-04' }
]

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    entityList.value = mockData.filter(item => {
      if (queryParams.entityName && !item.entityName.toLowerCase().includes(queryParams.entityName.toLowerCase())) return false
      return true
    })
    total.value = entityList.value.length
    loading.value = false
  }, 300)
}

const resetQuery = () => {
  queryParams.entityName = ''
  handleQuery()
}

const handleCreate = () => {
  form.entityId = undefined
  form.entityName = ''
  form.entityComment = ''
  form.tableName = ''
  form.status = '1'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  form.entityId = row.entityId
  form.entityName = row.entityName
  form.entityComment = row.entityComment
  form.tableName = row.tableName
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除实体 ${row.entityName}?`, '警告', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleConfig = (row: any) => {
  configFields.value = [
    { fieldName: 'id', fieldType: 'long', fieldComment: '主键ID', isRequired: true, isUnique: true, validateRule: '' },
    { fieldName: 'name', fieldType: 'string', fieldComment: '名称', isRequired: true, isUnique: false, validateRule: '' },
    { fieldName: 'createTime', fieldType: 'date', fieldComment: '创建时间', isRequired: false, isUnique: false, validateRule: '' }
  ]
  configVisible.value = true
}

const handleAddField = () => {
  configFields.value.push({
    fieldName: '',
    fieldType: 'string',
    fieldComment: '',
    isRequired: false,
    isUnique: false,
    validateRule: ''
  })
}

const handleDeleteField = (index: number) => {
  configFields.value.splice(index, 1)
}

const handleSaveConfig = () => {
  ElMessage.success('配置保存成功')
  configVisible.value = false
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return
  
  ElMessage.success(form.entityId ? '修改成功' : '创建成功')
  dialogVisible.value = false
  handleQuery()
}

handleQuery()
</script>

<style lang="scss" scoped>
.entity-manager {
  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    .toolbar-left,
    .toolbar-right {
      display: flex;
      gap: 10px;
    }
  }
}
</style>
