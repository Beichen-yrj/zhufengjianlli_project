<template>
  <div class="basic-panel">
    <!-- 排版：对齐选择 -->
    <div class="bp-section">
      <h3 class="bp-section-title">排版</h3>
      <div class="align-selector">
        <button v-for="a in aligns" :key="a.value" :class="['align-btn', { active: layout === a.value }]" @click="store.updateBasicInfo({ layout: a.value })" :title="a.label">
          <span v-html="a.icon"></span>
        </button>
      </div>
    </div>

    <!-- 基础信息字段 -->
    <div class="bp-section">
      <h3 class="bp-section-title">基础信息</h3>

      <!-- 头像 -->
      <div class="avatar-row">
        <el-upload action="#" :auto-upload="false" :show-file-list="false" :on-change="onAvatarChange" accept="image/*">
          <div class="avatar-preview" :style="avatarStyle">
            <span v-if="!basic.photo" class="avatar-placeholder">+</span>
          </div>
        </el-upload>
      </div>

      <!-- 字段列表（拖拽排序） -->
      <draggable v-model="localFields" :animation="200" handle=".drag-handle" item-key="key" @end="commitFieldOrder">
        <template #item="{ element: field }">
          <div class="bp-field-row" :class="{ 'bp-field-stealth': !field.visible }">
            <div class="drag-handle" v-if="field.key !== 'name'"><span>⠿</span></div>
            <div class="drag-handle drag-handle-empty" v-else></div>

            <div class="bp-field-content">
              <span class="bp-field-key">{{ fieldLabels[field.key] || field.key }}</span>
              <input
                v-if="field.type !== 'textarea'"
                class="bp-field-input"
                :value="basic[field.key] || ''"
                @input="onFieldChange(field.key, $event.target.value)"
                :placeholder="'请输入' + (fieldLabels[field.key] || field.key)"
              />
              <textarea
                v-else
                class="bp-field-textarea"
                :value="basic[field.key] || ''"
                @input="onFieldChange(field.key, $event.target.value)"
                :placeholder="'请输入' + (fieldLabels[field.key] || field.key)"
                rows="2"
              ></textarea>
            </div>

            <div class="bp-field-actions">
              <button :class="['bp-vis-btn', { active: field.visible }]" @click="toggleField(field.key)" :title="field.visible ? '显示中' : '已隐藏'">
                {{ field.visible ? '👁' : '👁‍🗨' }}
              </button>
              <button v-if="field.key !== 'name' && field.key !== 'title'" class="bp-del-btn" @click="deleteField(field.key)">🗑</button>
            </div>
          </div>
        </template>
      </draggable>
    </div>

    <!-- 自定义字段 -->
    <div class="bp-section">
      <h3 class="bp-section-title">自定义字段</h3>
      <div v-for="cf in customFields" :key="cf.id" class="bp-cf-row">
        <input class="bp-cf-input" v-model="cf.label" placeholder="字段名" @input="commitCustom" />
        <input class="bp-cf-input" v-model="cf.value" placeholder="值" @input="commitCustom" />
        <el-switch v-model="cf.visible" size="small" @change="commitCustom" />
        <button class="bp-del-btn" @click="deleteCustom(cf.id)">🗑</button>
      </div>
      <el-button text size="small" class="bp-add-btn" @click="addCustomField">+ 添加自定义字段</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import draggable from 'vuedraggable'
import { useResumeStore } from '@/stores/resume'

const store = useResumeStore()
const basic = computed(() => store.activeResume?.basic || {})
const layout = computed(() => basic.value?.layout || 'left')

const fieldLabels = { name: '姓名', title: '职位', email: '邮箱', phone: '电话', location: '所在地', birthDate: '出生日期', employementStatus: '在职状态', website: '个人网站' }

const defaultFields = [
  { key: 'name', type: 'text', visible: true },
  { key: 'title', type: 'text', visible: true },
  { key: 'email', type: 'text', visible: true },
  { key: 'phone', type: 'text', visible: true },
  { key: 'location', type: 'text', visible: true },
  { key: 'birthDate', type: 'text', visible: true },
]

const localFields = ref([])
const customFields = ref([])

watch(basic, (v) => {
  if (v?.fieldOrder?.length) {
    localFields.value = v.fieldOrder.map(f => ({ ...f, visible: f.visible ?? true }))
  } else {
    localFields.value = [...defaultFields]
  }
  customFields.value = (v?.customFields || []).map(f => ({ ...f, visible: f.visible ?? true }))
}, { immediate: true, deep: true })

function onFieldChange(key, val) {
  store.updateBasicInfo({ ...basic.value, [key]: val })
}

function toggleField(key) {
  const updated = localFields.value.map(f => f.key === key ? { ...f, visible: !f.visible } : f)
  localFields.value = updated
  store.updateBasicInfo({ ...basic.value, fieldOrder: updated })
}

function deleteField(key) {
  const updated = localFields.value.filter(f => f.key !== key)
  localFields.value = updated
  store.updateBasicInfo({ ...basic.value, fieldOrder: updated })
}

function commitFieldOrder() {
  store.updateBasicInfo({ ...basic.value, fieldOrder: localFields.value })
}

function addCustomField() {
  const cf = { id: Date.now().toString(36), label: '', value: '', icon: 'User', visible: true, displayLabel: false }
  customFields.value.push(cf)
  store.updateBasicInfo({ ...basic.value, customFields: [...customFields.value] })
}

function commitCustom() {
  store.updateBasicInfo({ ...basic.value, customFields: [...customFields.value] })
}

function deleteCustom(id) {
  customFields.value = customFields.value.filter(f => f.id !== id)
  store.updateBasicInfo({ ...basic.value, customFields: [...customFields.value] })
}

async function onAvatarChange(file) {
  const reader = new FileReader()
  reader.onload = (e) => {
    store.updateBasicInfo({
      ...basic.value,
      photo: e.target.result,
      photoConfig: { ...(basic.value?.photoConfig || {}), width: 90, height: 120, borderRadius: 'none', customBorderRadius: 0, visible: true }
    })
  }
  reader.readAsDataURL(file.raw)
}

const avatarStyle = computed(() => ({
  backgroundImage: basic.value?.photo ? `url(${basic.value.photo})` : 'none',
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  width: basic.value?.photoConfig?.width ? `${basic.value.photoConfig.width}px` : '80px',
  height: basic.value?.photoConfig?.height ? `${basic.value.photoConfig.height}px` : '80px',
  borderRadius: basic.value?.photoConfig?.borderRadius === 'full' ? '50%' : `${basic.value?.photoConfig?.customBorderRadius || 4}px`
}))

const aligns = [
  { value: 'left', label: '左对齐', icon: '⫷' },
  { value: 'center', label: '居中', icon: '⫿' },
  { value: 'right', label: '右对齐', icon: '⫸' },
]
</script>

<style scoped>
.basic-panel { padding: 16px; }
.bp-section { margin-bottom: 24px; }
.bp-section-title { font-size: 14px; font-weight: 600; color: #333; margin-bottom: 12px; }

.align-selector { display: flex; gap: 8px; }
.align-btn { width: 40px; height: 36px; border: 1px solid #ddd; border-radius: 6px; background: #fff; cursor: pointer; font-size: 18px; display: flex; align-items: center; justify-content: center; }
.align-btn.active { border-color: #667eea; background: #667eea15; color: #667eea; }
.align-btn:hover { border-color: #667eea; }

.avatar-row { display: flex; justify-content: center; margin-bottom: 16px; }
.avatar-preview { border: 2px dashed #ddd; border-radius: 8px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: border-color 0.2s; }
.avatar-preview:hover { border-color: #667eea; }
.avatar-placeholder { font-size: 24px; color: #ccc; }

.bp-field-row { display: flex; align-items: center; gap: 8px; padding: 8px; border-radius: 6px; margin-bottom: 4px; transition: background 0.15s; }
.bp-field-row:hover { background: #fafafa; }
.bp-field-stealth { opacity: 0.5; }
.drag-handle { cursor: grab; color: #bbb; width: 20px; display: flex; align-items: center; justify-content: center; }
.drag-handle-empty { visibility: hidden; }
.bp-field-content { flex: 1; display: flex; align-items: center; gap: 8px; }
.bp-field-key { font-size: 12px; color: #888; min-width: 40px; text-align: right; }
.bp-field-input { flex: 1; border: 1px solid #e0e0e0; border-radius: 4px; padding: 6px 8px; font-size: 13px; outline: none; }
.bp-field-input:focus { border-color: #667eea; }
.bp-field-textarea { flex: 1; border: 1px solid #e0e0e0; border-radius: 4px; padding: 6px 8px; font-size: 13px; outline: none; resize: vertical; }
.bp-field-actions { display: flex; gap: 4px; }
.bp-vis-btn { background: none; border: none; cursor: pointer; font-size: 14px; padding: 2px; opacity: 0.5; }
.bp-vis-btn.active { opacity: 1; }
.bp-del-btn { background: none; border: none; cursor: pointer; font-size: 14px; padding: 2px; opacity: 0.3; }
.bp-del-btn:hover { opacity: 1; color: #f56c6c; }

.bp-cf-row { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.bp-cf-input { flex: 1; border: 1px solid #e0e0e0; border-radius: 4px; padding: 6px 8px; font-size: 13px; outline: none; }
.bp-cf-input:focus { border-color: #667eea; }
.bp-add-btn { color: #667eea; margin-top: 4px; }
</style>
