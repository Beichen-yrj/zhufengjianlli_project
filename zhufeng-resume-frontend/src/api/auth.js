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

/**
 * 强制下线：销毁该用户所有 Token + Session（用于多账号切换）
 */
export function logoutAll() {
  return request({
    url: '/api/v1/auth/logout-all',
    method: 'POST'
  })
}

/**
 * 校验 Token 并从后端拉取最新用户信息
 * 用于应用启动时 / 路由切换时的"重新登录态校验"
 * 强制从后端取数，不信任任何前端缓存
 */
export function validateToken() {
  return request({
    url: '/api/v1/auth/me',
    method: 'GET'
  })
}