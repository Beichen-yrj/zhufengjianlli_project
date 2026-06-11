<template>
  <div class="ai-settings-page">
    <div class="settings-layout">
      <!-- 左侧：服务商列表 -->
      <div class="provider-panel">
        <div
          v-for="provider in providers"
          :key="provider.key"
          class="provider-item"
          :class="{ active: selectedProvider === provider.key }"
          @click="selectProvider(provider.key)"
        >
          <div class="provider-icon" :style="{ background: provider.iconBg }">
            <component :is="provider.icon" />
          </div>
          <div class="provider-text">
            <span class="provider-name">{{ provider.name }}</span>
            <span class="provider-status">{{ isConfigured(provider.key) ? '已配置' : '未配置' }}</span>
          </div>
          <div class="provider-check" v-if="selectedProvider === provider.key">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <circle cx="8" cy="8" r="7.5" fill="#1a1a2e"/>
              <path d="M5 8l2 2l4-4" stroke="#fff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="provider-radio" v-else>
            <div class="radio-circle"></div>
          </div>
        </div>
      </div>

      <!-- 右侧：配置面板 -->
      <div class="config-panel">
        <div class="config-header">
          <div class="config-icon" :style="{ background: currentProvider?.iconBg }">
            <component :is="currentProvider?.icon" />
          </div>
          <div class="config-title-group">
            <h2 class="config-title">{{ currentProvider?.name }}</h2>
            <p class="config-desc">{{ currentProvider?.description }}</p>
          </div>
        </div>

        <div class="config-form">
          <!-- API Key -->
          <div class="form-field">
            <label class="field-label">
              API Key
              <span class="field-hint" v-if="currentProvider?.key !== 'custom'">获取 API Key</span>
            </label>
            <input
              type="password"
              class="field-input"
              :placeholder="'API Key'"
              :value="currentModel?.apiKey || ''"
              @input="updateConfig('apiKey', $event.target.value)"
            />
          </div>

          <!-- 模型 ID -->
          <div class="form-field">
            <label class="field-label">模型 ID</label>
            <input
              type="text"
              class="field-input"
              placeholder="deepseek-chat, deepseek-v4-pro, deepseek-v4-flash..."
              :value="currentModel?.modelId || ''"
              @input="updateConfig('modelId', $event.target.value)"
            />
          </div>

          <!-- 自定义 API 端点 -->
          <div class="form-field">
            <label class="field-label">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none" style="vertical-align: -2px; margin-right: 4px;">
                <circle cx="7" cy="7" r="6" stroke="currentColor" stroke-width="1.2" fill="none"/>
                <path d="M7 4v3M7 10v0.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round"/>
              </svg>
              自定义 API 端点
              <span class="field-hint optional">(可选)</span>
            </label>
            <input
              type="text"
              class="field-input"
              placeholder="https://your-proxy.com/v1 (留空使用官方地址)"
              :value="currentModel?.endpoint || ''"
              @input="updateConfig('endpoint', $event.target.value)"
            />
          </div>

          <!-- 提示信息 -->
          <div class="form-tip">
            配置提供商的方 API 地址，常用模型：{{ currentProvider?.models }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, h } from 'vue'
import { useAiConfigStore } from '../stores/aiConfig'

const aiStore = useAiConfigStore()
const selectedProvider = ref(aiStore.selectedModel || 'deepseek')

// 图标组件
const IconDeepSeek = {
  render() {
    return h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('path', { d: 'M10 3c-.8 0-1.5.2-2 .6L5.5 6H3a1 1 0 00-1 1v6a1 1 0 001 1h2.5l2.5 2.4c.5.4 1.2.6 2 .6s1.5-.2 2-.6l2.5-2.4H17a1 1 0 001-1V7a1 1 0 00-1-1h-2.5l-2.5-2.4c-.5-.4-1.2-.6-2-.6z', fill: '#4F6EF7' })
    ])
  }
}

const IconDoubao = {
  render() {
    return h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('circle', { cx: 10, cy: 10, r: 7, fill: '#6366F1' }),
      h('circle', { cx: 7, cy: 8, r: 1.5, fill: '#fff', opacity: 0.8 }),
      h('circle', { cx: 13, cy: 8, r: 1.5, fill: '#fff', opacity: 0.8 }),
      h('path', { d: 'M7 13c1 1 3 1.5 6 0', stroke: '#fff', 'stroke-width': 1.2, 'stroke-linecap': 'round', fill: 'none' })
    ])
  }
}

const IconOpenAI = {
  render() {
    return h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('path', { d: 'M7.5 4.5a3 3 0 015 0l1 1.7a3 3 0 010 3.6l-1 1.7a3 3 0 01-5 0l-1-1.7a3 3 0 010-3.6l1-1.7z', fill: '#10A37F' })
    ])
  }
}

const IconGemini = {
  render() {
    return h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('path', { d: 'M10 2L12 8h6l-5 4 2 6-5-4-5 4 2-6-5-4h6L10 2z', fill: '#4285F4' })
    ])
  }
}

const IconMiMo = {
  render() {
    return h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('path', { d: 'M4 10l3-5h6l3 5-3 5H7l-3-5z', fill: '#FF6900' })
    ])
  }
}

const IconCustom = {
  render() {
    return h('svg', { width: 20, height: 20, viewBox: '0 0 20 20', fill: 'none' }, [
      h('circle', { cx: 10, cy: 10, r: 7, stroke: 'currentColor', 'stroke-width': 1.5, fill: 'none' }),
      h('path', { d: 'M10 6v8M6 10h8', stroke: 'currentColor', 'stroke-width': 1.5, 'stroke-linecap': 'round' })
    ])
  }
}

const providers = [
  {
    key: 'deepseek',
    name: 'DeepSeek',
    icon: IconDeepSeek,
    iconBg: '#EEF1FE',
    description: '在 DeepSeek 开放平台或兼容平台获取 API 密钥',
    models: 'deepseek-chat, deepseek-v4-pro, deepseek-v4-flash, deepseek reasoner'
  },
  {
    key: 'doubao',
    name: '豆包',
    icon: IconDoubao,
    iconBg: '#EEF0FE',
    description: '在火山引擎豆包开放平台获取 API 密钥',
    models: 'doubao-pro-32k, doubao-lite, doubao-1-5'
  },
  {
    key: 'openai',
    name: 'OpenAI',
    icon: IconOpenAI,
    iconBg: '#ECFDF5',
    description: '在 OpenAI 平台获取 API 密钥',
    models: 'gpt-4o, gpt-4-turbo, gpt-3.5-turbo'
  },
  {
    key: 'gemini',
    name: 'Gemini',
    icon: IconGemini,
    iconBg: '#EFF6FF',
    description: '在 Google AI Studio 获取 API 密钥',
    models: 'gemini-pro, gemini-1.5-flash, gemini-1.5-pro'
  },
  {
    key: 'mimo',
    name: '小米 MiMo',
    icon: IconMiMo,
    iconBg: '#FFF7ED',
    description: '在小米 AI 开放平台获取 API 密钥',
    models: 'mimo-chat, mimo-pro'
  },
  {
    key: 'custom',
    name: '自定义 (Custom)',
    icon: IconCustom,
    iconBg: '#F3F4F6',
    description: '配置任意 OpenAI 兼容的 API 服务',
    models: '自定义模型 ID'
  },
]

const currentProvider = computed(() => providers.find(p => p.key === selectedProvider.value))

const currentModel = computed(() => aiStore.models[selectedProvider.value])

const selectProvider = (key) => {
  selectedProvider.value = key
  aiStore.setSelectedModel(key)
}

const isConfigured = (key) => {
  const model = aiStore.models[key]
  return model && !!model.apiKey
}

const updateConfig = (field, value) => {
  aiStore.updateModelConfig(selectedProvider.value, { [field]: value })
}
</script>

<style scoped>
.ai-settings-page {
  width: 94%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 3% 3%;
}

.settings-layout {
  display: grid;
  grid-template-columns: 22% 1fr;
  gap: 2.5%;
  align-items: start;
}

/* ===== 左侧服务商列表 ===== */
.provider-panel {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.provider-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 11px;
  cursor: pointer;
  transition: all 0.15s ease;
  border: 1px solid transparent;
}

.provider-item:hover {
  background: #F9FAFB;
}

.provider-item.active {
  background: #F5F5F5;
  border-color: #E5E7EB;
}

.provider-icon {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.provider-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
  flex: 1;
}

.provider-name {
  font-size: 14px;
  font-weight: 600;
  color: #1F2937;
}

.provider-status {
  font-size: 12px;
  color: #9CA3AF;
}

.provider-item.active .provider-status {
  color: #22C55E;
}

.provider-check {
  flex-shrink: 0;
}

.provider-radio {
  flex-shrink: 0;
}

.radio-circle {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 1.5px solid #D1D5DB;
}

/* ===== 右侧配置面板 ===== */
.config-panel {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid #E5E7EB;
}

.config-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid #F0F0F0;
}

.config-icon {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.config-title-group {
  min-width: 0;
}

.config-title {
  margin: 0 0 4px;
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
}

.config-desc {
  margin: 0;
  font-size: 13px;
  color: #6B7280;
  line-height: 1.5;
}

/* ===== 表单样式 ===== */
.config-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  display: flex;
  align-items: center;
}

.field-hint {
  margin-left: auto;
  font-size: 12px;
  font-weight: 400;
  color: #3B82F6;
  cursor: pointer;
}

.field-hint.optional {
  color: #9CA3AF;
}

.field-input {
  padding: 11px 14px;
  border: 1px solid #D1D5DB;
  border-radius: 9px;
  font-size: 14px;
  color: #1F2937;
  outline: none;
  transition: all 0.15s;
  background: #FAFBFC;
}

.field-input:focus {
  border-color: #5B8DEF;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(91, 141, 239, 0.1);
}

.field-input::placeholder {
  color: #C0C5CF;
}

.form-tip {
  font-size: 12px;
  color: #9CA3AF;
  line-height: 1.6;
  padding-top: 4px;
}
</style>
