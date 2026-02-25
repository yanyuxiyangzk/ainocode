<template>
  <div class="plugin-manager card-box">
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.pluginName"
          placeholder="请输入插件名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="已启用" value="started" />
          <el-option label="已停止" value="stopped" />
          <el-option label="已禁用" value="disabled" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" icon="Upload" @click="handleInstall">安装插件</el-button>
        <el-button type="success" icon="Shop" @click="router.push('/plugin/market')">插件市场</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="pluginList" border stripe>
      <el-table-column label="插件名称" prop="pluginName" min-width="150" />
      <el-table-column label="插件ID" prop="pluginId" min-width="150" show-overflow-tooltip />
      <el-table-column label="版本" prop="version" width="100" align="center" />
      <el-table-column label="提供者" prop="provider" width="120" />
      <el-table-column label="状态" prop="status" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="安装时间" prop="installTime" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'stopped'"
            type="success"
            link
            icon="VideoPlay"
            @click="handleStart(row)"
          >启动</el-button>
          <el-button
            v-if="row.status === 'started'"
            type="warning"
            link
            icon="VideoPause"
            @click="handleStop(row)"
          >停止</el-button>
          <el-button type="primary" link icon="View" @click="handleDetail(row)">详情</el-button>
          <el-button type="primary" link icon="Setting" @click="handleConfig(row)">配置</el-button>
          <el-button type="danger" link icon="Delete" @click="handleUninstall(row)">卸载</el-button>
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

    <!-- 插件详情对话框 -->
    <el-dialog v-model="detailVisible" title="插件详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="插件名称">{{ currentPlugin.pluginName }}</el-descriptions-item>
        <el-descriptions-item label="插件ID">{{ currentPlugin.pluginId }}</el-descriptions-item>
        <el-descriptions-item label="版本">{{ currentPlugin.version }}</el-descriptions-item>
        <el-descriptions-item label="提供者">{{ currentPlugin.provider }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentPlugin.status)">
            {{ getStatusLabel(currentPlugin.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="安装时间">{{ currentPlugin.installTime }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentPlugin.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 安装插件对话框 -->
    <el-dialog v-model="installVisible" title="安装插件" width="500px">
      <el-upload
        class="plugin-upload"
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">
          拖拽插件文件到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 .jar 或 .zip 格式的插件包
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="installVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmInstall">安装</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'

defineOptions({ name: 'PluginList' })

const router = useRouter()
const loading = ref(false)
const total = ref(0)
const pluginList = ref<any[]>([])
const detailVisible = ref(false)
const installVisible = ref(false)
const currentPlugin = ref<any>({})
const uploadFile = ref<File | null>(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  pluginName: '',
  status: ''
})

// 模拟数据
const mockData = [
  { pluginId: 'workflow-plugin', pluginName: '工作流插件', version: '1.0.0', provider: 'Nocode', status: 'started', installTime: '2024-01-01 10:00:00', description: '提供工作流设计和执行能力' },
  { pluginId: 'report-plugin', pluginName: '报表插件', version: '1.2.0', provider: 'Nocode', status: 'started', installTime: '2024-01-02 14:30:00', description: '提供报表设计和展示能力' },
  { pluginId: 'ocr-plugin', pluginName: 'OCR识别插件', version: '2.0.0', provider: 'Community', status: 'stopped', installTime: '2024-01-03 09:00:00', description: '提供OCR文字识别能力' },
  { pluginId: 'sms-plugin', pluginName: '短信插件', version: '1.0.0', provider: 'Nocode', status: 'disabled', installTime: '2024-01-04 16:00:00', description: '提供短信发送能力' }
]

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    started: 'success',
    stopped: 'warning',
    disabled: 'info'
  }
  return types[status] || 'info'
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    started: '已启用',
    stopped: '已停止',
    disabled: '已禁用'
  }
  return labels[status] || status
}

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    pluginList.value = mockData.filter(item => {
      if (queryParams.pluginName && !item.pluginName.includes(queryParams.pluginName)) return false
      if (queryParams.status && item.status !== queryParams.status) return false
      return true
    })
    total.value = pluginList.value.length
    loading.value = false
  }, 300)
}

const resetQuery = () => {
  queryParams.pluginName = ''
  queryParams.status = ''
  handleQuery()
}

const handleStart = (row: any) => {
  ElMessage.success(`插件 ${row.pluginName} 启动成功`)
  row.status = 'started'
}

const handleStop = (row: any) => {
  ElMessage.success(`插件 ${row.pluginName} 已停止`)
  row.status = 'stopped'
}

const handleDetail = (row: any) => {
  currentPlugin.value = row
  detailVisible.value = true
}

const handleConfig = (row: any) => {
  ElMessage.info(`配置插件: ${row.pluginName}`)
}

const handleUninstall = (row: any) => {
  ElMessageBox.confirm(`确认卸载插件 ${row.pluginName}?`, '警告', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('卸载成功')
    handleQuery()
  })
}

const handleInstall = () => {
  uploadFile.value = null
  installVisible.value = true
}

const handleFileChange = (file: any) => {
  uploadFile.value = file.raw
}

const handleConfirmInstall = () => {
  if (!uploadFile.value) {
    ElMessage.warning('请先选择插件文件')
    return
  }
  ElMessage.success('插件安装成功')
  installVisible.value = false
  handleQuery()
}

handleQuery()
</script>

<style lang="scss" scoped>
.plugin-manager {
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

  .plugin-upload {
    width: 100%;
  }
}
</style>
