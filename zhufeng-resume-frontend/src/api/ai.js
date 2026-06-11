import request from './request'

/** AI 生成简历 */
export function aiGenerate(data) {
  return request({
    url: '/api/v1/ai/generate',
    method: 'POST',
    data
  })
}

/** AI 语法检查 */
export function aiCheckGrammar(data) {
  return request({
    url: '/api/v1/ai/grammar',
    method: 'POST',
    data
  })
}

/** AI 内容润色 */
export function aiPolish(data) {
  return request({
    url: '/api/v1/ai/polish',
    method: 'POST',
    data
  })
}

/** 获取可用 AI 模型列表 */
export function getAiModels() {
  return request({
    url: '/api/v1/ai/models',
    method: 'GET'
  })
}
