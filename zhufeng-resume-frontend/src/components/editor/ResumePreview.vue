<template>
  <div class="resume-preview" :style="previewStyle" ref="previewRef">
    <div class="resume-paper">
      <!-- 姓名和基本信息 -->
      <div class="basic-section">
        <h1 class="name">{{ resumeData.basicInfo?.name || '姓名' }}</h1>
        <div class="contact-info">
          <span v-if="resumeData.basicInfo?.phone">📱 {{ resumeData.basicInfo.phone }}</span>
          <span v-if="resumeData.basicInfo?.email">✉️ {{ resumeData.basicInfo.email }}</span>
          <span v-if="resumeData.basicInfo?.jobIntention">💼 {{ resumeData.basicInfo.jobIntention }}</span>
        </div>
      </div>

      <!-- 教育经历 -->
      <div v-if="resumeData.education?.length" class="section">
        <h2 class="section-title">教育经历</h2>
        <div v-for="(item, index) in resumeData.education" :key="index" class="item">
          <div class="item-header">
            <span class="item-title">{{ item.school }}</span>
            <span class="item-date">{{ item.dateRange?.join(' - ') }}</span>
          </div>
          <div class="item-content">{{ item.major }} | {{ item.degree }}</div>
        </div>
      </div>

      <!-- 工作经历 -->
      <div v-if="resumeData.experience?.length" class="section">
        <h2 class="section-title">工作经历</h2>
        <div v-for="(item, index) in resumeData.experience" :key="index" class="item">
          <div class="item-header">
            <span class="item-title">{{ item.company }}</span>
            <span class="item-sub">{{ item.position }}</span>
            <span class="item-date">{{ item.dateRange?.join(' - ') }}</span>
          </div>
          <div class="item-content">{{ item.description }}</div>
        </div>
      </div>

      <!-- 项目经历 -->
      <div v-if="resumeData.projects?.length" class="section">
        <h2 class="section-title">项目经历</h2>
        <div v-for="(item, index) in resumeData.projects" :key="index" class="item">
          <div class="item-header">
            <span class="item-title">{{ item.name }}</span>
            <span class="item-sub">{{ item.role }}</span>
          </div>
          <div class="item-content">{{ item.description }}</div>
        </div>
      </div>

      <!-- 技能特长 -->
      <div v-if="resumeData.skills?.length" class="section">
        <h2 class="section-title">技能特长</h2>
        <div class="skills">
          <el-tag v-for="(skill, index) in resumeData.skills" :key="index">
            {{ skill }}
          </el-tag>
        </div>
      </div>

      <!-- 自我评价 -->
      <div v-if="resumeData.selfEvaluation" class="section">
        <h2 class="section-title">自我评价</h2>
        <div class="item-content">{{ resumeData.selfEvaluation }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const previewRef = ref(null)

// 暴露给父组件调用
defineExpose({ previewRef })

const props = defineProps({
  resumeData: Object,
  templateConfig: Object
})

const previewStyle = computed(() => ({
  '--primary-color': props.templateConfig?.primaryColor || '#667eea'
}))
</script>

<style scoped>
.resume-preview {
  width: 100%;
  max-width: 595px;
  background: #f0f0f0;
  padding: 20px;
}

.resume-paper {
  background: white;
  padding: 40px;
  min-height: 842px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.basic-section {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid var(--primary-color);
}

.name {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px;
}

.contact-info {
  display: flex;
  justify-content: center;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.section {
  margin-bottom: 25px;
}

.section-title {
  font-size: 16px;
  color: var(--primary-color);
  border-left: 3px solid var(--primary-color);
  padding-left: 10px;
  margin-bottom: 15px;
}

.item {
  margin-bottom: 12px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.item-title {
  font-weight: bold;
  color: #333;
}

.item-sub {
  color: #666;
  font-size: 13px;
}

.item-date {
  color: #999;
  font-size: 12px;
}

.item-content {
  color: #666;
  font-size: 13px;
  line-height: 1.6;
}

.skills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>