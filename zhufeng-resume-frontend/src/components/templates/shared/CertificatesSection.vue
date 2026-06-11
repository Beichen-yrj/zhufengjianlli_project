<template>
  <div v-if="certificates && certificates.length" class="certificates-section">
    <div
      v-for="cert in certificates"
      :key="cert.id"
      class="cert-item"
      :style="{ width: `calc(${cert.width || 50}% - 8px)` }"
    >
      <img :src="cert.url" alt="Certificate" class="cert-img" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  certificates: { type: Array, default: () => [] }
})
</script>

<style scoped>
.certificates-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  width: 100%;
  margin-top: 8px;
}
.cert-item {
  display: flex;
  justify-content: center;
  max-width: 100%;
}
.cert-img {
  width: 100%;
  height: auto;
  object-fit: contain;
}
</style>
