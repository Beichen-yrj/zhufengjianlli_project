import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, isTokenValid, clearAuth } from '../utils/auth'
import { useUserStore } from '../stores/user'

// 创建 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000
})

// 防止多个 401 同时触发多次跳转
let isRedirectingToLogin = false

// 标记是否需要"完整清理"（401 时使用）
function fullClearAndRedirect(reason) {
  if (isRedirectingToLogin) return
  isRedirectingToLogin = true

  // 1. 清空所有本地存储 + Cookie
  clearAuth()

  // 2. 重置所有 Pinia store
  try {
    const userStore = useUserStore()
    userStore.resetAll()
  } catch (_) {}

  // 3. 跳转登录页
  setTimeout(() => {
    window.location.href = `/login?reason=${reason}`
    isRedirectingToLogin = false
  }, 800)
}

// 请求拦截器 - Token 过期检查 + 自动附加
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      // 检查 token 是否已过期
      if (!isTokenValid()) {
        fullClearAndRedirect('expired')
        return Promise.reject(new Error('登录已过期，请重新登录'))
      }
      config.headers.Authorization = `Bearer ${token}`
      // 防止 CSRF：标识这是 AJAX 请求
      config.headers['X-Requested-With'] = 'XMLHttpRequest'
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

    // 401 未授权：Token 无效/过期/被吊销 → 全量清除认证数据 + 重置所有 store
    if (status === 401) {
      ElMessage.error('登录已失效，请重新登录')
      fullClearAndRedirect('unauthorized')
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
