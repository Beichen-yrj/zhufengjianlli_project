<template>
  <div class="edit-panel">
    <!-- 标题栏 -->
    <div class="ep-title-bar">
      <span class="ep-title-icon">{{ currentSection?.icon || '📄' }}</span>

      <!-- 可编辑标题 (非 basic 可改名) -->
      <input
        v-if="activeSection !== 'basic'"
        class="ep-title-input"
        :value="currentSection?.title"
        @input="onTitleChange($event.target.value)"
      />
      <span v-else class="ep-title-text">{{ currentSection?.title || '基本信息' }}</span>

      <!-- 删除板块 (非 basic) -->
      <el-button v-if="activeSection !== 'basic'" text size="small" class="ep-del" @click="deleteSection">
        🗑
      </el-button>
    </div>

    <!-- 区块内容 -->
    <div class="ep-body">
      <BasicPanel v-if="activeSection === 'basic'" />
      <EducationPanel v-else-if="activeSection === 'education'" />
      <ExperiencePanel v-else-if="activeSection === 'experience'" />
      <ProjectsPanel v-else-if="activeSection === 'projects'" />
      <SkillsPanel v-else-if="activeSection === 'skills'" />
      <SelfEvaluationPanel v-else-if="activeSection === 'selfEvaluation'" @grammarCheck="$emit('grammarCheck')" @aiPolish="$emit('aiPolish')" />
      <CertificatesPanel v-else-if="activeSection === 'certificates'" />
      <div v-else-if="activeSection?.startsWith('custom')" class="ep-custom">
        <p>{{ currentSection?.title || '自定义模块' }}</p>
        <p class="ep-custom-hint">自定义模块内容请在预览中编辑</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useResumeStore } from '@/stores/resume'
import BasicPanel from './BasicPanel.vue'
import EducationPanel from './EducationPanel.vue'
import ExperiencePanel from './ExperiencePanel.vue'
import ProjectsPanel from './ProjectsPanel.vue'
import SkillsPanel from './SkillsPanel.vue'
import SelfEvaluationPanel from './SelfEvaluationPanel.vue'
import CertificatesPanel from './CertificatesPanel.vue'

const props = defineProps({
  activeSection: { type: String, default: 'basic' }
})

defineEmits(['grammarCheck', 'aiPolish'])

const store = useResumeStore()

const currentSection = computed(() =>
  store.activeResume?.menuSections?.find(s => s.id === props.activeSection)
)

function onTitleChange(val) {
  const sections = [...(store.activeResume?.menuSections || [])]
  const s = sections.find(s => s.id === props.activeSection)
  if (s) s.title = val
  store.updateMenuSections(sections)
}

function deleteSection() {
  const sections = (store.activeResume?.menuSections || []).filter(s => s.id !== props.activeSection)
  store.updateMenuSections(sections)
  // switch to previous section
  const prev = sections[sections.length - 1]
  if (prev) store.setActiveSection(prev.id)
}
</script>

<style scoped>
.edit-panel { height: 100%; display: flex; flex-direction: column; background: #fff; overflow: hidden; }
.ep-title-bar { display: flex; align-items: center; gap: 8px; padding: 14px 16px; border-bottom: 1px solid #f0f0f0; background: #fafafa; flex-shrink: 0; }
.ep-title-icon { font-size: 18px; }
.ep-title-text { font-size: 16px; font-weight: 600; color: #333; flex: 1; }
.ep-title-input { flex: 1; border: none; outline: none; font-size: 16px; font-weight: 600; background: transparent; border-bottom: 1px dashed #ccc; padding: 2px 0; }
.ep-title-input:focus { border-bottom-color: #667eea; }
.ep-del { color: #ccc; }
.ep-del:hover { color: #f56c6c; }
.ep-body { flex: 1; overflow-y: auto; }
.ep-custom { padding: 40px; text-align: center; color: #999; }
.ep-custom-hint { font-size: 12px; color: #ccc; margin-top: 8px; }
</style>
