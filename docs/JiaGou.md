# Magic Resume 项目架构文档

## 📋 目录

- [项目概述](#项目概述)
- [技术栈详解](#技术栈详解)
- [项目架构](#项目架构)
- [页面功能](#页面功能)
- [核心组件](#核心组件)
- [状态管理](#状态管理)
- [API 接口](#api-接口)
- [模板系统](#模板系统)
- [数据流](#数据流)

---

## 项目概述

**Magic Resume** 是一个现代化的在线简历编辑器，基于 **TanStack Start** 全栈框架构建，提供实时预览、自定义主题、AI 优化等功能。

### 项目特点

- 🚀 基于 TanStack Start 构建的同构应用
- 🎨 8 种精美简历模板
- 🤖 AI 语法检查和内容优化
- 📤 PDF 导出功能
- 💾 本地存储（File System Access API）
- 🌍 国际化支持（中英文）
- 📱 响应式设计

---

## 技术栈详解

### 核心框架

| 技术 | 版本 | 用途 |
|------|------|------|
| **React** | ^18 | UI 框架 |
| **TanStack Start** | ^1.160.2 | 全栈框架（同构应用） |
| **TanStack Router** | ^1.160.2 | 路由管理 |
| **TypeScript** | ^5 | 类型系统 |
| **Vite** | ^7.3.1 | 构建工具 |

### UI 相关

| 技术 | 用途 |
|------|------|
| **Tailwind CSS** | 样式框架 |
| **HeroUI** | React UI 组件库 |
| **Radix UI** | 无头 UI 组件 |
| **Framer Motion** | 动画库 |
| **Lucide React** | 图标库 |
| **Class Variance Authority** | 样式变体管理 |

### 编辑器相关

| 技术 | 用途 |
|------|------|
| **Tiptap** | 富文本编辑器 |
| **React Resizable Panels** | 可调整大小的面板 |

### 状态管理

| 技术 | 用途 |
|------|------|
| **Zustand** | 轻量级状态管理 |
| **Zustand Persist** | 持久化状态 |

### AI 集成

| 技术 | 用途 |
|------|------|
| **@google/generative-ai** | Gemini API |
| **Puppeteer** | 浏览器自动化（PDF） |

### PDF 导出

| 技术 | 用途 |
|------|------|
| **html2canvas** | HTML 转图片 |
| **html2pdf.js** | HTML 转 PDF |
| **pdfjs-dist** | PDF.js |

### 其他工具库

| 技术 | 用途 |
|------|------|
| **date-fns** | 日期处理 |
| **dayjs** | 日期库 |
| **lodash** | 工具库 |
| **Sonner** | Toast 通知 |
| **Next Themes** | 主题管理 |
| **Sharp** | 图片处理 |
| **Turndown** | HTML 转 Markdown |

---

## 项目架构

```
magic-resume/
├── public/                 # 静态资源
│   ├── features/          # 功能特性图片
│   ├── fonts/             # 中文字体
│   └── template-snapshots/ # 模板快照
├── src/
│   ├── app/               # 应用入口
│   │   ├── (public)/      # 公共页面
│   │   │   └── [locale]/  # 国际化路由
│   │   │       ├── layout.tsx
│   │   │       └── page.tsx
│   │   ├── api/           # API 路由（服务端）
│   │   │   ├── grammar/
│   │   │   ├── polish/
│   │   │   └── proxy/
│   │   └── app/           # 应用页面
│   │       ├── dashboard/ # 仪表盘
│   │       ├── workbench/ # 编辑器
│   │       └── page.tsx
│   ├── components/        # React 组件
│   │   ├── editor/       # 编辑器组件
│   │   ├── home/         # 首页组件
│   │   ├── preview/      # 预览组件
│   │   ├── shared/       # 共享组件
│   │   ├── templates/    # 简历模板
│   │   └── ui/           # UI 基础组件
│   ├── config/           # 配置文件
│   ├── hooks/            # 自定义 Hooks
│   ├── i18n/             # 国际化
│   ├── lib/              # 工具库
│   ├── routes/           # TanStack Router 路由
│   ├── store/            # Zustand 状态管理
│   ├── styles/           # 样式文件
│   ├── types/            # TypeScript 类型
│   └── utils/            # 工具函数
├── package.json          # 项目依赖
├── vite.config.ts        # Vite 配置
├── tailwind.config.ts    # Tailwind 配置
└── tsconfig.json         # TypeScript 配置
```

### 目录结构详解

#### 1. app/ - 应用核心

**布局文件：**
- `src/app/layout.tsx` - 根布局
- `src/app/app/layout.tsx` - 应用布局

**页面结构：**
```
app/
├── (public)/              # 公共路由组
│   └── [locale]/          # 国际化参数路由
│       ├── page.tsx       # 首页（Landing Page）
│       └── layout.tsx     # 公共布局
├── api/                   # 服务端 API（SSR）
│   ├── grammar/route.ts  # 语法检查 API
│   ├── polish/route.ts    # AI 优化 API
│   └── proxy/image/route.ts # 图片代理
└── app/                   # 应用路由组
    ├── dashboard/         # 仪表盘
    │   ├── page.tsx       # 重定向到 resumes
    │   ├── resumes/       # 简历列表
    │   ├── templates/    # 模板选择
    │   ├── ai/           # AI 设置
    │   └── settings/     # 设置页面
    └── workbench/         # 编辑器
        └── [id]/          # 简历编辑
            └── page.tsx   # 编辑器页面
```

#### 2. components/ - 组件库

**编辑器组件（editor/）：**
```
editor/
├── EditorHeader.tsx      # 编辑器头部
├── SidePanel.tsx         # 侧边栏（模块选择）
├── EditPanel.tsx         # 编辑面板
├── basic/                # 基本信息编辑
│   └── BasicPanel.tsx
├── education/           # 教育经历
│   ├── EducationItem.tsx
│   └── EducationPanel.tsx
├── experience/          # 工作经历
│   ├── ExperienceItem.tsx
│   └── ExperiencePanel.tsx
├── project/             # 项目经历
│   ├── ProjectItem.tsx
│   └── ProjectPanel.tsx
├── skills/              # 技能
│   └── SkillPanel.tsx
├── self-evaluation/    # 自我评价
│   └── SelfEvaluationPanel.tsx
├── certificates/       # 证书
│   ├── CertificateItem.tsx
│   └── CertificatesPanel.tsx
├── custom/              # 自定义模块
│   ├── CustomItem.tsx
│   └── CustomPanel.tsx
├── grammar/             # 语法检查
│   └── GrammarCheckDrawer.tsx
└── layout/              # 布局设置
    ├── LayoutItem.tsx
    └── LayoutSetting.tsx
```

**首页组件（home/）：**
- `LandingHeader.tsx` - 页头导航
- `HeroSection.tsx` - 英雄区域
- `FeaturesSection.tsx` - 功能特性
- `FAQSection.tsx` - 常见问题
- `CTASection.tsx` - 行动召唤
- `Footer.tsx` - 页脚

**模板组件（templates/）：**
```
templates/
├── classic/              # 经典风格
├── modern/               # 现代风格
├── left-right/          # 左右分栏
├── timeline/            # 时间线风格
├── minimalist/           # 极简风格
├── elegant/             # 优雅风格
├── creative/            # 创意风格
├── editorial/           # 编辑风格
├── shared/              # 共享组件
├── registry.ts          # 模板注册表
└── index.tsx            # 模板导出
```

**UI 组件（ui/）：**
- 基础组件：button, input, card, dialog 等
- 基于 Radix UI 和 HeroUI 构建

#### 3. routes/ - API 路由

**服务端 API：**
- `routes/api/polish.ts` - AI 优化简历
- `routes/api/grammar.ts` - 语法检查
- `routes/api/resume-import.ts` - 简历导入
- `routes/api/proxy/image.ts` - 图片代理

#### 4. store/ - 状态管理

```
store/
├── useResumeStore.ts    # 简历数据管理
├── useAIConfigStore.ts  # AI 配置管理
└── useGrammarStore.ts   # 语法检查状态
```

#### 5. config/ - 配置文件

```
config/
├── index.ts             # 主配置导出
├── ai.ts                # AI 模型配置
├── constants.ts         # 常量配置
├── faq.tsx              # FAQ 内容
├── initialResumeData.ts # 初始简历数据
└── modules.ts           # 模块配置
```

#### 6. types/ - 类型定义

```
types/
├── resume.ts            # 简历数据类型
├── template.ts          # 模板类型
├── global.d.ts          # 全局类型声明
└── mark.js.d.ts         # Mark.js 类型
```

---

## 页面功能

### 1. 首页（Landing Page）

**路由：** `/` 或 `/:locale`

**功能：**
- 产品介绍和特性展示
- 模板预览
- CTA 按钮跳转到仪表盘
- 国际化切换

**组件：**
- `LandingHeader` - 导航栏
- `HeroSection` - 英雄区域（包含视频和截图）
- `FeaturesSection` - 功能特性卡片
- `FAQSection` - 常见问题（手风琴）
- `CTASection` - 行动召唤

### 2. 仪表盘（Dashboard）

**路由：** `/app/dashboard`

**子页面：**

#### 2.1 简历列表页
**路由：** `/app/dashboard/resumes`

**功能：**
- 显示所有简历卡片
- 创建新简历（选择模板或空白）
- 导入简历（JSON/PDF）
- 删除简历
- 同步到本地文件夹

**组件：**
- `ResumeWorkbench` - 主容器
- `ResumeCardItem` - 简历卡片
- `CreateResumeModal` - 创建模态框
- `ImportResumeDialog` - 导入对话框
- `AnimatedImportButton` - 动画导入按钮

#### 2.2 模板选择页
**路由：** `/app/dashboard/templates`

**功能：**
- 展示 8 种模板预览
- 支持主题色切换
- 实时预览模板效果
- 选择模板创建简历

**特性：**
- 动态缩放预览
- 预设颜色选择
- 模板分类展示

#### 2.3 AI 设置页
**路由：** `/app/dashboard/ai`

**功能：**
- 配置多个 AI 模型
- 支持的模型：
  - 豆包 (Doubao)
  - DeepSeek
  - OpenAI
  - Gemini
  - 小米
  - 自定义 API

**配置项：**
- API Key
- 模型 ID
- API Endpoint

#### 2.4 设置页
**路由：** `/app/dashboard/settings`

**功能：**
- 配置同步文件夹
- 管理本地存储权限
- 数据备份设置

### 3. 编辑器（Workbench）

**路由：** `/app/workbench/:id`

**功能：**
- 三栏布局编辑器
- 实时预览简历
- AI 语法检查
- AI 内容优化
- 主题设置
- PDF 导出

**组件结构：**
```
EditorLayout
├── EditorHeader          # 顶部工具栏
│   ├── 返回按钮
│   ├── 简历标题
│   ├── 备份状态指示
│   ├── 语法检查按钮
│   ├── 主题切换
│   └── PDF 导出
├── ResizablePanelGroup   # 可调整的面板组
│   ├── SidePanel         # 左侧：模块选择
│   │   ├── 基础设置
│   │   ├── 布局设置
│   │   ├── 颜色设置
│   │   └── 模块列表
│   ├── ResizableHandle   # 拖拽分隔条
│   ├── EditPanel         # 中间：编辑表单
│   │   ├── 基本信息
│   │   ├── 教育经历
│   │   ├── 工作经历
│   │   ├── 项目经历
│   │   ├── 技能
│   │   ├── 自我评价
│   │   └── 自定义模块
│   ├── ResizableHandle
│   └── PreviewPanel      # 右侧：实时预览
│       ├── 模板渲染
│       ├── 分页指示
│       └── 缩放控制
└── PreviewDock          # 预览面板底部工具
```

**布局配置：**
- 默认布局：[20%, 32%, 48%]
- 侧边栏收起：[50%, 50%]
- 编辑聚焦：[20%, 80%]
- 预览聚焦：[20%, 80%]

---

## 核心组件

### 1. 编辑器组件

#### EditorHeader
- 显示简历标题（可编辑）
- 备份状态指示
- 语法检查按钮（带错误计数）
- 主题切换（深色/浅色）
- PDF 导出按钮

#### SidePanel
- 模块列表（可拖拽排序）
- 基础设置（字体、间距）
- 布局设置（对齐方式）
- 颜色设置（主题色）
- 可见性切换

#### EditPanel
- 根据 activeSection 渲染对应编辑组件
- 支持的编辑组件：
  - `BasicPanel` - 基本信息
  - `EducationPanel` - 教育经历
  - `ExperiencePanel` - 工作经历
  - `ProjectPanel` - 项目经历
  - `SkillPanel` - 技能
  - `SelfEvaluationPanel` - 自我评价
  - `CertificatesPanel` - 证书
  - `CustomPanel` - 自定义模块

#### PreviewPanel
- 实时渲染简历模板
- 支持分页指示
- 响应式缩放
- 自动一页检测

### 2. 模板系统

#### 模板注册表（registry.ts）
```typescript
export const TEMPLATE_REGISTRY: TemplateRegistryEntry[] = [
  { config: classicConfig, Component: ClassicTemplate },
  { config: modernConfig, Component: ModernTemplate },
  // ... 8 种模板
];

export function getTemplateComponent(layout: string) {
  return TEMPLATE_REGISTRY.find((e) => e.config.layout === layout)?.Component
    ?? ClassicTemplate;
}
```

#### 模板结构
每个模板包含：
- `config.ts` - 模板配置（颜色、间距、可用模块）
- `index.tsx` - 模板主组件
- `sections/` - 各部分组件
  - `BaseInfo.tsx` - 基本信息
  - `EducationSection.tsx` - 教育经历
  - `ExperienceSection.tsx` - 工作经历
  - `ProjectSection.tsx` - 项目经历
  - `SkillSection.tsx` - 技能
  - `SelfEvaluationSection.tsx` - 自我评价
  - `CustomSection.tsx` - 自定义模块
  - `SectionTitle.tsx` - 区块标题

### 3. UI 组件

基于 **Radix UI** 和 **HeroUI** 构建的组件库：

- **基础组件**：Button, Input, Card, Dialog, Drawer
- **表单组件**：Select, Switch, Slider, DatePicker
- **导航组件**：Tabs, Accordion, Tooltip
- **反馈组件**：Alert, Toast (Sonner)
- **布局组件**：Resizable Panels, ScrollArea

---

## 状态管理

### 1. useResumeStore

**用途：** 管理所有简历数据

**主要状态：**
```typescript
interface ResumeStore {
  resumes: Record<string, ResumeData>;  // 所有简历
  activeResumeId: string | null;        // 当前编辑的简历 ID
  activeResume: ResumeData | null;      // 当前简历数据
  
  // CRUD 操作
  createResume: (templateId: string | null, isBlank?: boolean) => string;
  deleteResume: (resume: ResumeData) => void;
  updateResume: (resumeId: string, data: Partial<ResumeData>) => void;
  setActiveResume: (resumeId: string) => void;
  
  // 数据更新
  updateBasicInfo: (data: Partial<BasicInfo>) => void;
  updateEducation: (data: Education) => void;
  updateExperience: (data: Experience) => void;
  updateProjects: (project: Project) => void;
  updateSkillContent: (skillContent: string) => void;
  updateSelfEvaluationContent: (content: string) => void;
  
  // 模块管理
  reorderSections: (newOrder: MenuSection[]) => void;
  toggleSectionVisibility: (sectionId: string) => void;
  setActiveSection: (sectionId: string) => void;
  
  // 全局设置
  updateGlobalSettings: (settings: Partial<GlobalSettings>) => void;
  setThemeColor: (color: string) => void;
  setTemplate: (templateId: string) => void;
}
```

**持久化：** 使用 `zustand/middleware` 的 `persist` 中间件，将状态保存到 localStorage

### 2. useAIConfigStore

**用途：** 管理 AI 模型配置

**主要状态：**
```typescript
interface AIConfigState {
  selectedModel: AIModelType;  // 当前选中的模型
  // 各模型的配置
  doubaoApiKey: string;
  geminiApiKey: string;
  deepseekApiKey: string;
  openaiApiKey: string;
  xiaomiApiKey: string;
  customApiKey: string;
  
  // 各模型的端点和模型 ID
  doubaoModelId: string;
  doubaoApiEndpoint: string;
  // ... 其他模型
  
  isConfigured: () => boolean;  // 验证配置是否完整
}
```

**支持的 AI 模型：**
- `doubao` - 豆包
- `deepseek` - DeepSeek
- `openai` - OpenAI
- `gemini` - Google Gemini
- `xiaomi` - 小米
- `custom` - 自定义 API

### 3. useGrammarStore

**用途：** 管理语法检查状态

---

## API 接口

### 1. AI 优化接口

**路由：** `POST /api/polish`

**功能：** 使用 AI 模型优化简历内容

**请求体：**
```typescript
{
  apiKey: string;
  model: string;
  content: string;
  modelType: AIModelType;
  apiEndpoint?: string;
}
```

**支持的优化类型：**
- 基本信息优化
- 工作经历优化
- 项目经历优化
- 教育经历优化
- 技能描述优化
- 自我评价优化

### 2. 语法检查接口

**路由：** `POST /api/grammar`

**功能：** 检查简历中的错别字和标点错误

**请求体：**
```typescript
{
  apiKey: string;
  model: string;
  content: string;
  modelType: AIModelType;
  apiEndpoint?: string;
}
```

**返回格式：**
```typescript
{
  errors: Array<{
    position: number;
    length: number;
    text: string;
    suggestion: string;
    type: "typo" | "punctuation";
  }>;
}
```

### 3. 图片代理接口

**路由：** `GET/POST /api/proxy/image`

**功能：** 代理图片请求，解决跨域问题

### 4. 简历导入接口

**路由：** `POST /api/resume-import`

**功能：** 从 JSON 文件导入简历数据

---

## 模板系统

### 8 种简历模板

| 模板 ID | 名称 | 布局 | 特点 |
|---------|------|------|------|
| `classic` | 经典风格 | 居中 | 传统简历布局 |
| `modern` | 现代风格 | 两栏 | 现代感设计 |
| `left-right` | 左右分栏 | 左右 | 经典两栏 |
| `timeline` | 时间线 | 时间轴 | 强调时间线 |
| `minimalist` | 极简风格 | 居中 | 简洁大方 |
| `elegant` | 优雅风格 | 居中 | 高端优雅 |
| `creative` | 创意风格 | 自由 | 创意展示 |
| `editorial` | 编辑风格 | 杂志 | 杂志排版 |

### 模板配置结构

```typescript
interface ResumeTemplate {
  id: string;
  name: string;
  description: string;
  thumbnail: string;
  layout: string;
  colorScheme: {
    primary: string;
    secondary: string;
    background: string;
    text: string;
  };
  spacing: {
    sectionGap: number;
    itemGap: number;
    contentPadding: number;
  };
  basic: {
    layout: "left" | "center" | "right";
  };
  availableSections?: string[];
}
```

### 模板注册流程

1. 在 `src/components/templates/` 下创建模板目录
2. 创建 `config.ts` 定义模板配置
3. 创建 `index.tsx` 实现模板组件
4. 在 `sections/` 下创建各部分组件
5. 在 `registry.ts` 中注册模板

---

## 数据流

### 简历数据流

```
User Input
    ↓
EditPanel (Form)
    ↓
useResumeStore.update*()
    ↓
Zustand Store (with Persist)
    ↓
localStorage
    ↓
PreviewPanel (render)
    ↓
PDF Export
```

### AI 功能数据流

```
User Action (Polish/Grammar)
    ↓
API Request (/api/polish or /api/grammar)
    ↓
Server Handler
    ↓
AI Model API Call
    ↓
Response Processing
    ↓
UI Update
```

### 文件同步数据流

```
User selects folder
    ↓
FileSystemDirectoryHandle
    ↓
useResumeStore.syncResumesFromDirectory()
    ↓
JSON files in folder
    ↓
Local state update
```

---

## 国际化

### 支持的语言
- `zh` - 简体中文
- `en` - English

### 国际化配置
- 配置文件：`src/i18n/config.ts`
- 翻译文件：Messages 目录
- 客户端支持：`useTranslations` hook
- 服务端支持：`getTranslations` 函数

---

## 样式系统

### Tailwind CSS 配置
- 使用 CSS 变量定义颜色
- 支持深色模式
- 自定义字体配置
- HeroUI 主题集成

### 主题系统
- 颜色主题可自定义
- 支持深色/浅色切换
- 持久化主题偏好

---

## 构建和部署

### 开发环境
```bash
pnpm install     # 安装依赖
pnpm dev         # 启动开发服务器（端口 3010）
```

### 构建生产版本
```bash
pnpm build       # 构建生产版本
pnpm start       # 启动生产服务器
```

### Docker 部署
```bash
docker compose up -d  # Docker Compose 启动
```

---

## 关键技术细节

### 同构渲染
- 使用 TanStack Start 实现前后端同构
- 服务端渲染页面
- 客户端水合交互

### 响应式设计
- 移动端适配
- 可调整大小的面板
- 动态布局切换

### PDF 导出
- 使用 Puppeteer 生成 PDF
- html2canvas 截图
- 自定义页眉页脚

### 本地存储
- File System Access API
- IndexedDB 备份
- 自动同步

---

## 总结

Magic Resume 是一个功能完整的简历编辑器应用，采用现代化的技术栈和架构设计。主要特点：

1. **同构应用** - TanStack Start 实现前后端一体化
2. **组件化** - 基于 React 的模块化组件系统
3. **状态管理** - Zustand 轻量级状态管理
4. **模板系统** - 可扩展的简历模板系统
5. **AI 集成** - 支持多种 AI 模型的语法检查和内容优化
6. **国际化** - 中英文支持
7. **响应式** - 适配多种设备

---

*文档生成时间：2026-05-10*
*项目版本：2.0.5*