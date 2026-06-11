import { createRouter, createWebHistory } from 'vue-router'

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

// 需要登录才能执行创建操作的路由
const authRequiredPaths = ['/dashboard', '/editor', '/ai-generate', '/templates', '/ai-settings', '/settings']

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  // MainLayout 子路由需要登录
  if (to.meta.requiresAuth && !token) {
    // 记住目标路径，登录后跳回
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
