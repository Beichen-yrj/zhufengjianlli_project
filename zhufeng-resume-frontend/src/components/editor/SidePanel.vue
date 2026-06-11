<template>
  <div class="side-panel">
    <!-- 布局设置：模块排序 -->
    <div class="sp-card">
      <div class="sp-card-title">📐 模块布局</div>
      <draggable
        :model-value="draggableSections"
        @update:model-value="onReorder"
        :animation="200"
        handle=".sp-drag"
        item-key="id"
      >
        <template #item="{ element: section }">
          <div class="sp-section-item" :class="{ active: section.id === activeSection }" @click="store.setActiveSection(section.id)">
            <span class="sp-drag">⠿</span>
            <span class="sp-sec-icon">{{ section.icon }}</span>
            <span class="sp-sec-name">{{ section.title }}</span>
            <el-switch :model-value="section.enabled" @update:model-value="store.toggleSectionVisibility(section.id)" @click.stop size="small" />
          </div>
        </template>
      </draggable>

      <el-popover trigger="click" placement="bottom-start" :width="200">
        <template #reference>
          <el-button text size="small" class="sp-add-btn">+ 添加模块</el-button>
        </template>
        <div class="sp-module-list">
          <div v-for="m in availableModules" :key="m.id" class="sp-module-item" @click="addStandard(m)">{{ m.icon }} {{ m.label }}</div>
          <div class="sp-module-divider"></div>
          <div class="sp-module-item sp-module-item--custom" @click="addCustom">➕ 自定义模块</div>
        </div>
      </el-popover>
    </div>

    <!-- 主题色 -->
    <div class="sp-card">
      <div class="sp-card-title">🎨 主题色</div>
      <div class="sp-color-grid">
        <button v-for="c in presetColors" :key="c" class="sp-color-btn" :class="{ active: themeColor === c }" :style="{ backgroundColor: c }" @click="store.setThemeColor(c)" />
      </div>
      <div class="sp-custom-color">
        <span class="sp-cc-label">自定义</span>
        <el-color-picker :model-value="themeColor" @update:model-value="store.setThemeColor($event)" size="small" />
      </div>
    </div>

    <!-- 排版 -->
    <div class="sp-card">
      <div class="sp-card-title">🔤 排版</div>
      <div class="sp-ctrl">
        <span class="sp-ctrl-label">字体</span>
        <el-select :model-value="gs.fontFamily || 'default'" @update:model-value="store.updateGlobalSettings({ fontFamily: $event })" size="small" class="sp-select">
          <el-option value="default" label="默认" />
          <el-option value="SimSun" label="宋体" />
          <el-option value="SimHei" label="黑体" />
          <el-option value="KaiTi" label="楷体" />
          <el-option value="Arial" label="Arial" />
        </el-select>
      </div>
      <div class="sp-ctrl">
        <span class="sp-ctrl-label">基础字号</span>
        <el-select :model-value="gs.baseFontSize || 14" @update:model-value="store.updateGlobalSettings({ baseFontSize: Number($event) })" size="small" class="sp-select-num">
          <el-option v-for="s in [12,13,14,15,16,18,20,24]" :key="s" :value="s" :label="s+'px'" />
        </el-select>
      </div>
      <div class="sp-ctrl">
        <span class="sp-ctrl-label">标题字号</span>
        <el-select :model-value="gs.headerSize || 18" @update:model-value="store.updateGlobalSettings({ headerSize: Number($event) })" size="small" class="sp-select-num">
          <el-option v-for="s in [14,15,16,18,20,24,28]" :key="s" :value="s" :label="s+'px'" />
        </el-select>
      </div>
      <div class="sp-ctrl">
        <span class="sp-ctrl-label">副标题字号</span>
        <el-select :model-value="gs.subheaderSize || 16" @update:model-value="store.updateGlobalSettings({ subheaderSize: Number($event) })" size="small" class="sp-select-num">
          <el-option v-for="s in [12,13,14,15,16,18,20,24]" :key="s" :value="s" :label="s+'px'" />
        </el-select>
      </div>
      <div class="sp-ctrl">
        <span class="sp-ctrl-label">行高</span>
        <div class="sp-slider-row">
          <el-slider :model-value="gs.lineHeight || 1.5" @update:model-value="store.updateGlobalSettings({ lineHeight: $event })" :min="1" :max="2" :step="0.1" size="small" />
          <span class="sp-val">{{ gs.lineHeight || 1.5 }}</span>
        </div>
      </div>
    </div>

    <!-- 间距 -->
    <div class="sp-card">
      <div class="sp-card-title">📏 间距</div>
      <div class="sp-ctrl">
        <span class="sp-ctrl-label">页内边距</span>
        <div class="sp-slider-row">
          <el-slider :model-value="gs.pagePadding || 0" @update:model-value="store.updateGlobalSettings({ pagePadding: $event })" :min="0" :max="100" :step="1" size="small" />
          <span class="sp-val">{{ gs.pagePadding || 0 }}</span>
        </div>
      </div>
      <div class="sp-ctrl">
        <span class="sp-ctrl-label">模块间距</span>
        <div class="sp-slider-row">
          <el-slider :model-value="gs.sectionSpacing || 24" @update:model-value="store.updateGlobalSettings({ sectionSpacing: $event })" :min="1" :max="100" :step="1" size="small" />
          <span class="sp-val">{{ gs.sectionSpacing || 24 }}</span>
        </div>
      </div>
      <div class="sp-ctrl">
        <span class="sp-ctrl-label">段落间距</span>
        <div class="sp-slider-row">
          <el-slider :model-value="gs.paragraphSpacing || 12" @update:model-value="store.updateGlobalSettings({ paragraphSpacing: $event })" :min="1" :max="50" :step="1" size="small" />
          <span class="sp-val">{{ gs.paragraphSpacing || 12 }}</span>
        </div>
      </div>
    </div>

    <!-- 模式 -->
    <div class="sp-card">
      <div class="sp-card-title">⚙️ 模式</div>
      <div class="sp-ctrl sp-ctrl--row">
        <span class="sp-ctrl-label">图标模式</span>
        <el-switch :model-value="gs.useIconMode" @update:model-value="store.updateGlobalSettings({ useIconMode: $event })" size="small" />
      </div>
      <div class="sp-ctrl sp-ctrl--row">
        <span class="sp-ctrl-label">副标题居中</span>
        <el-switch :model-value="gs.centerSubtitle" @update:model-value="store.updateGlobalSettings({ centerSubtitle: $event })" size="small" />
      </div>
      <div class="sp-ctrl sp-ctrl--row">
        <span class="sp-ctrl-label">灵活标题布局</span>
        <el-switch :model-value="gs.flexibleHeaderLayout" @update:model-value="store.updateGlobalSettings({ flexibleHeaderLayout: $event })" size="small" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import draggable from 'vuedraggable'
import { useResumeStore } from '@/stores/resume'

const props = defineProps({
  activeSection: { type: String, default: 'basic' }
})

const store = useResumeStore()

const gs = computed(() => store.activeResume?.globalSettings || {})
const themeColor = computed(() => gs.value.themeColor || '#667eea')
const allSections = computed(() => store.activeResume?.menuSections || [])
const draggableSections = computed(() => allSections.value.filter(s => s.id !== 'basic'))

const presetColors = ['#667eea','#764ba2','#f56c6c','#e6a23c','#67c23a','#409eff','#e040fb','#ff5722','#009688','#795548']

const STANDARD = {
  education: { id: 'education', icon: '🎓', label: '教育经历' },
  experience: { id: 'experience', icon: '💼', label: '工作经历' },
  projects: { id: 'projects', icon: '🚀', label: '项目经历' },
  skills: { id: 'skills', icon: '⚡', label: '技能特长' },
  selfEvaluation: { id: 'selfEvaluation', icon: '💬', label: '自我评价' },
  certificates: { id: 'certificates', icon: '📜', label: '证书' },
}

const availableModules = computed(() => {
  const ids = new Set(allSections.value.map(s => s.id))
  return Object.values(STANDARD).filter(m => !ids.has(m.id))
})

function onReorder(newOrder) {
  store.reorderSections(newOrder)
}

function addStandard(mod) {
  const sections = [...allSections.value, { id: mod.id, title: mod.label, icon: mod.icon, enabled: true, order: allSections.value.length }]
  store.updateMenuSections(sections)
}

function addCustom() {
  const n = allSections.value.filter(s => s.id.startsWith('custom')).length + 1
  const sections = [...allSections.value, { id: `custom-${n}`, title: `自定义${n}`, icon: '📌', enabled: true, order: allSections.value.length }]
  store.updateMenuSections(sections)
  store.addCustomData(`custom-${n}`)
}
</script>

<style scoped>
.side-panel { padding: 12px 8px; overflow-y: auto; height: 100%; }
.sp-card { margin-bottom: 16px; }
.sp-card-title { font-size: 12px; font-weight: 600; color: #999; text-transform: uppercase; margin-bottom: 8px; padding-left: 4px; letter-spacing: 0.5px; }

.sp-section-item { display: flex; align-items: center; gap: 8px; padding: 8px; border-radius: 6px; cursor: pointer; transition: background 0.15s; margin-bottom: 2px; }
.sp-section-item:hover { background: #f5f5ff; }
.sp-section-item.active { background: #667eea15; }
.sp-drag { cursor: grab; color: #bbb; font-size: 12px; user-select: none; }
.sp-sec-icon { font-size: 15px; }
.sp-sec-name { flex: 1; font-size: 13px; color: #444; }

.sp-add-btn { color: #667eea; font-size: 12px; margin-top: 4px; }
.sp-module-list { display: flex; flex-direction: column; }
.sp-module-item { padding: 8px 12px; cursor: pointer; font-size: 13px; border-radius: 6px; }
.sp-module-item:hover { background: #f5f5f5; }
.sp-module-item--custom { color: #999; font-style: italic; }
.sp-module-divider { height: 1px; background: #eee; margin: 4px 0; }

.sp-color-grid { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 8px; }
.sp-color-btn { width: 24px; height: 24px; border-radius: 50%; border: 2px solid transparent; cursor: pointer; transition: transform 0.15s; }
.sp-color-btn:hover { transform: scale(1.15); }
.sp-color-btn.active { border-color: #333; transform: scale(1.15); }
.sp-custom-color { display: flex; align-items: center; gap: 8px; }
.sp-cc-label { font-size: 12px; color: #999; }

.sp-ctrl { margin-bottom: 10px; }
.sp-ctrl--row { display: flex; align-items: center; justify-content: space-between; }
.sp-ctrl-label { font-size: 12px; color: #888; display: block; margin-bottom: 3px; }
.sp-select { width: 100%; }
.sp-select-num { width: 80px; }
.sp-slider-row { display: flex; align-items: center; gap: 8px; }
.sp-slider-row .el-slider { flex: 1; }
.sp-val { font-size: 12px; color: #999; min-width: 28px; text-align: right; }
</style>
