<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409EFF;">
              <i class="el-icon-connection" style="font-size: 32px;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ systemInfo.datasourceCount || 0 }}</div>
              <div class="stat-label">数据源数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a;">
              <i class="el-icon-menu" style="font-size: 32px;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalTables }}</div>
              <div class="stat-label">表数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c;">
              <i class="el-icon-open" style="font-size: 32px;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ systemInfo.apiEnabled ? '已启用' : '未启用' }}</div>
              <div class="stat-label">API状态</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <div slot="header">
        <span>快速开始</span>
      </div>
      <el-steps :active="1" finish-status="success" align-center>
        <el-step title="配置数据源" description="添加数据库连接"></el-step>
        <el-step title="选择数据表" description="查看并管理表"></el-step>
        <el-step title="使用API" description="调用生成的接口"></el-step>
      </el-steps>

      <div style="margin-top: 40px;">
        <h3>示例请求</h3>
        <el-card shadow="never" style="margin-top: 10px;">
          <p><strong>查询列表：</strong></p>
          <div class="code-block">
GET /api/{datasource}/{table}?page=1&size=10
          </div>

          <p style="margin-top: 15px;"><strong>查询单条：</strong></p>
          <div class="code-block">
GET /api/{datasource}/{table}/{id}
          </div>

          <p style="margin-top: 15px;"><strong>新增记录：</strong></p>
          <div class="code-block">
POST /api/{datasource}/{table}
Content-Type: application/json

{"name": "test", "value": 123}
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Home',
  data() {
    return {
      systemInfo: {},
      datasources: [],
      totalTables: 0
    }
  },
  async mounted() {
    await this.loadData()
  },
  methods: {
    async loadData() {
      try {
        this.systemInfo = await api.getSystemInfo()
        this.datasources = await api.getDatasources()
        let total = 0
        for (const ds of this.datasources) {
          total += ds.tableCount || 0
        }
        this.totalTables = total
      } catch (e) {
        this.$message.error('加载数据失败: ' + e.message)
      }
    }
  }
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
</style>
