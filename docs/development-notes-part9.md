# 逐风 AI 简历平台 — 开发笔记（第九部分）

> 更新日期：2026-05-12
> 内容：前端界面优化

---

## 一、优化概述

将之前分散的独立页面整合为统一的布局框架，添加侧边栏导航，提升整体使用体验。

### 优化前

```
每个页面独立，各自有 header
├── 登录页 /login
├── 注册页 /register
├── 首页 /home
├── 控制台 /dashboard
├── 模板中心 /templates
├── AI 生成 /ai-generate
└── 编辑器 /editor/:id
```

### 优化后

```
┌──────────────────────────────────────┐
│  公开页面（无侧边栏）                  │
│  ├── /home      首页                  │
│  ├── /login     登录                  │
│  └── /register  注册                  │
│                                      │
│  登录后页面（带侧边栏）                 │
│  ┌────────┬─────────────────────────┐ │
│  │侧边栏   │  主内容区                │ │
│  │📄 简历  │  ┌───────────────────┐  │ │
│  │📋 模板  │  │   页面内容         │  │ │
│  │🤖 AI    │  │                   │  │ │
│  │        │  └───────────────────┘  │ │
│  │[退出]  │                         │ │
│  └────────┴─────────────────────────┘ │
└──────────────────────────────────────┘
```

---

## 二、文件变更

### 新增

| 文件 | 路径 | 说明 |
|------|------|------|
| `MainLayout.vue` | `layouts/` | 主布局（侧边栏 + 内容区） |

### 修改

| 文件 | 修改内容 |
|------|----------|
| `router/index.js` | 路由嵌套结构，children 方式 |
| `Dashboard.vue` | 移除独立 header |
| `Home.vue` | 重新设计公开首页 |

---

## 三、路由重构（核心修改）

### 优化前（平铺路由）

```javascript
{
  path: '/dashboard',
  component: () => import('../pages/Dashboard.vue'),
  meta: { requiresAuth: true }
}
```

### 优化后（嵌套路由）

```javascript
{
  path: '/',
  component: () => import('../layouts/MainLayout.vue'),  // 父路由：布局
  meta: { requiresAuth: true },
  children: [
    { path: '/dashboard', component: () => import('../pages/Dashboard.vue') },
    { path: '/templates', component: () => import('../pages/Templates.vue') },
    { path: '/ai-generate', component: () => import('../pages/AiGenerate.vue') },
    { path: '/editor/:id', component: () => import('../pages/Editor.vue') }
  ]
}
```

### 关键点

- 父路由使用 `MainLayout.vue`
- 子路由通过 `<router-view />` 在布局内渲染
- 只在父路由设 `requiresAuth`，子路由自动继承

---

## 四、MainLayout 布局组件

### 结构

```vue
<div class="main-layout">
  <aside class="sidebar">
    <!-- Logo -->
    <!-- 导航菜单（el-menu router） -->
    <!-- 退出按钮 -->
  </aside>
  <div class="main-content">
    <router-view />  <!-- 子路由渲染在这里 -->
  </div>
</div>
```

### 侧边栏菜单

```vue
<el-menu default-active="activeMenu" router>
  <el-menu-item index="/dashboard">
    <el-icon><Document /></el-icon>
    <span>我的简历</span>
  </el-menu-item>
  <el-menu-item index="/templates">
    <el-icon><Grid /></el-icon>
    <span>模板中心</span>
  </el-menu-item>
  <el-menu-item index="/ai-generate">
    <el-icon><MagicStick /></el-icon>
    <span>AI 生成</span>
  </el-menu-item>
</el-menu>
```

### 高亮追踪

```javascript
const activeMenu = computed(() => route.path)
```

---

## 五、公开首页优化

### 设计要点

| 区域 | 内容 |
|------|------|
| 导航栏 | Logo + 登录/注册按钮 |
| 主视觉 | 标题 + 副标题 + CTA按钮 |
| 背景 | 深色渐变营造科技感 |

### 渐变背景

```css
background: linear-gradient(135deg, #1d1e2c 0%, #2d3a5c 50%, #667eea 100%);
```

---

## 六、后续美化建议

| 建议 | 说明 |
|------|------|
| 图标系统 | 统一使用 Element Plus Icons |
| 过渡动画 | 路由切换加过渡效果 |
| 响应式 | 侧边栏在小屏自动收起 |
| 暗色模式 | 支持亮色/暗色切换 |
| 顶部面包屑 | 显示当前位置层级 |
| 通知中心 | 操作反馈集中展示 |

### 路由过渡动画示例

```vue
<!-- router/index.js 外层 -->
<router-view v-slot="{ Component }">
  <transition name="fade" mode="out-in">
    <component :is="Component" />
  </transition>
</router-view>
```

```css
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
```

---

## 七、项目完成总结

### 最终项目结构

```
zhufengaijianli-project/
├── zhufeng-resume-backend/           # SpringBoot 后端
│   ├── common/     统一响应、JWT、异常
│   ├── config/     拦截器配置、跨域
│   ├── controller/ Auth、User、Template、Resume、AI
│   ├── service/    业务逻辑
│   ├── mapper/     MyBatis 数据层
│   ├── entity/     数据库实体
│   ├── dto/        请求参数
│   ├── vo/         返回视图
│   └── interceptor/ JWT 拦截
├── zhufeng-resume-frontend/          # Vue3 前端
│   ├── api/        Axios 接口封装
│   ├── components/ 公共组件
│   ├── layouts/    布局组件
│   ├── pages/      页面
│   ├── router/     路由
│   ├── stores/     Pinia 状态管理
│   └── utils/      工具函数
└── docs/                             # 开发文档
    ├── architecture-design.md
    ├── development-mindmap.md
    ├── development-notes-part1~9.md
    └── test.md
```

### 核心功能清单

| 模块 | 功能 | 技术点 |
|------|------|--------|
| 用户认证 | 注册、登录、JWT鉴权 | BCrypt + JWT + 拦截器 |
| 模板管理 | 模板列表、分类筛选 | JSONB 存储 |
| 简历 CRUD | 增删改查、权限校验 | MyBatis + RESTful |
| 简历编辑器 | 可视化编辑、实时预览 | v-model 组件通信 |
| AI 生成 | 输入提示词生成简历 | 模拟数据（可接入真实API）|
| PDF 导出 | 一键导出下载 | html2canvas + jsPDF |

### 技术栈总览

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.x |
| 数据库 | PostgreSQL |
| ORM | MyBatis |
| 前端框架 | Vue 3 (Composition API) |
| 构建工具 | Vite |
| UI 库 | Element Plus |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| 认证 | JWT + BCrypt |
