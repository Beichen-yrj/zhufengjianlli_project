<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getToken, isTokenValid, clearAuth } from './utils/auth'

/**
 * 应用启动时校验登录态
 *
 * 多账号安全：
 * - 如果当前是受保护页面（有 token），但本地已过期 → 强制清理
 * - 真正的用户信息校验由 router.afterEach 触发 fetchCurrentUser
 *   （强制从后端拉取，不信任本地缓存）
 */
const route = useRoute()

onMounted(() => {
  // 检查 token 完整性
  const token = getToken()
  if (token && !isTokenValid()) {
    // token 存在但已过期 → 强制清空
    clearAuth()
    // 不强制跳转：路由守卫会处理
  }
})
</script>
