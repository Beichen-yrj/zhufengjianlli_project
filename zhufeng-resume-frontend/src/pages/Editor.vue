<template>
  <div class="editor-container">
    <!-- 顶部工具栏 -->
    <div class="editor-toolbar">
      <div class="toolbar-left">
        <el-button @click="$router.push('/dashboard')">返回</el-button>
        <el-input v-model="resume.title" class="title-input" placeholder="简历标题" />
      </div>
      <div class="toolbar-right">
        <el-button type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
        <el-button type="success" @click="handleExport">导出PDF</el-button>
      </div>
    </div>

    <!-- 编辑器主体 -->
    <div class="editor-main">
      <!-- 左侧编辑面板 -->
      <div class="editor-sidebar">
        <el-tabs v-model="activeTab" type="border-card">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <BasicInfoForm v-model="resumeData.basicInfo" />
          </el-tab-pane>
          
          <!-- 教育经历 -->
          <el-tab-pane label="教育经历" name="education">
            <EducationForm v-model="resumeData.education" />
          </el-tab-pane>
          
          <!-- 工作经历 -->
          <el-tab-pane label="工作经历" name="experience">
            <ExperienceForm v-model="resumeData.experience" />
          </el-tab-pane>
          
          <!-- 项目经历 -->
          <el-tab-pane label="项目经历" name="projects">
            <ProjectsForm v-model="resumeData.projects" />
          </el-tab-pane>
          
          <!-- 技能特长 -->
          <el-tab-pane label="技能特长" name="skills">
            <SkillsForm v-model="resumeData.skills" />
          </el-tab-pane>
          
          <!-- 自我评价 -->
          <el-tab-pane label="自我评价" name="selfEvaluation">
            <SelfEvalForm v-model="resumeData.selfEvaluation" />
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 右侧预览区 -->
      <div class="editor-preview">
        <ResumePreview ref="previewComponent" :resumeData="resumeData" :templateConfig="templateConfig" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getResumeById, updateResume } from '../api/resume'
import { getTemplateById } from '../api/template'
import { ElMessage } from 'element-plus'

// 组件
import BasicInfoForm from '../components/editor/BasicInfoForm.vue'
import EducationForm from '../components/editor/EducationForm.vue'
import ExperienceForm from '../components/editor/ExperienceForm.vue'
import ProjectsForm from '../components/editor/ProjectsForm.vue'
import SkillsForm from '../components/editor/SkillsForm.vue'
import SelfEvalForm from '../components/editor/SelfEvalForm.vue'
import ResumePreview from '../components/editor/ResumePreview.vue'
import { exportToPdf } from '../utils/exportPdf'

// 简历预览组件的 ref
const previewComponent = ref(null)

// 导出 PDF
const handleExport = async () => {
  try {
    const el = previewComponent.value?.previewRef
    if (!el) {
      ElMessage.warning('预览未加载')
      return
    }
    await exportToPdf(el, resume.title || '我的简历')
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const route = useRoute()
const router = useRouter()

const resumeId = route.params.id
const activeTab = ref('basic')
const saving = ref(false)

const resume = reactive({
  id: null,
  title: '我的简历'
})

const resumeData = reactive({
  basicInfo: { name: '', phone: '', email: '', jobIntention: '' },
  education: [],
  experience: [],
  projects: [],
  skills: [],
  selfEvaluation: ''
})

const templateConfig = ref({})

onMounted(async () => {
  await loadResume()
})

const loadResume = async () => {
  try {
    const res = await getResumeById(resumeId)
    resume.id = res.data.id
    resume.title = res.data.title
    
    // 解析简历数据
    if (res.data.resumeData) {
      Object.assign(resumeData, JSON.parse(res.data.resumeData))
    }
    
    // 加载模板配置
    if (res.data.templateId) {
      const templateRes = await getTemplateById(res.data.templateId)
      templateConfig.value = JSON.parse(templateRes.data.templateConfig || '{}')
    }
  } catch (error) {
    ElMessage.error('加载简历失败')
    router.push('/dashboard')
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateResume(resumeId, {
      title: resume.title,
      resumeData: JSON.stringify(resumeData)
    })
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}
</script>

<style scoped>
.editor-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.toolbar-left, .toolbar-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.title-input {
  width: 300px;
}

.editor-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.editor-sidebar {
  width: 400px;
  overflow-y: auto;
  background: white;
}

.editor-preview {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  justify-content: center;
}
</style>