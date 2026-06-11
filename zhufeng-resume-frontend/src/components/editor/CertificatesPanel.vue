<template>
  <div class="certs-panel">
    <p class="certs-hint">支持粘贴图片或点击上传（图片会以Base64存储）</p>

    <div v-if="!items.length" class="certs-empty">
      暂无证书，点击下方按钮添加
    </div>

    <draggable
      v-else
      :model-value="items"
      @update:model-value="store.updateCertificatesBatch($event)"
      :animation="200"
      handle=".cert-drag"
      item-key="id"
    >
      <template #item="{ element: cert }">
        <div class="cert-item">
          <span class="cert-drag">⠿</span>
          <img :src="cert.url" class="cert-thumb" />
          <div class="cert-controls">
            <el-slider v-model="cert.width" :min="10" :max="100" size="small" style="width:100px" />
            <span class="cert-size">{{ cert.width || 50 }}%</span>
            <el-button text size="small" @click="store.removeCertificate(cert.id)">🗑</el-button>
          </div>
        </div>
      </template>
    </draggable>

    <input ref="fileInput" type="file" accept="image/*" multiple hidden @change="onFileChange" />
    <el-button class="certs-add" @click="$refs.fileInput.click()">
      + 添加证书图片
    </el-button>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import draggable from 'vuedraggable'
import { useResumeStore } from '@/stores/resume'

const store = useResumeStore()
const items = computed(() => store.activeResume?.certificates || [])
const fileInput = ref(null)

function toBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result)
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

async function onFileChange(e) {
  for (const file of e.target.files) {
    if (!file.type.startsWith('image/')) continue
    try {
      const url = await toBase64(file)
      store.addCertificate({ id: Date.now().toString(36) + Math.random().toString(36).slice(2), url, width: 50 })
    } catch (_) {}
  }
  if (fileInput.value) fileInput.value.value = ''
}
</script>

<style scoped>
.certs-panel { padding: 12px; }
.certs-hint { font-size: 12px; color: #999; margin-bottom: 12px; }
.certs-empty { padding: 32px; text-align: center; color: #ccc; border: 1px dashed #ddd; border-radius: 8px; font-size: 13px; }
.cert-item { display: flex; align-items: center; gap: 8px; padding: 8px; border: 1px solid #eee; border-radius: 8px; margin-bottom: 8px; }
.cert-drag { cursor: grab; color: #bbb; }
.cert-thumb { width: 48px; height: 48px; object-fit: contain; border-radius: 4px; background: #f5f5f5; }
.cert-controls { flex: 1; display: flex; align-items: center; gap: 6px; }
.cert-size { font-size: 11px; color: #999; min-width: 30px; }
.certs-add { margin-top: 8px; width: 100%; }
</style>
