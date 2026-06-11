<template>
  <div v-if="content" class="c-skill">
    <div
      class="c-skill-body"
      :style="{
        fontSize: `${gs.baseFontSize || 14}px`,
        lineHeight: gs.lineHeight || 1.6
      }"
      v-html="content"
    ></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  content: { type: String, default: '' },
  globalSettings: { type: Object, default: () => ({}) },
  showTitle: { type: Boolean, default: true }
})

const gs = computed(() => props.globalSettings || {})
</script>

<style scoped>
.c-skill-body :deep(p) { margin: 4px 0; }
.c-skill-body :deep(ul) { padding-left: 18px; margin: 4px 0; }
</style>
