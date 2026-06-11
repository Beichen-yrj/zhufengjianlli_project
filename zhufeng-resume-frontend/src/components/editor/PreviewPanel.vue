<template>
  <div class="preview-panel" ref="previewRef">
    <div class="pp-a4" :style="a4Style">
      <div class="pp-content" :style="previewStyle">
        <component
          :is="templateComp"
          v-if="templateComp"
          :resumeData="resumeData"
          :template="templateConfig"
        />
        <div v-else class="pp-empty">选择模板开始编辑</div>
      </div>
    </div>

    <!-- 缩放控制 -->
    <div class="pp-zoom">
      <button @click="zoom = Math.max(0.3, zoom - 0.1)">−</button>
      <span>{{ Math.round(zoom * 100) }}%</span>
      <button @click="zoom = Math.min(2, zoom + 0.1)">+</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useResumeStore } from '@/stores/resume'
import { getTemplateComponent, getTemplateConfig } from '../templates/registry.js'

const store = useResumeStore()
const zoom = ref(1)
const previewRef = ref(null)

const templateComp = computed(() => {
  const layout = store.activeResume?.templateId || 'classic'
  return getTemplateComponent(layout)
})

const templateConfig = computed(() => {
  const layout = store.activeResume?.templateId || 'classic'
  return getTemplateConfig(layout)
})

// Build resumeData for templates (maps store fields)
const resumeData = computed(() => {
  const r = store.activeResume
  if (!r) return {}
  return {
    basic: r.basic || {},
    education: r.education || [],
    experience: r.experience || [],
    projects: r.projects || [],
    skillContent: r.skillContent || '',
    selfEvaluationContent: r.selfEvaluationContent || '',
    certificates: r.certificates || [],
    customData: r.customData || {},
    menuSections: r.menuSections || [],
    globalSettings: r.globalSettings || {}
  }
})

const gs = computed(() => store.activeResume?.globalSettings || {})

const a4Style = computed(() => ({
  fontFamily: gs.value.fontFamily === 'default' ? 'inherit' : gs.value.fontFamily,
  transform: `scale(${zoom.value})`,
  transformOrigin: 'top center'
}))

const previewStyle = computed(() => ({
  padding: `${gs.value.pagePadding || 40}px`,
  lineHeight: gs.value.lineHeight || 1.6,
  fontSize: `${gs.value.baseFontSize || 14}px`,
}))

defineExpose({ previewComponent: previewRef })
</script>

<style scoped>
.preview-panel { height: 100%; overflow-y: auto; background: #e8e8e8; display: flex; flex-direction: column; align-items: center; padding: 20px 0 60px; }
.pp-a4 { width: 210mm; min-height: 297mm; background: #fff; box-shadow: 0 4px 24px rgba(0,0,0,0.12); margin: 0 auto; }
.pp-content { min-height: 100%; }
.pp-empty { display: flex; align-items: center; justify-content: center; height: 297mm; color: #ccc; font-size: 18px; }
.pp-zoom { position: sticky; bottom: 0; display: flex; align-items: center; gap: 12px; background: rgba(255,255,255,0.95); padding: 8px 16px; border-radius: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); margin-top: 16px; }
.pp-zoom button { width: 32px; height: 32px; border: 1px solid #ddd; border-radius: 50%; background: #fff; cursor: pointer; font-size: 16px; display: flex; align-items: center; justify-content: center; }
.pp-zoom button:hover { background: #f0f0f0; }
.pp-zoom span { font-size: 13px; color: #666; min-width: 44px; text-align: center; }
</style>
