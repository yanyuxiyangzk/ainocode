<template>
  <div class="sidebar-container">
    <Logo :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :unique-opened="true"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical"
      >
        <SidebarItem
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { constantRoutes } from '@/router'
import Logo from './Logo.vue'
import SidebarItem from './SidebarItem.vue'
import variables from '@/assets/styles/variables.scss?inline'

defineOptions({ name: 'Sidebar' })

const route = useRoute()
const appStore = useAppStore()

const routes = computed(() => constantRoutes)
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu as string
  }
  return path
})
const isCollapse = computed(() => !appStore.sidebar.opened)
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: 100%;

  :deep(.el-menu) {
    border: none;
    height: 100%;
    width: 100% !important;
  }

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    &:hover {
      background-color: #263445 !important;
    }
  }

  :deep(.el-menu-item.is-active) {
    background-color: #409eff !important;
  }
}
</style>
