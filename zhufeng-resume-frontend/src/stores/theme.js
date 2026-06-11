import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    darkMode: localStorage.getItem('darkMode') === 'true',
    locale: localStorage.getItem('locale') || 'zh-CN'
  }),

  actions: {
    toggleTheme() {
      this.darkMode = !this.darkMode
      localStorage.setItem('darkMode', this.darkMode)
      document.documentElement.classList.toggle('dark', this.darkMode)
    },

    setLocale(locale) {
      this.locale = locale
      localStorage.setItem('locale', locale)
    }
  }
})
