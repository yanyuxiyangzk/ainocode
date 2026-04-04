import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

/**
 * 路由配置
 */
const routes = [
  {
    path: '/',
    redirect: '/nocode'
  },
  {
    path: '/nocode',
    component: () => import('@/views/nocode/dashboard'),
    meta: { title: '首页' }
  },
  {
    path: '/nocode/form',
    component: () => import('@/views/nocode/form'),
    meta: { title: '表单设计器' }
  },
  {
    path: '/nocode/form/view/:formId',
    component: () => import('@/views/nocode/form/view'),
    meta: { title: '表单视图' }
  },
  {
    path: '/nocode/workflow',
    component: () => import('@/views/nocode/workflow'),
    meta: { title: '工作流设计器' }
  },
  {
    path: '/nocode/code-generator',
    component: () => import('@/views/nocode/codeGenerator'),
    meta: { title: '代码生成器' }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router