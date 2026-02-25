<template>
  <div class="user-manager card-box">
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.userName" placeholder="用户名" clearable style="width: 150px" />
        <el-input v-model="queryParams.phonenumber" placeholder="手机号" clearable style="width: 150px" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" icon="Plus" @click="handleCreate">新增</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="userList" border stripe>
      <el-table-column label="用户名" prop="userName" min-width="100" />
      <el-table-column label="昵称" prop="nickName" min-width="100" />
      <el-table-column label="部门" prop="deptName" min-width="120" />
      <el-table-column label="手机号" prop="phonenumber" width="120" />
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="{ row }">
          <el-switch v-model="row.status" active-value="0" inactive-value="1" @change="handleStatusChange(row)" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link icon="Key" @click="handleResetPwd(row)">重置密码</el-button>
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
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

defineOptions({ name: 'User' })

const loading = ref(false)
const total = ref(4)
const userList = ref([
  { userId: 1, userName: 'admin', nickName: '管理员', deptName: '研发部', phonenumber: '15888888888', status: '0', createTime: '2024-01-01' },
  { userId: 2, userName: 'zhangsan', nickName: '张三', deptName: '研发部', phonenumber: '15666666666', status: '0', createTime: '2024-01-02' },
  { userId: 3, userName: 'lisi', nickName: '李四', deptName: '市场部', phonenumber: '15666666667', status: '0', createTime: '2024-01-03' },
  { userId: 4, userName: 'wangwu', nickName: '王五', deptName: '财务部', phonenumber: '15666666668', status: '1', createTime: '2024-01-04' }
])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  phonenumber: '',
  status: ''
})

const handleQuery = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 300)
}

const resetQuery = () => {
  queryParams.userName = ''
  queryParams.phonenumber = ''
  queryParams.status = ''
  handleQuery()
}

const handleCreate = () => {
  ElMessage.info('新增用户功能开发中')
}

const handleEdit = (row: any) => {
  ElMessage.info(`编辑用户: ${row.userName}`)
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除用户 ${row.userName}?`, '警告', { type: 'warning' })
    .then(() => ElMessage.success('删除成功'))
}

const handleResetPwd = (row: any) => {
  ElMessage.success(`已重置 ${row.userName} 的密码`)
}

const handleStatusChange = (row: any) => {
  ElMessage.success(`已${row.status === '0' ? '启用' : '停用'}用户 ${row.userName}`)
}
</script>
