<template>
  <div v-if="content" class="c-self-eval">
    <div
      class="c-self-eval-body"
      :style="{
        fontSize: `${gs.baseFontSize || 14}px`,
        lineHeight: gs.lineHeight || 1.7
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
.c-self-eval-body :deep(p) { margin: 4px 0; }
</style>
