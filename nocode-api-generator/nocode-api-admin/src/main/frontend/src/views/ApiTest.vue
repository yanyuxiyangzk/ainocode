<template>
  <div class="api-test-page">
    <el-row :gutter="20">
      <!-- 请求配置 -->
      <el-col :span="8">
        <el-card shadow="never" class="request-card">
          <div slot="header">
            <span><i class="el-icon-edit"></i> 请求配置</span>
          </div>
          <el-form :model="requestForm" label-width="80px">
            <el-form-item label="数据源">
              <el-select v-model="requestForm.datasource" @change="onDatasourceChange" placeholder="选择数据源" style="width: 100%;">
                <el-option v-for="ds in datasources" :key="ds.name" :label="ds.name" :value="ds.name"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="模式" v-if="schemas.length > 0">
              <el-select v-model="currentSchema" @change="onSchemaChange" placeholder="选择模式" style="width: 100%;">
                <el-option v-for="s in schemas" :key="s" :label="s" :value="s"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="数据表">
              <el-select v-model="requestForm.table" @change="onTableChange" placeholder="选择数据表" style="width: 100%;" filterable>
                <el-option v-for="t in tables" :key="t" :label="t" :value="t"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="请求方法">
              <el-select v-model="requestForm.method" style="width: 100%;" @change="onMethodChange">
                <el-option label="GET - 查询列表" value="GET">
                  <span style="float: left;">GET</span>
                  <span style="float: right; color: #8492a6; font-size: 12px;">查询列表</span>
                </el-option>
                <el-option label="GET - 查询单条" value="GET_ONE">
                  <span style="float: left;">GET</span>
                  <span style="float: right; color: #8492a6; font-size: 12px;">查询单条</span>
                </el-option>
                <el-option label="POST - 新增" value="POST">
                  <span style="float: left;">POST</span>
                  <span style="float: right; color: #8492a6; font-size: 12px;">新增记录</span>
                </el-option>
                <el-option label="PUT - 更新" value="PUT">
                  <span style="float: left;">PUT</span>
                  <span style="float: right; color: #8492a6; font-size: 12px;">更新记录</span>
                </el-option>
                <el-option label="DELETE - 删除" value="DELETE">
                  <span style="float: left;">DELETE</span>
                  <span style="float: right; color: #8492a6; font-size: 12px;">删除记录</span>
                </el-option>
              </el-select>
            </el-form-item>

            <!-- GET列表参数 -->
            <template v-if="requestForm.method === 'GET'">
              <el-form-item label="页码">
                <el-input-number v-model="requestForm.page" :min="1" :max="1000" style="width: 100%;"></el-input-number>
              </el-form-item>
              <el-form-item label="每页数量">
                <el-input-number v-model="requestForm.size" :min="1" :max="1000" style="width: 100%;"></el-input-number>
              </el-form-item>
            </template>

            <!-- ID参数 -->
            <template v-if="['GET_ONE', 'PUT', 'DELETE'].includes(requestForm.method)">
              <el-form-item label="ID">
                <el-input v-model="requestForm.id" :placeholder="'请输入' + (requestForm.table ? requestForm.table + '的' : '') + '主键ID'"></el-input>
              </el-form-item>
            </template>

            <!-- 请求体 -->
            <el-form-item label="请求体" v-if="['POST', 'PUT', 'BATCH_INSERT', 'BATCH_DELETE'].includes(requestForm.method)">
              <el-input v-model="requestForm.body" type="textarea" :rows="8" placeholder='{"field": "value"}' ref="bodyInput"></el-input>
              <div class="format-hint">
                <el-button type="text" size="mini" @click="formatJson" icon="el-icon-document">格式化JSON</el-button>
                <el-button type="text" size="mini" @click="validateJson" icon="el-icon-warning">验证JSON</el-button>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="sendRequest" :loading="loading" icon="el-icon-upload2">
                {{ getMethodName(requestForm.method) }}
              </el-button>
              <el-button @click="resetForm" icon="el-icon-refresh">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 响应结果 -->
      <el-col :span="16">
        <el-card shadow="never" class="response-card">
          <div slot="header">
            <span><i class="el-icon-tickets"></i> 响应结果</span>
            <el-tag :type="responseStatusType" size="small" style="margin-left: 10px;">{{ responseStatusText }}</el-tag>
          </div>

          <div v-if="!response && !loading" class="empty-tip">
            <i class="el-icon-info" style="font-size: 48px; color: #c0c4cc; margin-bottom: 15px;"></i>
            <p>暂无响应数据</p>
            <p class="hint">配置请求参数后点击"发送请求"按钮</p>
          </div>

          <div v-else-if="loading" class="loading-tip">
            <i class="el-icon-loading"></i>
            <span>正在发送请求...</span>
          </div>

          <template v-else-if="response">
            <!-- 响应状态 -->
            <div class="response-meta">
              <span :class="['status-indicator', response.success ? 'success' : 'error']">
                <i :class="response.success ? 'el-icon-success' : 'el-icon-error'"></i>
                {{ response.success ? '请求成功' : '请求失败' }}
              </span>
              <span class="message">{{ response.message }}</span>
            </div>

            <!-- 响应数据 -->
            <el-tabs v-model="activeTab" class="response-tabs">
              <el-tab-pane label="响应体" name="data">
                <div class="json-viewer">
                  <pre class="json-content">{{ formatJsonData(response.data) }}</pre>
                </div>
              </el-tab-pane>
              <el-tab-pane label="完整响应" name="full">
                <json-viewer :data="response"></json-viewer>
              </el-tab-pane>
            </el-tabs>

            <!-- 响应详情 -->
            <div class="response-details" v-if="response.total !== undefined">
              <el-descriptions :column="3" border size="small">
                <el-descriptions-item label="总记录数">{{ response.total }}</el-descriptions-item>
                <el-descriptions-item label="当前页码">{{ response.page }}</el-descriptions-item>
                <el-descriptions-item label="每页大小">{{ response.size }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import api from '../api'
import JsonViewer from '../components/JsonViewer.vue'

export default {
  name: 'ApiTest',
  components: { JsonViewer },
  data() {
    return {
      loading: false,
      datasources: [],
      tables: [],
      schemas: [],
      currentSchema: '',
      requestForm: {
        datasource: '',
        table: '',
        method: 'GET',
        page: 1,
        size: 10,
        id: '',
        body: ''
      },
      response: null,
      activeTab: 'data'
    }
  },
  computed: {
    responseStatusType() {
      if (!this.response) return 'info'
      return this.response.success ? 'success' : 'danger'
    },
    responseStatusText() {
      if (!this.response) return '无响应'
      return this.response.success ? '200 OK' : '400 ERROR'
    }
  },
  async mounted() {
    await this.loadDatasources()
    if (this.$route.query.datasource) {
      this.requestForm.datasource = this.$route.query.datasource
      await this.loadTables()
    }
    if (this.$route.query.table) {
      this.requestForm.table = this.$route.query.table
    }
  },
  methods: {
    async loadDatasources() {
      try {
        this.datasources = await api.getDatasources()
        if (this.datasources.length > 0 && !this.requestForm.datasource) {
          this.requestForm.datasource = this.datasources[0].name
          await this.loadSchemas()
        }
      } catch (e) {
        this.$message.error('加载数据源失败: ' + e.message)
      }
    },
    async loadSchemas() {
      if (!this.requestForm.datasource) return
      try {
        this.schemas = await api.getSchemas(this.requestForm.datasource)
        console.log('获取到模式列表:', this.schemas)
        if (this.schemas && this.schemas.length > 0) {
          // 有 schema，默认选择 public 或第一个
          if (this.schemas.includes('public')) {
            this.currentSchema = 'public'
          } else {
            this.currentSchema = this.schemas[0]
          }
          await this.loadTables()
        } else {
          // 没有 schema（MySQL 等），直接加载表
          this.schemas = []
          this.currentSchema = ''
          await this.loadTables()
        }
      } catch (e) {
        console.error('加载模式列表失败:', e)
        // 获取模式失败，直接加载表
        this.schemas = []
        this.currentSchema = ''
        await this.loadTables()
      }
    },
    async loadTables() {
      if (!this.requestForm.datasource) return
      try {
        let tableList
        if (this.currentSchema) {
          // 按 schema 加载表
          tableList = await api.getTablesBySchema(this.requestForm.datasource, this.currentSchema)
        } else {
          // 直接加载所有表
          tableList = await api.getTables(this.requestForm.datasource)
        }
        this.tables = tableList
        if (this.tables.length > 0 && !this.requestForm.table) {
          this.requestForm.table = this.tables[0]
        }
      } catch (e) {
        this.$message.error('加载表列表失败: ' + e.message)
        this.tables = []
      }
    },
    onDatasourceChange() {
      this.requestForm.table = ''
      this.tables = []
      this.schemas = []
      this.currentSchema = ''
      this.loadSchemas()
    },
    onSchemaChange() {
      this.requestForm.table = ''
      this.tables = []
      this.loadTables()
    },
    onTableChange() {},
    onMethodChange() {
      this.requestForm.id = ''
      this.requestForm.body = ''
    },
    getMethodName(method) {
      const names = {
        GET: '查询列表',
        GET_ONE: '查询单条',
        POST: '新增数据',
        PUT: '更新数据',
        DELETE: '删除数据'
      }
      return names[method] || '发送请求'
    },
    formatJson() {
      // 空字符串或 null/undefined 时不处理
      if (!this.requestForm.body || this.requestForm.body === '') {
        this.$message.warning('请求体为空')
        return
      }
      try {
        const parsed = JSON.parse(this.requestForm.body)
        this.requestForm.body = JSON.stringify(parsed, null, 2)
        this.$message.success('JSON已格式化')
      } catch (e) {
        // 只在有内容时才显示错误
        if (this.requestForm.body && this.requestForm.body.trim() !== '') {
          this.$message.error('JSON格式不正确: ' + e.message)
        }
      }
    },
    validateJson() {
      // 空字符串或 null/undefined 时不处理
      if (!this.requestForm.body || this.requestForm.body === '') {
        this.$message.warning('请求体为空')
        return
      }
      try {
        JSON.parse(this.requestForm.body)
        this.$message.success('JSON格式正确')
      } catch (e) {
        // 只在有内容时才显示错误
        if (this.requestForm.body && this.requestForm.body.trim() !== '') {
          this.$message.error('JSON格式错误: ' + e.message)
        }
      }
    },
    async sendRequest() {
      if (!this.requestForm.datasource || !this.requestForm.table) {
        this.$message.warning('请选择数据源和数据表')
        return
      }

      const { datasource, table, method, page, size, id, body } = this.requestForm

      this.loading = true
      this.response = null
      this.activeTab = 'data'

      try {
        switch (method) {
          case 'GET':
            this.response = await api.testQueryList(datasource, table, { page, size })
            break
          case 'GET_ONE':
            if (!id) {
              this.$message.warning('请输入ID')
              this.loading = false
              return
            }
            this.response = await api.testQueryOne(datasource, table, id)
            break
          case 'POST':
            if (!body || body.trim() === '') {
              this.$message.warning('请输入请求体')
              this.loading = false
              return
            }
            const insertData = JSON.parse(body)
            this.response = await api.testInsert(datasource, table, insertData)
            break
          case 'PUT':
            if (!id) {
              this.$message.warning('请输入ID')
              this.loading = false
              return
            }
            if (!body || body.trim() === '') {
              this.$message.warning('请输入请求体')
              this.loading = false
              return
            }
            const updateData = JSON.parse(body)
            this.response = await api.testUpdate(datasource, table, id, updateData)
            break
          case 'DELETE':
            if (!id) {
              this.$message.warning('请输入ID')
              this.loading = false
              return
            }
            this.response = await api.testDelete(datasource, table, id)
            break
        }
      } catch (e) {
        this.response = {
          success: false,
          message: '请求失败: ' + e.message,
          data: null
        }
      } finally {
        this.loading = false
      }
    },
    resetForm() {
      this.requestForm.body = ''
      this.requestForm.id = ''
      this.response = null
    },
    formatJsonData(data) {
      if (data === null || data === undefined) return 'null'
      try {
        return JSON.stringify(data, null, 2)
      } catch (e) {
        return String(data)
      }
    }
  }
}
</script>

<style scoped>
.api-test-page {
  max-width: 1400px;
  margin: 0 auto;
}
.request-card,
.response-card {
  height: calc(100vh - 140px);
  overflow-y: auto;
}
.format-hint {
  display: flex;
  justify-content: flex-end;
  margin-top: 5px;
}
.empty-tip {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}
.empty-tip .hint {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 10px;
}
.loading-tip {
  text-align: center;
  padding: 60px 20px;
  color: #409EFF;
}
.loading-tip i {
  font-size: 32px;
  margin-bottom: 15px;
  display: block;
}
.response-meta {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px 15px;
  background: #f5f7fa;
  border-radius: 4px;
}
.status-indicator {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
  margin-right: 15px;
}
.status-indicator.success {
  background: #f0f9eb;
  color: #67c23a;
}
.status-indicator.error {
  background: #fef0f0;
  color: #f56c6c;
}
.status-indicator i {
  margin-right: 5px;
}
.message {
  color: #606266;
  font-size: 13px;
}
.response-tabs {
  margin-top: 15px;
}
.json-viewer {
  background: #282c34;
  border-radius: 4px;
  overflow: auto;
  max-height: 400px;
}
.json-content {
  padding: 15px;
  margin: 0;
  font-family: 'Consolas', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #abb2bf;
  white-space: pre-wrap;
  word-wrap: break-word;
}
.response-details {
  margin-top: 15px;
}
</style>
