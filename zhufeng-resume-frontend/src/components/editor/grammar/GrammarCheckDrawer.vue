<template>
  <el-drawer
    :model-value="visible"
    title="语法检查"
    size="400px"
    @update:model-value="v => $emit('update:visible', v)"
  >
    <!-- 状态 -->
    <div v-if="checking" class="gc-loading">
      <el-icon class="is-loading"><svg viewBox="0 0 1024 1024" width="24" height="24"><path fill="currentColor" d="M512 64a32 32 0 0 1 32 32v192a32 32 0 0 1-64 0V96a32 32 0 0 1 32-32zm0 640a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V736a32 32 0 0 1 32-32zm448-192a32 32 0 0 1-32 32H736a32 32 0 1 1 0-64h192a32 32 0 0 1 32 32zm-640 0a32 32 0 0 1-32 32H96a32 32 0 0 1 0-64h192a32 32 0 0 1 32 32z"/></svg></el-icon>
      <p>正在检查语法...</p>
    </div>

    <!-- 无错误 -->
    <div v-else-if="errorList.length === 0" class="gc-empty">
      <div class="gc-empty-icon">✅</div>
      <h3>未发现语法问题</h3>
      <p>你的内容语法正确</p>
    </div>

    <!-- 错误列表 -->
    <div v-else class="gc-list">
      <div class="gc-summary">
        发现 <strong>{{ errorList.length }}</strong> 个问题
      </div>
      <div
        v-for="(err, idx) in errorList"
        :key="idx"
        class="gc-item"
        :class="{ applied: resolved.has(idx) }"
      >
        <div class="gc-item-badge">
          <el-tag :type="err.type === 'typo' ? 'warning' : 'info'" size="small">
            {{ err.type === 'typo' ? '错别字' : '标点' }}
          </el-tag>
        </div>
        <div class="gc-item-content">
          <div class="gc-item-text">
            <span class="gc-original">{{ err.text }}</span>
            <span class="gc-arrow">→</span>
            <span class="gc-suggestion">{{ err.suggestion }}</span>
          </div>
          <div class="gc-item-context">位置: {{ err.position }}-{{ err.position + err.length }}</div>
        </div>
        <div class="gc-item-action">
          <el-button
            size="small"
            type="primary"
            text
            :disabled="resolved.has(idx)"
            @click="fixItem(idx)"
          >
            {{ resolved.has(idx) ? '已修正' : '修正' }}
          </el-button>
        </div>
      </div>

      <div class="gc-footer">
        <el-button type="primary" @click="fixAll" :disabled="resolved.size >= errorList.length">
          一键修正全部
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useGrammarCheck } from '../../../composables/useGrammarCheck'

const props = defineProps({
  visible: { type: Boolean, default: false },
  content: { type: String, default: '' }
})

const emit = defineEmits(['update:visible', 'applyFix'])

const { checking, errors, applyFix } = useGrammarCheck()
const resolved = ref(new Set())

const errorList = ref([])

watch(() => props.visible, async (v) => {
  if (v && props.content) {
    resolved.value.clear()
    const result = await checkGrammar(props.content)
    errorList.value = result
  }
})

async function checkGrammar(text) {
  checking.value = true
  try {
    const { checkGrammar: doCheck } = useGrammarCheck()
    return await doCheck(text)
  } finally {
    checking.value = false
  }
}

function fixItem(index) {
  if (resolved.value.has(index)) return
  const err = errorList.value[index]
  if (!err) return
  resolved.value.add(index)
  emit('applyFix', err)
}

function fixAll() {
  errorList.value.forEach((err, idx) => {
    if (!resolved.value.has(idx)) {
      resolved.value.add(idx)
      emit('applyFix', err)
    }
  })
  ElMessage.success('已应用所有修正')
}
</script>

<style scoped>
.gc-loading {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.is-loading {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.gc-empty {
  text-align: center;
  padding: 60px 20px;
}

.gc-empty-icon {
  font-size: 42px;
  margin-bottom: 12px;
}

.gc-empty h3 {
  margin: 0 0 6px;
  font-size: 18px;
  color: #333;
}

.gc-empty p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.gc-summary {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.gc-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  margin-bottom: 8px;
  transition: opacity 0.2s;
}

.gc-item.applied {
  opacity: 0.4;
}

.gc-item-badge {
  flex-shrink: 0;
  padding-top: 2px;
}

.gc-item-content {
  flex: 1;
}

.gc-item-text {
  font-size: 14px;
  margin-bottom: 4px;
}

.gc-original {
  background: #fff3cd;
  padding: 1px 4px;
  border-radius: 3px;
  color: #856404;
  text-decoration: line-through;
}

.gc-arrow {
  margin: 0 8px;
  color: #aaa;
}

.gc-suggestion {
  background: #d4edda;
  padding: 1px 4px;
  border-radius: 3px;
  color: #155724;
}

.gc-item-context {
  font-size: 11px;
  color: #bbb;
}

.gc-item-action {
  flex-shrink: 0;
}

.gc-footer {
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}
</style>
