<template>
  <div class="editor-header">
    <div class="eh-left">
      <div class="eh-back" @click="$router.push('/dashboard')">
        <span class="eh-logo">📄</span>
        <span class="eh-app-name">逐风resume</span>
      </div>
    </div>

    <div class="eh-center">
      <input
        class="eh-title-input"
        :value="store.activeResume?.title || ''"
        @blur="store.updateResumeTitle($event.target.value || '未命名简历')"
        placeholder="简历标题"
      />
    </div>

    <div class="eh-right">
      <el-button size="small" @click="$emit('save')" :loading="saving" type="primary">
        保存
      </el-button>
      <el-tooltip content="导出PDF" placement="bottom">
        <el-button size="small" circle @click="$emit('export')">📥</el-button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup>
import { useResumeStore } from '@/stores/resume'

defineProps({ saving: Boolean })
defineEmits(['save', 'export'])

const store = useResumeStore()
</script>

<style scoped>
.editor-header {
  display: flex; justify-content: space-between; align-items: center;
  height: 56px; padding: 0 20px;
  background: #fff; border-bottom: 1px solid #e5e5e5;
  flex-shrink: 0; position: sticky; top: 0; z-index: 40;
}
.eh-left { display: flex; align-items: center; }
.eh-back { display: flex; align-items: center; gap: 8px; cursor: pointer; transition: transform 0.15s; }
.eh-back:hover { transform: scale(1.03); }
.eh-logo { font-size: 20px; }
.eh-app-name { font-size: 16px; font-weight: 600; color: #333; }
.eh-center { flex: 1; display: flex; justify-content: center; max-width: 320px; margin: 0 16px; }
.eh-title-input { width: 100%; border: 1px solid #e0e0e0; border-radius: 6px; padding: 6px 12px; font-size: 14px; outline: none; text-align: center; }
.eh-title-input:focus { border-color: #667eea; }
.eh-right { display: flex; align-items: center; gap: 10px; }
</style>
