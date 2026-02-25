import request from '@/utils/request'
import { AxiosPromise } from 'axios'

interface LoginResult {
  token: string
}

interface UserInfo {
  userId: number
  userName: string
  nickName: string
  avatar: string
}

interface GetInfoResult {
  user: UserInfo
  roles: string[]
  permissions: string[]
}

// 登录
export const login = (username: string, password: string, code: string, uuid: string): AxiosPromise<LoginResult> => {
  const data = {
    username,
    password,
    code,
    uuid
  }
  return request({
    url: '/auth/login',
    method: 'post',
    headers: {
      isToken: false
    },
    data
  })
}

// 退出登录
export const logout = (): AxiosPromise<void> => {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取用户信息
export const getInfo = (): AxiosPromise<GetInfoResult> => {
  return request({
    url: '/system/user/getInfo',
    method: 'get'
  })
}

// 获取验证码
export const getCodeImg = (): AxiosPromise<{ img: string; uuid: string; captchaEnabled: boolean }> => {
  return request({
    url: '/auth/code',
    method: 'get',
    timeout: 20000
  })
}
