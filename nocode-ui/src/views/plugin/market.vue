<template>
  <div class="plugin-market card-box">
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索插件"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="categoryFilter" placeholder="分类" clearable style="width: 150px">
          <el-option label="全部" value="" />
          <el-option label="开发工具" value="dev" />
          <el-option label="系统集成" value="integration" />
          <el-option label="数据分析" value="analytics" />
          <el-option label="UI组件" value="ui" />
        </el-select>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :span="6" v-for="plugin in pluginList" :key="plugin.pluginId">
        <el-card class="plugin-card" shadow="hover">
          <div class="plugin-header">
            <div class="plugin-icon">
              <el-icon :size="40"><component :is="plugin.icon" /></el-icon>
            </div>
            <div class="plugin-info">
              <h3>{{ plugin.name }}</h3>
              <p class="provider">{{ plugin.provider }}</p>
            </div>
          </div>
          <div class="plugin-desc">{{ plugin.description }}</div>
          <div class="plugin-meta">
            <el-rate v-model="plugin.rating" disabled />
            <span class="downloads">{{ plugin.downloads }} 次下载</span>
          </div>
          <div class="plugin-footer">
            <el-tag size="small">{{ plugin.category }}</el-tag>
            <el-button
              :type="plugin.installed ? 'info' : 'primary'"
              size="small"
              :disabled="plugin.installed"
              @click="handleInstall(plugin)"
            >
              {{ plugin.installed ? '已安装' : '安装' }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Connection, DataAnalysis, Monitor, Cpu, Document, ChatLineSquare } from '@element-plus/icons-vue'

defineOptions({ name: 'PluginMarket' })

const searchKeyword = ref('')
const categoryFilter = ref('')

const pluginList = ref([
  {
    pluginId: 'workflow-plugin',
    name: '工作流引擎',
    provider: 'Nocode',
    description: '强大的工作流设计和执行引擎，支持BPMN 2.0标准',
    category: '开发工具',
    icon: 'Connection',
    rating: 5,
    downloads: 1256,
    installed: true
  },
  {
    pluginId: 'report-plugin',
    name: '报表设计器',
    provider: 'Nocode',
    description: '可视化报表设计，支持多种图表和数据源',
    category: '数据分析',
    icon: 'DataAnalysis',
    rating: 4.5,
    downloads: 892,
    installed: true
  },
  {
    pluginId: 'ocr-plugin',
    name: 'OCR识别',
    provider: 'Community',
    description: '高精度OCR文字识别，支持多种语言',
    category: '系统集成',
    icon: 'Document',
    rating: 4,
    downloads: 567,
    installed: false
  },
  {
    pluginId: 'chart-plugin',
    name: '高级图表',
    provider: 'Community',
    description: '丰富的图表组件，支持3D图表和自定义主题',
    category: 'UI组件',
    icon: 'Monitor',
    rating: 4.5,
    downloads: 1102,
    installed: false
  },
  {
    pluginId: 'ai-plugin',
    name: 'AI助手',
    provider: 'Nocode',
    description: '集成大语言模型，提供智能对话和内容生成',
    category: '开发工具',
    icon: 'Cpu',
    rating: 5,
    downloads: 2048,
    installed: false
  },
  {
    pluginId: 'im-plugin',
    name: '即时通讯',
    provider: 'Community',
    description: '实时消息推送和在线聊天功能',
    category: '系统集成',
    icon: 'ChatLineSquare',
    rating: 4,
    downloads: 745,
    installed: false
  },
  {
    pluginId: 'etl-plugin',
    name: 'ETL工具',
    provider: 'Nocode',
    description: '数据抽取、转换、加载一体化工具',
    category: '数据分析',
    icon: 'DataAnalysis',
    rating: 4.5,
    downloads: 456,
    installed: false
  },
  {
    pluginId: 'form-plugin',
    name: '动态表单',
    provider: 'Community',
    description: '复杂表单设计，支持动态校验和联动',
    category: 'UI组件',
    icon: 'Document',
    rating: 4,
    downloads: 823,
    installed: false
  }
])

const handleSearch = () => {
  ElMessage.info('搜索功能开发中...')
}

const handleInstall = (plugin: any) => {
  ElMessage.success(`正在安装插件: ${plugin.name}`)
  plugin.installed = true
}
</script>

<style lang="scss" scoped>
.plugin-market {
  .table-toolbar {
    margin-bottom: 20px;
  }

  .plugin-card {
    margin-bottom: 20px;

    .plugin-header {
      display: flex;
      align-items: center;
      gap: 15px;
      margin-bottom: 15px;

      .plugin-icon {
        width: 60px;
        height: 60px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      .plugin-info {
        h3 {
          margin: 0;
          font-size: 16px;
        }

        .provider {
          margin: 5px 0 0;
          color: #909399;
          font-size: 12px;
        }
      }
    }

    .plugin-desc {
      color: #606266;
      font-size: 13px;
      margin-bottom: 15px;
      line-height: 1.5;
    }

    .plugin-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      .downloads {
        font-size: 12px;
        color: #909399;
      }
    }

    .plugin-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
