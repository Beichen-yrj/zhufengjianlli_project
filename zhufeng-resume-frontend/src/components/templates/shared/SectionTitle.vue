<template>
  <h3
    v-if="showTitle"
    class="section-title"
    :class="{ 'section-title--sidebar': variant === 'sidebar' }"
    :style="titleStyle"
  >
    {{ sectionTitle }}
  </h3>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  sectionTitle: { type: String, default: '' },
  globalSettings: { type: Object, default: () => ({}) },
  showTitle: { type: Boolean, default: true },
  variant: { type: String, default: 'default' } // 'default' | 'sidebar'
})

const isSidebar = computed(() => props.variant === 'sidebar')
const themeColor = computed(() => props.globalSettings?.themeColor || '#000000')

const titleStyle = computed(() => ({
  fontSize: `${isSidebar.value ? (props.globalSettings?.headerSize || 18) - 2 : (props.globalSettings?.headerSize || 18)}px`,
  fontWeight: 'bold',
  color: isSidebar.value ? '#ffffff' : themeColor.value,
  borderColor: isSidebar.value ? 'rgba(255,255,255,0.2)' : themeColor.value,
  marginBottom: isSidebar.value ? '12px' : `${props.globalSettings?.paragraphSpacing || 8}px`
}))
</script>

<style scoped>
.section-title {
  padding-bottom: 0.25rem; border-bottom: 1px solid;
  margin-bottom: 0.5rem; text-transform: uppercase; letter-spacing: 0.05em;
}
.section-title--sidebar { border-bottom: 1px solid rgba(255,255,255,0.2); }
</style>
