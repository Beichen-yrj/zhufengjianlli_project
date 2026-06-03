<template>
  <div class="templates-page">
    <div class="header">
      <h1>选择简历模板</h1>
      <p>精选多款专业简历模板，找到最适合你的风格</p>
    </div>

    <div class="template-list">
      <div 
        v-for="template in templates" 
        :key="template.id" 
        class="template-item"
        @click="selectTemplate(template)"
      >
        <div class="template-preview">
          <img :src="template.previewImage" :alt="template.name" />
        </div>
        <div class="template-info">
          <h3>{{ template.name }}</h3>
          <span class="tag">{{ template.category }}</span>
          <p>{{ template.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTemplates } from '../api/template'
import { ElMessage } from 'element-plus'

const router = useRouter()
const templates = ref([])

onMounted(async () => {
  try {
    const res = await getTemplates()
    templates.value = res.data
  } catch (error) {
    ElMessage.error('加载模板失败')
  }
})

const selectTemplate = (template) => {
  router.push(`/editor?templateId=${template.id}`)
}
</script>

<style scoped>
.templates-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40px 20px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.header h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.header p {
  color: #666;
  font-size: 16px;
}

.template-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.template-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.template-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.template-preview {
  aspect-ratio: 3/4;
  background: #f0f0f0;
}

.template-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.template-info {
  padding: 16px;
}

.template-info h3 {
  margin: 0 0 8px;
  font-size: 18px;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  background: #667eea;
  color: white;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 8px;
}

.template-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}
</style>