import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import router from '@/router'
import { useUserStore } from '@/store/modules/user'
import { getToken } from '@/utils/auth'
import { isHttp } from '@/utils/validate'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/404', '/401']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  const userStore = useUserStore()
  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      if (userStore.roles.length === 0) {
        try {
          await userStore.GetInfo()
          next({ ...to, replace: true })
        } catch (error) {
          await userStore.LogOut()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
