<template>
  <div class="dept-manager card-box">
    <el-table :data="deptList" row-key="deptId" border default-expand-all>
      <el-table-column label="部门名称" prop="deptName" min-width="180" />
      <el-table-column label="排序" prop="orderNum" width="80" align="center" />
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link icon="Plus" @click="handleCreate(row)">新增</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

defineOptions({ name: 'Dept' })

const deptList = ref([
  { deptId: 100, deptName: '科技有限公司', orderNum: 0, status: '0', createTime: '2024-01-01', children: [
    { deptId: 101, deptName: '深圳总公司', orderNum: 1, status: '0', createTime: '2024-01-01', children: [
      { deptId: 103, deptName: '研发部门', orderNum: 1, status: '0', createTime: '2024-01-01' },
      { deptId: 104, deptName: '市场部门', orderNum: 2, status: '0', createTime: '2024-01-01' },
      { deptId: 105, deptName: '财务部门', orderNum: 3, status: '0', createTime: '2024-01-01' }
    ]},
    { deptId: 102, deptName: '长沙分公司', orderNum: 2, status: '0', createTime: '2024-01-01', children: [
      { deptId: 106, deptName: '研发部门', orderNum: 1, status: '0', createTime: '2024-01-01' },
      { deptId: 107, deptName: '市场部门', orderNum: 2, status: '0', createTime: '2024-01-01' }
    ]}
  ]}
])

const handleEdit = (row: any) => ElMessage.info(`编辑部门: ${row.deptName}`)
const handleCreate = (row: any) => ElMessage.info(`新增子部门: ${row.deptName}`)
</script>
