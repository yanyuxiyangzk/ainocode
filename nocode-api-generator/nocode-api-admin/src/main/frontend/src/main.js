import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import App from './App.vue'
import router from './router'
import './assets/styles.css'

Vue.use(ElementUI, { size: 'small' })

Vue.config.productionTip = false

// 全局错误处理
Vue.config.errorHandler = function (err, vm, info) {
  console.error('全局错误:', err)
  console.error('组件:', vm)
  console.error('信息:', info)
}

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
