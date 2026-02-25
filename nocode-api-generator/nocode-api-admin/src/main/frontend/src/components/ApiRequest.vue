<template>
  <div class="api-request">
    <div class="request-header">
      <span :class="['method-badge', method]">{{ method }}</span>
      <span class="request-url">{{ url }}</span>
      <el-button size="mini" type="text" @click="copyUrl">
        <i class="el-icon-document-copy"></i>
      </el-button>
    </div>
    <div class="request-meta" v-if="description">
      <span class="description">{{ description }}</span>
    </div>
    <el-collapse v-if="parameters.length || example">
      <el-collapse-item title="请求参数" name="params">
        <el-table v-if="parameters.length" :data="parameters" size="small" border>
          <el-table-column prop="name" label="参数名" width="150"></el-table-column>
          <el-table-column prop="type" label="类型" width="100"></el-table-column>
          <el-table-column prop="required" label="必填" width="70">
            <template slot-scope="{row}">
              <el-tag v-if="row.required" type="danger" size="mini">是</el-tag>
              <span v-else>否</span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="说明"></el-table-column>
        </el-table>
        <div v-else class="no-params">无参数</div>
      </el-collapse-item>
      <el-collapse-item title="请求示例" name="example" v-if="example">
        <div class="code-block">{{ example }}</div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
export default {
  name: 'ApiRequest',
  props: {
    method: {
      type: String,
      required: true,
      validator: (val) => ['GET', 'POST', 'PUT', 'DELETE'].includes(val)
    },
    url: {
      type: String,
      required: true
    },
    description: {
      type: String,
      default: ''
    },
    parameters: {
      type: Array,
      default: () => []
    },
    example: {
      type: String,
      default: ''
    }
  },
  methods: {
    copyUrl() {
      const input = document.createElement('input')
      input.value = this.url
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      this.$message.success('已复制到剪贴板')
    }
  }
}
</script>

<style scoped>
.api-request {
  background: #fff;
  border-radius: 4px;
  margin-bottom: 15px;
}
.request-header {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background: #fafafa;
  border-radius: 4px 4px 0 0;
}
.method-badge {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  margin-right: 10px;
  color: #fff;
}
.method-badge.GET { background: #67c23a; }
.method-badge.POST { background: #409EFF; }
.method-badge.PUT { background: #e6a23c; }
.method-badge.DELETE { background: #f56c6c; }
.request-url {
  font-family: 'Consolas', monospace;
  font-size: 14px;
  color: #303133;
  flex: 1;
}
.request-meta {
  padding: 10px 15px;
  border-bottom: 1px solid #ebeef5;
}
.description {
  color: #909399;
  font-size: 13px;
}
.no-params {
  padding: 20px;
  text-align: center;
  color: #909399;
}
</style>
