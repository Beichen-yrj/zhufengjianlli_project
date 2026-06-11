import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, isTokenValid, clearAuth } from '../utils/auth'
import { useRouter } from 'vue-router'

// 创建 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000
})

// 防止多个 401 同时触发多次跳转
let isRedirectingToLogin = false

// 请求拦截器 - Token 过期检查 + 自动附加
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      // 检查 token 是否已过期
      if (!isTokenValid()) {
        // token 过期，清除认证数据并中断请求
        clearAuth()
        // 延迟跳转，避免在拦截器中直接操作路由（非组件上下文）
        if (!isRedirectingToLogin) {
          isRedirectingToLogin = true
          setTimeout(() => {
            window.location.href = '/login?reason=expired'
            isRedirectingToLogin = false
          }, 100)
        }
        return Promise.reject(new Error('登录已过期，请重新登录'))
      }
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 401 统一处理 + 业务错误提示
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  (error) => {
    const status = error.response?.status

    // 401 未授权：Token 无效/过期/被吊销 → 全量清除认证数据
    if (status === 401) {
      clearAuth()
      if (!isRedirectingToLogin) {
        isRedirectingToLogin = true
        ElMessage.error('登录已失效，请重新登录')
        setTimeout(() => {
          window.location.href = '/login?reason=unauthorized'
          isRedirectingToLogin = false
        }, 800)
      }
    } else if (status === 403) {
      ElMessage.error('没有权限执行此操作')
    } else if (status === 429) {
      ElMessage.error('操作过于频繁，请稍后再试')
    } else if (status >= 500) {
      ElMessage.error(error.response?.data?.message || '服务器内部错误')
    } else if (!error.response) {
      // 网络错误 / 超时
      ElMessage.error('网络连接失败，请检查网络后重试')
    } else {
      ElMessage.error(error.response?.data?.message || '请求失败')
    }

    return Promise.reject(error)
  }
)

export default request
