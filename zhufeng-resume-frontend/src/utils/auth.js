/**
 * 认证工具 - Token 管理 + 用户信息缓存 + 过期机制
 *
 * 安全策略：
 * - Token 存储在 localStorage（跨标签页持久化）
 * - 每个Token附带过期时间（默认7天）
 * - 请求前检查是否过期，过期则自动清除并跳转登录
 * - 401 响应时全量清除认证数据
 */

const TOKEN_KEY = 'token'
const TOKEN_EXPIRE_KEY = 'token_expire'
const USER_INFO_KEY = 'user_info'
const DEFAULT_EXPIRE_DAYS = 7

// ===== Token 操作 =====

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token, expireDays = DEFAULT_EXPIRE_DAYS) {
  localStorage.setItem(TOKEN_KEY, token)
  // 设置过期时间戳（毫秒）
  const expireTime = Date.now() + expireDays * 24 * 60 * 60 * 1000
  localStorage.setItem(TOKEN_EXPIRE_KEY, String(expireTime))
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(TOKEN_EXPIRE_KEY)
}

// ===== Token 有效性检查 =====

/**
 * 检查 token 是否存在且未过期
 */
export function isTokenValid() {
  const token = getToken()
  if (!token) return false
  const expireStr = localStorage.getItem(TOKEN_EXPIRE_KEY)
  if (!expireStr) return true // 无过期时间则视为有效（兼容旧数据）
  return Date.now() < Number(expireStr)
}

/**
 * 获取 token 剩余有效时间（毫秒），已过期返回 0
 */
export function getTokenRemainingMs() {
  const expireStr = localStorage.getItem(TOKEN_EXPIRE_KEY)
  if (!expireStr) return Infinity
  const remaining = Number(expireStr) - Date.now()
  return remaining > 0 ? remaining : 0
}

/**
 * 格式化显示剩余时间
 */
export function getTokenRemainingText() {
  const ms = getTokenRemainingMs()
  if (ms === Infinity) return '长期有效'
  if (ms <= 0) return '已过期'
  const days = Math.floor(ms / (24 * 60 * 60 * 1000))
  if (days > 0) return `${days}天后过期`
  const hours = Math.floor(ms / (60 * 60 * 1000))
  if (hours > 0) return `${hours}小时后过期`
  const minutes = Math.floor(ms / (60 * 1000))
  return `${minutes}分钟后过期`
}

// ===== 用户信息缓存 =====

export function getUserInfoCache() {
  try {
    const raw = localStorage.getItem(USER_INFO_KEY)
    return raw ? JSON.parse(raw) : null
  } catch { return null }
}

export function setUserInfoCache(info) {
  if (info) {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
  }
}

export function removeUserInfoCache() {
  localStorage.removeItem(USER_INFO_KEY)
}

// ===== 全量清除（登出/401时调用）=====

/**
 * 清除所有认证相关数据
 */
export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(TOKEN_EXPIRE_KEY)
  localStorage.removeItem(USER_INFO_KEY)
  // 清除所有 AI 服务商配置（每个用户的 API Key 不同，必须清理）
  clearAiConfig()
}

/** 清除所有 AI 配置 */
function clearAiConfig() {
  const models = ['deepseek', 'doubao', 'openai', 'gemini', 'mimo', 'custom']
  models.forEach(m => {
    localStorage.removeItem(`${m}_apiKey`)
    localStorage.removeItem(`${m}_modelId`)
    localStorage.removeItem(`${m}_endpoint`)
  })
  localStorage.removeItem('ai_selected_model')
}
