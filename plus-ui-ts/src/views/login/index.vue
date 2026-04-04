<template>
  <div class="login">
    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
      <div class="title-box">
        <h3 class="title">{{ title }}</h3>
      </div>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="请输入用户名"
        >
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="请输入密码"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button :loading="loading" size="large" type="primary" style="width: 100%" @click.prevent="handleLogin">
          <span v-if="!loading">登 录</span>
          <span v-else>登录中...</span>
        </el-button>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2018-2026 疯狂的狮子Li All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { login as loginApi } from '@/api/login';
import { useUserStore } from '@/store/modules/user';
import type { LoginData } from '@/api/types';
import { to } from 'await-to-js';

const userStore = useUserStore();
const router = useRouter();

const title = import.meta.env.VITE_APP_TITLE;

const loginForm = ref<LoginData>({
  username: 'admin',
  password: 'admin123',
  clientId: import.meta.env.VITE_APP_CLIENT_ID,
  grantType: 'password'
});

const loginRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入用户名' }],
  password: [{ required: true, trigger: 'blur', message: '请输入密码' }]
};

const loading = ref(false);
const loginRef = ref<ElFormInstance>();

const handleLogin = () => {
  loginRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      const [err] = await to(userStore.login(loginForm.value));
      if (!err) {
        await router.push('/');
      } else {
        ElMessage.error('登录失败，用户名或密码错误');
      }
      loading.value = false;
    }
  });
};
</script>

<style lang="scss" scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('../../assets/images/login-background.jpg');
  background-size: cover;
}

.title-box {
  text-align: center;
  margin-bottom: 30px;

  .title {
    margin: 0;
    color: #707070;
  }
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px;
  z-index: 1;

  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial, serif;
  font-size: 12px;
  letter-spacing: 1px;
}
</style>
