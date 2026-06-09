<template>
  <div class="ai-page">
    <div class="ai-container">
      <h1>AI 智能生成简历</h1>
      <p>输入你的需求，AI 帮你一键生成专业简历</p>

      <el-card class="ai-card">
        <el-form :model="form">
          <el-form-item label="简历模板">
            <el-select v-model="form.templateId" placeholder="选择模板" style="width: 100%">
              <el-option
                v-for="t in templates"
                :key="t.id"
                :label="t.name"
                :value="t.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="提示词">
            <el-input
              v-model="form.prompt"
              type="textarea"
              :rows="6"
              placeholder="请描述你的需求，例如：我是计算机专业应届毕业生，熟悉Java和SpringBoot，帮我生成一份求职简历"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              @click="handleGenerate"
              :loading="loading"
            >
              🤖 AI 生成简历
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 生成结果 -->
      <div v-if="result" class="result-card">
        <el-result icon="success" title="生成成功">
          <template #extra>
            <el-button type="primary" @click="editResume">去编辑</el-button>
          </template>
        </el-result>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTemplates } from '../api/template'
import { aiGenerate } from '../api/ai'
import { ElMessage } from 'element-plus'

const router = useRouter()
const templates = ref([])
const loading = ref(false)
const result = ref(null)

const form = ref({
  templateId: null,
  prompt: ''
})

onMounted(async () => {
  const res = await getTemplates()
  templates.value = res.data
})

const handleGenerate = async () => {
  if (!form.value.prompt.trim()) {
    ElMessage.warning('请输入提示词')
    return
  }
  
  loading.value = true
  try {
    const res = await aiGenerate({
      prompt: form.value.prompt,
      templateId: form.value.templateId
    })
    result.value = res.data
    ElMessage.success('AI 生成成功')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const editResume = () => {
  if (result.value?.resumeId) {
    router.push(`/editor/${result.value.resumeId}`)
  }
}
</script>

<style scoped>
.ai-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.ai-container {
  max-width: 700px;
  margin: 0 auto;
  color: white;
  text-align: center;
}

.ai-container h1 {
  font-size: 36px;
  margin-bottom: 10px;
}

.ai-container p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 30px;
}

.ai-card {
  text-align: left;
}

.result-card {
  margin-top: 30px;
}
</style>