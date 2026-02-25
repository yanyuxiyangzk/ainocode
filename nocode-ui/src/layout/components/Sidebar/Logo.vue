<template>
  <div class="sidebar-logo-container" :class="{ collapse: collapse }">
    <router-link key="collapse" class="sidebar-logo-link" to="/">
      <img v-if="logo" :src="logo" class="sidebar-logo" />
      <h1 v-show="!collapse" class="sidebar-title">{{ title }}</h1>
    </router-link>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineOptions({ name: 'SidebarLogo' })

defineProps<{ collapse: boolean }>()

const title = ref('零代码平台')
const logo = ref('')
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter-from,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;

  &.collapse {
    .sidebar-logo {
      margin-right: 0;
    }
  }

  .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .sidebar-logo {
    width: 32px;
    height: 32px;
    vertical-align: middle;
    margin-right: 12px;
  }

  .sidebar-title {
    display: inline-block;
    margin: 0;
    color: #fff;
    font-weight: 600;
    line-height: 50px;
    font-size: 14px;
    font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
    vertical-align: middle;
  }
}
</style>
