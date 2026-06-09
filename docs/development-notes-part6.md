# 逐风 AI 简历平台 — 开发笔记（第六部分）

> 更新日期：2026-05-12
> 内容：简历编辑器（核心模块）

---

## 一、模块概述

简历编辑器是平台最核心的页面，实现简历的**可视化编辑 + 实时预览**。

### 设计思路

```
┌──────────────────────────────────────────────────┐
│                   工具栏                          │
│   [返回] [标题输入框]        [保存] [导出PDF]    │
├────────────┬─────────────────────────────────────┤
│  编辑面板   │           实时预览                  │
│  ┌───────┐ │  ┌─────────────────────────────┐   │
│  │基本   │ │  │                             │   │
│  │信息   │ │  │       简历纸张预览           │   │
│  ├───────┤ │  │                             │   │
│  │教育   │ │  │   姓名                      │   │
│  │经历   │ │  │   📱138xxxx ✉️email         │   │
│  ├───────┤ │  │                             │   │
│  │工作   │ │  │   教育经历                   │   │
│  │经历   │ │  │   工作经历                   │   │
│  ├───────┤ │  │   项目经历                   │   │
│  │项目   │ │  │   技能标签                   │   │
│  │经历   │ │  │                             │   │
│  ├───────┤ │  │                             │   │
│  │技能   │ │  │                             │   │
│  ├───────┤ │  │                             │   │
│  │自我   │ │  │                             │   │
│  │评价   │ │  │                             │   │
│  └───────┘ │  └─────────────────────────────┘   │
└────────────┴─────────────────────────────────────┘
```

### 左右联动机制

```
左侧表单编辑
    ↓ v-model 双向绑定
resumeData 响应式对象
    ↓ props 传递
右侧 ResumePreview 渲染
```

---

## 二、文件结构

### 2.1 页面文件

```
前端 - 编辑器
├── pages/
│   └── Editor.vue                  # 编辑器主页面
└── components/editor/
    ├── BasicInfoForm.vue           # 基本信息表单
    ├── EducationForm.vue           # 教育经历表单
    ├── ExperienceForm.vue          # 工作经历表单
    ├── ProjectsForm.vue            # 项目经历表单
    ├── SkillsForm.vue              # 技能特长表单
    ├── SelfEvalForm.vue            # 自我评价表单
    └── ResumePreview.vue           # 简历预览组件
```

### 2.2 路由配置

```javascript
{
  path: '/editor/:id',
  name: 'Editor',
  component: () => import('../pages/Editor.vue'),
  meta: { requiresAuth: true }  // 需要登录
}
```

---

## 三、Editor.vue 主页面

### 核心状态

```javascript
// 简历基本信息
const resume = reactive({
  id: null,
  title: '我的简历'
})

// 简历内容数据（所有表单编辑的数据存在这里）
const resumeData = reactive({
  basicInfo: { name: '', phone: '', email: '', jobIntention: '' },
  education: [],
  experience: [],
  projects: [],
  skills: [],
  selfEvaluation: ''
})

// 模板样式配置
const templateConfig = ref({})
```

### 数据加载流程

```
页面加载 onMounted()
    ↓
调用 getResumeById(resumeId)
    ↓
解析 resumeData（JSON.parse）
    ↓
调用 getTemplateById(templateId) 获取模板配置
    ↓
渲染预览
```

### 保存功能

```javascript
const handleSave = async () => {
  await updateResume(resumeId, {
    title: resume.title,
    resumeData: JSON.stringify(resumeData)  // 序列化后存入数据库
  })
  ElMessage.success('保存成功')
}
```

---

## 四、表单组件设计

### 4.1 v-model 双向绑定模式

所有表单组件采用统一的 v-model 模式：

```vue
<!-- 父组件（Editor.vue） -->
<BasicInfoForm v-model="resumeData.basicInfo" />

<!-- 子组件（BasicInfoForm.vue） -->
<script setup>
const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

// 监听变化通知父组件
watch(form, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })
</script>
```

### 4.2 各表单组件说明

| 组件 | 数据类型 | 特点 |
|------|----------|------|
| **BasicInfoForm** | 对象 | 简单表单，姓名、手机、邮箱、求职意向 |
| **EducationForm** | 数组 | 动态列表，支持增删，含日期范围选择 |
| **ExperienceForm** | 数组 | 动态列表，支持增删，含文本域 |
| **ProjectsForm** | 数组 | 动态列表，支持增删 |
| **SkillsForm** | 数组 | 标签式管理，回车添加，点击删除 |
| **SelfEvalForm** | 字符串 | 单个文本域 |

### 4.3 列表增删模式

```javascript
// 添加项
const add = () => {
  list.push({ /* 空模板 */ })
  emit('update:modelValue', list)
}

// 删除项
const remove = (index) => {
  list.splice(index, 1)
  emit('update:modelValue', list)
}
```

---

## 五、ResumePreview 预览组件

### 核心功能

实时渲染简历数据为可视化的纸张预览。

### 样式变量

```javascript
const previewStyle = computed(() => ({
  '--primary-color': props.templateConfig?.primaryColor || '#667eea'
}))
```

### 条件渲染

```vue
<!-- 只有存在数据时才渲染对应模块 -->
<div v-if="resumeData.education?.length" class="section">
  <h2>教育经历</h2>
  ...
</div>
```

### A4 纸预览尺寸

```css
.resume-paper {
  width: 595px;           /* A4 宽度 */
  min-height: 842px;      /* A4 高度 */
  padding: 40px;
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}
```

---

## 六、后续优化方向

当前编辑器为**基础功能版**，后续可优化：

| 优化项 | 说明 |
|--------|------|
| 多模板切换 | 切换模板后实时更新预览样式 |
| 拖拽排序 | 教育经历、工作经历支持拖拽排序 |
| 富文本编辑 | 自我评价支持富文本 |
| 自动保存 | 定时自动保存，防止丢失 |
| 预览缩放 | 支持缩小/放大预览 |
| A4 打印 | 精确匹配打印纸张 |

---

## 七、关键知识点

### 7.1 响应式数据选择

| 类型 | 用途 | 示例 |
|------|------|------|
| `ref()` | 单值（字符串、数字） | `const token = ref('')` |
| `reactive()` | 对象/数组 | `const resumeData = reactive({...})` |
| `computed()` | 计算属性 | `const previewStyle = computed(...)` |

### 7.2 组件通信方式

| 方式 | 适用场景 | 本模块使用 |
|------|----------|------------|
| props + emits | 父子组件 | ✅ 表单组件传数据 |
| v-model | 双向绑定 | ✅ 编辑数据同步 |
| Pinia store | 全局状态 | 用户信息 |
