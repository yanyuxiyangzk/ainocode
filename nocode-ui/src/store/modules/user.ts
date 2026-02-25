import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const name = ref('')
  const avatar = ref('')
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])

  // 登录
  const Login = async (userInfo: { username: string; password: string; code: string; uuid: string }) => {
    const res = await login(userInfo.username, userInfo.password, userInfo.code, userInfo.uuid)
    setToken(res.token)
    token.value = res.token
    return res
  }

  // 获取用户信息
  const GetInfo = async () => {
    const res = await getInfo()
    const user = res.user
    avatar.value = user.avatar || ''
    name.value = user.nickName || user.userName || ''
    
    if (res.roles && res.roles.length > 0) {
      roles.value = res.roles
      permissions.value = res.permissions
    } else {
      roles.value = ['ROLE_DEFAULT']
    }
    return res
  }

  // 退出登录
  const LogOut = async () => {
    try {
      await logout()
    } finally {
      token.value = ''
      roles.value = []
      permissions.value = []
      removeToken()
    }
  }

  return {
    token,
    name,
    avatar,
    roles,
    permissions,
    Login,
    GetInfo,
    LogOut
  }
})
