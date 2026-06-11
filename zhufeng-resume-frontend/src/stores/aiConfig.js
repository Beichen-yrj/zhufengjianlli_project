import { defineStore } from 'pinia'

/** 获取当前登录用户的 ID，用于 localStorage 命名空间隔离 */
function getUserId() {
  try {
    const raw = localStorage.getItem('user_info')
    if (raw) {
      const info = JSON.parse(raw)
      return info.id
    }
  } catch {}
  return 'guest'
}

const MODEL_DEFAULTS = {
  deepseek: { name: 'DeepSeek', modelId: 'deepseek-chat', endpoint: 'https://api.deepseek.com' },
  doubao:   { name: '豆包',     modelId: 'doubao-pro-32k', endpoint: 'https://ark.cn-beijing.volces.com/api/v3' },
  openai:   { name: 'OpenAI',   modelId: 'gpt-4o',          endpoint: 'https://api.openai.com' },
  gemini:   { name: 'Gemini',   modelId: 'gemini-1.5-flash', endpoint: 'https://generativelanguage.googleapis.com' },
  mimo:     { name: '小米 MiMo', modelId: 'mimo-chat',      endpoint: 'https://api.mi.com' },
  custom:   { name: '自定义',   modelId: '',                endpoint: '' }
}

function buildModelsFromStorage() {
  const uid = getUserId()
  const models = {}
  for (const [key, def] of Object.entries(MODEL_DEFAULTS)) {
    models[key] = {
      name: def.name,
      apiKey: localStorage.getItem(`ai:${uid}:${key}_apiKey`) || '',
      modelId: localStorage.getItem(`ai:${uid}:${key}_modelId`) || def.modelId,
      endpoint: localStorage.getItem(`ai:${uid}:${key}_endpoint`) || def.endpoint
    }
  }
  return models
}

export const useAiConfigStore = defineStore('aiConfig', {
  state: () => ({
    selectedModel: 'deepseek',
    models: buildModelsFromStorage()
  }),

  getters: {
    currentModel: (state) => state.models[state.selectedModel],
    isConfigured: (state) => {
      const model = state.models[state.selectedModel]
      return !!model.apiKey && !!model.modelId
    }
  },

  actions: {
    setSelectedModel(model) {
      this.selectedModel = model
    },

    updateModelConfig(model, config) {
      if (this.models[model]) {
        const uid = getUserId()
        Object.assign(this.models[model], config)
        localStorage.setItem(`ai:${uid}:${model}_apiKey`, config.apiKey || '')
        if (config.modelId) localStorage.setItem(`ai:${uid}:${model}_modelId`, config.modelId)
        if (config.endpoint) localStorage.setItem(`ai:${uid}:${model}_endpoint`, config.endpoint)
      }
    },

    /**
     * 用户登录后调用：重新加载当前用户的 AI 配置
     * 必须在 setUserInfoCache 之后调用
     */
    reloadConfig() {
      this.models = buildModelsFromStorage()
    },

    /**
     * 登出时调用：清除当前用户所有 AI 配置
     */
    clearConfig() {
      const uid = getUserId()
      for (const key of Object.keys(MODEL_DEFAULTS)) {
        localStorage.removeItem(`ai:${uid}:${key}_apiKey`)
        localStorage.removeItem(`ai:${uid}:${key}_modelId`)
        localStorage.removeItem(`ai:${uid}:${key}_endpoint`)
      }
      this.models = buildModelsFromStorage()
    }
  }
})
