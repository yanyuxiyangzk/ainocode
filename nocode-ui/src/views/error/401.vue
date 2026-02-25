<template>
  <div class="error-page">
    <div class="error-content">
      <h1>401</h1>
      <h2>没有访问权限</h2>
      <p>抱歉，您没有权限访问此页面</p>
      <el-button type="primary" @click="router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

defineOptions({ name: 'Error401' })

const router = useRouter()
</script>

<style lang="scss" scoped>
.error-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);

  .error-content {
    text-align: center;
    color: #fff;

    h1 {
      font-size: 120px;
      margin: 0;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
    }

    h2 {
      font-size: 32px;
      margin: 20px 0;
    }

    p {
      font-size: 16px;
      margin-bottom: 30px;
      opacity: 0.8;
    }
  }
}
</style>
