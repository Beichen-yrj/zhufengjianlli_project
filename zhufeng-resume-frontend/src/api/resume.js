import request from './request'

// 获取简历列表
export function getResumes() {
  return request({
    url: '/api/v1/resumes',
    method: 'GET'
  })
}

// 获取简历详情
export function getResumeById(id) {
  return request({
    url: `/api/v1/resumes/${id}`,
    method: 'GET'
  })
}

// 创建简历
export function createResume(data) {
  return request({
    url: '/api/v1/resumes',
    method: 'POST',
    data
  })
}

// 更新简历
export function updateResume(id, data) {
  return request({
    url: `/api/v1/resumes/${id}`,
    method: 'PUT',
    data
  })
}

// 删除简历
export function deleteResume(id) {
  return request({
    url: `/api/v1/resumes/${id}`,
    method: 'DELETE'
  })
}