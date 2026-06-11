<template>
  <div class="simple-editor-panel">
    <Field
      v-model="content"
      kind="editor"
      placeholder="描述你的技能、专长等..."
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Field from './Field.vue'
import { useResumeStore } from '@/stores/resume'

const store = useResumeStore()

const content = computed({
  get() {
    const v = store.activeResume?.skillContent
    if (Array.isArray(v)) return v.map(s => typeof s === 'string' ? s : s.name).join('、')
    return v || ''
  },
  set(val) { store.updateSkillContent(val) }
})
</script>

<style scoped>
.simple-editor-panel { padding: 12px; }
</style>
