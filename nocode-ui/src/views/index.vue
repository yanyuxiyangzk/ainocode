<template>
  <div class="dashboard-container">
    <div class="dashboard-editor-container">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="welcome-card">
            <template #header>
              <div class="card-header">
                <span>欢迎使用 RuoYi-Cloud-Nocode 零代码微服务平台</span>
              </div>
            </template>
            <div class="welcome-content">
              <p>本平台是一款基于 Spring 生态的零代码微服务平台，采用「Skill + LLM + Json2UI + Agent + 代码知识库」核心技术架构。</p>
              <el-divider />
              <h4>核心功能：</h4>
              <el-row :gutter="20">
                <el-col :span="8" v-for="feature in features" :key="feature.title">
                  <div class="feature-item" @click="handleNav(feature.path)">
                    <el-icon :size="40" :color="feature.color">
                      <component :is="feature.icon" />
                    </el-icon>
                    <div class="feature-title">{{ feature.title }}</div>
                    <div class="feature-desc">{{ feature.desc }}</div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <template #header>
              <span>快捷入口</span>
            </template>
            <div class="quick-entry">
              <el-button type="primary" class="entry-btn" @click="router.push('/nocode/table')">
                <el-icon><Grid /></el-icon>
                表管理
              </el-button>
              <el-button type="success" class="entry-btn" @click="router.push('/nocode/entity')">
                <el-icon><Document /></el-icon>
                实体配置
              </el-button>
              <el-button type="warning" class="entry-btn" @click="router.push('/nocode/page')">
                <el-icon><Monitor /></el-icon>
                界面配置
              </el-button>
              <el-button type="info" class="entry-btn" @click="router.push('/plugin/list')">
                <el-icon><Component /></el-icon>
                插件管理
              </el-button>
            </div>
          </el-card>
          <el-card class="mt-10">
            <template #header>
              <span>系统信息</span>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="版本">v1.0.0</el-descriptions-item>
              <el-descriptions-item label="技术栈">Vue3 + Element Plus</el-descriptions-item>
              <el-descriptions-item label="后端框架">Spring Cloud</el-descriptions-item>
              <el-descriptions-item label="插件框架">PF4J + Liquor</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Grid, Document, Monitor, Component, Setting, Cpu, Connection, DataAnalysis } from '@element-plus/icons-vue'

defineOptions({ name: 'Index' })

const router = useRouter()

const features = ref([
  { title: '表管理', desc: '可视化建表，自动生成数据库表结构', icon: 'Grid', color: '#409eff', path: '/nocode/table' },
  { title: '实体配置', desc: '配置实体字段、验证规则和关联关系', icon: 'Document', color: '#67c23a', path: '/nocode/entity' },
  { title: '界面配置', desc: '拖拽式设计表单和列表页面', icon: 'Monitor', color: '#e6a23c', path: '/nocode/page' },
  { title: 'API配置', desc: '可视化配置 RESTful API 接口', icon: 'Connection', color: '#f56c6c', path: '/nocode/api' },
  { title: '插件管理', desc: '热插拔插件生命周期管理', icon: 'Component', color: '#909399', path: '/plugin/list' },
  { title: '代码生成', desc: '根据配置自动生成完整代码', icon: 'Cpu', color: '#9b59b6', path: '/nocode/table' }
])

const handleNav = (path: string) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;

  .welcome-card {
    .welcome-content {
      padding: 10px;
    }
  }

  .feature-item {
    text-align: center;
    padding: 20px 10px;
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      background-color: #f5f7fa;
      transform: translateY(-5px);
    }

    .feature-title {
      font-size: 16px;
      font-weight: bold;
      margin-top: 10px;
      color: #303133;
    }

    .feature-desc {
      font-size: 12px;
      color: #909399;
      margin-top: 5px;
    }
  }

  .quick-entry {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;

    .entry-btn {
      height: 60px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 5px;
    }
  }
}
</style>
