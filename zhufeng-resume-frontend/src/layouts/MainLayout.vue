<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <!-- Logo -->
      <div class="sidebar-logo">
        <div class="logo-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="2" width="16" height="20" rx="2" stroke="#5B8DEF" stroke-width="1.5" fill="none"/>
            <line x1="7" y1="6" x2="17" y2="6" stroke="#5B8DEF" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="7" y1="10" x2="17" y2="10" stroke="#E0E0E0" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="7" y1="14" x2="13" y2="14" stroke="#E0E0E0" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <span class="logo-text">逐风<span class="logo-highlight">resume</span></span>
      </div>

      <!-- 导航菜单 -->
      <nav class="sidebar-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <component :is="item.icon" class="nav-icon" />
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- 底部用户信息 -->
      <div class="sidebar-footer" @click="toggleUserMenu" v-click-outside="closeUserMenu">
        <div class="user-avatar">
          {{ userInitial }}
        </div>
        <div class="user-info" v-if="userName">
          <span class="user-name">{{ userName }}</span>
          <span class="user-role">个人版</span>
        </div>
        <svg class="footer-arrow" width="16" height="16" viewBox="0 0 16 16" fill="none">
          <path d="M6 4L10 8L6 12" stroke="#999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>

        <!-- 退出登录 弹层菜单 -->
        <transition name="fade">
          <div v-if="userMenuOpen" class="user-menu" @click.stop>
            <div class="user-menu-header" v-if="userStore.userInfo">
              <div class="user-menu-name">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</div>
              <div class="user-menu-email" v-if="userStore.userInfo.email">{{ userStore.userInfo.email }}</div>
            </div>
            <div class="user-menu-divider"></div>
            <div class="user-menu-item" @click="goSettings">
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                <circle cx="8" cy="8" r="2.5" stroke="currentColor" stroke-width="1.2" fill="none"/>
                <path d="M8 1v2M8 13v2M15 8h-2M3 8H1M13 3l-1.4 1.4M4.4 11.6L3 13M13 13l-1.4-1.4M4.4 4.4L3 3" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
              </svg>
              <span>通用设置</span>
            </div>
            <div class="user-menu-item danger" @click="handleLogout">
              <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                <path d="M10 12V13C10 13.5 9.6 14 9 14H4C3.4 14 3 13.5 3 13V3C3 2.5 3.4 2 4 2H9C9.6 2 10 2.5 10 3V4" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
                <path d="M14 8H6M6 8L8 6M6 8L8 10" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>退出登录</span>
            </div>
          </div>
        </transition>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed, h, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 应用启动 / 路由进入时强制从后端拉取最新用户信息
// 多账号安全：切换账号后必须重新校验，绝不信任本地缓存
onMounted(async () => {
  try {
    await userStore.fetchCurrentUser(true)
  } catch (_) {
    // 401 已由 request.js 拦截器统一处理：清空 + 跳转 login
  }
})

// 用户菜单展开状态
const userMenuOpen = ref(false)
const toggleUserMenu = () => { userMenuOpen.value = !userMenuOpen.value }
const closeUserMenu = () => { userMenuOpen.value = false }

const goSettings = () => {
  userMenuOpen.value = false
  router.push('/settings')
}

const handleLogout = async () => {
  userMenuOpen.value = false
  await userStore.logout()
  // store 内部已跳转 /login，这里兜底再跳一次
  if (route.path !== '/login') router.push('/login?reason=logout')
}

// 强制下线（多账号切换场景）：销毁该用户所有 Token + Session
const handleForceLogout = async () => {
  userMenuOpen.value = false
  await userStore.logoutAll()
}

// v-click-outside 自定义指令：点击元素外部时触发回调
const vClickOutside = {
  mounted(el, binding) {
    el._clickOutsideHandler = (event) => {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value(event)
      }
    }
    document.addEventListener('click', el._clickOutsideHandler)
  },
  unmounted(el) {
    document.removeEventListener('click', el._clickOutsideHandler)
  }
}

// 图标组件
const IconResume = {
  render() {
    return h('svg', { width: 18, height: 18, viewBox: '0 0 18 18', fill: 'none', xmlns: 'http://www.w3.org/2000/svg' }, [
      h('rect', { x: 2, y: 2, width: 14, height: 15, rx: 2, stroke: 'currentColor', 'stroke-width': '1.4', fill: 'none' }),
      h('path', { d: 'M5 6h8M5 9h8M5 12h5', stroke: 'currentColor', 'stroke-width': '1.4', 'stroke-linecap': 'round' })
    ])
  }
}

const IconTemplate = {
  render() {
    return h('svg', { width: 18, height: 18, viewBox: '0 0 18 18', fill: 'none', xmlns: 'http://www.w3.org/2000/svg' }, [
      h('rect', { x: 2, y: 2, width: 6, height: 6, rx: 1.5, fill: '#FF6B35' }),
      h('rect', { x: 10, y: 2, width: 6, height: 6, rx: 1.5, fill: '#22C55E' }),
      h('rect', { x: 2, y: 10, width: 6, height: 6, rx: 1.5, fill: '#3B82F6' }),
      h('rect', { x: 10, y: 10, width: 6, height: 6, rx: 1.5, fill: '#A855F7' })
    ])
  }
}

const IconAI = {
  render() {
    return h('svg', { width: 18, height: 18, viewBox: '0 0 18 18', fill: 'none', xmlns: 'http://www.w3.org/2000/svg' }, [
      h('path', { d: 'M9 2L11.5 6.5L16 7L13 10.5L13.5 15L9 12.5L4.5 15L5 10.5L2 7L6.5 6.5L9 2Z', fill: 'currentColor' })
    ])
  }
}

const IconSettings = {
  render() {
    return h('svg', { width: 18, height: 18, viewBox: '0 0 18 18', fill: 'none', xmlns: 'http://www.w3.org/2000/svg' }, [
      h('circle', { cx: 9, cy: 9, r: 3, stroke: 'currentColor', 'stroke-width': '1.4', fill: 'none' }),
      h('path', { d: 'M9 1v2M9 15v2M17 9h-2M3 9H1M14.5 3.5l-1.4 1.4M4.9 13.1l-1.4 1.4M14.5 14.5l-1.4-1.4M4.9 4.9L3.5 3.5', stroke: 'currentColor', 'stroke-width': '1.4', 'stroke-linecap': 'round' })
    ])
  }
}

const navItems = [
  { path: '/dashboard', label: '我的简历', icon: IconResume },
  { path: '/templates', label: '简历模板', icon: IconTemplate },
  { path: '/ai-settings', label: 'AI服务商', icon: IconAI },
  { path: '/settings', label: '通用设置', icon: IconSettings },
]

const isActive = (path) => route.path === path

const userName = computed(() => userStore.userInfo?.username || userStore.userInfo?.nickname || '')
const userInitial = computed(() => {
  const name = userName.value
  return name ? name.charAt(0).toUpperCase() : 'U'
})
</script>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  background: #F7F8FA;
}

/* ===== 侧边栏 ===== */
.sidebar {
  width: 16%;
  min-width: 200px;
  max-width: 260px;
  background: #fff;
  border-right: 1px solid #EEEFF2;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

/* Logo */
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 18px 20px;
  border-bottom: 1px solid #F0F1F3;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #EEF3FE;
  border-radius: 8px;
}

.logo-text {
  font-size: 15px;
  font-weight: 700;
  color: #1a1a2e;
  letter-spacing: -0.3px;
}

.logo-highlight {
  color: #5B8DEF;
  font-weight: 600;
}

/* 导航 */
.sidebar-nav {
  display: flex;
  flex-direction: column;
  padding: 8px 10px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 14px;
  border-radius: 10px;
  text-decoration: none;
  color: #555C6B;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: pointer;
  margin-bottom: 2px;
}

.nav-item:hover {
  background: #F4F5F7;
  color: #333;
}

.nav-item.active {
  background: #EBF1FE;
  color: #3B6EF5;
}

.nav-item.active .nav-icon {
  color: #3B6EF5;
}

.nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  color: #888;
  transition: color 0.2s;
}

.nav-item:nth-child(1) .nav-icon { color: #3B82F6; }
.nav-item:nth-child(1).active .nav-icon { color: #3B6EF5; }

.nav-item:nth-child(2) .nav-icon { color: #22C55E; }
.nav-item:nth-child(2).active .nav-icon { color: #16A34A; }

.nav-item:nth-child(3) .nav-icon { color: #A855F7; }
.nav-item:nth-child(3).active .nav-icon { color: #9333EA; }

.nav-item:nth-child(4) .nav-icon { color: #666; }
.nav-item:nth-child(4).active .nav-icon { color: #444; }

.nav-label {
  line-height: 1;
}

/* 底部用户区 */
.sidebar-footer {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-top: 1px solid #F0F1F3;
  cursor: pointer;
  transition: background 0.2s;
}

.sidebar-footer:hover {
  background: #FAFBFC;
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-role {
  font-size: 11px;
  color: #aaa;
}

.footer-arrow {
  margin-left: auto;
  flex-shrink: 0;
  opacity: 0.5;
}

/* 用户弹层菜单 */
.user-menu {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 10px;
  right: 10px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  border: 1px solid #EEEFF2;
  padding: 6px;
  z-index: 100;
}

.user-menu-header {
  padding: 10px 10px 8px;
}

.user-menu-name {
  font-size: 13px;
  font-weight: 600;
  color: #1a1a2e;
}

.user-menu-email {
  font-size: 11px;
  color: #888;
  margin-top: 2px;
}

.user-menu-divider {
  height: 1px;
  background: #F0F1F3;
  margin: 4px 0;
}

.user-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 6px;
  font-size: 13px;
  color: #555C6B;
  cursor: pointer;
  transition: all 0.15s;
}

.user-menu-item:hover {
  background: #F4F5F7;
  color: #1a1a2e;
}

.user-menu-item.danger {
  color: #ef4444;
}

.user-menu-item.danger:hover {
  background: #FEF2F2;
  color: #dc2626;
}

.user-menu-item svg {
  flex-shrink: 0;
}

/* sidebar-footer 需要 relative 以便 user-menu 绝对定位 */
.sidebar-footer {
  position: relative;
}

/* 菜单动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s, transform 0.15s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(4px);
}
/* 主内容区 */
.main-content {
  flex: 1;
  overflow-y: auto;
}
</style>
