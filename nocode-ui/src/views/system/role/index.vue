<template>
  <div class="role-manager card-box">
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.roleName" placeholder="角色名称" clearable style="width: 200px" />
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" icon="Plus" @click="handleCreate">新增</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="roleList" border stripe>
      <el-table-column label="角色编号" prop="roleId" width="100" />
      <el-table-column label="角色名称" prop="roleName" min-width="120" />
      <el-table-column label="权限字符" prop="roleKey" min-width="120" />
      <el-table-column label="排序" prop="roleSort" width="80" align="center" />
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link icon="Key" @click="handleAuth(row)">权限</el-button>
          <el-button type="danger" link icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

defineOptions({ name: 'Role' })

const loading = ref(false)
const roleList = ref([
  { roleId: 1, roleName: '超级管理员', roleKey: 'admin', roleSort: 1, status: '0', createTime: '2024-01-01' },
  { roleId: 2, roleName: '普通角色', roleKey: 'common', roleSort: 2, status: '0', createTime: '2024-01-01' },
  { roleId: 3, roleName: '开发人员', roleKey: 'developer', roleSort: 3, status: '0', createTime: '2024-01-02' }
])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: ''
})

const handleQuery = () => {}
const resetQuery = () => { queryParams.roleName = '' }
const handleCreate = () => ElMessage.info('新增角色')
const handleEdit = (row: any) => ElMessage.info(`编辑角色: ${row.roleName}`)
const handleAuth = (row: any) => ElMessage.info(`配置权限: ${row.roleName}`)
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除角色 ${row.roleName}?`, '警告', { type: 'warning' })
    .then(() => ElMessage.success('删除成功'))
}
</script>
