<template>
  <div class="menu-manager card-box">
    <el-table :data="menuList" row-key="menuId" border default-expand-all>
      <el-table-column label="菜单名称" prop="menuName" min-width="180" />
      <el-table-column label="图标" prop="icon" width="80" align="center">
        <template #default="{ row }">
          <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="orderNum" width="80" align="center" />
      <el-table-column label="路由地址" prop="path" min-width="150" />
      <el-table-column label="组件路径" prop="component" min-width="150" />
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">
            {{ row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
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

defineOptions({ name: 'Menu' })

const menuList = ref([
  { menuId: 1, menuName: '系统管理', icon: 'Setting', orderNum: 1, path: '/system', component: '', status: '0', children: [
    { menuId: 11, menuName: '用户管理', icon: 'User', orderNum: 1, path: 'user', component: 'system/user/index', status: '0' },
    { menuId: 12, menuName: '角色管理', icon: 'UserFilled', orderNum: 2, path: 'role', component: 'system/role/index', status: '0' },
    { menuId: 13, menuName: '菜单管理', icon: 'Menu', orderNum: 3, path: 'menu', component: 'system/menu/index', status: '0' },
    { menuId: 14, menuName: '部门管理', icon: 'OfficeBuilding', orderNum: 4, path: 'dept', component: 'system/dept/index', status: '0' }
  ]},
  { menuId: 2, menuName: '零代码配置', icon: 'Grid', orderNum: 2, path: '/nocode', component: '', status: '0', children: [
    { menuId: 21, menuName: '表管理', icon: 'Table', orderNum: 1, path: 'table', component: 'nocode/table/index', status: '0' },
    { menuId: 22, menuName: '实体配置', icon: 'Document', orderNum: 2, path: 'entity', component: 'nocode/entity/index', status: '0' },
    { menuId: 23, menuName: '界面配置', icon: 'Monitor', orderNum: 3, path: 'page', component: 'nocode/page/index', status: '0' },
    { menuId: 24, menuName: 'API配置', icon: 'Connection', orderNum: 4, path: 'api', component: 'nocode/api/index', status: '0' }
  ]},
  { menuId: 3, menuName: '插件管理', icon: 'Component', orderNum: 3, path: '/plugin', component: '', status: '0', children: [
    { menuId: 31, menuName: '插件列表', icon: 'List', orderNum: 1, path: 'list', component: 'plugin/list', status: '0' },
    { menuId: 32, menuName: '插件市场', icon: 'Shop', orderNum: 2, path: 'market', component: 'plugin/market', status: '0' }
  ]}
])

const handleEdit = (row: any) => ElMessage.info(`编辑菜单: ${row.menuName}`)
const handleCreate = (row: any) => ElMessage.info(`新增子菜单: ${row.menuName}`)
</script>
