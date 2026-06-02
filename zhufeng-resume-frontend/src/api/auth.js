import request from './request'

export function login(data) {
  return request({
    url: '/api/v1/auth/login',
    method: 'POST',
    data
  })
}

export function register(data) {
  return request({
    url: '/api/v1/auth/register',
    method: 'POST',
    data
  })
}