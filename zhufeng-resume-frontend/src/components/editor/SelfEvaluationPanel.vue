<template>
  <div class="simple-editor-panel">
    <div class="se-toolbar">
      <el-button size="small" text @click="$emit('grammarCheck')">🔍 语法检查</el-button>
      <el-button size="small" text @click="$emit('aiPolish')">✨ AI 润色</el-button>
    </div>
    <Field
      v-model="content"
      kind="editor"
      placeholder="描述你的自我评价..."
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Field from './Field.vue'
import { useResumeStore } from '@/stores/resume'

defineEmits(['grammarCheck', 'aiPolish'])

const store = useResumeStore()
const content = computed({
  get: () => store.activeResume?.selfEvaluationContent || '',
  set: (val) => store.updateSelfEvaluationContent(val)
})
</script>

<style scoped>
.simple-editor-panel { padding: 12px; }
.se-toolbar { display: flex; gap: 8px; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #f0f0f0; }
</style>
