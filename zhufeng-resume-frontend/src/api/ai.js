import request from './request'

export function aiGenerate(data) {
  return request({
    url: '/api/v1/ai/generate',
    method: 'POST',
    data
  })
}