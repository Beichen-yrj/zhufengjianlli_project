<template>
  <div class="ai-page">
    <div class="ai-container">
      <div class="ai-header">
        <h1>AI 智能助手</h1>
        <p>AI 生成简历、语法检查、内容润色</p>
      </div>

      <el-tabs v-model="activeTab" class="ai-tabs">
        <!-- Tab 1: AI 生成 -->
        <el-tab-pane label="AI 生成" name="generate">
          <el-card class="ai-card">
            <el-form :model="genForm">
              <el-form-item label="简历模板">
                <el-select v-model="genForm.templateId" placeholder="选择模板">
                  <el-option v-for="t in templates" :key="t.id" :label="t.name" :value="t.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="提示词">
                <el-input
                  v-model="genForm.prompt"
                  type="textarea"
                  :rows="6"
                  placeholder="描述你的需求，例如：我是计算机专业应届毕业生，熟悉Java和SpringBoot"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" style="width:100%"
                  @click="handleGenerate" :loading="loading"
                >🤖 AI 生成简历</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <div v-if="result" class="result-card">
            <el-result icon="success" title="生成成功">
              <template #extra>
                <el-button type="primary" @click="editResume">去编辑</el-button>
              </template>
            </el-result>
          </div>
        </el-tab-pane>

        <!-- Tab 2: 模型配置 -->
        <el-tab-pane label="模型配置" name="config">
          <el-card class="ai-card">
            <el-form label-width="100px">
              <el-form-item label="选择模型">
                <el-select v-model="aiStore.selectedModel" @change="onModelChange">
                  <el-option
                    v-for="(model, key) in aiStore.models"
                    :key="key"
                    :label="model.name"
                    :value="key"
                  />
                </el-select>
              </el-form-item>

              <template v-for="(model, key) in aiStore.models" :key="key">
                <template v-if="key === aiStore.selectedModel">
                  <el-form-item label="API Key">
                    <el-input
                      :model-value="model.apiKey"
                      type="password"
                      show-password
                      placeholder="输入 API Key"
                      @update:model-value="v => aiStore.updateModelConfig(key, { apiKey: v })"
                    />
                  </el-form-item>
                  <el-form-item label="模型 ID">
                    <el-input
                      :model-value="model.modelId"
                      placeholder="模型标识"
                      @update:model-value="v => aiStore.updateModelConfig(key, { modelId: v })"
                    />
                  </el-form-item>
                  <el-form-item label="API 端点">
                    <el-input
                      :model-value="model.endpoint"
                      placeholder="https://api.openai.com"
                      @update:model-value="v => aiStore.updateModelConfig(key, { endpoint: v })"
                    />
                  </el-form-item>
                </template>
              </template>

              <el-form-item>
                <el-button type="primary" @click="testConnection">
                  测试连接
                </el-button>
                <el-tag v-if="aiStore.isConfigured" type="success" style="margin-left:8px">
                  已配置
                </el-tag>
                <el-tag v-else type="warning" style="margin-left:8px">
                  未配置
                </el-tag>
              </el-form-item>
            </el-form>

            <el-alert
              title="支持 OpenAI 兼容的 API 格式"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <p style="margin:4px 0">支持 DeepSeek、豆包、OpenAI 及任何兼容 /v1/chat/completions 的 API</p>
              </template>
            </el-alert>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { DEFAULT_TEMPLATES } from '../components/templates/registry.js'
import { aiGenerate } from '../api/ai'
import { ElMessage } from 'element-plus'
import { useAiConfigStore } from '../stores/aiConfig'

const router = useRouter()
const activeTab = ref('generate')
const templates = ref(DEFAULT_TEMPLATES)
const loading = ref(false)
const result = ref(null)
const aiStore = useAiConfigStore()

const genForm = ref({
  templateId: null,
  prompt: ''
})

onMounted(() => {
  if (templates.value.length > 0 && !genForm.value.templateId) {
    genForm.value.templateId = templates.value[0].id
  }
})

const handleGenerate = async () => {
  if (!genForm.value.prompt.trim()) {
    ElMessage.warning('请输入提示词')
    return
  }
  loading.value = true
  try {
    const res = await aiGenerate({
      prompt: genForm.value.prompt,
      templateId: genForm.value.templateId
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

function onModelChange() {
  // 切换模型时自动提示
}

async function testConnection() {
  if (!aiStore.isConfigured) {
    ElMessage.warning('请先填写 API Key 和模型 ID')
    return
  }
  ElMessage.success('模型已配置，连接验证可在语法检查时自动进行')
}
</script>

<style scoped>
.ai-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 3% 3%;
}

.ai-container {
  width: 70%;
  max-width: 800px;
  margin: 0 auto;
}

.ai-header {
  text-align: center;
  margin-bottom: 28px;
}

.ai-header h1 {
  font-size: 28px;
  color: #1a1a1a;
  margin: 0 0 6px;
}

.ai-header p {
  font-size: 15px;
  color: #999;
  margin: 0;
}

.ai-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

.ai-card {
  border: none;
  box-shadow: none;
}

.result-card {
  margin-top: 24px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
</style>
