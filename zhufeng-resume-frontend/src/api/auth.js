import request from './request'

export function login(data, expireDays = 7) {
  return request({
    url: '/api/v1/auth/login',
    method: 'POST',
    data,
    params: { expireDays }
  })
}

export function register(data) {
  return request({
    url: '/api/v1/auth/register',
    method: 'POST',
    data
  })
}

export function logout() {
  return request({
    url: '/api/v1/auth/logout',
    method: 'POST'
  })
}