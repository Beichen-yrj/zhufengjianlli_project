import { defineStore } from 'pinia'

export const useAiConfigStore = defineStore('aiConfig', {
  state: () => ({
    selectedModel: 'deepseek',
    models: {
      deepseek: {
        name: 'DeepSeek',
        apiKey: localStorage.getItem('deepseek_apiKey') || '',
        modelId: 'deepseek-chat',
        endpoint: 'https://api.deepseek.com'
      },
      doubao: {
        name: '豆包',
        apiKey: localStorage.getItem('doubao_apiKey') || '',
        modelId: 'doubao-pro-32k',
        endpoint: 'https://ark.cn-beijing.volces.com/api/v3'
      },
      openai: {
        name: 'OpenAI',
        apiKey: localStorage.getItem('openai_apiKey') || '',
        modelId: 'gpt-4o',
        endpoint: 'https://api.openai.com'
      },
      gemini: {
        name: 'Gemini',
        apiKey: localStorage.getItem('gemini_apiKey') || '',
        modelId: 'gemini-1.5-flash',
        endpoint: 'https://generativelanguage.googleapis.com'
      },
      mimo: {
        name: '小米 MiMo',
        apiKey: localStorage.getItem('mimo_apiKey') || '',
        modelId: 'mimo-chat',
        endpoint: 'https://api.mi.com'
      },
      custom: {
        name: '自定义',
        apiKey: localStorage.getItem('custom_apiKey') || '',
        modelId: '',
        endpoint: ''
      }
    }
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
        Object.assign(this.models[model], config)
        localStorage.setItem(`${model}_apiKey`, config.apiKey || '')
      }
    }
  }
})
