import { defineStore } from 'pinia'
import { getToken, setToken, clearAuth, isTokenValid } from '../utils/auth'
import { login, register, logout as logoutApi, logoutAll as logoutAllApi, validateToken } from '../api/auth'
import { getUserProfile } from '../api/user'
import { useAiConfigStore } from './aiConfig'
import { useResumeStore } from './resume'

/**
 * 用户 Store
 *
 * 多账号安全策略：
 * - 初始化时不从 localStorage 读 userInfo（避免显示上一账号的姓名/头像）
 * - 登录后只缓存 userInfo 用于"立即展示"，刷新页面必须从后端重新拉
 * - 登出 / 401 / Token 失效时强制重置本 store + aiConfig store + resume store
 *   并清空所有本地存储
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    // 不从缓存读 userInfo：刷新页面后由 fetchCurrentUser() 强制从后端拉
    userInfo: null
  }),

  actions: {
    /**
     * 登录
     * @param {Object} loginForm - { username, password }
     * @param {number} expireDays - token有效天数，支持小数（0.125=3小时）
     */
    async login(loginForm, expireDays = 7) {
      // 登录前先清空一切残留（防止 A 账号登录后还残留 B 账号的数据）
      this.resetAll()
      clearAuth()

      const res = await login(loginForm, expireDays)
      this.token = res.data.token
      this.userInfo = res.data.user
      // 存 token（同时写 sessionStorage + localStorage 过期时间）
      setToken(this.token, expireDays)
      // userInfo 仅作为"立即展示"缓存，刷新页面会被 fetchCurrentUser 覆盖
      setUserInfoCacheLocal(this.userInfo)
      // 加载当前用户的 AI 配置
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
     * 强制从后端拉取当前登录用户（不信任任何前端缓存）
     * 应用启动 / 路由切换 / 刷新页面时调用
     */
    async fetchCurrentUser(forceRefresh = false) {
      if (!isTokenValid()) {
        this.userInfo = null
        return null
      }
      // 有缓存且不强制刷新：直接返回（仅用于"刚登录完立即展示"，刷新页面会强制刷新）
      if (this.userInfo && this.userInfo.id && !forceRefresh) {
        return this.userInfo
      }
      try {
        // 优先用 /auth/me 校验 Token 有效性并获取最新用户
        const res = await validateToken()
        this.userInfo = res.data
        setUserInfoCacheLocal(this.userInfo)
        return this.userInfo
      } catch (e) {
        // 401 → 由 request.js 统一处理清除 + 跳转
        this.userInfo = null
        throw e
      }
    },

    /**
     * 兼容旧代码：通过 /user/profile 拉取
     */
    async getUserInfo(forceRefresh = false) {
      if (this.userInfo && this.userInfo.id && !forceRefresh) return this.userInfo
      try {
        const res = await getUserProfile()
        this.userInfo = res.data
        setUserInfoCacheLocal(this.userInfo)
        return this.userInfo
      } catch (e) {
        console.error('获取用户信息失败:', e)
        throw e
      }
    },

    /**
     * 登出（标准版）
     * 1. 通知后端让 Token 立即失效
     * 2. 重置本 store + aiConfig + resume store
     * 3. 清空 localStorage / sessionStorage / Cookie
     * 4. 跳转登录页
     */
    async logout(redirect = true) {
      try {
        await logoutApi()
      } catch (e) {
        console.warn('后端登出请求失败，仍清理本地状态:', e)
      }
      this.afterLogout(redirect)
    },

    /**
     * 强制下线（多账号切换场景）：销毁该用户所有 Token + Session
     */
    async logoutAll(redirect = true) {
      try {
        await logoutAllApi()
      } catch (e) {
        console.warn('后端强制下线请求失败，仍清理本地状态:', e)
      }
      this.afterLogout(redirect)
    },

    /**
     * 登出后清理动作
     */
    afterLogout(redirect = true) {
      this.resetAll()
      clearAuth()
      if (redirect) {
        // 用 import 避免循环依赖
        import('../router').then(({ default: router }) => {
          router.push('/login?reason=logout')
        }).catch(() => {
          window.location.href = '/login?reason=logout'
        })
      }
    },

    /**
     * 重置本 store + 所有相关 store（不清本地存储，由 clearAuth 负责）
     */
    resetAll() {
      this.token = ''
      this.userInfo = null
      // 重置 AI 配置 store（依赖 userInfo）
      try { useAiConfigStore().$reset && useAiConfigStore().$reset() } catch (_) {}
      // 重置简历 store（防止下一个用户看到上一用户的简历数据）
      try { useResumeStore().$reset && useResumeStore().$reset() } catch (_) {}
    },

    /**
     * 检查当前登录状态是否有效
     */
    isLoggedIn() {
      return isTokenValid()
    }
  }
})

/**
 * 写 userInfo 到 localStorage（仅用于"刚登录完立即展示"）
 * 内部函数：避免循环依赖
 */
function setUserInfoCacheLocal(info) {
  try {
    if (info) localStorage.setItem('user_info', JSON.stringify(info))
  } catch (_) {}
}
