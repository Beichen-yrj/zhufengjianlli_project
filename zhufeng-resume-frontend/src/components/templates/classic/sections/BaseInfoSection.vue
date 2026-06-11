<template>
  <div class="c-basic-info" :class="[`align-${layout}`]">
    <!-- 照片区域 -->
    <div v-if="basic.photo && basic.photoConfig?.visible" class="c-photo-wrap" :class="photoAlignClass">
      <div
        class="c-photo"
        :style="{
          width: `${basic.photoConfig?.width || 100}px`,
          height: `${basic.photoConfig?.height || 100}px`,
          borderRadius: photoBorderRadius
        }"
      >
        <img :src="basic.photo" :alt="`${basic.name}的照片`" class="c-photo-img" />
      </div>
    </div>

    <!-- 姓名和职位 -->
    <div class="c-name-title" :class="nameTitleAlign">
      <h1
        v-if="nameField?.visible !== false && basic.name"
        class="c-name"
        :style="{ fontSize: '30px' }"
      >{{ basic.name }}</h1>
      <h2
        v-if="titleField?.visible !== false && basic.title"
        class="c-title"
        :style="{ fontSize: '18px' }"
      >{{ basic.title }}</h2>
    </div>

    <!-- 联系方式 -->
    <div v-if="visibleFields.length" class="c-contact" :class="fieldsAlign" :style="{ fontSize: `${gs.baseFontSize || 14}px`, color: '#4b5563' }">
      <template v-for="field in visibleFields" :key="field.key">
        <span v-if="!useIconMode" class="c-contact-item">
          <span class="c-contact-label">{{ field.label }}:</span>
          <a v-if="field.key === 'email'" :href="`mailto:${field.value}`" class="c-contact-link">{{ field.value }}</a>
          <span v-else>{{ field.value }}</span>
        </span>
        <span v-else class="c-contact-item c-contact-icon">
          <span v-if="field.key === 'email'">
            <a :href="`mailto:${field.value}`" class="c-contact-link">{{ field.value }}</a>
          </span>
          <span v-else>{{ field.value }}</span>
        </span>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  basic: { type: Object, default: () => ({}) },
  globalSettings: { type: Object, default: () => ({}) },
  template: { type: Object, default: () => ({}) }
})

const gs = computed(() => props.globalSettings || {})
const useIconMode = computed(() => gs.value.useIconMode ?? false)
const layout = computed(() => props.basic?.layout || props.template?.basic?.layout || 'left')

// Field ordering
const nameField = computed(() => props.basic?.fieldOrder?.find(f => f.key === 'name') || { key: 'name', label: '姓名', visible: true })
const titleField = computed(() => props.basic?.fieldOrder?.find(f => f.key === 'title') || { key: 'title', label: '职位', visible: true })

const visibleFields = computed(() => {
  const basic = props.basic || {}
  if (!basic.fieldOrder) {
    const fields = []
    if (basic.email) fields.push({ key: 'email', value: basic.email, label: '电子邮箱' })
    if (basic.phone) fields.push({ key: 'phone', value: basic.phone, label: '电话' })
    if (basic.birthDate) fields.push({ key: 'birthDate', value: basic.birthDate, label: '出生日期' })
    if (basic.location) fields.push({ key: 'location', value: basic.location, label: '所在地' })
    return fields
  }
  return basic.fieldOrder
    .filter(f => f.visible !== false && f.key !== 'name' && f.key !== 'title')
    .map(f => ({
      key: f.key,
      value: basic[f.key] || '',
      label: f.label || '',
      visible: f.visible
    }))
    .filter(f => Boolean(f.value))
})

const photoBorderRadius = computed(() => {
  const config = props.basic?.photoConfig || {}
  switch (config.borderRadius) {
    case 'full': return '50%'
    case 'rounded': return '8px'
    default: return `${config.customBorderRadius || 0}px`
  }
})

const photoAlignClass = computed(() => {
  if (layout.value === 'center') return 'flex-col-center'
  return 'flex-row'
})

const nameTitleAlign = computed(() => {
  if (layout.value === 'center') return 'text-center'
  if (layout.value === 'right') return 'text-right'
  return 'text-left'
})

const fieldsAlign = computed(() => {
  if (layout.value === 'center') return 'justify-center'
  return ''
})
</script>

<style scoped>
.c-basic-info { padding-bottom: 20px; }
.align-center { text-align: center; display: flex; flex-direction: column; align-items: center; gap: 12px; }
.align-left   { text-align: left; }
.align-right  { text-align: right; }

.c-photo-wrap { margin-bottom: 8px; }
.flex-col-center { display: flex; justify-content: center; }
.flex-row { display: flex; }

.c-photo { overflow: hidden; }
.c-photo-img { width: 100%; height: 100%; object-fit: cover; }

.text-center { text-align: center; }
.text-left { text-align: left; min-width: 0; }
.text-right { text-align: right; min-width: 0; }

.c-name-title { display: flex; flex-direction: column; }

.c-name {
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 4px;
  white-space: pre-wrap;
  word-break: break-word;
}

.c-title {
  color: #666;
  margin: 0 0 10px;
  white-space: pre-wrap;
  word-break: break-word;
}

.c-contact {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  width: 100%;
}
.justify-center { justify-content: center; }

.c-contact-item { display: flex; align-items: flex-start; gap: 4px; }
.c-contact-icon { gap: 4px; }
.c-contact-label { flex-shrink: 0; }
.c-contact-link { text-decoration: underline; word-break: break-word; }
</style>
