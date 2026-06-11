<template>
  <div class="preview-dock">
    <div class="dock-container">
      <!-- 切换模板 -->
      <el-tooltip content="切换模板" placement="left">
        <button class="dock-btn" @click="$emit('switchTemplate')">
          <span class="dock-icon">🎨</span>
        </button>
      </el-tooltip>

      <!-- 语法检查 -->
      <el-tooltip content="语法检查" placement="left">
        <button class="dock-btn" :class="{ 'dock-btn--pulse': grammarChecking }" @click="$emit('grammarCheck')">
          <span class="dock-icon">🔍</span>
        </button>
      </el-tooltip>

      <!-- 自动一页 -->
      <el-tooltip :content="autoOnePage ? '关闭自动一页' : '开启自动一页'" placement="left">
        <button
          class="dock-btn"
          :class="{ 'dock-btn--active': autoOnePage }"
          @click="toggleAutoOnePage"
        >
          <span class="dock-icon">📄</span>
        </button>
      </el-tooltip>

      <!-- 导出 PDF -->
      <el-tooltip content="导出 PDF" placement="left">
        <button class="dock-btn" @click="$emit('export')">
          <span class="dock-icon">📥</span>
        </button>
      </el-tooltip>

      <!-- 复制简历 -->
      <el-tooltip content="复制简历" placement="left">
        <button class="dock-btn" @click="$emit('copyResume')">
          <span class="dock-icon">📋</span>
        </button>
      </el-tooltip>

      <span class="dock-divider"></span>

      <!-- 侧边栏切换 -->
      <el-tooltip :content="sideCollapsed ? '展开侧边栏' : '收起侧边栏'" placement="left">
        <button
          class="dock-btn dock-btn--square"
          :class="{ 'dock-btn--active': !sideCollapsed }"
          @click="$emit('toggleSide')"
        >
          <span class="dock-icon-small">◧</span>
        </button>
      </el-tooltip>

      <!-- 编辑面板切换 -->
      <el-tooltip :content="editCollapsed ? '展开编辑面板' : '收起编辑面板'" placement="left">
        <button
          class="dock-btn dock-btn--square"
          :class="{ 'dock-btn--active': !editCollapsed }"
          @click="$emit('toggleEdit')"
        >
          <span class="dock-icon-small">✎</span>
        </button>
      </el-tooltip>

      <!-- 预览切换 -->
      <el-tooltip :content="previewCollapsed ? '展开预览' : '收起预览'" placement="left">
        <button
          class="dock-btn dock-btn--square"
          :class="{ 'dock-btn--active': !previewCollapsed }"
          @click="$emit('togglePreview')"
        >
          <span class="dock-icon-small">◉</span>
        </button>
      </el-tooltip>

      <span class="dock-divider"></span>

      <!-- 返回仪表盘 -->
      <el-tooltip content="返回仪表盘" placement="left">
        <button class="dock-btn" @click="goToDashboard">
          <span class="dock-icon">🏠</span>
        </button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  sideCollapsed: { type: Boolean, default: false },
  editCollapsed: { type: Boolean, default: false },
  previewCollapsed: { type: Boolean, default: false },
  autoOnePage: { type: Boolean, default: false },
  grammarChecking: { type: Boolean, default: false }
})

defineEmits([
  'switchTemplate', 'grammarCheck', 'export',
  'copyResume', 'toggleSide', 'toggleEdit', 'togglePreview'
])

function toggleAutoOnePage() {
  // Will be handled by parent
}

function goToDashboard() {
  router.push('/dashboard')
}
</script>

<style scoped>
.preview-dock {
  position: fixed;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  z-index: 50;
}

.dock-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(12px);
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  padding: 8px 6px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.dock-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
  color: #888;
}
.dock-btn:hover { background: #f0f0f0; color: #333; }
.dock-btn--active { background: rgba(102,126,234,0.1); color: #667eea; }
.dock-btn--square { border-radius: 4px; }
.dock-btn--pulse { animation: dock-pulse 1.5s infinite; }

.dock-icon { font-size: 16px; line-height: 1; }
.dock-icon-small { font-size: 14px; line-height: 1; }

.dock-divider {
  width: 20px;
  height: 1px;
  background: #e8e8e8;
  margin: 4px auto;
}

@keyframes dock-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
