# 逐风简历（Zhufeng Resume）- 项目总结文档

> **项目类型**：在线 AI 智能简历编辑器（前后端分离）
> **技术架构**：Vue 3 + Spring Boot + PostgreSQL + Redis
> **开发周期**：2026 年 Q2
> **仓库地址**：[GitHub](https://github.com/用户名/zhufengaijianli-project)

---

## 一、项目概述

逐风简历是一款**在线智能简历编辑器**，支持多套专业模板、AI 内容润色/提取、实时预览、PDF 导出等核心功能。采用前后端分离架构，前端基于 Vue 3 + Vite 构建，后端基于 Spring Boot + MyBatis 提供 RESTful API 服务。

### 核心亮点

| 亮点 | 说明 |
|------|------|
| **10+ 套简历模板** | 经典/现代/极简/优雅/创意/编辑风/左右布局/时间线/简历本风格 |
| **AI 智能辅助** | 支持 DeepSeek / 豆包 / OpenAI / Gemini / MiMo / 自定义 API 共 6 家服务商 |
| **JWT + Redis 双重认证** | Token 白名单机制，支持主动吊销和一键强制下线 |
| **多账号数据隔离** | 前后端全链路数据隔离，防止跨账号数据泄漏 |
| **实时预览与导出** | A4 纸张比例预览、缩放控制、PDF 导出、打印功能 |
| **自动保存 & 回收站** | 3 秒防抖自动保存、5 天过期回收站、本地备份 |

---

## 二、技术栈

### 2.1 前端技术栈

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| 核心框架 | Vue.js | ^3.5.34 | 渐进式 JavaScript 框架 |
| 构建工具 | Vite | ^8.0.12 | 下一代前端构建工具 |
| UI 组件库 | Element Plus | ^2.14.1 | 企业级 UI 组件 |
| 状态管理 | Pinia | ^3.0.4 | Vue 官方状态管理 |
| 路由 | Vue Router | ^4.6.4 | SPA 路由管理 |
| HTTP 客户端 | Axios | ^1.16.1 | HTTP 请求封装 |
| 富文本编辑 | TipTap (Starter Kit) | ^3.26.0 | 基于 ProseMirror 的编辑器 |
| 工具库 | VueUse | ^14.3.0 | 组合式 API 工具集 |
| 拖拽排序 | vuedraggable | ^4.1.0 | 基于 SortableJS 的拖拽组件 |
| PDF 导出 | html2canvas + jspdf | ^1.4.1 / ^4.2.1 | HTML → Canvas → PDF |
| 国际化 | vue-i18n | ^9.14.4 | 中英文双语支持 |
| CSS 框架 | Tailwind CSS | ^4.3.0 | 原子化 CSS |
| CSS 预处理 | Sass | ^1.100.0 | SCSS 预处理器 |

### 2.2 后端技术栈

| 类别 | 技术 | 版本 | 用途 |
|------|------|------|------|
| 基础框架 | Spring Boot | 4.0.6 | Java 应用框架 |
| Java 版本 | Java | 17 | LTS 长期支持版 |
| ORM 框架 | MyBatis | 4.0.1 | 数据持久层 |
| 数据库 | PostgreSQL | - | 关系型数据库 |
| 缓存 | Redis (Spring Data Redis) | - | Token/Session/限流存储 |
| JWT 认证 | JJWT | 0.12.3 | Token 生成与解析 |
| 密码加密 | BCrypt (Spring Security Crypto) | 6.2.0 | 密码哈希加密 |
| JSON 处理 | Jackson (jackson-databind) | - | JSON 序列化 |
| HTTP 客户端 | Hutool | 5.8.23 | 调用外部 AI API |
| 参数校验 | Spring Validation | - | 请求参数校验 |
| 连接池 | Lettuce (commons-pool2) | - | Redis 连接池 |

### 2.3 基础设施

| 组件 | 说明 |
|------|------|
| 数据库 | PostgreSQL（端口 5432） |
| 缓存 | Redis（端口 6379） |
| 后端服务 | Spring Boot（端口 8081） |
| 前端开发 | Vite Dev Server（端口 5173/5174） |

---

## 三、项目结构

```
zhufengaijianli-project/
├── docs/                              # 项目文档
│   ├── JiaGou.md                      # 架构设计文档
│   ├── architecture-design-v2.md      # 架构设计 v2
│   ├── development-notes-part1~9.md   # 开发笔记（共 9 部分）
│   ├── migration.sql                  # 数据库迁移脚本
│   └── kill_duankou.md                # 端口占用解决方案手册
│
├── zhufeng-resume-backend/            # ====== 后端项目 ======
│   └── src/main/java/com/zhufeng/
│       ├── ZhufengResumeBackendApplication.java    # 启动类
│       ├── common/                    # 公共模块
│       │   ├── JwtUtil.java           # JWT 工具类（生成/解析/校验）
│       │   ├── UserContext.java       # 用户上下文（ThreadLocal）
│       │   ├── Result.java            # 统一响应封装
│       │   ├── ResultCode.java        # 响应状态码枚举
│       │   ├── BusinessException.java # 业务异常
│       │   └── GlobalExceptionHandler.java  # 全局异常处理
│       ├── config/
│       │   ├── WebMvcConfig.java      # MVC 配置（拦截器/CORS）
│       │   └── RedisConfig.java       # Redis 序列化配置
│       ├── controller/                # 控制器层
│       │   ├── AuthController.java    # 认证（登录/注册/登出/me）
│       │   ├── ResumeController.java  # 简历 CRUD
│       │   ├── AiController.java      # AI 功能（生成/润色/语法检查）
│       │   ├── UserController.java    # 用户管理
│       │   ├── TemplateController.java # 模板管理
│       │   └── TestController.java    # 测试接口
│       ├── service/                   # 服务层
│       │   ├── AuthService.java       # 认证核心逻辑
│       │   ├── ResumeService.java     # 简历业务逻辑
│       │   ├── AiService.java         # AI 服务调用
│       │   ├── UserService.java       # 用户服务
│       │   ├── TemplateService.java   # 模板服务
│       │   └── RedisService.java      # Redis 统一操作
│       ├── interceptor/
│       │   ├── JwtAuthInterceptor.java    # JWT 认证拦截器
│       │   └── NoCacheInterceptor.java     # 禁用浏览器缓存
│       ├── dto/                       # 数据传输对象
│       ├── entity/                    # 实体类（User/Resume/Template）
│       ├── vo/                        # 视图对象
│       └── mapper/                    # MyBatis Mapper 接口
│
├── zhufeng-resume-frontend/           # ====== 前端项目 ======
│   └── src/
│       ├── main.js                    # 应用入口
│       ├── App.vue                    # 根组件
│       ├── router/index.js            # 路由配置（含守卫+后置钩子）
│       ├── api/                       # API 接口定义
│       │   ├── request.js             # Axios 实例（请求/响应拦截器）
│       │   ├── auth.js                # 认证接口
│       │   ├── resume.js              # 简历接口
│       │   ├── ai.js                  # AI 接口
│       │   ├── user.js                # 用户接口
│       │   └── template.js            # 模板接口
│       ├── pages/                     # 页面组件
│       │   ├── Home.vue               # 首页（Landing Page）
│       │   ├── Dashboard.vue          # 工作台（列表+回收站）
│       │   ├── Editor.vue             # 编辑器（核心页面 ~1080 行）
│       │   ├── Templates.vue          # 模板选择页
│       │   ├── Login.vue / Register.vue  # 登录/注册
│       │   ├── Settings.vue           # 设置页
│       │   ├── AiGenerate.vue         # AI 生成页
│       │   └── AiSettings.vue         # AI 服务商配置
│       ├── components/editor/         # 编辑器组件组（15 个组件）
│       │   ├── EditorFormPanel.vue    # 左侧表单面板（含所有模块 Tab）
│       │   ├── EditorHeader.vue       # 编辑器头部
│       │   ├── EditorFloatingBar.vue  # 浮动工具栏
│       │   ├── PreviewPanel.vue       # 右侧预览面板
│       │   ├── BasicPanel.vue         # 基本信息
│       │   ├── EducationPanel.vue     # 教育经历
│       │   ├── ExperiencePanel.vue    # 工作经历
│       │   ├── ProjectsPanel.vue      # 项目经历
│       │   ├── SkillsPanel.vue        # 专业技能
│       │   ├── SelfEvaluationPanel.vue # 自我评价
│       │   ├── CertificatesPanel.vue  # 证书荣誉
│       │   ├── FieldFontBarContent.vue # 字体样式调节栏
│       │   └── grammar/GrammarCheckDrawer.vue # 语法检查抽屉
│       ├── components/templates/      # 简历模板（10 套）
│       │   ├── jianliben/JianlibenTemplate.vue  # 主模板（简历本风格）
│       │   ├── classic/ClassicTemplate.vue       # 经典深蓝
│       │   ├── modern/ModernTemplate.vue         # 现代简约
│       │   ├── minimalist/MinimalistTemplate.vue  # 极简风
│       │   ├── elegant/ElegantTemplate.vue        # 优雅商务
│       │   ├── creative/CreativeTemplate.vue     # 创意设计
│       │   ├── editorial/EditorialTemplate.vue    # 编辑排版
│       │   ├── left-right/LeftRightTemplate.vue  # 左右布局
│       │   ├── timeline/TimelineTemplate.vue      # 时间线
│       │   └── shared/                          # 共享组件
│       ├── stores/                    # Pinia 状态管理
│       │   ├── user.js                # 用户 Store（认证/登出/强制下线）
│       │   ├── resume.js              # 简历 Store（CRUD/模块管理）
│       │   ├── aiConfig.js            # AI 配置 Store（6 家服务商）
│       │   └── theme.js               # 主题 Store
│       ├── utils/
│       │   ├── auth.js                # 认证工具（Token 管理/全量清除）
│       │   └── exportPdf.js           # PDF 导出工具
│       ├── layouts/MainLayout.vue     # 主布局
│       └── i18n/                      # 国际化（中/英）
```

---

## 四、核心功能

### 4.1 用户系统

| 功能 | 接口 | 说明 |
|------|------|------|
| 注册 | `POST /api/v1/auth/register` | 用户名唯一性校验 + BCrypt 密码加密 |
| 登录 | `POST /api/v1/auth/login?expireDays=N` | 支持"记住我"（7天 vs 3小时） |
| 登出 | `POST /api/v1/auth/logout` | 删除 Redis Token + Session |
| 强制下线 | `POST /api/v1/auth/logout-all` | 销毁该用户所有设备 Token |
| 校验身份 | `GET /api/v1/auth/me` | 从 Token 强制获取当前用户 |
| 个人设置 | `GET /api/v1/user/profile` | 获取/更新用户资料 |

### 4.2 简历编辑器（核心功能）

#### 编辑器布局
```
┌─────────────────────────────────────────────┐
│  EditorHeader（标题栏：保存状态/模板切换/AI）  │
├──────────────────┬──────────────────────────┤
│                  │                          │
│  EditorFormPanel │    PreviewPanel          │
│  （左侧表单）     │    （右侧 A4 预览）        │
│                  │                          │
│  - 基本信息       │    - 实时渲染             │
│  - 教育经历       │    - 30%~200% 缩放        │
│  - 工作经历       │    - 全屏模式             │
│  - 项目经历       │                          │
│  - 专业技能       │                          │
│  - 自我评价       │                          │
│  - 证书荣誉       │                          │
│  （含获奖经历）    │                          │
│                  │                          │
├──────────────────┴──────────────────────────┤
│  EditorFloatingBar（底部浮动工具栏）          │
│  [切换] [AI分析] [PDF导出] [复制] [打印] ...  │
└─────────────────────────────────────────────┘
```

#### 表单字段体系

| 模块 | 字段 | 特殊功能 |
|------|------|----------|
| 基本信息 | 姓名/邮箱/电话/头像/求职意向 | 头像上传裁剪、字体样式调节 |
| 教育经历 | 学校/专业/学位/GPA/时间/描述/课程 | 可添加多条、拖拽排序 |
| 工作经历 | 公司/职位/起止时间/描述/内容 | 富文本编辑（TipTap）、AI 润色 |
| 项目经历 | 项目名/角色/链接/起止时间/描述 | 同上 |
| 专业技能 | 技能标签输入 | 标签式展示 |
| 自我评价 | 多行文本 | 富文本编辑 |
| 证书荣誉 | 名称/机构/**获取时间**/**获奖经历** | 新增获奖经历字段（textarea） |

#### 高级交互能力
- **字体样式调节**：每个字段独立支持字体大小/粗细/颜色/字间距调整（`FieldFontBarContent`）
- **模块显隐排序**：可自定义显示的简历模块及顺序（拖拽调整）
- **AI 辅助编辑**：
  - AI 润色（STAR 法则优化，对比弹窗选择是否采用）
  - AI 提取（从纯文本提取结构化 JSON 数据）
  - AI 语法检查（错别字/标点/语法错误检测）
  - AI 整体分析（简历质量评估报告）
- **自动保存**：3 秒防抖，formData 变化后自动调用 PUT API
- **模板切换**：弹窗选择不同模板，实时应用预填充数据（6 套示例数据）

### 4.3 模板系统

| 模板 ID | 名称 | 风格 | 特色 |
|---------|------|------|------|
| 116 | 经典深蓝 | 专业稳重 | 左右分栏、蓝色主题 |
| 117 | 商务蓝 | 商务正式 | 简洁大方 |
| 118 | 清新绿 | 清新自然 | 绿色调、适合应届生 |
| 119 | 稳重灰 | 成熟内敛 | 灰色系、适合资深人士 |
| 120 | 创意粉 | 个性活泼 | 粉色调、适合设计岗 |
| 129 | 深色黑 | 科技感强 | 暗色背景、适合程序员 |
| jianliben | 简历本 | 打印友好 | A4 精确排版、支持拖拽排序模块 |

### 4.4 AI 能力集成

支持的 AI 服务商：

| 服务商 | 默认模型 | 配置项 |
|--------|---------|--------|
| DeepSeek | deepseek-chat | API Key + Model ID |
| 豆包（火山引擎） | ep-20240515152103-hxqmr | API Key + Model ID + Endpoint |
| OpenAI | gpt-4o-mini | API Key + Model ID + Endpoint |
| Gemini | gemini-2.0-flash | API Key + Model ID + Endpoint |
| 小米 MiMo | moonshot-v1-8k | API Key + Model ID + Endpoint |
| 自定义 | 任意 OpenAI 兼容 | API Key + Model ID + Endpoint（完全可配）|

AI 功能接口：

| 功能 | 接口 | 说明 |
|------|------|------|
| AI 生成 | `POST /api/v1/ai/generate` | 根据提示词生成完整简历 |
| AI 润色 | `POST /api/v1/ai/polish` | STAR 法则优化各模块内容 |
| AI 语法检查 | `POST /api/v1/ai/grammar` | 检测错别字/标点/语法 |
| 模型列表 | `GET /api/v1/ai/models` | 获取可用 AI 模型 |

### 4.5 回收站与备份

**回收站机制**：
- 删除简历时先备份到 localStorage（key: `resume_recycle_bin:{userId}`）
- 支持恢复（重新通过 API 创建）
- 支持永久删除
- 5 天后自动清理过期数据
- 一键清理所有简历到回收站
- **多账号隔离**：按 userId 命名空间，防止跨账号看到他人回收站

**数据备份**：
- 可配置本地备份文件夹路径
- 自动备份开关（每次编辑后保存备份文件）
- 设置持久化到 localStorage

### 4.6 PDF 导出

- **技术方案**：html2canvas（DOM → Canvas）+ jspdf（Canvas → PDF）
- **流程**：获取模板 DOM → 渲染为 Canvas → 创建 PDF → 触发下载
- **支持**：A4 尺寸、高分辨率、保留样式

---

## 五、攻克的关键问题

### 5.1 JWT 访问控制与认证安全

**问题**：传统 JWT 无状态，Token 一旦签发无法主动撤销；单靠客户端删除 Token 无法保证安全性。

**解决方案**：JWT + Redis 白名单双重认证机制

```
传统 JWT:        签发 → 客户端持有 → 到期自动失效（无法提前吊销）
本项目方案:      签发 → 存入 Redis 白名单 → 拦截器双重校验 → 可随时吊销
```

**实现细节**：

1. **Token 存储**：登录时同时写入 Redis（`token:{value}` → `userId:username`），TTL 与 Token 过期时间一致
2. **反向索引**：每个用户的 Token 存入 Set（`token:user:{userId}`），用于一键清空
3. **双重校验流程**：
   - 拦截器先查 Redis 白名单（Token 是否存在且未过期）
   - 再解析 JWT 签名（防篡改）
   - 两者都通过才放行
4. **主动吊销能力**：
   - `/logout`：删除单个 Token → 该 Token 立即失效
   - `/logout-all`：遍历反向索引 Set → 批量删除所有 Token → 所有设备立即下线

**关键文件**：
- [JwtUtil.java](zhufeng-resume-backend/src/main/java/com/zhufeng/common/JwtUtil.java)
- [RedisService.java](zhufeng-resume-backend/src/main/java/com/zhufeng/service/RedisService.java)
- [JwtAuthInterceptor.java](zhufeng-resume-backend/src/main/java/com/zhufeng/interceptor/JwtAuthInterceptor.java)

### 5.2 多账号登录数据残留防护

**问题**：同一浏览器切换账号时，上一账号的用户信息、AI 配置、简历数据可能残留在 localStorage/Pinia Store 中，导致数据泄漏或显示错误。

**解决方案**：全链路数据隔离规范

**前端防护层**：

| 层级 | 措施 | 实现位置 |
|------|------|----------|
| Token 存储 | sessionStorage（关闭浏览器即失效） | [`auth.js`](zhufeng-resume-frontend/src/utils/auth.js) |
| 用户信息 | 不从缓存读取，刷新页面强制从后端拉取 | [`user.js`](zhufeng-resume-frontend/src/stores/user.js) `fetchCurrentUser()` |
| AI 配置 | 按 userId 命名空间隔离：`ai:{uid}:{provider}_apiKey` | [`aiConfig.js`](zhufeng-resume-frontend/src/stores/aiConfig.js) |
| 回收站 | 按 userId 命名空间：`resume_recycle_bin:{uid}` | [`Dashboard.vue`](zhufeng-resume-frontend/src/pages/Dashboard.vue) |
| 登出清理 | 清空 sessionStorage + localStorage + Cookie + 所有 Pinia Store | [`clearAuth()`](zhufeng-resume-frontend/src/utils/auth.js) |
| 401 处理 | 全量清除 + 重定向登录（防重复跳转） | [`request.js`](zhufeng-resume-frontend/src/api/request.js) |

**后端防护层**：

| 层级 | 措施 | 实现位置 |
|------|------|----------|
| 用户上下文 | ThreadLocal 存储当前 userId，每次请求自动设置/清理 | [`UserContext.java`](zhufeng-resume-backend/src/main/java/com/zhufeng/common/UserContext.java) |
| 数据查询 | 所有业务查询通过 `UserContext.getUserId()` 过滤，绝不信任前端传的 userId | [`ResumeService.java`](zhufeng-resume-backend/src/main/java/com/zhufeng/service/ResumeService.java) |
| 权限校验 | 访问他人简历返回 403 "无权访问" | 同上 |
| Token 反向索引 | logoutAll 时批量销毁某用户所有 Token | [`RedisService.deleteUserTokens()`](zhufeng-resume-backend/src/main/java/com/zhufeng/service/RedisService.java) |

**测试验证**（28 项用例全部通过）：
- testuser01 登录 → 创建简历 → 登出 → Token 失效 ✅
- testuser02 登录 → 看不到 testuser01 的简历 ✅
- testuser02 访问 testuser01 的简历 id → 返回 403 ✅
- logout-all 后所有设备 Token 失效 ✅

### 5.3 简历页面复杂交互

**问题**：简历编辑器需要同时处理表单编辑、实时预览、模块管理、AI 辅助、自动保存等多种交互，状态管理复杂度高。

**解决方案**：分层架构 + Pinia 集中管理 + 防抖自动保存

**架构分层**：

```
Editor.vue（主控制器 ~1080 行）
├── EditorFormPanel（左侧表单 - 各模块 Panel）
│   ├── BasicPanel / EducationPanel / ExperiencePanel ...
│   └── FieldFontBarContent（字体样式调节 - 按需弹出）
├── PreviewPanel / PreviewDock（右侧预览 - 模板渲染）
├── EditorFloatingBar（底部工具栏）
└── EditorHeader（顶部标题栏）
```

**关键技术点**：

1. **双向绑定 + 防抖保存**：watch 监听 formData 变化 → 3 秒防抖 → 自动调用 PUT API
2. **左右面板宽度可调**：拖拽分割线调整（25% ~ 55%）
3. **模块动态显隐**：menuSections 数组控制哪些模块显示及顺序
4. **字体样式独立控制**：每个字段有独立的 fontStyle 对象（size/color/weight/letterSpacing/lineHeight）
5. **模板热切换**：切换模板时保留当前编辑数据，仅更换渲染组件
6. **AI 润色对比弹窗**：原文 vs AI 优化版本并排展示，用户选择是否采纳

### 5.4 组件精细化调节

**问题**：简历是高度个性化的文档，用户需要对每个字段的字体、颜色、间距进行精细控制，且需要在预览中实时反映。

**解决方案**：FieldFontBarContent 字体样式调节组件 + Per-Field Style 对象

**实现方式**：

```javascript
// 每个字段独立的样式对象
globalSettings: {
  themeColor: '#667eea',      // 主题色
  fontFamily: 'default',       // 字体族
  baseFontSize: 14,            // 基础字号
  headerSize: 18,              // 标题字号
  subheaderSize: 16,           // 副标题字号
  lineHeight: 1.5,             // 行高
  pagePadding: 40,             // 页边距
  sectionSpacing: 24,          // 模块间距
  paragraphSpacing: 12         // 段落间距
}

// 单个字段的覆盖样式（可选）
nonBasicFields: {
  'cert-0-name': { fontSize: 13, fontWeight: 'bold', color: '#333' },
  'cert-0-desc': { fontSize: 9, color: '#666' }
}
```

**组件特性**：
- 点击字段聚焦时自动弹出字体调节栏
- 支持字号、粗细、颜色、字间距、行高调节
- 样式变更实时反映到右侧预览
- 不同模板可复用同一套样式系统

### 5.5 缓存机制

**问题**：频繁访问后端 API 造成性能浪费，但过度缓存又会导致数据不一致和多账号数据残留。

**解决方案**：多层分级缓存策略

| 缓存层级 | 存储位置 | 数据类型 | TTL / 清理策略 |
|---------|---------|---------|---------------|
| **Token 白名单** | Redis (`token:{value}`) | userId:username | 与 Token 有效期一致 |
| **用户 Session** | Redis (`session:{uid}`) | User JSON | 与 Token 有效期一致 |
| **登录限流计数** | Redis (`login_limit:{name}`) | 失败次数 | 15 分钟窗口 |
| **用户偏好** | localStorage | darkMode / locale | 跨账号保留（不清除） |
| **AI 配置** | localStorage (`ai:{uid}:{provider}_*`) | API Key 等 | 按用户隔离，登出清除 |
| **回收站** | localStorage (`recycle_bin:{uid}`) | 已删简历 | 5 天自动过期 |
| **用户信息缓存** | localStorage (`user_info`) | UserVO | 仅用于"立即展示"，刷新必拉后端 |
| **浏览器响应缓存** | HTTP Headers | API 响应 | **禁用**（NoCacheInterceptor） |

**关键决策**：
- 用户数据（简历列表、详情）**不做前端缓存**，每次进入页面重新请求后端
- 仅保留"用户体验型"偏好（暗色模式、语言）跨账号保留
- NoCacheInterceptor 强制所有 API 响应头包含 `Cache-Control: no-store, no-cache`

### 5.6 数据备份机制

**问题**：用户编辑过程中可能遇到网络中断、误操作等风险，需要可靠的数据保护。

**解决方案**：三级备份策略

| 级别 | 方式 | 触发条件 | 存储位置 |
|------|------|---------|---------|
| L1 自动保存 | 3 秒防抖 PUT API | formData 变化 | 后端 PostgreSQL |
| L2 回收站 | 删除前备份 | 用户点击删除 | localStorage（5天过期） |
| L3 本地备份 | JSON 文件导出 | 用户手动触发 / 自动开关 | 本地文件系统 |

**回收站详细机制**：

```javascript
// 删除时：先备份到回收站
function deleteResume(id) {
  const resume = resumes.value.find(r => r.id === id)
  recycleBin.push({ ...resume, deletedAt: new Date().toISOString() })
  saveRecycleBin()  // 写入 localStorage
  // 再调用 API 真正删除
}

// 加载时：过滤掉已过期的（> 5 天）
function loadRecycleBin() {
  const raw = localStorage.getItem(getRecycleBinKey())  // key 含 userId
  const list = JSON.parse(raw) || []
  const now = Date.now()
  const valid = list.filter(item => {
    const elapsed = now - new Date(item.deletedAt).getTime()
    return elapsed < 5 * 24 * 60 * 60 * 1000  // 5 天
  })
  // 过期数据自动清理
}
```

### 5.7 API Key 防泄密

**问题**：用户需要配置多家 AI 服务商的 API Key，这些密钥属于敏感信息，必须严格保护。

**解决方案**：前端隔离存储 + 后端不落库 + 多账号隔离

**安全措施**：

| 措施 | 实现 | 说明 |
|------|------|------|
| 前端存储 | localStorage（每用户独立命名空间） | Key 格式：`ai:{userId}:{provider}_apiKey` |
| 输入保护 | `<input type="password" show-password>` | 默认隐藏，可切换显示 |
| 传输安全 | HTTPS（生产环境） | 防中间人窃听 |
| 后端不存储 | 收到后直接转发给 AI 服务商 | 不写入数据库任何地方 |
| 多账号隔离 | Key 名含 userId | 切换账号后无法读取他人的 API Key |
| 登出清理 | `clearAuth()` 清空 localStorage | 切换账号/退出时全部清除 |
| 前端不提交 Git | `.gitignore` 排除 .env 文件 | 防止 accidental commit |

**AI 调用链路**：

```
用户输入 API Key → localStorage（浏览器本地）
     ↓
用户触发 AI 功能 → 前端从 localStorage 读取 → 通过 Bearer Token 发送到后端
     ↓
后端 AiService → 取出 API Key → 直接转发给 AI 服务商 API（不存库）
     ↓
AI 服务商返回结果 → 后端透传给前端 → 展示给用户
```

### 5.8 登录暴力破解防护

**问题**：公开注册的登录接口可能被暴力破解。

**解决方案**：Redis 计数器 + 时间窗口限流

```
登录失败流程：
  第 1 次失败 → redis INCR login_limit:{username} → count = 1
  第 2 次失败 → count = 2
  ...
  第 5 次失败 → count = 5 ≥ 阈值 → 返回"登录尝试次数过多，请15分钟后再试"

登录成功流程：
  → redis DEL login_limit:{username}  → 清除失败计数 → 重置
```

**实现位置**：[`AuthService.handleLoginFail()`](zhufeng-resume-backend/src/main/java/com/zhufeng/service/AuthService.java) + [`RedisService`](zhufeng-resume-backend/src/main/java/com/zhufeng/service/RedisService.java)

---

## 六、API 接口清单

### 6.1 认证模块 (`/api/v1/auth`)

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/register` | 否 | 用户注册 |
| POST | `/login` | 否 | 用户登录（支持 expireDays 参数） |
| POST | `/logout` | 否 | 登出（删除 Token） |
| POST | `/logout-all` | 否 | 强制下线（删除所有 Token） |
| GET | `/me` | 是 | 校验 Token 并返回用户信息 |

### 6.2 简历模块 (`/api/v1/resumes`)

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| GET | `/resumes` | 是 | 获取当前用户的简历列表 |
| GET | `/resumes/{id}` | 是 | 获取简历详情（权限校验） |
| POST | `/resumes` | 是 | 创建新简历 |
| PUT | `/resumes/{id}` | 是 | 更新简历（权限校验） |
| DELETE | `/resumes/{id}` | 是 | 删除简历（权限校验） |

### 6.3 AI 模块 (`/api/v1/ai`)

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/generate` | 是 | AI 生成完整简历 |
| POST | `/polish` | 是 | AI 内容润色（支持 6 种类型） |
| POST | `/grammar` | 是 | AI 语法检查 |
| GET | `/models` | 是 | 获取可用 AI 模型列表 |

### 6.4 其他模块

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| GET | `/templates` | 否 | 获取模板列表 |
| GET | `/user/profile` | 是 | 获取用户资料 |
| GET | `/test/hello` | 否 | 健康检查 |

---

## 七、安全体系总览

```
┌─────────────────────────────────────────────────────┐
│                   安全防御体系                         │
├─────────────────────────────────────────────────────┤
│                                                     │
│  【认证层】                                           │
│  ├─ JWT Token + Redis 白名单（双重校验）               │
│  ├─ BCrypt 密码哈希加密                               │
│  ├─ 登录限流（15分钟/5次）                            │
│  └─ Token 主动吊销 + 一键强制下线                      │
│                                                     │
│  【授权层】                                           │
│  ├─ UserContext ThreadLocal 用户上下文                 │
│  ├─ 后端强制从 Token 获取 userId（不信前端传值）        │
│  └─ 数据级权限校验（访问他人简历返回 403）              │
│                                                     │
│  【传输层】                                           │
│  ├─ CSRF 防护（X-Requested-With 头）                  │
│  ├─ CORS 跨域白名单                                   │
│  └─ NoCache 响应头（防浏览器缓存敏感数据）              │
│                                                     │
│  【数据隔离层】                                        │
│  ├─ 前端：localStorage 按 userId 命名空间隔离           │
│  ├─ 前端：Pinia Store 登出时全量 reset                 │
│  ├─ 前端：401 响应时 fullClearAndRedirect             │
│  └─ 后端：所有查询通过 UserContext.getUserId() 过滤     │
│                                                     │
│  【密钥保护层】                                        │
│  ├─ API Key 前端 localStorage 隔离存储                 │
│  ├─ API Key 后端不落库（直接转发）                     │
│  ├─ 多账号 API Key 完全隔离                           │
│  └─ 登出时全量清除含 API Key                           │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 八、部署说明

### 8.1 环境要求

| 组件 | 版本要求 |
|------|---------|
| JDK | 17+ |
| Node.js | 18+ |
| PostgreSQL | 14+ |
| Redis | 6+ |

### 8.2 启动步骤

```bash
# 1. 启动 Redis
redis-server

# 2. 启动 PostgreSQL（确保数据库 zhufeng_resume 已创建）

# 3. 启动后端
cd zhufeng-resume-backend
mvn spring-boot:run
# 访问 http://localhost:8081/api/v1/test/hello 验证

# 4. 启动前端
cd zhufeng-resume-frontend
npm install
npm run dev
# 访问 http://localhost:5173
```

### 8.3 生产构建

```bash
# 前端生产构建
cd zhufeng-resume-frontend
npm run build
# 输出到 dist/ 目录，可部署到 Nginx

# 后端打包
cd zhufeng-resume-backend
mvn clean package -DskipTests
# 输出到 target/*.jar
```

---

## 九、未来规划

- [ ] 单元测试覆盖率提升（目前仅有基础启动测试）
- [ ] GitHub Actions CI/CD 自动化部署流水线
- [ ] Docker 容器化部署（docker-compose 一键启动）
- [ ] 更多简历模板（行业定制模板：互联网/金融/教育等）
- [ ] 简历分享链接（匿名分享/二维码）
- [ ] 简历解析（上传 PDF/图片 → 自动识别填入表单）
- [ ] 协同编辑（多人同时编辑同一份简历）
- [ ] 国际化完善（完整的中英文界面）

---

> **文档版本**: v1.0  
> **更新日期**: 2026-06-11  
> **作者**: 逐风简历开发团队
