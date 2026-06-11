<template>
  <div v-if="visibleItems.length">
    <div
      v-for="item in visibleItems"
      :key="item.id || item.company"
      class="c-exp-item"
      :style="{ marginTop: `${paragraphSpacing || 8}px` }"
    >
      <div class="c-exp-hd">
        <span
          class="c-exp-company"
          :style="{ fontSize: `${gs.subheaderSize || 16}px` }"
        >{{ item.company }}</span>
        <span
          class="c-exp-date"
          :style="{ fontSize: `${gs.subheaderSize || 16}px` }"
        >{{ item.date }}</span>
      </div>
      <div
        v-if="item.position"
        class="c-exp-pos"
        :style="{ fontSize: `${gs.subheaderSize || 16}px` }"
      >{{ item.position }}</div>
      <div
        v-if="item.details"
        class="c-exp-detail"
        :style="{
          fontSize: `${gs.baseFontSize || 14}px`,
          lineHeight: gs.lineHeight || 1.6
        }"
        v-html="item.details"
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
  paragraphSpacing: { type: Number, default: 8 }
})

const gs = computed(() => props.globalSettings || {})
const visibleItems = computed(() => (props.items || []).filter(item => item.visible !== false))
</script>

<style scoped>
.c-exp-item { margin-bottom: 0; }
.c-exp-hd {
  display: flex;
  gap: 8px;
  align-items: center;
}
.c-exp-company {
  font-weight: 700;
  flex: 1.5;
}
.c-exp-date {
  flex-shrink: 0;
  flex: 1;
  text-align: right;
}
.c-exp-pos { margin-top: 2px; }
.c-exp-detail { margin-top: 4px; }
.c-exp-detail :deep(p) { margin: 2px 0; }
.c-exp-detail :deep(ul) { padding-left: 18px; margin: 4px 0; }
</style>
