<template>
  <div class="nocode-dashboard">
    <!-- Header -->
    <div class="dashboard-header">
      <div class="header-left">
        <h1>Zero-Code Platform</h1>
        <p class="subtitle">Visual Development Platform for Microservices</p>
      </div>
      <div class="header-right">
        <el-button type="primary" size="large" @click="goToFormDesigner">
          <i class="el-icon-edit"></i> Form Designer
        </el-button>
        <el-button type="success" size="large" @click="goToWorkflowDesigner">
          <i class="el-icon-s-order"></i> Workflow Designer
        </el-button>
        <el-button type="warning" size="large" @click="goToCodeGenerator">
          <i class="el-icon-document"></i> Code Generator
        </el-button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card" @click="goToFormDesigner">
            <div class="stat-icon form-icon">
              <i class="el-icon-edit"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.formCount }}</div>
              <div class="stat-label">Forms</div>
            </div>
            <div class="stat-trend up">
              <i class="el-icon-top"></i> 12%
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" @click="goToWorkflowDesigner">
            <div class="stat-icon workflow-icon">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.workflowCount }}</div>
              <div class="stat-label">Workflows</div>
            </div>
            <div class="stat-trend up">
              <i class="el-icon-top"></i> 8%
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon api-icon">
              <i class="el-icon-connection"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.apiCount }}</div>
              <div class="stat-label">APIs</div>
            </div>
            <div class="stat-trend up">
              <i class="el-icon-top"></i> 25%
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon code-icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.generatedCodeCount }}</div>
              <div class="stat-label">Generated</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- Additional Stats -->
      <el-row :gutter="20" style="margin-top: 16px;">
        <el-col :span="8">
          <div class="stat-card small">
            <div class="stat-icon user-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeUsers }}</div>
              <div class="stat-label">Active Users</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card small">
            <div class="stat-icon form-submit-icon">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.formSubmissions }}</div>
              <div class="stat-label">Form Submissions</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card small">
            <div class="stat-icon runtime-icon">
              <i class="el-icon-cpu"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.workflowInstances }}</div>
              <div class="stat-label">Running Instances</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Charts Section -->
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="chart-card">
            <div class="chart-header">
              <h4>Form Submissions Trend</h4>
              <el-radio-group v-model="chartTimeRange" size="small">
                <el-radio-button label="7d">7 Days</el-radio-button>
                <el-radio-button label="30d">30 Days</el-radio-button>
                <el-radio-button label="90d">90 Days</el-radio-button>
              </el-radio-group>
            </div>
            <div ref="formChartRef" class="chart-container"></div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="chart-card">
            <div class="chart-header">
              <h4>Workflow Status</h4>
            </div>
            <div ref="workflowPieChartRef" class="chart-container-pie"></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Main Content -->
    <div class="dashboard-content">
      <el-row :gutter="20">
        <!-- Form Designer Preview -->
        <el-col :span="8">
          <div class="module-card" @click="goToFormDesigner">
            <div class="module-header">
              <i class="el-icon-edit"></i>
              <h3>Form Designer</h3>
            </div>
            <div class="module-body">
              <p>Drag-and-drop form builder with:</p>
              <ul>
                <li>Visual component palette</li>
                <li>Real-time preview</li>
                <li>Form validation rules</li>
                <li>Multiple input types</li>
              </ul>
            </div>
            <div class="module-footer">
              <el-button type="primary">Open Designer</el-button>
            </div>
          </div>
        </el-col>

        <!-- Workflow Designer Preview -->
        <el-col :span="8">
          <div class="module-card" @click="goToWorkflowDesigner">
            <div class="module-header">
              <i class="el-icon-s-order"></i>
              <h3>Workflow Designer</h3>
            </div>
            <div class="module-body">
              <p>Visual workflow modeling with:</p>
              <ul>
                <li>BPMN 2.0 support</li>
                <li>Multiple node types</li>
                <li>Gateway branching</li>
                <li>Deploy & suspend</li>
              </ul>
            </div>
            <div class="module-footer">
              <el-button type="success">Open Designer</el-button>
            </div>
          </div>
        </el-col>

        <!-- Code Generator Preview -->
        <el-col :span="8">
          <div class="module-card" @click="goToCodeGenerator">
            <div class="module-header">
              <i class="el-icon-document"></i>
              <h3>Code Generator</h3>
            </div>
            <div class="module-body">
              <p>Auto-generate CRUD code:</p>
              <ul>
                <li>Entity & Repository</li>
                <li>Service & Controller</li>
                <li>MyBatis Mapper</li>
                <li>Vue frontend</li>
              </ul>
            </div>
            <div class="module-footer">
              <el-button type="warning">Open Generator</el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- Recent Items and Activity -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <div class="recent-card">
            <div class="recent-header">
              <h4>Recent Forms</h4>
              <el-button type="text" @click="goToFormDesigner">View All</el-button>
            </div>
            <el-table :data="recentForms" size="small" max-height="240">
              <el-table-column prop="name" label="Name"></el-table-column>
              <el-table-column prop="status" label="Status" width="100">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 'PUBLISHED' ? 'success' : 'info'" size="mini">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="updateTime" label="Updated" width="150"></el-table-column>
              <el-table-column label="Actions" width="80">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="handleEditForm(scope.row)">Edit</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="recent-card">
            <div class="recent-header">
              <h4>Recent Workflows</h4>
              <el-button type="text" @click="goToWorkflowDesigner">View All</el-button>
            </div>
            <el-table :data="recentWorkflows" size="small" max-height="240">
              <el-table-column prop="name" label="Name"></el-table-column>
              <el-table-column prop="status" label="Status" width="100">
                <template slot-scope="scope">
                  <el-tag :type="getStatusType(scope.row.status)" size="mini">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="version" label="Ver" width="60"></el-table-column>
              <el-table-column label="Actions" width="80">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="handleEditWorkflow(scope.row)">Edit</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>

      <!-- Quick Actions & Activity Timeline -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="8">
          <div class="quick-actions-card">
            <div class="quick-actions-header">
              <h4>Quick Actions</h4>
            </div>
            <div class="quick-actions-grid">
              <div class="quick-action-item" @click="goToFormDesigner">
                <i class="el-icon-edit"></i>
                <span>New Form</span>
              </div>
              <div class="quick-action-item" @click="goToWorkflowDesigner">
                <i class="el-icon-s-order"></i>
                <span>New Workflow</span>
              </div>
              <div class="quick-action-item" @click="goToCodeGenerator">
                <i class="el-icon-document"></i>
                <span>Generate Code</span>
              </div>
              <div class="quick-action-item" @click="handleImport">
                <i class="el-icon-upload2"></i>
                <span>Import</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="activity-card">
            <div class="activity-header">
              <h4>Recent Activity</h4>
              <el-button type="text" size="mini">Clear</el-button>
            </div>
            <div class="activity-timeline">
              <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item">
                <div class="activity-icon" :class="activity.type">
                  <i :class="activity.icon"></i>
                </div>
                <div class="activity-content">
                  <div class="activity-text">{{ activity.text }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="system-card">
            <div class="system-header">
              <h4>System Status</h4>
            </div>
            <div class="system-items">
              <div class="system-item">
                <span class="system-label">Form Service</span>
                <el-switch v-model="systemStatus.formService" disabled size="small"></el-switch>
              </div>
              <div class="system-item">
                <span class="system-label">Workflow Service</span>
                <el-switch v-model="systemStatus.workflowService" disabled size="small"></el-switch>
              </div>
              <div class="system-item">
                <span class="system-label">Generator Service</span>
                <el-switch v-model="systemStatus.generatorService" disabled size="small"></el-switch>
              </div>
              <div class="system-item">
                <span class="system-label">Database</span>
                <el-switch v-model="systemStatus.database" disabled size="small"></el-switch>
              </div>
            </div>
            <div class="system-info">
              <p>Uptime: {{ systemUptime }}</p>
              <p>Version: 1.0.0</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Architecture Diagram -->
    <div class="architecture-section">
      <h2>Platform Architecture</h2>
      <div class="architecture-diagram">
        <div class="arch-layer">
          <div class="arch-label">Frontend</div>
          <div class="arch-components">
            <div class="arch-component">Vue 3</div>
            <div class="arch-component">Element UI</div>
            <div class="arch-component">Vuex</div>
          </div>
        </div>
        <div class="arch-arrow">^</div>
        <div class="arch-layer">
          <div class="arch-label">API Gateway</div>
          <div class="arch-components">
            <div class="arch-component">Spring Cloud Gateway</div>
            <div class="arch-component">Nacos</div>
            <div class="arch-component">Sentinel</div>
          </div>
        </div>
        <div class="arch-arrow">^</div>
        <div class="arch-layer">
          <div class="arch-label">Microservices</div>
          <div class="arch-components">
            <div class="arch-component">Form Service</div>
            <div class="arch-component">Workflow Service</div>
            <div class="arch-component">Generator Service</div>
            <div class="arch-component">Auth Service</div>
          </div>
        </div>
        <div class="arch-arrow">^</div>
        <div class="arch-layer">
          <div class="arch-label">Data Layer</div>
          <div class="arch-components">
            <div class="arch-component">MySQL</div>
            <div class="arch-component">Redis</div>
            <div class="arch-component">Dynamic DS</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { listFormConfig } from '@/api/nocode/form'
import { listWorkflowDefinition } from '@/api/nocode/workflow'

export default {
  name: 'NocodeDashboard',
  data() {
    return {
      chartTimeRange: '7d',
      stats: {
        formCount: 0,
        workflowCount: 0,
        apiCount: 0,
        generatedCodeCount: 0,
        activeUsers: 0,
        formSubmissions: 0,
        workflowInstances: 0
      },
      recentForms: [],
      recentWorkflows: [],
      recentActivities: [
        { type: 'form', icon: 'el-icon-edit', text: 'Form "User Registration" updated', time: '2 minutes ago' },
        { type: 'workflow', icon: 'el-icon-s-order', text: 'Workflow "Approval Process" deployed', time: '15 minutes ago' },
        { type: 'code', icon: 'el-icon-document', text: 'Code generated for "Order Management"', time: '1 hour ago' },
        { type: 'form', icon: 'el-icon-edit', text: 'Form "Survey Form" created', time: '2 hours ago' },
        { type: 'workflow', icon: 'el-icon-s-order', text: 'Workflow "Leave Request" suspended', time: '3 hours ago' }
      ],
      systemStatus: {
        formService: true,
        workflowService: true,
        generatorService: true,
        database: true
      },
      systemUptime: '15 days 8 hours',
      formChart: null,
      workflowPieChart: null
    }
  },
  watch: {
    chartTimeRange() {
      this.loadFormChartData()
    }
  },
  methods: {
    goToFormDesigner() {
      this.$router.push('/nocode/form')
    },
    goToWorkflowDesigner() {
      this.$router.push('/nocode/workflow')
    },
    goToCodeGenerator() {
      this.$router.push('/nocode/code-generator')
    },
    getStatusType(status) {
      const typeMap = {
        DRAFT: 'info',
        DEPLOYED: 'success',
        SUSPENDED: 'warning'
      }
      return typeMap[status] || 'info'
    },
    loadStats() {
      listFormConfig().then(res => {
        if (res.code === 200) {
          this.stats.formCount = res.data.length
          this.recentForms = res.data.slice(0, 5)
        }
      }).catch(() => {
        this.stats.formCount = 12
        this.recentForms = [
          { id: 1, name: 'User Registration', status: 'PUBLISHED', updateTime: '2026-04-03 10:00' },
          { id: 2, name: 'Contact Form', status: 'DRAFT', updateTime: '2026-04-02 15:30' },
          { id: 3, name: 'Survey Form', status: 'PUBLISHED', updateTime: '2026-04-01 09:00' }
        ]
      })
      listWorkflowDefinition().then(res => {
        if (res.code === 200) {
          this.stats.workflowCount = res.data.length
          this.recentWorkflows = res.data.slice(0, 5)
        }
      }).catch(() => {
        this.stats.workflowCount = 8
        this.recentWorkflows = [
          { id: 1, name: 'Leave Request', status: 'DEPLOYED', version: '1.2' },
          { id: 2, name: 'Approval Process', status: 'DRAFT', version: '1.0' },
          { id: 3, name: 'Order Workflow', status: 'SUSPENDED', version: '2.1' }
        ]
      })
      this.stats.apiCount = 24
      this.stats.generatedCodeCount = 156
      this.stats.activeUsers = 42
      this.stats.formSubmissions = 1289
      this.stats.workflowInstances = 37
    },
    initCharts() {
      this.initFormChart()
      this.initWorkflowPieChart()
    },
    initFormChart() {
      if (!this.$refs.formChartRef) return
      this.formChart = echarts.init(this.$refs.formChartRef)
      this.loadFormChartData()
    },
    loadFormChartData() {
      if (!this.formChart) return
      const days = this.chartTimeRange === '7d' ? 7 : this.chartTimeRange === '30d' ? 30 : 90
      const data = []
      const labels = []
      for (let i = days - 1; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        labels.push(`${date.getMonth() + 1}/${date.getDate()}`)
        data.push(Math.floor(Math.random() * 100) + 20)
      }
      this.formChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: labels },
        yAxis: { type: 'value', name: 'Submissions' },
        series: [
          {
            name: 'Form Submissions',
            type: 'line',
            smooth: true,
            areaStyle: { opacity: 0.3 },
            data: data,
            itemStyle: { color: '#667eea' },
            areaColor: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
              ]
            }
           }
        ]
      })
    },
    initWorkflowPieChart() {
      if (!this.$refs.workflowPieChartRef) return
      this.workflowPieChart = echarts.init(this.$refs.workflowPieChartRef)
      this.workflowPieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center' },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
            label: { show: false },
            emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
            data: [
              { value: this.stats.workflowCount || 8, name: 'Active', itemStyle: { color: '#67c23a' } },
              { value: 3, name: 'Draft', itemStyle: { color: '#909399' } },
              { value: 2, name: 'Suspended', itemStyle: { color: '#f56c6c' } }
            ]
          }
        ]
      })
    },
    handleEditForm(row) {
      this.$router.push({ path: '/nocode/form', query: { id: row.id } })
    },
    handleEditWorkflow(row) {
      this.$router.push({ path: '/nocode/workflow', query: { id: row.id } })
    },
    handleImport() {
      this.$message.info('Import dialog would open here')
    },
    handleResize() {
      if (this.formChart) this.formChart.resize()
      if (this.workflowPieChart) this.workflowPieChart.resize()
    }
  },
  mounted() {
    this.loadStats()
    this.$nextTick(() => {
      this.initCharts()
    })
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.formChart) this.formChart.dispose()
    if (this.workflowPieChart) this.workflowPieChart.dispose()
  }
}
</script>

<style scoped>
.nocode-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30px 40px;
  border-radius: 12px;
  color: #fff;
  margin-bottom: 24px;
}

.dashboard-header h1 {
  margin: 0;
  font-size: 28px;
}

.subtitle {
  margin: 8px 0 0 0;
  opacity: 0.9;
}

.header-right {
  display: flex;
  gap: 12px;
}

.header-right .el-button {
  padding: 16px 24px;
}

.stats-container {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-card.small {
  padding: 16px 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-card.small .stat-icon {
  width: 48px;
  height: 48px;
  font-size: 22px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.stat-card.small .stat-value {
  font-size: 24px;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.stat-trend {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
}

.stat-trend.up {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.1);
}

.stat-trend.down {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
}

.form-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.workflow-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
.api-icon { background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%); }
.code-icon { background: linear-gradient(135deg, #f2994a 0%, #f2c94c 100%); }
.user-icon { background: linear-gradient(135deg, #fc466b 0%, #3f5efb 100%); }
.form-submit-icon { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); color: #666; }
.runtime-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }

.charts-section {
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h4 {
  margin: 0;
  font-size: 16px;
}

.chart-container {
  height: 280px;
}

.chart-container-pie {
  height: 280px;
}

.dashboard-content {
  margin-bottom: 24px;
}

.module-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.module-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.module-header {
  padding: 20px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 12px;
}

.module-header i {
  font-size: 24px;
}

.module-header h3 {
  margin: 0;
  font-size: 18px;
}

.module-card:nth-child(1) .module-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.module-card:nth-child(2) .module-header { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
.module-card:nth-child(3) .module-header { background: linear-gradient(135deg, #f2994a 0%, #f2c94c 100%); }

.module-body {
  padding: 20px;
}

.module-body p {
  margin: 0 0 12px 0;
  color: #666;
}

.module-body ul {
  margin: 0;
  padding-left: 20px;
  color: #999;
}

.module-body li {
  margin-bottom: 4px;
}

.module-footer {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

.recent-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.recent-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.recent-header h4 {
  margin: 0;
}

.quick-actions-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  height: 100%;
}

.quick-actions-header h4 {
  margin: 0 0 16px 0;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 12px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-action-item:hover {
  background: #667eea;
  color: #fff;
}

.quick-action-item i {
  font-size: 28px;
}

.quick-action-item span {
  font-size: 12px;
}

.activity-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  height: 100%;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.activity-header h4 {
  margin: 0;
}

.activity-timeline {
  max-height: 200px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  flex-shrink: 0;
}

.activity-icon.form { background: #667eea; }
.activity-icon.workflow { background: #67c23a; }
.activity-icon.code { background: #f2994a; }

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 13px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-time {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}

.system-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  height: 100%;
}

.system-header h4 {
  margin: 0 0 16px 0;
}

.system-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.system-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.system-label {
  font-size: 13px;
  color: #666;
}

.system-info {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.system-info p {
  margin: 4px 0;
  font-size: 12px;
  color: #999;
}

.architecture-section {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
}

.architecture-section h2 {
  margin: 0 0 24px 0;
  text-align: center;
}

.architecture-diagram {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.arch-layer {
  width: 100%;
  max-width: 800px;
  text-align: center;
}

.arch-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.arch-components {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.arch-component {
  padding: 12px 24px;
  background: #667eea;
  color: #fff;
  border-radius: 20px;
  font-size: 14px;
}

.arch-arrow {
  font-size: 20px;
  color: #667eea;
}
</style>