<template>
  <label class="field-wrap">
    <div class="field-label-row" v-if="label || showStealthToggle">
      <span class="field-label">{{ label }}</span>
      <el-switch
        v-if="showStealthToggle"
        :model-value="isStealth"
        @update:model-value="toggleStealth"
        size="small"
        inline-prompt
        active-text="显示"
        inactive-text="隐藏"
      />
    </div>

    <!-- text -->
    <template v-if="kind === 'text'">
      <el-input
        :model-value="modelValue"
        @update:model-value="$emit('update:modelValue', $event)"
        :placeholder="placeholder"
        class="field-input"
      />
    </template>

    <!-- textarea -->
    <template v-else-if="kind === 'textarea'">
      <el-input
        :model-value="modelValue"
        @update:model-value="$emit('update:modelValue', $event)"
        type="textarea"
        :rows="rows"
        :placeholder="placeholder"
        class="field-textarea"
      />
    </template>

    <!-- editor (Tiptap) -->
    <template v-else-if="kind === 'editor'">
      <div class="field-editor-wrap">
        <div class="fe-toolbar">
          <button
            v-for="btn in editorBtns"
            :key="btn.key"
            class="fe-btn"
            :class="{ active: editorState[btn.key] }"
            @click="btn.action"
            :title="btn.title"
          >
            {{ btn.icon }}
          </button>
          <span class="fe-divider"></span>
          <button
            class="fe-btn ai-btn"
            @click="$emit('aiPolish')"
            title="AI 润色"
          >
            ✨
          </button>
        </div>
        <div class="fe-content" ref="editorEl" contenteditable="true" @input="onEditorInput" @paste="onEditorPaste" />
      </div>
    </template>

    <!-- date -->
    <template v-else-if="kind === 'date'">
      <div class="field-date-row">
        <el-date-picker
          :model-value="isPresent ? '' : modelValue"
          @update:model-value="d => $emit('update:modelValue', d || '')"
          type="month"
          placeholder="选择日期"
          format="YYYY-MM"
          value-format="YYYY-MM"
          :disabled="isPresent"
          class="field-date"
        />
        <el-switch
          :model-value="isPresent"
          @update:model-value="togglePresent"
          size="small"
          inline-prompt
          active-text="至今"
          inactive-text="至今"
          class="present-switch"
        />
      </div>
    </template>

    <!-- date-range -->
    <template v-else-if="kind === 'dateRange'">
      <div class="field-date-range">
        <el-date-picker
          :model-value="dateRangeStart"
          @update:model-value="updateRangeStart"
          type="month"
          placeholder="开始"
          format="YYYY-MM"
          value-format="YYYY-MM"
          class="field-date"
        />
        <span class="date-range-sep">至</span>
        <el-date-picker
          :model-value="dateRangeEnd"
          @update:model-value="updateRangeEnd"
          type="month"
          placeholder="结束"
          format="YYYY-MM"
          value-format="YYYY-MM"
          :disabled="isPresent"
          class="field-date"
        />
        <el-switch
          :model-value="isPresent"
          @update:model-value="togglePresent"
          size="small"
          inline-prompt
          active-text="至今"
          inactive-text="至今"
          class="present-switch"
        />
      </div>
    </template>
  </label>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' },
  label: { type: String, default: '' },
  kind: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  rows: { type: Number, default: 4 },
  showStealthToggle: { type: Boolean, default: false },
  isStealth: { type: Boolean, default: false },
})

const emit = defineEmits(['update:modelValue', 'update:isStealth', 'aiPolish'])

function toggleStealth() {
  emit('update:isStealth', !props.isStealth)
}

// ===== 至今逻辑 =====
const PRESENT_MARK = '至今'

const isPresent = computed(() => {
  if (!props.modelValue) return false
  return props.modelValue === PRESENT_MARK || props.modelValue.endsWith(` - ${PRESENT_MARK}`)
})

function togglePresent(checked) {
  if (props.kind === 'date') {
    emit('update:modelValue', checked ? PRESENT_MARK : '')
  } else if (props.kind === 'dateRange') {
    const [start] = (props.modelValue || '').split(' - ')
    emit('update:modelValue', checked ? `${start || ''} - ${PRESENT_MARK}` : (start || ''))
  }
}

// date-range 分离
const dateRangeStart = computed(() => {
  const parts = (props.modelValue || '').split(' - ')
  return parts[0] || ''
})
const dateRangeEnd = computed(() => {
  const parts = (props.modelValue || '').split(' - ')
  if (parts[1] === PRESENT_MARK) return ''
  return parts[1] || ''
})

function updateRangeStart(val) {
  const end = dateRangeEnd.value || (isPresent.value ? PRESENT_MARK : '')
  emit('update:modelValue', end ? `${val} - ${end}` : val)
}
function updateRangeEnd(val) {
  const start = dateRangeStart.value
  emit('update:modelValue', start ? `${start} - ${val}` : val)
}

// ===== 轻量编辑器 =====
const editorEl = ref(null)
const editorState = ref({ bold: false, italic: false, underline: false })

function exec(command, val) {
  document.execCommand(command, false, val || undefined)
  nextTick(() => editorEl.value?.focus())
}

function onEditorInput() {
  emit('update:modelValue', editorEl.value?.innerHTML || '')
}
function onEditorPaste(e) {
  e.preventDefault()
  const text = e.clipboardData.getData('text/plain')
  exec('insertText', text)
}

const editorBtns = [
  { key: 'bold', icon: 'B', title: '加粗', action: () => exec('bold') },
  { key: 'italic', icon: 'I', title: '斜体', action: () => exec('italic') },
  { key: 'underline', icon: 'U', title: '下划线', action: () => exec('underline') },
]

watch(() => props.modelValue, (v) => {
  if (editorEl.value && v !== editorEl.value.innerHTML) {
    editorEl.value.innerHTML = v || ''
  }
})
</script>

<style scoped>
.field-wrap { display: block; margin-bottom: 14px; }
.field-label-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 4px; }
.field-label { font-size: 13px; color: #555; font-weight: 500; }
.field-input, .field-textarea { width: 100%; }
.field-date-row, .field-date-range { display: flex; align-items: center; gap: 8px; }
.field-date { flex: 1; }
.date-range-sep { color: #999; font-size: 13px; }
.present-switch { flex-shrink: 0; }

/* 轻量编辑器 */
.field-editor-wrap { border: 1px solid #dcdfe6; border-radius: 6px; overflow: hidden; }
.fe-toolbar { display: flex; align-items: center; gap: 2px; padding: 6px 8px; background: #fafafa; border-bottom: 1px solid #eee; }
.fe-btn { width: 28px; height: 28px; border: none; background: transparent; border-radius: 4px; cursor: pointer; font-size: 13px; color: #555; display: flex; align-items: center; justify-content: center; transition: all 0.15s; }
.fe-btn:hover { background: #e8e8e8; }
.fe-btn.active, .fe-btn:active { background: #d0d5ff; color: #667eea; }
.fe-btn.ai-btn { margin-left: auto; font-size: 15px; }
.fe-divider { width: 1px; height: 18px; background: #ddd; margin: 0 4px; }
.fe-content { min-height: 80px; padding: 10px 12px; outline: none; font-size: 14px; line-height: 1.6; word-break: break-word; }
.fe-content:empty::before { content: attr(placeholder); color: #c0c4cc; }
.fe-content[placeholder] {  }
</style>
