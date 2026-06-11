<template>
  <div v-if="visibleItems.length">
    <div
      v-for="item in visibleItems"
      :key="item.id || item.school"
      class="c-edu-item"
      :style="{ marginTop: `${paragraphSpacing || 8}px` }"
    >
      <div class="c-edu-hd" :class="{ 'c-edu-hd--sidebar': isSidebar }">
        <span
          class="c-edu-school"
          :style="{
            fontSize: `${isSidebar ? (gs.baseFontSize || 14) + 2 : (gs.subheaderSize || 16)}px`,
            color: isSidebar ? '#fff' : 'inherit'
          }"
        >{{ item.school }}</span>
        <span
          class="c-edu-date"
          :style="{
            fontSize: isSidebar ? '12px' : `${gs.subheaderSize || 16}px`,
            color: isSidebar ? '#fff' : 'inherit',
            opacity: isSidebar ? '0.8' : '1'
          }"
        >{{ item.startDate }} ~ {{ item.endDate }}</span>
      </div>
      <div
        class="c-edu-sub"
        :style="{
          fontSize: isSidebar ? '12px' : `${gs.subheaderSize || 16}px`,
          color: isSidebar ? '#fff' : 'inherit',
          opacity: isSidebar ? '0.9' : '1'
        }"
      >
        {{ [item.major, item.degree].filter(Boolean).join(' · ') }}
        <template v-if="item.gpa"> · GPA {{ item.gpa }}</template>
      </div>
      <div
        v-if="item.description"
        class="c-edu-desc"
        :style="{
          fontSize: `${isSidebar ? (gs.baseFontSize || 14) - 2 : (gs.baseFontSize || 14)}px`,
          lineHeight: gs.lineHeight || 1.6,
          color: isSidebar ? '#fff' : 'inherit',
          opacity: isSidebar ? '0.8' : '1'
        }"
        v-html="item.description"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  globalSettings: { type: Object, default: () => ({}) },
  showTitle: { type: Boolean, default: true },
  variant: { type: String, default: 'default' },
  paragraphSpacing: { type: Number, default: 8 }
})

const gs = computed(() => props.globalSettings || {})
const isSidebar = computed(() => props.variant === 'sidebar')
const visibleItems = computed(() => (props.items || []).filter(item => item.visible !== false))
</script>

<style scoped>
.c-edu-item { margin-bottom: 0; }
.c-edu-hd {
  display: flex;
  gap: 16px;
  align-items: center;
  justify-content: space-between;
}
.c-edu-hd--sidebar {
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}
.c-edu-school { font-weight: 700; }
.c-edu-date { flex-shrink: 0; white-space: nowrap; }
.c-edu-sub { margin-top: 2px; }
.c-edu-desc { margin-top: 4px; }
</style>
