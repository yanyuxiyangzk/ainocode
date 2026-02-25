<template>
  <div class="login-container">
    <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">RuoYi-Cloud-Nocode 零代码平台</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
          prefix-icon="User"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          prefix-icon="Lock"
          show-password
          @keyup.enter="handleLogin"
        />
      </el-form-item>
      <el-form-item v-if="captchaEnabled" prop="code">
        <el-row :gutter="10">
          <el-col :span="16">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="验证码"
              prefix-icon="Key"
              style="width: 100%"
              @keyup.enter="handleLogin"
            />
          </el-col>
          <el-col :span="8">
            <div class="login-code">
              <img :src="codeUrl" class="login-code-img" @click="getCode" />
            </div>
          </el-col>
        </el-row>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" class="login-remember">记住密码</el-checkbox>
      <el-form-item>
        <el-button
          :loading="loading"
          type="primary"
          class="login-btn"
          @click.prevent="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
      </el-form-item>
    </el-form>
    <div class="login-footer">
      <span>Copyright © 2024 RuoYi-Cloud-Nocode All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getCodeImg } from '@/api/login'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

defineOptions({ name: 'Login' })

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const codeUrl = ref('')
const captchaEnabled = ref(true)

const loginForm = reactive({
  username: 'admin',
  password: 'admin123',
  rememberMe: false,
  code: '',
  uuid: ''
})

const loginRules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const getCode = async () => {
  try {
    const res = await getCodeImg()
    captchaEnabled.value = res.captchaEnabled !== false
    if (captchaEnabled.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img
      loginForm.uuid = res.uuid
    }
  } catch (error) {
    console.error('获取验证码失败', error)
  }
}

const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate()
  if (!valid) return

  loading.value = true
  try {
    await userStore.Login(loginForm)
    router.push({ path: '/' })
    ElMessage.success('登录成功')
  } catch (error) {
    loading.value = false
    if (captchaEnabled.value) {
      getCode()
    }
  }
}

onMounted(() => {
  getCode()
})
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;

  .title {
    margin: 0 auto 30px auto;
    text-align: center;
    color: #707070;
    font-size: 24px;
  }

  .login-form {
    border-radius: 6px;
    background: #fff;
    width: 400px;
    padding: 25px 25px 5px 25px;

    .login-remember {
      margin-bottom: 15px;
    }

    .login-btn {
      width: 100%;
    }
  }

  .login-code {
    width: 100%;
    height: 32px;

    .login-code-img {
      cursor: pointer;
      width: 100%;
      height: 32px;
      border-radius: 4px;
    }
  }

  .login-footer {
    position: fixed;
    bottom: 20px;
    color: #fff;
    font-size: 12px;
  }
}
</style>
