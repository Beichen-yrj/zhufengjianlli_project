# zhufeng-resume 分步修改方案

> 对标 magic-resume-main，共 **6 个阶段**，按依赖关系排序，每步改动范围明确、可独立验证。

---

## 修改总览

```
阶段1: 基础设施      阶段2: 模板系统      阶段3: 编辑器重构
  ┌──────────┐       ┌──────────┐       ┌──────────┐
  │Tailwind   │  ──►  │8套模板   │  ──►  │三栏布局   │
  │Tiptap    │       │注册表    │       │可拖拽面板 │
  │国际化     │       │预览增强  │       │实时预览   │
  └──────────┘       └──────────┘       └──────────┘
       │                   │                   │
       └───────────────────┴───────────────────┘
                           │
       ┌───────────────────┴───────────────────┐
       ▼                   ▼                   ▼
 阶段4: AI强化       阶段5: 首页改造      阶段6: 后端扩展
  ┌──────────┐       ┌──────────┐       ┌──────────┐
  │语法检查   │       │Landing页  │       │API扩展   │
  │内容润色   │       │功能区     │       │数据迁移   │
  │多模型     │       │FAQ       │       │AI代理     │
  └──────────┘       └──────────┘       └──────────┘
```

---

## 阶段 1：前端基础设施升级

> 目标：引入 Tailwind CSS、Tiptap、国际化，为后续开发打地基。
> 风险：低，不影响现有功能。

### 1.1 安装依赖

```bash
cd zhufeng-resume-frontend
npm install -D tailwindcss @tailwindcss/vite postcss autoprefixer
npm install @tiptap/vue-3 @tiptap/starter-kit @tiptap/extension-underline @tiptap/extension-placeholder
npm install vue-i18n@9
npm install @vueuse/core
```

### 1.2 配置 Tailwind CSS

**新增文件：**
- `tailwind.config.js`
- `postcss.config.js`
- 修改 `vite.config.js` — 添加 Tailwind 插件
- 修改 `src/assets/styles/main.css` — 添加 `@tailwind` 指令
- 修改 `src/main.js` — 引入新样式入口

**改动文件：**
```
新增: tailwind.config.js
新增: postcss.config.js
修改: vite.config.js       (添加 tailwindcss 插件)
修改: src/main.js          (导入 main.css)
新增: src/assets/styles/main.css   (替代 style.css)
删除: src/style.css        (整合到 main.css)
```

### 1.3 配置国际化

**新增文件：**
```
新增: src/i18n/index.js             (Vue I18n 实例)
新增: src/i18n/locales/zh-CN.json   (中文翻译)
新增: src/i18n/locales/en-US.json   (英文翻译)
```

**改动文件：**
```
修改: src/main.js    (注册 i18n 插件)
修改: src/App.vue    (添加语言切换入口)
```

### 1.4 目录准备

创建后续阶段需要的空目录和占位文件：

```
新增目录: src/composables/
新增目录: src/config/
新增目录: src/components/templates/
新增目录: src/components/templates/shared/
新增目录: src/components/editor/grammar/
新增目录: src/components/editor/settings/
新增目录: src/stores/resume.js      (占位)
新增目录: src/stores/aiConfig.js    (占位)
新增目录: src/stores/theme.js       (占位)
```

### 1.5 验证

- `npm run dev` 正常启动
- Tailwind class 生效（在 App.vue 中写一个测试元素）
- Tiptap 编辑器可正常渲染
- 中英文切换正常

---

## 阶段 2：模板系统建设

> 目标：建立8套模板组件 + 注册表模式，模板页能预览所有模板。
> 风险：中，涉及大量新组件但独立于编辑器。

### 2.1 定义核心类型

**新增文件：**
```
新增: src/config/constants.js     (主题色、模板ID等常量)
新增: src/config/modules.js       (简历模块定义)
```

`modules.js` 内容：
```js
export const STANDARD_MODULES = {
  skills:       { id: 'skills', titleKey: 'skills', icon: '⚡' },
  experience:   { id: 'experience', titleKey: 'experience', icon: '💼' },
  projects:     { id: 'projects', titleKey: 'projects', icon: '🚀' },
  education:    { id: 'education', titleKey: 'education', icon: '🎓' },
  selfEvaluation: { id: 'selfEvaluation', titleKey: 'selfEvaluation', icon: '💬' },
  certificates: { id: 'certificates', titleKey: 'certificates', icon: '🏆' },
}
```

### 2.2 构建模板注册表

**新增文件：**
```
新增: src/components/templates/registry.js   (模板注册表)
```

参考 magic-resume-main 的 registry.ts：
```js
import { classicConfig } from './classic/config'
import ClassicTemplate from './classic/ClassicTemplate.vue'
// ... 共8个

export const TEMPLATE_REGISTRY = [
  { config: classicConfig, component: ClassicTemplate },
  { config: modernConfig, component: ModernTemplate },
  // ...
]

export function getTemplateComponent(layout) { ... }
export const DEFAULT_TEMPLATES = TEMPLATE_REGISTRY.map(e => e.config)
```

### 2.3 构建8套模板组件

每个模板目录结构相同：

```
components/templates/classic/
├── config.js                # 模板配置
├── ClassicTemplate.vue      # 主组件
└── sections/                # 区块组件
    ├── BaseInfoSection.vue
    ├── EducationSection.vue
    ├── ExperienceSection.vue
    ├── ProjectSection.vue
    ├── SkillSection.vue
    └── SelfEvalSection.vue
```

**第一批（先做3套核心模板）：**
- `classic/` — 经典风格（纯文字、居中、最简单，先做这个验证流程）
- `modern/` — 两栏布局（对标 magic-resume 的 modern）
- `timeline/` — 时间线风格

**第二批（再做5套）：**
- `left-right/` — 左右分栏
- `minimalist/` — 极简
- `elegant/` — 优雅
- `creative/` — 创意
- `editorial/` — 编辑风格

每个模板主组件的 Props 接口：
```js
// props
{
  resumeData: Object,   // 简历数据
  template: Object,     // 当前模板 config
  globalSettings: Object // 全局设置（主题色、字号等）
}
```

**新增文件清单（第一批）：**
```
新增: components/templates/classic/config.js
新增: components/templates/classic/ClassicTemplate.vue
新增: components/templates/classic/sections/BaseInfoSection.vue
新增: components/templates/classic/sections/EducationSection.vue
新增: components/templates/classic/sections/ExperienceSection.vue
新增: components/templates/classic/sections/ProjectSection.vue
新增: components/templates/classic/sections/SkillSection.vue
新增: components/templates/classic/sections/SelfEvalSection.vue

(modern/ 和 timeline/ 同理，共约 18 个文件)

新增: components/templates/shared/SectionWrapper.vue   (通用外层)
新增: components/templates/shared/SectionTitle.vue     (通用标题)
```

### 2.4 改造模板选择页

**改动文件：**
```
修改: src/pages/Templates.vue
    - 使用 registry 获取模板列表
    - 卡片展示缩略图 + 名称
    - 鼠标悬停效果
    - 点击创建简历（选择模板）

修改: src/components/editor/ResumePreview.vue
    - 改为根据 templateId 动态加载对应模板组件
    - 不再硬编码渲染逻辑
```

### 2.5 ResumPreview 改为模板渲染器

```vue
<!-- ResumePreview.vue 改造后 -->
<template>
  <component
    :is="templateComponent"
    :resumeData="resumeData"
    :template="templateConfig"
    :globalSettings="globalSettings"
  />
</template>

<script setup>
import { computed } from 'vue'
import { getTemplateComponent } from '../templates/registry'

const templateComponent = computed(() =>
  getTemplateComponent(props.templateConfig?.layout)
)
</script>
```

### 2.6 验证

- 模板页展示8套模板卡片
- 点击某个模板可创建简历
- ResumePreview 根据 templateId 正确渲染对应模板

---

## 阶段 3：编辑器重构

> 目标：从两栏 tabs 布局升级为三栏可拖拽面板布局，支持模块排序/显隐、全局设置。
> 风险：中高，核心页面改动大，需保证数据兼容。

### 3.1 新增 resume Store

**新增文件：**
```
新增: src/stores/resume.js
```

核心功能：
```js
export const useResumeStore = defineStore('resume', {
  state: () => ({
    resumes: {},           // id → ResumeData
    activeResumeId: null
  }),
  getters: {
    activeResume: (state) => state.resumes[state.activeResumeId]
  },
  actions: {
    createResume(templateId) { /* 调用API创建 + 本地添加 */ },
    updateResume(id, data) { /* 本地更新 */ },
    setActiveResume(id) { /* 设置当前 */ },
    updateBasicInfo(data) { /* 局部更新 */ },
    updateEducation(data) { /* 局部更新 */ },
    updateExperience(data) { /* 局部更新 */ },
    updateProjects(data) { /* 局部更新 */ },
    updateSkillContent(content) { /* 局部更新 */ },
    updateSelfEvalContent(content) { /* 局部更新 */ },
    updateGlobalSettings(settings) { /* 全局设置 */ },
    reorderSections(newOrder) { /* 模块排序 */ },
    toggleSectionVisibility(sectionId) { /* 模块显隐 */ },
  }
})
```

### 3.2 搭建三栏布局

**新增文件：**
```
新增: src/components/editor/EditorLayout.vue
```

使用 CSS 实现可拖拽分栏（不需要额外库，用 `@vueuse/core` 的 `useDraggable` 或手写 resize）：

```
┌──────────────────────────────────────────────────┐
│              EditorHeader                        │
├───────┬────────────────────┬─────────────────────┤
│ Side  │                    │                     │
│ Panel │     EditPanel      │    PreviewPanel     │
│       │                    │                     │
│ ←拖拽→│     ←拖拽→        │                     │
└───────┴────────────────────┴─────────────────────┘
```

**实现方案：** 用 CSS `resize` 属性或手动 mousedown 事件实现分隔条拖拽。注意 A4 预览区的缩放。

### 3.3 构建各面板组件

**新增/重写文件：**
```
新增: src/components/editor/EditorHeader.vue    (顶部工具栏)
    - 返回按钮
    - 简历标题（可编辑）
    - 保存状态指示
    - 语法检查按钮（带计数badge）
    - 主题切换
    - 导出按钮

重写: src/components/editor/SidePanel.vue       (左侧面板)
    - 模块列表（可拖拽排序、可见性开关）
    - 全局设置区（主题色、字体、字号、间距）
    - 布局设置（基本信息对齐方式）

保留: src/components/editor/EditPanel.vue       (中间表单)
    - 根据 activeSection 渲染对应 Form
    - 原 BasicInfoForm / EducationForm 等保留，只包裹一层

重写: src/components/editor/PreviewPanel.vue    (右侧预览)
    - 缩放控制（滑块）
    - 分页指示（A4 分页检测）
    - 内嵌 ResumePreview（模板渲染器）
```

### 3.4 重构 Editor.vue

**改动文件：**
```
重写: src/pages/Editor.vue
```

新的 Editor.vue 结构：
```vue
<template>
  <div class="h-screen flex flex-col bg-gray-100">
    <EditorHeader />
    <EditorLayout>
      <template #side>
        <SidePanel />
      </template>
      <template #edit>
        <EditPanel />
      </template>
      <template #preview>
        <PreviewPanel />
      </template>
    </EditorLayout>
  </div>
</template>
```

原有 tabs + 左右两栏的布局完全替换。

### 3.5 改造 Dashboard.vue

**改动文件：**
```
修改: src/pages/Dashboard.vue
    - 改为 resumes store 驱动
    - 显示简历卡片列表（模仿 magic-resume 卡片样式）
    - 创建简历弹窗（选择模板）
    - 删除确认
```

### 3.6 验证

- 编辑器三栏正常显示，分隔条可拖拽
- 左侧模块点击切换，右侧实时预览
- 全局设置（颜色、字号）生效
- 保存后刷新数据仍在

---

## 阶段 4：AI 功能强化

> 目标：在现有 AI 生成基础上，增加语法检查和内容润色功能。
> 风险：中，依赖后端 API 扩展，可在后端完成后端到端验证。

### 4.1 新增 AI 配置 Store

**新增文件：**
```
新增: src/stores/aiConfig.js
```

```js
export const useAiConfigStore = defineStore('aiConfig', {
  state: () => ({
    selectedModel: 'doubao',
    models: {
      doubao:  { apiKey: '', modelId: 'doubao-pro-32k', endpoint: 'https://ark.cn-beijing...' },
      deepseek: { apiKey: '', modelId: 'deepseek-chat', endpoint: 'https://api.deepseek.com' },
      openai:   { apiKey: '', modelId: 'gpt-4o', endpoint: 'https://api.openai.com' },
      custom:   { apiKey: '', modelId: '', endpoint: '' }
    }
  }),
  getters: {
    currentModelConfig: (state) => state.models[state.selectedModel],
    isConfigured: (state) => !!state.models[state.selectedModel].apiKey
  }
})
```

### 4.2 语法检查功能

**前端新增：**
```
新增: src/composables/useGrammarCheck.js
    - checkGrammar(content) → 调用 API → 返回错误列表
    - highlightErrors(errors) → 在编辑区高亮标记
    - fixError(error, newText) → 替换文本

新增: src/components/editor/grammar/GrammarCheckDrawer.vue
    - 抽屉/侧边面板，列出所有语法错误
    - 每条错误显示：原文 → 建议
    - 一键应用所有修正 / 逐条确认

修改: src/api/ai.js
    - 新增 checkGrammar(content) 请求
    - 新增 polishContent(content, type) 请求
```

**后端新增：**
```
新增: src/main/java/.../dto/AiGrammarDTO.java
新增: src/main/java/.../dto/AiPolishDTO.java
新增: src/main/java/.../vo/AiGrammarVO.java
新增: src/main/java/.../vo/AiPolishVO.java
修改: src/main/java/.../controller/AiController.java   (新增两个接口)
修改: src/main/java/.../service/AiService.java          (新增两个方法)
```

### 4.3 AI 设置页

**改动文件：**
```
改造成: src/pages/AiGenerate.vue → 重命名为 AiSettings.vue
    - 保留 AI 生成功能（tab切换）
    - 新增模型选择下拉框
    - 新增 API Key 输入
    - 新增端点配置
    - 测试连接按钮
```

### 4.4 在编辑器中集成 AI 按钮

在 EditPanel 中每个编辑表单旁增加：
- "语法检查" 按钮 → 触发当前字段语法检查
- "AI润色" 按钮 → 润色当前字段内容

```vue
<!-- 在 EducationForm.vue 中 -->
<el-input v-model="item.description" type="textarea" />
<div class="ai-actions">
  <el-button size="small" @click="checkGrammar(item.description)">
    🔍 语法检查
  </el-button>
  <el-button size="small" @click="polishContent(item.description, 'education')">
    ✨ AI润色
  </el-button>
</div>
```

### 4.5 验证

- AI 设置页能配置多个模型
- 语法检查返回错误列表并高亮
- 内容润色返回优化文本
- 端到端流程：输入文本 → 点击润色 → 等待 → 替换

---

## 阶段 5：首页改造

> 目标：将简单首页改造为 Landing Page，展示产品特性。
> 风险：低，独立页面，不影响核心功能。

### 5.1 新建首页组件

**新增文件：**
```
新增: src/components/home/LandingHeader.vue    (导航栏)
新增: src/components/home/HeroSection.vue       (主视觉区)
新增: src/components/home/FeaturesSection.vue   (功能特性)
新增: src/components/home/FAQSection.vue        (常见问题)
新增: src/components/home/CTASection.vue        (行动召唤)
```

### 5.2 改造路由

**改动文件：**
```
修改: src/pages/Home.vue    (整合新的 Landing 组件)
```

### 5.3 视觉风格对齐

参考 magic-resume-main 首页风格：
- 顶部导航栏（透明→白色滚动变化）
- Hero 区大标题 + 描述 + CTA按钮
- 功能卡片网格（语法检查、AI润色、多模板、本地存储、PDF导出等）
- FAQ 手风琴
- 底部 CTA

**素材准备：**
```
新增: public/features/grammar.png
新增: public/features/polish.png
新增: public/features/templates.png
新增: public/features/export.png
```

### 5.4 验证

- 访问 `/` 看到 Landing Page
- 已登录跳转到 `/dashboard`
- 未登录显示 CTA 跳转登录

---

## 阶段 6：后端扩展与数据迁移

> 目标：后端同步升级，支持新增功能；数据表扩展。
> 风险：中高，涉及数据库变更，需备份。

### 6.1 数据库扩展

```sql
-- 执行前请备份数据库！
ALTER TABLE resume ADD COLUMN IF NOT EXISTS template_id VARCHAR(50) DEFAULT 'classic';
ALTER TABLE resume ADD COLUMN IF NOT EXISTS global_settings JSON;
ALTER TABLE resume ADD COLUMN IF NOT EXISTS menu_sections JSON;
ALTER TABLE resume ADD COLUMN IF NOT EXISTS custom_data JSON;
ALTER TABLE resume ADD COLUMN IF NOT EXISTS certificates JSON;
```

### 6.2 后端代码改动清单

| 文件 | 改动内容 |
|------|---------|
| `entity/Resume.java` | 增加 templateId, globalSettings, menuSections, customData, certificates 字段 |
| `dto/CreateResumeDTO.java` | 增加 templateId |
| `dto/UpdateResumeDTO.java` | 增加全局设置、模块排序等字段 |
| `vo/ResumeVO.java` | 返回新字段 |
| `mapper/ResumeMapper.java` | 映射新字段 |
| `service/ResumeService.java` | CRUD 逻辑适配 |
| `controller/ResumeController.java` | 接口适配 |
| `dto/AiGrammarDTO.java` | **新增** |
| `dto/AiPolishDTO.java` | **新增** |
| `vo/AiGrammarVO.java` | **新增** |
| `vo/AiPolishVO.java` | **新增** |
| `service/AiService.java` | 新增 checkGrammar() / polishContent() |
| `controller/AiController.java` | 新增两个接口 |
| `application.yml` | 增加 AI 相关配置（可选） |
| `pom.xml` | 增加 HTTP Client 依赖（如 WebClient） |

### 6.3 AiService 核心逻辑

```java
// AiService.java 新增方法
@Service
public class AiService {
    
    private final WebClient webClient;
    
    // 语法检查
    public AiGrammarVO checkGrammar(AiGrammarDTO dto) {
        String prompt = buildGrammarPrompt(dto.getContent());
        String response = callAI(dto.getModel(), dto.getApiKey(), 
                                  dto.getEndpoint(), prompt);
        return parseGrammarResult(response);
    }
    
    // 内容润色  
    public AiPolishVO polishContent(AiPolishDTO dto) {
        String prompt = buildPolishPrompt(dto.getContent(), dto.getType());
        String response = callAI(dto.getModel(), dto.getApiKey(),
                                  dto.getEndpoint(), prompt);
        return new AiPolishVO(response);
    }
    
    // 统一 AI 调用（OpenAI 兼容格式）
    private String callAI(String model, String apiKey, String endpoint, String prompt) {
        // POST {endpoint}/v1/chat/completions
        // { model, messages: [{role:"user", content:prompt}] }
    }
}
```

### 6.4 验证

- 数据库迁移成功
- 创建简历可选择模板
- 更新简历保存全局设置
- 语法检查 API 可正常调用
- 润色 API 可正常调用

---

## 附录：改动文件总清单

### 新建文件（约 60+ 个）

```
# 配置
tailwind.config.js
postcss.config.js
src/config/constants.js
src/config/modules.js
src/config/ai.js

# 国际化
src/i18n/index.js
src/i18n/locales/zh-CN.json
src/i18n/locales/en-US.json

# Stores
src/stores/resume.js
src/stores/aiConfig.js
src/stores/theme.js

# Composables
src/composables/useGrammarCheck.js

# 模板系统 (8套 × ~6文件/套 + 共享组件)
src/components/templates/registry.js
src/components/templates/shared/SectionWrapper.vue
src/components/templates/shared/SectionTitle.vue
src/components/templates/classic/config.js
src/components/templates/classic/ClassicTemplate.vue
src/components/templates/classic/sections/*.vue    (6个)
src/components/templates/modern/config.js
src/components/templates/modern/ModernTemplate.vue
src/components/templates/modern/sections/*.vue      (6个)
... (timeline/left-right/minimalist/elegant/creative/editorial 同理)

# 编辑器
src/components/editor/EditorLayout.vue
src/components/editor/EditorHeader.vue
src/components/editor/SidePanel.vue
src/components/editor/EditPanel.vue
src/components/editor/PreviewPanel.vue
src/components/editor/grammar/GrammarCheckDrawer.vue

# 首页
src/components/home/LandingHeader.vue
src/components/home/HeroSection.vue
src/components/home/FeaturesSection.vue
src/components/home/FAQSection.vue
src/components/home/CTASection.vue

# 后端
(约8个新文件: dto ×2, vo ×2, 接口扩展)
```

### 修改文件（约 15 个）

```
# 前端
vite.config.js
src/main.js
src/App.vue
src/router/index.js
src/pages/Home.vue
src/pages/Editor.vue          (重写)
src/pages/Dashboard.vue
src/pages/Templates.vue
src/pages/AiGenerate.vue      (改造为 AiSettings)
src/api/ai.js
src/components/editor/ResumePreview.vue  (重写)
src/assets/styles/main.css

# 后端
entity/Resume.java
dto/CreateResumeDTO.java
dto/UpdateResumeDTO.java
vo/ResumeVO.java
mapper/ResumeMapper.java
service/ResumeService.java
service/AiService.java
controller/ResumeController.java
controller/AiController.java
application.yml
pom.xml
```

### 删除文件

```
src/style.css           (整合到 main.css)
src/components/HelloWorld.vue   (未使用)
```

---

## 执行建议

1. **每个阶段完成后 commit 一次**，便于回滚
2. **阶段2（模板系统）先只做1个模板（classic），验证流程通了再做其余7个**
3. **阶段3 编辑器重构前备份 Editor.vue**
4. **阶段4 AI功能依赖后端，先确保后端接口就绪再调试前端**
5. **阶段6 数据库变更前务必执行 `mysqldump` 备份**
