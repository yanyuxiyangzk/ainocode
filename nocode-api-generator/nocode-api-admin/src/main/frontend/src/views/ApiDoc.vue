<template>
  <div class="api-doc-page">
    <el-card shadow="never">
      <div slot="header">
        <span><i class="el-icon-document"></i> API文档</span>
      </div>

      <!-- 选择器 -->
      <el-form :inline="true" class="selector-form">
        <el-form-item label="数据源">
          <el-select v-model="queryForm.datasource" @change="onDatasourceChange" placeholder="选择数据源" style="width: 160px;">
            <el-option v-for="ds in datasources" :key="ds.name" :label="ds.name" :value="ds.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据表">
          <el-select v-model="queryForm.table" @change="onTableChange" placeholder="选择数据表" style="width: 200px;" :disabled="!queryForm.datasource">
            <el-option v-for="t in tables" :key="t" :label="t" :value="t"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-refresh" @click="loadApiDoc" :loading="loading">刷新</el-button>
        </el-form-item>
      </el-form>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <i class="el-icon-loading"></i>
        <span>加载中...</span>
      </div>

      <!-- API文档内容 -->
      <template v-else-if="apiDoc">
        <!-- 基本信息 -->
        <el-card shadow="never" class="info-card">
          <div slot="header">
            <span><i class="el-icon-info"></i> 基本信息</span>
          </div>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="表名">
              <code>{{ apiDoc.tableName }}</code>
            </el-descriptions-item>
            <el-descriptions-item label="表注释">{{ apiDoc.tableComment || '-' }}</el-descriptions-item>
            <el-descriptions-item label="API前缀">/api/{{ queryForm.datasource }}/{{ apiDoc.tableName }}</el-descriptions-item>
            <el-descriptions-item label="主键字段">{{ apiDoc.primaryKey }}</el-descriptions-item>
            <el-descriptions-item label="主键类型">{{ apiDoc.primaryKeyType }}</el-descriptions-item>
            <el-descriptions-item label="字段数量">{{ apiDoc.fields ? apiDoc.fields.length : 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 字段列表 -->
        <el-card shadow="never" class="fields-card">
          <div slot="header">
            <span><i class="el-icon-notebook-2"></i> 字段列表</span>
          </div>
          <el-table :data="apiDoc.fields" stripe size="small">
            <el-table-column prop="name" label="字段名" width="150">
              <template slot-scope="{row}">
                <code>{{ row.name }}</code>
              </template>
            </el-table-column>
            <el-table-column prop="type" label="数据库类型" width="120"></el-table-column>
            <el-table-column prop="javaType" label="Java类型" width="140"></el-table-column>
            <el-table-column prop="comment" label="注释" min-width="150"></el-table-column>
            <el-table-column label="主键" width="70" align="center">
              <template slot-scope="{row}">
                <el-tag v-if="row.primaryKey" type="success" size="mini">是</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- API接口列表 -->
        <el-card shadow="never" class="apis-card">
          <div slot="header">
            <span><i class="el-icon-link"></i> API接口列表</span>
          </div>

          <el-collapse v-model="activeCollapse">
            <el-collapse-item v-for="(api, index) in apiDoc.apis" :key="index" :name="index" :title="api.description">
              <div class="api-detail">
                <div class="api-header">
                  <span :class="['method-badge', api.method]">{{ api.method }}</span>
                  <code class="api-path">{{ api.path }}</code>
                </div>
                <div class="api-description">{{ api.description }}</div>

                <div class="api-params-section" v-if="api.parameters && api.parameters.length">
                  <h4>请求参数</h4>
                  <el-table :data="formatParams(api.parameters)" size="small" border>
                    <el-table-column prop="name" label="参数" width="200"></el-table-column>
                    <el-table-column prop="desc" label="说明"></el-table-column>
                  </el-table>
                </div>

                <div class="api-example-section" v-if="api.example">
                  <h4>请求示例</h4>
                  <div class="code-block">{{ api.example }}</div>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </template>

      <!-- 空状态 -->
      <el-empty v-else-if="queryForm.datasource && queryForm.table" description="加载失败，请检查数据源配置">
        <el-button type="primary" @click="loadApiDoc">重试</el-button>
      </el-empty>
      <el-empty v-else description="请选择数据源和数据表">
        <el-button type="primary" @click="$router.push('/datasources')">添加数据源</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'ApiDoc',
  data() {
    return {
      loading: false,
      datasources: [],
      tables: [],
      queryForm: {
        datasource: '',
        table: ''
      },
      apiDoc: null,
      activeCollapse: ['0', '1', '2', '3', '4', '5', '6', '7']
    }
  },
  async mounted() {
    await this.loadDatasources()
    // 处理URL参数
    if (this.$route.query.datasource) {
      this.queryForm.datasource = this.$route.query.datasource
    }
    if (this.$route.query.table) {
      this.queryForm.table = this.$route.query.table
    }
    if (this.queryForm.datasource) {
      await this.loadTables()
      if (this.queryForm.table) {
        await this.loadApiDoc()
      }
    }
  },
  methods: {
    async loadDatasources() {
      try {
        this.datasources = await api.getDatasources()
      } catch (e) {
        this.$message.error('加载数据源失败: ' + e.message)
      }
    },
    async onDatasourceChange() {
      this.queryForm.table = ''
      this.apiDoc = null
      await this.loadTables()
    },
    onTableChange() {
      this.loadApiDoc()
    },
    async loadTables() {
      if (!this.queryForm.datasource) return
      try {
        this.tables = await api.getTables(this.queryForm.datasource)
      } catch (e) {
        this.$message.error('加载表列表失败: ' + e.message)
      }
    },
    async loadApiDoc() {
      if (!this.queryForm.datasource || !this.queryForm.table) return
      this.loading = true
      try {
        this.apiDoc = await api.getApiDoc(this.queryForm.datasource, this.queryForm.table)
      } catch (e) {
        this.$message.error('加载API文档失败: ' + e.message)
        this.apiDoc = null
      } finally {
        this.loading = false
      }
    },
    formatParams(params) {
      if (!params) return []
      return params.map(p => {
        const parts = p.split(':')
        return {
          name: parts[0] || '',
          desc: parts[1] || ''
        }
      })
    }
  }
}
</script>

<style scoped>
.api-doc-page {
  max-width: 1200px;
  margin: 0 auto;
}
.selector-form {
  margin-bottom: 20px;
}
.loading-container {
  text-align: center;
  padding: 40px;
  color: #909399;
}
.loading-container i {
  font-size: 24px;
  margin-right: 10px;
}
.info-card,
.fields-card,
.apis-card {
  margin-bottom: 20px;
}
.api-detail {
  padding: 10px 0;
}
.api-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.method-badge {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  margin-right: 10px;
}
.method-badge.GET { background: #67c23a; }
.method-badge.POST { background: #409EFF; }
.method-badge.PUT { background: #e6a23c; }
.method-badge.DELETE { background: #f56c6c; }
.api-path {
  background: #f5f7fa;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 14px;
}
.api-description {
  color: #606266;
  margin-bottom: 15px;
}
.api-params-section,
.api-example-section {
  margin-top: 15px;
}
.api-params-section h4,
.api-example-section h4 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 14px;
}
.code-block {
  background: #282c34;
  color: #abb2bf;
  padding: 15px;
  border-radius: 4px;
  font-family: 'Consolas', monospace;
  font-size: 13px;
  overflow-x: auto;
}
</style>
