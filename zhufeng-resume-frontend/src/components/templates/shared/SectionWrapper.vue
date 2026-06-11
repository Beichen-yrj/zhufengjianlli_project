<template>
  <div
    class="section-wrapper"
    :data-resume-section-id="sectionId"
    :style="mergedStyle"
  >
    <slot />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  sectionId: { type: String, required: true },
  style: { type: Object, default: () => ({}) }
})

const mergedStyle = computed(() => ({ ...props.style }))
</script>

<style scoped>
.section-wrapper {
  border-radius: 6px; transition: all 0.3s ease-in-out;
}
.section-wrapper:hover {
  background-color: #f9f8f3; box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
</style>
