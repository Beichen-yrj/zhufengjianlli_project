import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '../utils/auth'
import { login, register } from '../api/auth'
import { getUserProfile } from '../api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: null
  }),

  actions: {
    async login(loginForm) {
      const res = await login(loginForm)
      this.token = res.data.token
      this.userInfo = res.data.user
      setToken(this.token)
      return res
    },

    async register(registerForm) {
      return await register(registerForm)
    },

    async getUserInfo() {
      const res = await getUserProfile()
      this.userInfo = res.data
      return res
    },

    logout() {
      this.token = ''
      this.userInfo = null
      removeToken()
    }
  }
})