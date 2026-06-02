# 逐风 AI 简历平台 — 开发笔记（第三部分）

> 更新日期：2026-05-12
> 内容：前端 Vue3 项目骨架

---

## 一、项目概述

### 1.1 技术栈

| 类别 | 技术选型 | 说明 |
|------|----------|------|
| 框架 | Vue3 | 组合式 API (Composition API) |
| 构建工具 | Vite | 快速的开发服务器和构建工具 |
| 路由 | Vue Router 4 | Vue3 官方路由 |
| 状态管理 | Pinia | Vue3 官方推荐的状态管理 |
| UI 组件库 | Element Plus | Vue3 最成熟的 UI 库 |
| HTTP 客户端 | Axios | 异步请求 |
| CSS 预处理器 | SCSS | 强大的 CSS 扩展 |

### 1.2 项目地址

```
zhufeng-resume-frontend/
```

### 1.3 启动命令

```bash
cd zhufeng-resume-frontend
npm install        # 首次安装依赖
npm run dev        # 开发模式启动
```

启动后访问：http://localhost:5173

---

## 二、项目目录结构

```
zhufeng-resume-frontend/
├── public/                     # 静态资源（不经过构建）
├── src/
│   ├── api/                    # 接口请求
│   │   ├── request.js         # Axios 封装（核心）
│   │   ├── auth.js            # 认证接口
│   │   └── user.js            # 用户接口
│   ├── assets/                 # 静态资源（经过构建）
│   │   └── styles/
│   │       └── global.scss     # 全局样式
│   ├── components/             # 公共组件
│   ├── pages/                  # 页面组件
│   │   ├── Home.vue           # 首页
│   │   ├── Login.vue          # 登录页
│   │   ├── Register.vue        # 注册页
│   │   └── Dashboard.vue      # 控制台
│   ├── router/                 # 路由配置
│   │   └── index.js
│   ├── stores/                 # Pinia 状态管理
│   │   └── user.js
│   ├── utils/                  # 工具函数
│   │   └── auth.js            # Token 管理
│   ├── App.vue                # 根组件
│   └── main.js                # 入口文件
├── index.html                 # HTML 模板
├── package.json               # 项目配置
├── vite.config.js             # Vite 配置
└── README.md
```

---

## 三、核心文件说明

### 3.1 api/request.js（Axios 封装）

**作用**：封装 Axios，统一处理请求和响应

**核心功能**：
| 功能 | 说明 |
|------|------|
| 基础配置 | 设置 baseURL 和 timeout |
| 请求拦截器 | 自动在请求头添加 Token |
| 响应拦截器 | 统一处理错误（401跳转登录等）|

**代码结构**：
```javascript
// 1. 创建 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000
})

// 2. 请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 3. 响应拦截器
request.interceptors.response.use(
  response => res.data,           // 成功：返回 data
  error => {                     // 失败：统一处理
    if (error.response?.status === 401) {
      // 跳转登录
    }
  }
)
```

---

### 3.2 router/index.js（路由配置）

**作用**：配置页面路由和导航守卫

**路由表**：
| 路径 | 组件 | 需要登录 |
|------|------|----------|
| /home | Home.vue | ❌ |
| /login | Login.vue | ❌ |
| /register | Register.vue | ❌ |
| /dashboard | Dashboard.vue | ✅ |

**路由守卫**：
```javascript
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')  // 未登录，跳转登录页
  } else {
    next()
  }
})
```

---

### 3.3 stores/user.js（用户状态管理）

**作用**：管理用户登录状态和数据

**状态（state）**：
```javascript
state: () => ({
  token: localStorage.getItem('token') || '',  // 登录令牌
  userInfo: null                               // 用户信息
})
```

**操作（actions）**：
| 方法 | 功能 |
|------|------|
| login() | 登录，保存 token 和用户信息 |
| register() | 注册 |
| getUserInfo() | 获取用户信息 |
| logout() | 退出登录，清除状态和 token |

---

### 3.4 utils/auth.js（Token 工具）

**作用**：封装 localStorage 操作

```javascript
export function getToken() { return localStorage.getItem(TOKEN_KEY) }
export function setToken(token) { localStorage.setItem(TOKEN_KEY, token) }
export function removeToken() { localStorage.removeItem(TOKEN_KEY) }
```

---

### 3.5 main.js（入口文件）

**作用**：初始化 Vue 应用，配置插件

```javascript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'           // UI 组件库
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'                     // 路由
import { createPinia } from 'pinia'             // 状态管理

import './assets/styles/global.scss'             // 全局样式

const app = createApp(App)

app.use(ElementPlus)                            // 注册 Element Plus
app.use(router)                                 // 注册路由
app.use(createPinia())                          // 注册 Pinia

app.mount('#app')
```

---

## 四、页面说明

### 4.1 Home.vue（首页）

**路径**：/home

**功能**：展示产品，引导用户注册/登录

**组件结构**：
```vue
<template>
  <div class="home">
    <div class="hero">
      <h1>逐风 AI 简历平台</h1>
      <p>智能 AI 驱动的在线简历制作平台</p>
      <div class="buttons">
        <el-button type="primary">立即开始</el-button>
        <el-button>已有账号登录</el-button>
      </div>
    </div>
  </div>
</template>
```

---

### 4.2 Login.vue（登录页）

**路径**：/login

**功能**：用户登录

**核心逻辑**：
```javascript
const handleLogin = async () => {
  await formRef.value.validate()        // 表单校验
  await userStore.login(loginForm.value) // 调用登录
  router.push('/dashboard')              // 跳转控制台
}
```

---

### 4.3 Register.vue（注册页）

**路径**：/register

**功能**：用户注册

**表单字段**：
| 字段 | 必填 | 校验 |
|------|------|------|
| username | ✅ | 3-20位 |
| password | ✅ | 至少6位 |
| email | ❌ | 邮箱格式 |
| nickname | ❌ | - |

---

### 4.4 Dashboard.vue（控制台）

**路径**：/dashboard

**权限**：需要登录才能访问

**功能**：
- 显示用户信息
- 退出登录

---

## 五、登录流程详解

```
用户输入账号密码
    ↓
点击"登录"按钮
    ↓
handleLogin() 执行
    ↓
formRef.value.validate() 表单校验
    ↓
userStore.login(loginForm) 调用
    ↓
api/auth.js 的 login() 函数
    ↓
request.js 的 axios 请求
    ↓
携带 username, password 发送 POST 请求
    ↓
后端 AuthController.login() 处理
    ↓
返回 { token, user }
    ↓
前端保存 token 和 userInfo
    ↓
跳转到 /dashboard
```

---

## 六、请求流程详解

### 6.1 请求流程图

```
前端 axios 请求
    ↓
request.js 请求拦截器
    ↓
从 localStorage 获取 token
    ↓
在请求头添加 Authorization: Bearer <token>
    ↓
发送请求到后端
    ↓
后端返回响应
    ↓
request.js 响应拦截器
    ↓
检查 code 是否为 200
    ↓
├─ 是 → 返回 data
└─ 否 → 提示错误信息
```

### 6.2 401 处理流程

```
后端返回 401（未登录）
    ↓
响应拦截器捕获错误
    ↓
提示"请先登录"
    ↓
清除 localStorage 中的 token
    ↓
跳转到登录页 /login
```

---

## 七、开发命令

| 命令 | 说明 |
|------|------|
| `npm install` | 安装依赖 |
| `npm run dev` | 开发模式启动 |
| `npm run build` | 生产环境构建 |
| `npm run preview` | 预览构建结果 |

---

## 八、后续开发计划

- [ ] 第 4 步：模板管理模块
  - 后端：模板 CRUD
  - 前端：模板列表页
- [ ] 第 5 步：简历 CRUD 模块
  - 后端：简历增删改查
  - 前端：简历列表、新建、编辑
- [ ] 第 6 步：简历编辑器
  - 左侧编辑面板
  - 右侧实时预览
- [ ] 第 7 步：AI 生成简历功能
  - 调用 AI 接口
  - 解析结果
- [ ] 第 8 步：简历导出 PDF