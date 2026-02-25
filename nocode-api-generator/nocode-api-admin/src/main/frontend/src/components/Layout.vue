<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-content">
        <div class="logo">
          <i class="el-icon-cpu"></i>
          <span>NoCode API Generator</span>
        </div>
        <div class="header-right">
          <el-tag type="success" size="small">v1.0.0</el-tag>
        </div>
      </div>
    </el-header>
    <el-container>
      <el-aside class="layout-aside" width="220px">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#1f2d3d"
          text-color="#bfcbd9"
          active-text-color="#409EFF">
          <el-menu-item index="/">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>
          <el-menu-item index="/datasources">
            <i class="el-icon-connection"></i>
            <span slot="title">数据源管理</span>
          </el-menu-item>
          <el-menu-item index="/tables">
            <i class="el-icon-menu"></i>
            <span slot="title">数据表列表</span>
          </el-menu-item>
          <el-menu-item index="/create-table">
            <i class="el-icon-plus"></i>
            <span slot="title">创建表</span>
          </el-menu-item>
          <el-menu-item index="/api-doc">
            <i class="el-icon-document"></i>
            <span slot="title">API文档</span>
          </el-menu-item>
          <el-menu-item index="/test">
            <i class="el-icon-view"></i>
            <span slot="title">在线测试</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Layout',
  computed: {
    activeMenu() {
      return this.$route.path
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.layout-header {
  background: linear-gradient(135deg, #1f2d3d 0%, #304156 100%);
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.12);
}
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}
.logo {
  display: flex;
  align-items: center;
  color: #fff;
  font-size: 18px;
  font-weight: 500;
}
.logo i {
  margin-right: 10px;
  font-size: 24px;
  color: #409EFF;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}
.layout-aside {
  background: #1f2d3d;
  overflow: hidden;
}
.el-menu {
  border-right: none;
}
.el-menu-item {
  display: flex;
  align-items: center;
}
.el-menu-item i {
  margin-right: 5px;
}
.layout-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
