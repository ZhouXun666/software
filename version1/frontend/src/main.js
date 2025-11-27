import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// 创建Vue应用实例
const app = createApp(App)

// 配置Element Plus
app.use(ElementPlus)

// 配置路由
app.use(router)

// 配置Axios
axios.defaults.baseURL = '/api'
axios.defaults.timeout = 10000

// 请求拦截器 - 添加token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器 - 处理错误
axios.interceptors.response.use(response => {
  return response.data
}, error => {
  if (error.response) {
    // 处理401未授权
    if (error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
    // 显示错误消息
    if (error.response.data && error.response.data.message) {
      console.error(error.response.data.message)
    } else {
      console.error('请求失败')
    }
  }
  return Promise.reject(error)
})

// 将axios挂载到全局属性
app.config.globalProperties.$axios = axios

// 挂载应用
app.mount('#app')