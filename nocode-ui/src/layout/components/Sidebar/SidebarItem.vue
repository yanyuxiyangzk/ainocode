<template>
  <div v-if="!item.meta?.hidden">
    <template v-if="hasOneShowingChild(item.children, item)">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)">
          <el-icon v-if="onlyOneChild.meta?.icon">
            <component :is="onlyOneChild.meta.icon" />
          </el-icon>
          <template #title>
            <span>{{ onlyOneChild.meta?.title }}</span>
          </template>
        </el-menu-item>
      </app-link>
    </template>

    <el-sub-menu v-else :index="resolvePath(item.path)" teleported>
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <SidebarItem
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import { isExternal } from '@/utils/validate'
import AppLink from './Link.vue'

defineOptions({ name: 'SidebarItem' })

const props = defineProps<{
  item: RouteRecordRaw
  basePath: string
}>()

const onlyOneChild = ref<RouteRecordRaw>({} as RouteRecordRaw)

const hasOneShowingChild = (children: RouteRecordRaw[] = [], parent: RouteRecordRaw): boolean => {
  const showingChildren = children.filter((item) => {
    if (item.meta?.hidden) {
      return false
    } else {
      onlyOneChild.value = item
      return true
    }
  })

  if (showingChildren.length === 1) {
    return true
  }

  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true } as any
    return true
  }

  return false
}

const resolvePath = (routePath: string): string => {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  const path = props.basePath.endsWith('/')
    ? props.basePath + routePath
    : props.basePath + '/' + routePath
  return path.replace(/\/+/g, '/').replace(/\/$/, '') || '/'
}
</script>
