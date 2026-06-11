<template>
  <div class="editor-floating-bar" :class="{ hidden: isMobile, collapsed: !expanded }">
    <!-- 收起状态：小三角触发器（点击展开） -->
    <div v-if="!expanded" class="bar-trigger" @click="expanded = true" title="展开工具栏">
      <svg width="14" height="20" viewBox="0 0 12 16" fill="none">
        <path d="M10 1L2 8L10 15" stroke="#667eea" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </div>
    <!-- 展开状态：完整工具栏 -->
    <template v-else>
      <div class="bar-header">
        <span class="bar-title">工具</span>
        <button class="bar-collapse-btn" @click="expanded = false" title="收起">
          <svg width="10" height="14" viewBox="0 0 10 14" fill="none">
            <path d="M2 13L8 7L2 1" stroke="#667eea" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
      <div class="bar-inner">
        <button
          v-for="(item, index) in toolbarItems"
          :key="item.key"
          class="bar-btn"
          :class="{ active: item.active?.() }"
          :data-tooltip="item.label"
          @click="handleClick(item)"
        >
          <span v-html="item.icon" class="btn-icon"></span>
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  showForm: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits([
  'toggle-form',
  'switch-template',
  'ai-analyze',
  'export-pdf',
  'copy-resume',
  'print',
  'fullscreen',
  'back-to-dashboard'
])

const isMobile = ref(false)
const expanded = ref(true) // 默认展开，可改为 false 默认收起

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})

const sidebarIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2"/><line x1="9" y1="3" x2="9" y2="21"/></svg>`

const templateIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14,2 14,8 20,8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10,9 9,9 8,9"/></svg>`

const aiIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2z"/></svg>`

const downloadIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7,10 12,15 17,10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>`

const copyIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/></svg>`

const printIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6,9 6,2 18,2 18,9"/><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/><rect x="6" y="14" width="12" height="8"/></svg>`

const fullscreenIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15,3 21,3 21,9"/><polyline points="9,21 3,21 3,15"/><line x1="21" y1="3" x2="14" y2="10"/><line x1="3" y1="21" x2="10" y2="14"/></svg>`

const backIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12,19 5,12 12,5"/></svg>`

const helpIcon = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><point cx="12" cy="17"/></svg>`

const toolbarItems = computed(() => [
  {
    key: 'sidebar',
    label: props.showForm ? '收起侧边栏' : '展开侧边栏',
    icon: sidebarIcon,
    event: 'toggle-form',
    active: () => props.showForm
  },
  {
    key: 'template',
    label: '切换模板',
    icon: templateIcon,
    event: 'switch-template'
  },
  {
    key: 'ai',
    label: 'AI 分析',
    icon: aiIcon,
    event: 'ai-analyze'
  },
  {
    key: 'export',
    label: '导出 PDF',
    icon: downloadIcon,
    event: 'export-pdf'
  },
  {
    key: 'copy',
    label: '复制简历',
    icon: copyIcon,
    event: 'copy-resume'
  },
  {
    key: 'print',
    label: '打印',
    icon: printIcon,
    event: 'print'
  },
  {
    key: 'fullscreen',
    label: '全屏预览',
    icon: fullscreenIcon,
    event: 'fullscreen'
  },
  {
    key: 'back',
    label: '返回工作台',
    icon: backIcon,
    event: 'back-to-dashboard'
  },
  {
    key: 'help',
    label: '帮助',
    icon: helpIcon,
    event: null
  }
])

const handleClick = (item) => {
  if (item.event) {
    emit(item.event)
  }
}
</script>

<style scoped>
.editor-floating-bar {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 9999;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.editor-floating-bar.hidden {
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
}

/* 收起状态：小三角 */
.editor-floating-bar.collapsed {
  right: 0;
}
.bar-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 64px;
  background: #fff;
  border-radius: 8px 0 0 8px;
  box-shadow: -2px 2px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.2s ease;
}
.bar-trigger:hover {
  background: #667eea;
  box-shadow: -2px 2px 16px rgba(102, 126, 234, 0.3);
}
.bar-trigger:hover svg path {
  stroke: #fff;
}

/* 展开状态 */
.bar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px 4px;
}
.bar-title {
  font-size: 11px;
  font-weight: 600;
  color: #999;
  letter-spacing: 1px;
  text-transform: uppercase;
}
.bar-collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border: 1.5px solid #e0e4ec;
  background: #f7f8fc;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.15s ease;
}
.bar-collapse-btn:hover {
  background: #667eea;
  border-color: #667eea;
}
.bar-collapse-btn:hover svg path {
  stroke: #fff;
}

.bar-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 44px;
  padding: 4px 0 10px;
  background: #ffffff;
  border-radius: 10px 0 0 10px;
  box-shadow: -2px 0 16px rgba(0, 0, 0, 0.08), 0 2px 6px rgba(0, 0, 0, 0.04);
  gap: 2px;
}

.bar-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  color: #555;
  transition: all 0.2s ease;
  outline: none;
}

.bar-btn:hover {
  background: #f0f2f5;
  color: #2563eb;
}

.bar-btn.active {
  background: #eff6ff;
  color: #2563eb;
}

.bar-btn:active {
  transform: scale(0.92);
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
}

.btn-icon :deep(svg) {
  width: 100%;
  height: 100%;
}

/* Tooltip - 纯 CSS 实现，向左弹出 */
.bar-btn::before {
  content: attr(data-tooltip);
  position: absolute;
  right: calc(100% + 8px);
  top: 50%;
  transform: translateY(-50%);
  padding: 6px 12px;
  background: #1e293b;
  color: #fff;
  font-size: 12px;
  white-space: nowrap;
  border-radius: 6px;
  pointer-events: none;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.2s ease, visibility 0.2s ease, transform 0.2s ease;
  transform: translateY(-50%) translateX(4px);
  z-index: 10000;
  line-height: 1.4;
  font-weight: 400;
}

/* Tooltip 箭头 */
.bar-btn::after {
  content: '';
  position: absolute;
  right: calc(100% + 2px);
  top: 50%;
  transform: translateY(-50%);
  border: 5px solid transparent;
  border-left-color: #1e293b;
  pointer-events: none;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.2s ease, visibility 0.2s ease;
  z-index: 10000;
}

.bar-btn:hover::before,
.bar-btn:hover::after {
  opacity: 1;
  visibility: visible;
  transform: translateY(-50%) translateX(0);
}

.bar-btn:hover::after {
  transform: translateY(-50%);
}

/* 响应式：平板设备缩小间距 */
@media (max-width: 1024px) {
  .bar-inner {
    width: 40px;
    padding: 4px 0 8px;
  }

  .bar-btn {
    width: 36px;
    height: 36px;
  }
}
</style>
