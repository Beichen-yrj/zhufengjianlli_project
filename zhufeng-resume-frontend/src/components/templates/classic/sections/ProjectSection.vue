<template>
  <div v-if="visibleItems.length">
    <div
      v-for="item in visibleItems"
      :key="item.id || item.name"
      class="c-proj-item"
      :style="{ marginTop: `${paragraphSpacing || 8}px` }"
    >
      <div class="c-proj-hd">
        <span
          class="c-proj-name"
          :style="{ fontSize: `${gs.subheaderSize || 16}px` }"
        >{{ item.name }}</span>
        <span
          class="c-proj-date"
          :style="{ fontSize: `${gs.subheaderSize || 16}px` }"
        >{{ item.date }}</span>
      </div>
      <div
        v-if="item.role"
        class="c-proj-role"
        :style="{ fontSize: `${gs.subheaderSize || 16}px` }"
      >{{ item.role }}</div>
      <div
        v-if="item.description"
        class="c-proj-desc"
        :style="{
          fontSize: `${gs.baseFontSize || 14}px`,
          lineHeight: gs.lineHeight || 1.6
        }"
      >{{ item.description }}</div>
      <a
        v-if="item.link"
        :href="item.link"
        target="_blank"
        class="c-proj-link"
      >{{ item.linkLabel || '项目链接' }}</a>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  globalSettings: { type: Object, default: () => ({}) },
  showTitle: { type: Boolean, default: true },
  paragraphSpacing: { type: Number, default: 8 }
})

const gs = computed(() => props.globalSettings || {})
const visibleItems = computed(() => (props.items || []).filter(item => item.visible !== false))
</script>

<style scoped>
.c-proj-item { margin-bottom: 0; }
.c-proj-hd {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: space-between;
}
.c-proj-name { font-weight: 700; }
.c-proj-date { flex-shrink: 0; white-space: nowrap; }
.c-proj-role { margin-top: 2px; }
.c-proj-desc { margin-top: 4px; }
.c-proj-link {
  font-size: 12px;
  color: #4a90d9;
  text-decoration: none;
  margin-top: 4px;
  display: inline-block;
}
</style>
