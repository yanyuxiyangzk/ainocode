import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

/**
 * 公共路由
 */
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/redirect',
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login.vue'),
    meta: { hidden: true }
  },
  {
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    meta: { hidden: true }
  },
  {
    path: '/401',
    component: () => import('@/views/error/401.vue'),
    meta: { hidden: true }
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index.vue'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  // 零代码配置平台路由
  {
    path: '/nocode',
    component: Layout,
    redirect: '/nocode/table',
    meta: { title: '零代码配置', icon: 'setting' },
    children: [
      {
        path: 'table',
        component: () => import('@/views/nocode/table/index.vue'),
        name: 'NocodeTable',
        meta: { title: '表管理', icon: 'table' }
      },
      {
        path: 'entity',
        component: () => import('@/views/nocode/entity/index.vue'),
        name: 'NocodeEntity',
        meta: { title: '实体配置', icon: 'document' }
      },
      {
        path: 'page',
        component: () => import('@/views/nocode/page/index.vue'),
        name: 'NocodePage',
        meta: { title: '界面配置', icon: 'monitor' }
      },
      {
        path: 'api',
        component: () => import('@/views/nocode/api/index.vue'),
        name: 'NocodeApi',
        meta: { title: 'API配置', icon: 'connection' }
      }
    ]
  },
  // 插件管理路由
  {
    path: '/plugin',
    component: Layout,
    redirect: '/plugin/list',
    meta: { title: '插件管理', icon: 'component' },
    children: [
      {
        path: 'list',
        component: () => import('@/views/plugin/list.vue'),
        name: 'PluginList',
        meta: { title: '插件列表', icon: 'list' }
      },
      {
        path: 'market',
        component: () => import('@/views/plugin/market.vue'),
        name: 'PluginMarket',
        meta: { title: '插件市场', icon: 'shopping' }
      }
    ]
  },
  // 系统管理路由
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'system' },
    children: [
      {
        path: 'user',
        component: () => import('@/views/system/user/index.vue'),
        name: 'User',
        meta: { title: '用户管理', icon: 'user' }
      },
      {
        path: 'role',
        component: () => import('@/views/system/role/index.vue'),
        name: 'Role',
        meta: { title: '角色管理', icon: 'peoples' }
      },
      {
        path: 'menu',
        component: () => import('@/views/system/menu/index.vue'),
        name: 'Menu',
        meta: { title: '菜单管理', icon: 'tree-table' }
      },
      {
        path: 'dept',
        component: () => import('@/views/system/dept/index.vue'),
        name: 'Dept',
        meta: { title: '部门管理', icon: 'tree' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_APP_ROUTER_BASE),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

export default router
