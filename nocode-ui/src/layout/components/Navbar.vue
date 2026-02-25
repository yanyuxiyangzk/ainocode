<template>
  <div class="navbar">
    <div class="left-menu">
      <hamburger class="hamburger-container" @toggle-click="toggleSideBar" />
      <breadcrumb class="breadcrumb-container" />
    </div>
    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="30" :src="avatar">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <span class="user-name">{{ name }}</span>
          <el-icon class="caret-bottom"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link to="/user/profile">
              <el-dropdown-item>个人中心</el-dropdown-item>
            </router-link>
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { UserFilled, ArrowDown } from '@element-plus/icons-vue'
import Hamburger from './Hamburger.vue'
import Breadcrumb from './Breadcrumb.vue'

defineOptions({ name: 'Navbar' })

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const name = computed(() => userStore.name)
const avatar = computed(() => userStore.avatar)

const toggleSideBar = () => {
  appStore.toggleSidebar(false)
}

const logout = async () => {
  await userStore.LogOut()
  router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px;

  .left-menu {
    display: flex;
    align-items: center;
  }

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    margin-left: 8px;
  }

  .right-menu {
    display: flex;
    align-items: center;

    .avatar-container {
      cursor: pointer;
    }

    .avatar-wrapper {
      display: flex;
      align-items: center;
      gap: 8px;

      .user-name {
        font-size: 14px;
      }

      .caret-bottom {
        cursor: pointer;
      }
    }
  }
}
</style>
