import { createRouter, createWebHistory } from 'vue-router'
import { isTokenValid, clearAuth } from '../utils/auth'

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

// 路由守卫 - 检查 token 有效性（不仅是存在性）
router.beforeEach((to, from, next) => {
  // 需要认证的页面
  if (to.meta.requiresAuth) {
    // 检查 token 是否存在且未过期
    if (!isTokenValid()) {
      // token 不存在或已过期，清除残留数据并跳转登录
      clearAuth()
      next({ path: '/login', query: { redirect: to.fullPath, reason: 'expired' } })
      return
    }
  }

  // 已登录用户访问 login/register 时重定向到工作台
  if ((to.path === '/login' || to.path === '/register') && isTokenValid()) {
    next({ path: '/dashboard' })
    return
  }

  next()
})

export default router
