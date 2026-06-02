import request from './request'

export function getUserProfile() {
  return request({
    url: '/api/v1/user/profile',
    method: 'GET'
  })
}