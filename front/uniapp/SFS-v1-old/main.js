import App from './App'
import * as mockAPI from './api/mock.js'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
Vue.prototype.$mockAPI = mockAPI
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  // 挂载mockAPI到全局属性
  app.config.globalProperties.$mockAPI = mockAPI
  return {
    app
  }
}
// #endif