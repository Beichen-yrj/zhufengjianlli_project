import { defineStore } from 'pinia'
import { getToken, setToken, clearAuth, isTokenValid, getUserInfoCache, setUserInfoCache } from '../utils/auth'
import { login, register, logout as logoutApi } from '../api/auth'
import { getUserProfile } from '../api/user'
import { useAiConfigStore } from './aiConfig'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUserInfoCache() // 优先从缓存读取，避免每次刷新都请求后端
  }),

  actions: {
    /**
     * 登录
     * @param {Object} loginForm - { username, password }
     * @param {number} expireDays - token有效天数，默认7天
     */
    async login(loginForm, expireDays = 7) {
      const res = await login(loginForm, expireDays)
      this.token = res.data.token
      this.userInfo = res.data.user
      // 存储token + 过期时间 + 用户信息到localStorage
      setToken(this.token, expireDays)
      setUserInfoCache(this.userInfo)
      // 加载当前用户的 AI 配置（按 userId 隔离的 apiKey/model/endpoint）
      useAiConfigStore().reloadConfig()
      return res
    },

    /**
     * 注册（注册成功后自动登录）
     */
    async register(registerForm) {
      const res = await register(registerForm)
      return res
    },

    /**
     * 获取用户信息（优先使用缓存，过期或不存在时请求后端）
     */
    async getUserInfo(forceRefresh = false) {
      if (this.userInfo && !forceRefresh) return this.userInfo

      try {
        const res = await getUserProfile()
        this.userInfo = res.data
        setUserInfoCache(this.userInfo)
        return this.userInfo
      } catch (e) {
        console.error('获取用户信息失败:', e)
        throw e
      }
    },

    /**
     * 登出 - 先调后端销毁 Token，再清本地缓存，最后跳转登录页
     */
    async logout(redirect = true) {
      try {
        // 通知后端让 Token 立即失效
        await logoutApi()
      } catch (e) {
        console.warn('后端登出请求失败，仍清理本地状态:', e)
      }
      // 先清 AI 配置（依赖 userInfo，必须在 clearAuth 之前调）
      useAiConfigStore().clearConfig()
      this.token = ''
      this.userInfo = null
      clearAuth()
      if (redirect) {
        const { default: router } = await import('../router')
        router.push('/login')
      }
    },

    /**
     * 检查当前登录状态是否有效
     */
    isLoggedIn() {
      return isTokenValid()
    }
  }
})
