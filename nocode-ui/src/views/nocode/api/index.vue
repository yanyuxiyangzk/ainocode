<template>
  <div class="api-manager card-box">
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.apiName"
          placeholder="请输入API名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
        <el-select v-model="queryParams.method" placeholder="请求方法" clearable style="width: 120px">
          <el-option label="GET" value="GET" />
          <el-option label="POST" value="POST" />
          <el-option label="PUT" value="PUT" />
          <el-option label="DELETE" value="DELETE" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" icon="Plus" @click="handleCreate">新建API</el-button>
        <el-button type="success" icon="Download" @click="handleExportSwagger">导出Swagger</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="apiList" border stripe>
      <el-table-column label="API名称" prop="apiName" min-width="150" />
      <el-table-column label="请求路径" prop="path" min-width="200" show-overflow-tooltip />
      <el-table-column label="请求方法" prop="method" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getMethodType(row.method)">{{ row.method }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="所属模块" prop="module" width="120" />
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '1' ? 'success' : 'info'">
            {{ row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link icon="View" @click="handleTest(row)">测试</el-button>
          <el-button type="primary" link icon="DocumentCopy" @click="handleCopy(row)">复制</el-button>
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

    <!-- 新建/编辑 API 对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="API名称" prop="apiName">
              <el-input v-model="form.apiName" placeholder="请输入API名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求方法" prop="method">
              <el-select v-model="form.method" placeholder="请选择请求方法">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求路径" prop="path">
              <el-input v-model="form.path" placeholder="请输入请求路径，如 /api/user/list" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属模块" prop="module">
              <el-input v-model="form.module" placeholder="请输入所属模块" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="1">启用</el-radio>
                <el-radio value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="API描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入API描述" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">请求参数</el-divider>
        
        <el-table :data="form.params" border style="width: 100%">
          <el-table-column label="参数名" prop="name" min-width="120">
            <template #default="{ row }">
              <el-input v-model="row.name" placeholder="参数名" />
            </template>
          </el-table-column>
          <el-table-column label="类型" prop="type" width="120">
            <template #default="{ row }">
              <el-select v-model="row.type" placeholder="类型">
                <el-option label="String" value="String" />
                <el-option label="Integer" value="Integer" />
                <el-option label="Long" value="Long" />
                <el-option label="Boolean" value="Boolean" />
                <el-option label="Object" value="Object" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="必填" width="60" align="center">
            <template #default="{ row }">
              <el-checkbox v-model="row.required" />
            </template>
          </el-table-column>
          <el-table-column label="参数位置" width="100">
            <template #default="{ row }">
              <el-select v-model="row.position" size="small">
                <el-option label="Query" value="query" />
                <el-option label="Path" value="path" />
                <el-option label="Body" value="body" />
                <el-option label="Header" value="header" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="说明" min-width="150">
            <template #default="{ row }">
              <el-input v-model="row.description" placeholder="参数说明" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button type="danger" link icon="Delete" @click="handleDeleteParam($index)" />
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" link icon="Plus" class="mt-10" @click="handleAddParam">
          添加参数
        </el-button>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- API 测试对话框 -->
    <el-dialog v-model="testVisible" title="API 测试" width="800px" append-to-body>
      <div class="api-test">
        <div class="test-info">
          <el-tag :type="getMethodType(testApi.method)">{{ testApi.method }}</el-tag>
          <span class="path">{{ testApi.path }}</span>
        </div>
        <el-divider />
        <el-form label-width="80px">
          <el-form-item v-for="param in testParams" :key="param.name" :label="param.name">
            <el-input v-model="param.value" :placeholder="param.description" />
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="executeTest">发送请求</el-button>
        <el-divider />
        <div class="response-area">
          <h4>响应结果</h4>
          <pre class="response-content">{{ testResponse }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

defineOptions({ name: 'NocodeApi' })

const loading = ref(false)
const total = ref(0)
const apiList = ref<any[]>([])
const dialogVisible = ref(false)
const testVisible = ref(false)
const formRef = ref<FormInstance>()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  apiName: '',
  method: ''
})

const form = reactive({
  apiId: undefined as number | undefined,
  apiName: '',
  method: 'GET',
  path: '',
  module: '',
  status: '1',
  description: '',
  params: [] as any[]
})

const testApi = ref<any>({})
const testParams = ref<any[]>([])
const testResponse = ref('')

const rules: FormRules = {
  apiName: [{ required: true, message: '请输入API名称', trigger: 'blur' }],
  method: [{ required: true, message: '请选择请求方法', trigger: 'change' }],
  path: [{ required: true, message: '请输入请求路径', trigger: 'blur' }]
}

const dialogTitle = computed(() => (form.apiId ? '编辑API' : '新建API'))

// 模拟数据
const mockData = [
  { apiId: 1, apiName: '获取用户列表', method: 'GET', path: '/api/system/user/list', module: '系统管理', status: '1', createTime: '2024-01-01' },
  { apiId: 2, apiName: '创建用户', method: 'POST', path: '/api/system/user', module: '系统管理', status: '1', createTime: '2024-01-01' },
  { apiId: 3, apiName: '更新用户', method: 'PUT', path: '/api/system/user', module: '系统管理', status: '1', createTime: '2024-01-01' },
  { apiId: 4, apiName: '删除用户', method: 'DELETE', path: '/api/system/user/{id}', module: '系统管理', status: '1', createTime: '2024-01-01' },
  { apiId: 5, apiName: '获取订单列表', method: 'GET', path: '/api/biz/order/list', module: '业务管理', status: '1', createTime: '2024-01-02' }
]

const getMethodType = (method: string) => {
  const types: Record<string, string> = {
    GET: 'success',
    POST: 'primary',
    PUT: 'warning',
    DELETE: 'danger'
  }
  return types[method] || 'info'
}

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    apiList.value = mockData.filter(item => {
      if (queryParams.apiName && !item.apiName.includes(queryParams.apiName)) return false
      if (queryParams.method && item.method !== queryParams.method) return false
      return true
    })
    total.value = apiList.value.length
    loading.value = false
  }, 300)
}

const resetQuery = () => {
  queryParams.apiName = ''
  queryParams.method = ''
  handleQuery()
}

const handleCreate = () => {
  form.apiId = undefined
  form.apiName = ''
  form.method = 'GET'
  form.path = ''
  form.module = ''
  form.status = '1'
  form.description = ''
  form.params = []
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  form.apiId = row.apiId
  form.apiName = row.apiName
  form.method = row.method
  form.path = row.path
  form.module = row.module
  form.status = row.status
  form.description = ''
  form.params = [
    { name: 'pageNum', type: 'Integer', required: false, position: 'query', description: '页码' },
    { name: 'pageSize', type: 'Integer', required: false, position: 'query', description: '每页数量' }
  ]
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除API ${row.apiName}?`, '警告', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleTest = (row: any) => {
  testApi.value = row
  testParams.value = [
    { name: 'pageNum', value: '1', description: '页码' },
    { name: 'pageSize', value: '10', description: '每页数量' }
  ]
  testResponse.value = ''
  testVisible.value = true
}

const executeTest = () => {
  testResponse.value = JSON.stringify({
    code: 200,
    msg: '操作成功',
    data: {
      rows: [
        { id: 1, name: '测试数据1' },
        { id: 2, name: '测试数据2' }
      ],
      total: 2
    }
  }, null, 2)
}

const handleCopy = (row: any) => {
  ElMessage.success(`已复制API: ${row.apiName}`)
}

const handleExportSwagger = () => {
  ElMessage.success('Swagger文档导出成功')
}

const handleAddParam = () => {
  form.params.push({
    name: '',
    type: 'String',
    required: false,
    position: 'query',
    description: ''
  })
}

const handleDeleteParam = (index: number) => {
  form.params.splice(index, 1)
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return
  
  ElMessage.success(form.apiId ? '修改成功' : '创建成功')
  dialogVisible.value = false
  handleQuery()
}

handleQuery()
</script>

<style lang="scss" scoped>
.api-manager {
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

  .api-test {
    .test-info {
      display: flex;
      align-items: center;
      gap: 10px;

      .path {
        font-family: monospace;
        font-size: 14px;
      }
    }

    .response-area {
      .response-content {
        background: #f5f5f5;
        padding: 15px;
        border-radius: 4px;
        font-family: monospace;
        font-size: 12px;
        max-height: 300px;
        overflow: auto;
      }
    }
  }
}
</style>
