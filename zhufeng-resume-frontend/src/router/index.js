import { createRouter, createWebHistory } from 'vue-router'
import { isTokenValid, clearAuth, getToken } from '../utils/auth'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../pages/Home.vue')
  },
  {
    path: '/templates-old',
    name: 'TemplatesOld',
    component: () => import('../pages/Templates.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../pages/Register.vue')
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../pages/Dashboard.vue')
      },
      {
        path: '/templates',
        name: 'Templates',
        component: () => import('../pages/Templates.vue')
      },
      {
        path: '/ai-settings',
        name: 'AiSettings',
        component: () => import('../pages/AiSettings.vue')
      },
      {
        path: '/settings',
        name: 'Settings',
        component: () => import('../pages/Settings.vue')
      },
      {
        path: '/ai-generate',
        name: 'AiGenerate',
        component: () => import('../pages/AiGenerate.vue')
      },
      {
        path: '/editor/:id',
        name: 'Editor',
        component: () => import('../pages/Editor.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 严格校验 Token 有效性（不仅是存在性），并强制从后端校验
router.beforeEach((to, from, next) => {
  // 需要认证的页面
  if (to.meta.requiresAuth) {
    // 检查 token 是否存在
    if (!getToken()) {
      clearAuth()
      next({ path: '/login', query: { redirect: to.fullPath, reason: 'unauthorized' } })
      return
    }
    // 检查 token 是否过期（本地时间戳粗判）
    if (!isTokenValid()) {
      clearAuth()
      next({ path: '/login', query: { redirect: to.fullPath, reason: 'expired' } })
      return
    }
    // 注意：真正的 Token 有效性校验由后端拦截器完成
    // 401 响应时 request.js 拦截器会统一处理：清空 + 跳转 login
  }

  // 已登录用户访问 login/register 时重定向到工作台
  if ((to.path === '/login' || to.path === '/register') && isTokenValid()) {
    next({ path: '/dashboard' })
    return
  }

  next()
})

// 全局后置钩子：进入受保护页面时，强制刷新用户信息
router.afterEach((to, from) => {
  // 仅在切换到需要鉴权的页面时执行，且不是从登录态校验跳转
  if (to.meta.requiresAuth && isTokenValid()) {
    // 用 nextTick 延迟执行，避免阻塞路由
    setTimeout(() => {
      import('../stores/user').then(({ useUserStore }) => {
        const userStore = useUserStore()
        // 静默刷新用户信息（forceRefresh=true 强制从后端拉取，不信任本地缓存）
        // 多账号安全：切换账号后必须重新校验
        if (from.path !== to.path) {
          userStore.fetchCurrentUser(true).catch(() => {
            // 401 已由 request.js 拦截器处理
          })
        }
      }).catch(() => {})
    }, 0)
  }
})

export default router
