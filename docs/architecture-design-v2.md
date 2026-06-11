# zhufeng-resume 架构设计文档 V2

> 对标 magic-resume-main 项目，在保持 Vue 3 + Spring Boot 主体框架不变的前提下，进行全面功能升级。

---

## 📋 目录

- [1. 项目概述](#1-项目概述)
- [2. 技术栈](#2-技术栈)
- [3. 前端架构](#3-前端架构)
- [4. 后端架构](#4-后端架构)
- [5. 数据库设计](#5-数据库设计)
- [6. 模板系统设计](#6-模板系统设计)
- [7. 编辑器设计](#7-编辑器设计)
- [8. AI 子系统设计](#8-ai-子系统设计)
- [9. 路由设计](#9-路由设计)
- [10. 国际化设计](#10-国际化设计)
- [11. 数据流设计](#11-数据流设计)

---

## 1. 项目概述

**zhufeng-resume** 是一个全栈在线简历编辑器，提供多模板选择、实时预览、AI 辅助、PDF 导出等功能。

### 核心能力

- 多套精美简历模板（对标 magic-resume 的8套）
- 三栏可拖拽编辑器，实时预览
- AI 语法检查 + AI 内容润色（多模型支持）
- PDF 导出
- 用户系统（登录/注册）
- 国际化（中/英文）
- 深色/浅色主题

---

## 2. 技术栈

### 前端

| 技术 | 用途 | 状态 |
|------|------|------|
| **Vue 3** | UI 框架 | 已有 |
| **Vite** | 构建工具 | 已有 |
| **Vue Router 4** | 路由管理 | 已有 |
| **Pinia** | 状态管理 | 已有 |
| **Element Plus** | UI 组件库 | 已有 |
| **Tailwind CSS** | 原子化样式框架 | **新增** |
| **Tiptap** | 富文本编辑器 | **新增** |
| **html2canvas + jspdf** | PDF 导出 | 已有 |
| **i18n** | 国际化 | **新增** |

### 后端

| 技术 | 用途 | 状态 |
|------|------|------|
| **Spring Boot 3** | 后端框架 | 已有 |
| **MyBatis / MyBatis-Plus** | ORM | 已有 |
| **MySQL** | 数据库 | 已有 |
| **JWT** | 认证 | 已有 |
| **HTTP Client** | AI API 调用 | **新增** |

---

## 3. 前端架构

### 3.1 目录结构（目标状态）

```
zhufeng-resume-frontend/
├── public/
│   ├── fonts/                    # 中文字体（新增）
│   ├── template-snapshots/       # 模板快照（新增）
│   └── favicon.ico
├── src/
│   ├── api/                      # API 请求层
│   │   ├── request.js           # axios 封装（已有）
│   │   ├── auth.js              # 登录注册（已有）
│   │   ├── user.js              # 用户信息（已有）
│   │   ├── resume.js            # 简历 CRUD（已有）
│   │   ├── template.js          # 模板接口（已有）
│   │   └── ai.js                # AI 接口（已有，需扩展）
│   │
│   ├── assets/
│   │   ├── styles/
│   │   │   ├── main.css         # Tailwind入口（重写）
│   │   │   ├── global.scss      # 全局 SCSS（保留部分）
│   │   │   └── tailwind.css     # Tailwind 指令（新增）
│   │   └── images/
│   │
│   ├── components/
│   │   ├── common/              # 通用组件
│   │   │   └── IconView.vue     # 图标组件
│   │   │
│   │   ├── editor/              # 编辑器组件 ★核心
│   │   │   ├── EditorLayout.vue        # 编辑器主布局（重写）
│   │   │   ├── EditorHeader.vue        # 顶部工具栏（重写）
│   │   │   ├── SidePanel.vue           # 左侧：模块选择+设置（重写）
│   │   │   ├── EditPanel.vue           # 中间：表单编辑（重写）
│   │   │   ├── PreviewPanel.vue         # 右侧：实时预览（重写）
│   │   │   ├── forms/                   # 各模块编辑表单
│   │   │   │   ├── BasicInfoForm.vue    # 基本信息（已有）
│   │   │   │   ├── EducationForm.vue    # 教育经历（已有）
│   │   │   │   ├── ExperienceForm.vue   # 工作经历（已有）
│   │   │   │   ├── ProjectsForm.vue     # 项目经历（已有）
│   │   │   │   ├── SkillsForm.vue       # 技能（已有）
│   │   │   │   └── SelfEvalForm.vue     # 自我评价（已有）
│   │   │   ├── grammar/                 # 语法检查组件（新增）
│   │   │   │   └── GrammarCheckDrawer.vue
│   │   │   └── settings/                # 布局/颜色设置（新增）
│   │   │       ├── LayoutSettings.vue
│   │   │       └── ThemeSettings.vue
│   │   │
│   │   ├── templates/           # 简历模板 ★核心新增
│   │   │   ├── registry.js              # 模板注册表
│   │   │   ├── classic/                 # 经典风格
│   │   │   │   ├── ClassicTemplate.vue
│   │   │   │   └── config.js
│   │   │   ├── modern/                  # 现代/两栏风格
│   │   │   │   ├── ModernTemplate.vue
│   │   │   │   └── config.js
│   │   │   ├── left-right/              # 左右分栏风格
│   │   │   │   ├── LeftRightTemplate.vue
│   │   │   │   └── config.js
│   │   │   ├── timeline/                # 时间线风格
│   │   │   │   ├── TimelineTemplate.vue
│   │   │   │   └── config.js
│   │   │   ├── minimalist/              # 极简风格
│   │   │   │   ├── MinimalistTemplate.vue
│   │   │   │   └── config.js
│   │   │   ├── elegant/                 # 优雅风格
│   │   │   │   ├── ElegantTemplate.vue
│   │   │   │   └── config.js
│   │   │   ├── creative/                # 创意风格
│   │   │   │   ├── CreativeTemplate.vue
│   │   │   │   └── config.js
│   │   │   ├── editorial/               # 编辑风格/杂志风
│   │   │   │   ├── EditorialTemplate.vue
│   │   │   │   └── config.js
│   │   │   └── shared/                  # 共享区块组件
│   │   │       ├── SectionTitle.vue
│   │   │       ├── SkillSection.vue
│   │   │       └── CertificatesSection.vue
│   │   │
│   │   └── home/                # 首页组件（新增）
│   │       ├── LandingHeader.vue
│   │       ├── HeroSection.vue
│   │       ├── FeaturesSection.vue
│   │       ├── FAQSection.vue
│   │       └── CTASection.vue
│   │
│   ├── composables/             # 组合式函数（新增）
│   │   ├── useResumeStore.js    # 简历状态
│   │   ├── useAI.js             # AI 功能
│   │   ├── useGrammarCheck.js   # 语法检查
│   │   └── useExport.js         # 导出功能
│   │
│   ├── config/                  # 配置文件（新增）
│   │   ├── modules.js           # 模块配置
│   │   ├── constants.js         # 常量
│   │   └── ai.js                # AI 模型配置
│   │
│   ├── i18n/                    # 国际化（新增）
│   │   ├── index.js
│   │   └── locales/
│   │       ├── zh-CN.json
│   │       └── en-US.json
│   │
│   ├── layouts/
│   │   └── MainLayout.vue       # 主布局（已有，需改造）
│   │
│   ├── pages/
│   │   ├── Home.vue             # 首页/着陆页（已有，需改造）
│   │   ├── Login.vue            # 登录（已有，保留）
│   │   ├── Register.vue         # 注册（已有，保留）
│   │   ├── Dashboard.vue        # 仪表盘/简历列表（已有，需改造）
│   │   ├── Templates.vue        # 模板选择页（已有，需改造）
│   │   ├── Editor.vue           # 编辑器（已有，需重写）
│   │   ├── AiGenerate.vue       # AI 生成（已有，需改造→AI设置）
│   │   └── Settings.vue         # 设置页（新增）
│   │
│   ├── router/
│   │   └── index.js             # 路由配置（已有，需扩展）
│   │
│   ├── stores/                  # Pinia Store
│   │   ├── user.js              # 用户状态（已有）
│   │   ├── resume.js            # 简历状态（新增）
│   │   ├── aiConfig.js          # AI 配置（新增）
│   │   └── theme.js             # 主题状态（新增）
│   │
│   ├── utils/
│   │   ├── auth.js              # Token工具（已有）
│   │   ├── exportPdf.js         # PDF导出（已有，需增强）
│   │   └── index.js             # 通用工具
│   │
│   ├── App.vue                  # 根组件（已有）
│   └── main.js                  # 入口（已有，需改造）
│
├── index.html
├── vite.config.js
├── tailwind.config.js           # Tailwind 配置（新增）
├── package.json
└── postcss.config.js            # PostCSS 配置（新增）
```

### 3.2 关键架构决策

#### 模板系统设计

采用 **注册表模式**（Registry Pattern），参考 magic-resume-main：

```
模板目录                          注册表                    消费者
┌──────────────┐              ┌──────────────┐        ┌──────────────┐
│ classic/     │──┐           │              │        │              │
│  config.js   │  │    注册    │  TEMPLATE    │  查找  │  Editor.vue  │
│  *.vue       │──┼─────────►│  REGISTRY    │──────►│  Templates   │
│              │  │           │              │        │  Preview     │
│ modern/      │──┘           │  8 个模板    │        │              │
│  config.js   │              │  config+组件 │        └──────────────┘
│  *.vue       │              └──────────────┘
└──────────────┘
```

每个模板包含：
- `config.js` — 模板元数据（id, name, colors, spacing, layout）
- `TemplateName.vue` — 模板总组件，接收 `resumeData` + `globalSettings`
- `sections/*.vue` — 各区块组件（BaseInfo, Education, Experience等）

#### 编辑器三栏布局

```
┌─────────────────────────────────────────────────────────────┐
│                      EditorHeader                           │
│  [返回] [标题]           [语法检查(3)] [主题] [导出]         │
├──────────┬──────────────────────┬───────────────────────────┤
│SidePanel │     EditPanel        │     PreviewPanel          │
│(可收起)  │    (编辑表单)         │    (实时预览)              │
│          │                      │                           │
│▪ 基本信息 │  BasicInfoForm       │  ┌─────────────────┐     │
│▪ 教育经历 │  EducationForm       │  │  简历预览渲染     │     │
│▪ 工作经历 │  ExperienceForm      │  │                  │     │
│▪ 项目经历 │  ProjectsForm        │  │  [第 1/2 页]     │     │
│▪ 技能特长 │  SkillsForm          │  │  [缩放:100%]     │     │
│▪ 自我评价 │  SelfEvalForm        │  └─────────────────┘     │
│          │                      │                           │
│────设置──│                      │                           │
│▪ 全局设置 │                      │                           │
│▪ 布局设置 │                      │                           │
├──────────┴──────────────────────┴───────────────────────────┤
│                    PreviewDock                              │
│              [上一页] [页码] [下一页] [缩放-] [缩放+]        │
└─────────────────────────────────────────────────────────────┘
```

#### 状态管理方案

```
stores/
├── user.js          # 用户认证（已有）
│   ├── token
│   ├── userInfo
│   └── login/logout
│
├── resume.js        # 简历数据（核心新增）
│   ├── resumes: Map<id, ResumeData>
│   ├── activeResumeId
│   ├── create/update/delete
│   ├── updateBasicInfo/updateEducation/...
│   └── reorderSections/toggleVisibility
│
├── aiConfig.js      # AI配置（新增）
│   ├── selectedModel
│   ├── apiKeys: {}
│   └── modelConfigs: {}
│
└── theme.js         # 主题设置（新增）
    ├── darkMode
    ├── locale
    └── toggleTheme/setLocale
```

---

## 4. 后端架构

### 4.1 分层结构

```
zhufeng-resume-backend/
├── controller/
│   ├── AuthController.java        # 认证（已有）
│   ├── UserController.java        # 用户（已有）
│   ├── ResumeController.java      # 简历CRUD（已有，需扩展）
│   ├── TemplateController.java    # 模板（已有，需扩展）
│   └── AiController.java          # AI（已有，需扩展）
│
├── service/
│   ├── AuthService.java           # （已有）
│   ├── UserService.java           # （已有）
│   ├── ResumeService.java         # （已有，需扩展）
│   ├── TemplateService.java       # （已有，需扩展）
│   └── AiService.java             # （已有，需扩展）
│
├── mapper/
│   ├── UserMapper.java            # （已有）
│   ├── ResumeMapper.java          # （已有，需扩展）
│   └── TemplateMapper.java        # （已有）
│
├── entity/
│   ├── User.java                  # （已有）
│   ├── Resume.java                # （已有，需扩展）
│   └── Template.java              # （已有）
│
├── dto/
│   ├── LoginDTO.java              # （已有）
│   ├── RegisterDTO.java           # （已有）
│   ├── CreateResumeDTO.java       # （已有）
│   ├── UpdateResumeDTO.java       # （已有，需扩展）
│   ├── AiGenerateDTO.java         # （已有）
│   ├── AiGrammarDTO.java          # （新增）
│   └── AiPolishDTO.java           # （新增）
│
├── vo/
│   ├── UserVO.java                # （已有）
│   ├── LoginVO.java               # （已有）
│   ├── ResumeVO.java              # （已有，需扩展）
│   ├── ResumeListVO.java          # （已有）
│   ├── TemplateVO.java            # （已有，需扩展）
│   ├── AiGenerationVO.java        # （已有）
│   ├── AiGrammarVO.java           # （新增）
│   └── AiPolishVO.java            # （新增）
│
└── common/
    ├── JwtUtil.java               # （已有）
    ├── UserContext.java           # （已有）
    ├── Result.java                # （已有）
    ├── ResultCode.java            # （已有）
    ├── BusinessException.java     # （已有）
    └── GlobalExceptionHandler.java # （已有）
```

### 4.2 新增/扩展的 API 接口

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | /api/templates | 获取所有模板（含缩略图） | 扩展 |
| GET | /api/templates/:id | 获取模板详情 | 扩展 |
| GET | /api/resumes | 简历列表 | 已有 |
| POST | /api/resumes | 创建简历（指定模板） | 已有 |
| GET | /api/resumes/:id | 获取简历详情 | 已有 |
| PUT | /api/resumes/:id | 更新简历 | 扩展 |
| DELETE | /api/resumes/:id | 删除简历 | 已有 |
| POST | /api/ai/generate | AI 生成简历 | 已有 |
| POST | /api/ai/grammar | AI 语法检查 | **新增** |
| POST | /api/ai/polish | AI 内容润色 | **新增** |
| GET | /api/ai/models | 获取可用 AI 模型列表 | **新增** |

### 4.3 AI 子系统后端设计

```
用户请求
    │
    ▼
AiController
    │
    ├── POST /api/ai/grammar  ──► AiService.checkGrammar()
    │                                  │
    │                                  ├── 构建 Prompt
    │                                  ├── 调用 AI API（OpenAI兼容格式）
    │                                  ├── 解析返回的语法错误列表
    │                                  └── 返回 AiGrammarVO
    │
    └── POST /api/ai/polish   ──► AiService.polishContent()
                                       │
                                       ├── 构建润色 Prompt
                                       ├── 调用 AI API
                                       ├── 解析润色后内容
                                       └── 返回 AiPolishVO

支持的 AI 模型（通过 OpenAI 兼容 API 统一调用）：
┌──────────┬──────────────────────────┬────────────────────┐
│ 模型名称  │ API Endpoint             │ 模型 ID           │
├──────────┼──────────────────────────┼────────────────────┤
│ doubao   │ https://ark.cn-beijing...│ doubao-pro-32k    │
│ deepseek │ https://api.deepseek.com │ deepseek-chat     │
│ openai   │ https://api.openai.com   │ gpt-4o            │
│ gemini   │ https://generativelan... │ gemini-pro        │
│ custom   │ 用户自定义                │ 用户自定义         │
└──────────┴──────────────────────────┴────────────────────┘
```

---

## 5. 数据库设计

### 5.1 Resume 表扩展

在现有字段基础上增加：

```sql
ALTER TABLE resume ADD COLUMN template_id VARCHAR(50);
ALTER TABLE resume ADD COLUMN global_settings JSON;
ALTER TABLE resume ADD COLUMN menu_sections JSON;
ALTER TABLE resume ADD COLUMN custom_data JSON;
ALTER TABLE resume ADD COLUMN certificates JSON;
```

`resume_data` 字段（大 JSON）结构参考：

```json
{
  "basic": {
    "name": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "title": "前端工程师",
    "birthDate": "1998-06",
    "location": "北京",
    "photo": "base64...",
    "photoConfig": { "borderRadius": "full", "visible": true },
    "customFields": [],
    "fieldOrder": []
  },
  "education": [
    {
      "id": "uuid",
      "school": "北京大学",
      "major": "计算机科学",
      "degree": "本科",
      "startDate": "2016-09",
      "endDate": "2020-06",
      "gpa": "3.8",
      "description": "主修课程...",
      "visible": true
    }
  ],
  "experience": [
    {
      "id": "uuid",
      "company": "某科技公司",
      "position": "前端开发",
      "date": "2020-07 ~ 2023-06",
      "details": "<p>负责...</p>",
      "visible": true
    }
  ],
  "projects": [
    {
      "id": "uuid",
      "name": "电商平台",
      "role": "前端负责人",
      "date": "2022-01 ~ 2022-06",
      "description": "技术栈...",
      "link": "https://...",
      "linkLabel": "项目链接",
      "visible": true
    }
  ],
  "skillContent": "<p>技能描述富文本</p>",
  "selfEvaluationContent": "<p>自我评价富文本</p>",
  "customData": {
    "custom-section-1": [
      { "id": "uuid", "title": "...", "subtitle": "...", "dateRange": "...", "description": "..." }
    ]
  },
  "certificates": [
    { "id": "uuid", "url": "data:image/png;base64,...", "width": 50 }
  ]
}
```

---

## 6. 模板系统设计

### 6.1 模板配置接口

```javascript
// config.js
export const templateConfig = {
  id: "modern",
  name: "两栏布局",           // 显示名称
  nameEn: "Two Column",       // 英文名
  description: "经典两栏，突出个人特色",
  thumbnail: "modern",        // 缩略图key
  layout: "modern",           // 布局标识
  colorScheme: {
    primary: "#000000",
    secondary: "#6b7280",
    background: "#ffffff",
    text: "#212529"
  },
  spacing: {
    sectionGap: 8,            // 模块间距
    itemGap: 4,               // 条目间距
    contentPadding: 0         // 内边距
  },
  basic: {
    layout: "center"          // 信息对齐: left | center | right
  },
  availableSections: [        // 可用模块
    "skills", "experience", "projects",
    "education", "selfEvaluation", "certificates"
  ]
}
```

### 6.2 8 种模板对比

| 模板 ID | 名称 | 布局特点 | 适用场景 |
|---------|------|---------|---------|
| classic | 经典风格 | 居中、传统 | 通用 |
| modern | 两栏布局 | 左基本信息+右内容 | 技术岗 |
| left-right | 左右分栏 | 经典两栏、背景色区分 | 通用 |
| timeline | 时间线 | 时间轴贯穿 | 工作经历丰富 |
| minimalist | 极简风格 | 大量留白、简洁 | 设计/创意岗 |
| elegant | 优雅风格 | 高端排版、衬线字体 | 管理/金融 |
| creative | 创意风格 | 自由布局、强视觉 | 设计师 |
| editorial | 编辑风格 | 杂志排版、双栏 | 内容创作 |

---

## 7. 编辑器设计

### 7.1 编辑器核心数据流

```
SidePanel                     EditPanel                    PreviewPanel
(模块选择+设置)               (表单编辑)                    (模板渲染)
     │                            │                            │
     │  点击模块                   │                            │
     ├───activeSection──────────►│                            │
     │                            │  数据变化                  │
     │                            ├───updateResume───────────►│
     │                            │                            │ 实时渲染
     │                            │◄───resumeData─────────────│
     │                            │                            │
     │  全局设置变更               │                            │
     ├───updateGlobalSettings───►├───globalSettings──────────►│
     │                            │                            │ 重新渲染
     │◄───themeColor──────────────────────────────────────────│
     │                            │                            │
```

### 7.2 面板折叠策略

```
状态1: 默认 — [SidePanel 20% | EditPanel 35% | PreviewPanel 45%]
状态2: 侧栏收起 — [EditPanel 40% | PreviewPanel 60%]
状态3: 全屏编辑 — [EditPanel 100% | PreviewPanel 0%]
状态4: 全屏预览 — [SidePanel 0% | EditPanel 0% | PreviewPanel 100%]
```

---

## 8. AI 子系统设计

### 8.1 功能矩阵

```
                  语法检查 (Grammar)        内容润色 (Polish)
                 ┌─────────────────┐    ┌─────────────────┐
  基本信息        │ 错别字、标点     │    │ 优化表达、更专业  │
  教育经历        │ 错别字           │    │ 优化描述          │
  工作经历        │ 错别字、标点     │    │ STAR法则重写      │
  项目经历        │ 错别字           │    │ 量化成果          │
  技能特长        │ —               │    │ 归类整理          │
  自我评价        │ 错别字           │    │ 更专业、更真诚    │
                 └─────────────────┘    └─────────────────┘
```

### 8.2 语法检查 Prompt 模板

```
你是中文简历语法检查专家。请检查以下简历内容中的错别字、标点错误和语法问题。

对于每个错误，用 JSON 数组返回，格式：
[{"position": 10, "length": 3, "text": "原文", "suggestion": "建议修改", "type": "typo"}]

注意：
- position 为错误文字在原文中的起始位置
- type 为 "typo"（错别字）或 "punctuation"（标点）
- 如果没有错误，返回 []
- 只返回 JSON，不要其他文字

原文：{content}
```

### 8.3 内容润色 Prompt 模板

```
你是专业简历撰写顾问。请优化以下简历内容，使其更专业、更有吸引力。

要求：
1. 使用 STAR 法则（情境、任务、行动、结果）优化工作/项目经历
2. 量化成果（如"提升30%"、"管理5人团队"）
3. 使用行业关键词
4. 保持原有语言风格和真实性
5. 直接返回优化后的文本，不要额外解释

原文：{content}
```

---

## 9. 路由设计

```
/                          → Home.vue (着陆页)
/login                     → Login.vue
/register                  → Register.vue

(需要认证)
/dashboard                 → Dashboard.vue (简历列表)
/dashboard/templates       → Templates.vue (模板市场)
/dashboard/ai              → AiSettings.vue (AI配置)
/dashboard/settings        → Settings.vue (设置)
/editor/:id                → Editor.vue (编辑器)
/editor/:id/preview        → Preview.vue (全屏预览)
```

---

## 10. 国际化设计

### 10.1 翻译 Key 设计

```json
// zh-CN.json
{
  "common": {
    "save": "保存",
    "cancel": "取消",
    "delete": "删除",
    "export": "导出"
  },
  "editor": {
    "basicInfo": "基本信息",
    "education": "教育经历",
    "experience": "工作经历",
    "projects": "项目经历",
    "skills": "技能特长",
    "selfEvaluation": "自我评价"
  },
  "template": {
    "classic": "经典风格",
    "modern": "两栏布局",
    "leftRight": "左右分栏",
    "timeline": "时间线",
    "minimalist": "极简风格",
    "elegant": "优雅风格",
    "creative": "创意风格",
    "editorial": "编辑风格"
  }
}
```

---

## 11. 数据流设计

### 11.1 简历生命周期

```
创建简历
    │
    ├── 1. 在 Templates 页选择模板
    ├── 2. 调用 POST /api/resumes { templateId }
    ├── 3. 后端创建简历记录，填充默认数据
    ├── 4. 返回简历 ID，跳转到 /editor/:id
    │
编辑简历
    │
    ├── 1. 在 Editor 页编辑数据
    ├── 2. 每次修改触发 Pinia store 更新
    ├── 3. Preview 自动重新渲染
    ├── 4. 保存按钮触发 PUT /api/resumes/:id
    │
导出简历
    │
    ├── 1. 点击导出按钮
    ├── 2. 渲染当前模板为 DOM 节点
    ├── 3. html2canvas → png
    ├── 4. jspdf → pdf 下载
```

### 11.2 AI 功能数据流

```
AI 语法检查:
  选中文本 → 发送到 /api/ai/grammar → 后端调用 AI → 返回错误列表
  → GrammarCheckDrawer 展示 → 用户逐个确认修正

AI 内容润色:
  选中文本 + 上下文类型 → 发送到 /api/ai/polish → 后端调用 AI
  → 返回润色后文本 → 替换编辑器内容
```
