<template>
  <div class="templates-page">
    <!-- 页面标题 + 分类筛选 -->
    <div class="page-header">
      <h1 class="page-title">模板</h1>
      <div class="filter-bar">
        <!-- 分类标签 -->
        <div class="category-tabs">
          <button
            v-for="cat in categories"
            :key="cat.value"
            class="cat-tab"
            :class="{ active: selectedCategory === cat.value }"
            @click="selectedCategory = cat.value"
          >{{ cat.label }}</button>
        </div>
        <!-- 颜色筛选 -->
        <div class="color-picker">
          <button
            v-for="(c, idx) in colorOptions"
            :key="idx"
            class="color-dot"
            :class="{ active: selectedColor === c.value }"
            :style="{ background: c.bg }"
            :title="c.name"
            @click="selectedColor = c.value"
          ></button>
        </div>
      </div>
    </div>

    <!-- 模板网格 -->
    <div class="template-grid">
      <div
        v-for="tpl in filteredTemplates"
        :key="tpl.id"
        class="template-card"
        @click="useTemplate(tpl)"
      >
        <!-- 真实模板预览图 -->
        <div class="template-preview" @click.stop="previewTemplate(tpl)">
          <img :src="tpl.img" :alt="tpl.name" class="tpl-img" loading="lazy" />
          <div class="usage-badge">{{ formatUsage(tpl.usage) }}人使用</div>
        </div>

        <!-- 模板信息 -->
        <div class="template-info">
          <h3 class="template-name">{{ tpl.name }}</h3>
          <span class="template-cat">{{ tpl.category }}</span>
          <div class="template-actions">
            <button class="btn-use" @click.stop="useTemplate(tpl)">使用此模板</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { JIANLIBEN_TEMPLATES, TEMPLATE_CATEGORIES } from '../data/jianlibenTemplates.js'
import { useResumeStore } from '../stores/resume'
import { createResume as apiCreateResume } from '../api/resume'
import { ElMessage } from 'element-plus'

const router = useRouter()
const resumeStore = useResumeStore()
const templates = ref(JIANLIBEN_TEMPLATES.slice(0, 5))
const selectedCategory = ref('all')
const selectedColor = ref('all')
const categories = TEMPLATE_CATEGORIES

// 颜色选项
const colorOptions = [
  { name: '全部', value: 'all', bg: '#E5E7EB' },
  { name: '蓝色', value: 'blue', bg: '#3B82F6' },
  { name: '绿色', value: 'green', bg: '#22C55E' },
  { name: '紫色', value: 'purple', bg: '#A855F7' },
  { name: '橙色', value: 'orange', bg: '#F97316' },
  { name: '红色', value: 'red', bg: '#EF4444' },
  { name: '深灰/黑', value: 'dark', bg: '#374151' },
]

// 筛选逻辑
const filteredTemplates = computed(() => {
  let list = templates.value
  if (selectedCategory.value !== 'all') {
    list = list.filter(t => t.category === selectedCategory.value)
  }
  if (selectedColor.value !== 'all') {
    const colorMap = {
      blue: ['#2196F3', '#3F51B5', '#1565C0', '#0D47A1', '#283593', '#303F9F'],
      green: ['#4CAF50', '#009688', '#00796B', '#43A047', '#2E7D32'],
      purple: ['#673AB7', '#7B1FA2', '#AD1457'],
      orange: ['#FF5722', '#BF360C', '#F97316'],
      red: ['#E91E63', '#C62828', '#D32F2F', '#D81B60'],
      dark: ['#333', '#555', '#424242', '#212121', '#37474F', '#455A64', '#546E7A', '#607D8B', '#795548', '#6D4C41'],
    }
    const colors = colorMap[selectedColor.value]
    if (colors) {
      list = list.filter(t => colors.some(c => t.color.toLowerCase() === c.toLowerCase()))
    }
  }
  return list
})

function formatUsage(n) {
  if (n >= 1000000) return (n / 10000).toFixed(0) + '万'
  return n.toLocaleString()
}

function previewTemplate(tpl) {
  // 可扩展为弹窗预览大图
  ElMessage.info(`预览模板：${tpl.name}`)
}

async function useTemplate(tpl) {
  const token = localStorage.getItem('token')
  if (!token) {
    router.push({ path: '/login', query: { redirect: '/templates' } })
    return
  }

  const tplId = String(tpl.id)

  // 本地创建简历
  const localId = resumeStore.createResume(tplId, tpl.name)

  try {
    const res = await apiCreateResume({
      title: tpl.name,
      templateId: tplId,
    })
    const local = resumeStore.resumes[localId]
    if (local && res.data?.id) {
      resumeStore.resumes[res.data.id] = { ...local, id: String(res.data.id) }
      delete resumeStore.resumes[localId]
      resumeStore.activeResumeId = String(res.data.id)
      // 通过URL参数传递模板ID，确保编辑器能获取到正确的模板
      router.push(`/editor/${res.data.id}?tpl=${tplId}`)
    } else {
      router.push(`/editor/${localId}?tpl=${tplId}`)
    }
  } catch (_) {
    router.push(`/editor/${localId}?tpl=${tplId}`)
  }
}
</script>

<style scoped>
.templates-page {
  width: 94%;
  max-width: 1500px;
  margin: 0 auto;
  padding: 3% 3%;
}

/* ===== 页面标题 ===== */
.page-header { margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 700; color: #1a1a2e; margin: 0 0 16px; letter-spacing: -0.5px; }

/* ===== 筛选栏 ===== */
.filter-bar { display: flex; align-items: center; gap: 20px; flex-wrap: wrap; }

.category-tabs { display: flex; gap: 6px; flex-wrap: wrap; }

.cat-tab {
  padding: 5px 14px; border-radius: 20px; border: 1px solid #E5E7EB;
  background: #fff; color: #6B7280; font-size: 13px; cursor: pointer;
  transition: all 0.15s ease;
}
.cat-tab:hover { border-color: #9CA3AF; color: #374151; }
.cat-tab.active {
  background: #1F2937; color: #fff; border-color: #1F2937;
}

.color-picker { display: flex; gap: 8px; align-items: center; }

.color-dot {
  width: 24px; height: 24px; border-radius: 50%; border: 2px solid transparent;
  cursor: pointer; transition: all 0.15s ease; outline: none;
}
.color-dot:hover { transform: scale(1.12); }
.color-dot.active {
  border-color: #1a1a2e; box-shadow: 0 0 0 2px #fff, 0 0 0 4px #1a1a2e;
}

/* ===== 模板网格 ===== */
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

/* ===== 模板卡片 ===== */
.template-card {
  background: #fff; border-radius: 12px; overflow: hidden;
  border: 1px solid #E5E7EB; transition: box-shadow 0.2s ease, border-color 0.2s ease;
  cursor: pointer;
}
.template-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  border-color: #93B5F4;
}

.template-preview {
  position: relative; aspect-ratio: 210 / 297; overflow: hidden;
  cursor: pointer; background: #f5f5f5;
}
.tpl-img {
  width: 100%; height: 100%; object-fit: contain;
  transition: transform 0.3s;
}
.template-preview:hover .tpl-img { transform: scale(1.02); }

.usage-badge {
  position: absolute; bottom: 8px; right: 8px;
  background: rgba(0,0,0,0.5); color: #fff; font-size: 11px;
  padding: 2px 8px; border-radius: 10px; pointer-events: none;
}

/* ===== 模板信息 ===== */
.template-info { padding: 14px; }
.template-name { margin: 0 0 4px; font-size: 15px; font-weight: 600; color: #1F2937; }
.template-cat {
  display: inline-block; font-size: 11px; color: #9CA3AF; background: #F3F4F6;
  padding: 2px 8px; border-radius: 10px; margin-bottom: 12px;
}

.template-actions { display: flex; }
.btn-use {
  flex: 1; padding: 8px 0; border: none; border-radius: 8px;
  background: #1F2937; color: #fff; font-size: 13px; font-weight: 500;
  cursor: pointer; transition: all 0.15s;
}
.btn-use:hover { background: #111827; }
</style>
