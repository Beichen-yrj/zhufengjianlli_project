import request from './request'

export function getTemplates() {
  return request({
    url: '/api/v1/templates',
    method: 'GET'
  })
}

export function getTemplateById(id) {
  return request({
    url: `/api/v1/templates/${id}`,
    method: 'GET'
  })
}

export function getTemplatesByCategory(category) {
  return request({
    url: `/api/v1/templates/category/${category}`,
    method: 'GET'
  })
}