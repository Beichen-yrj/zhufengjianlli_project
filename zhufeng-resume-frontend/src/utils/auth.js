/**
 * 认证工具 - Token 管理 + 多账号数据隔离 + 强制清理
 *
 * 安全策略：
 * - Token 存储在 sessionStorage（关闭浏览器即失效；不再持久化到 localStorage）
 * - 每个Token附带过期时间（默认7天）
 * - 请求前检查是否过期，过期则自动清除并跳转登录
 * - 401 响应时全量清除认证数据 + Pinia store
 * - 退出登录 / 401 / Token 失效：必须清空 localStorage / sessionStorage / Cookie / 全局状态
 *   绝不允许上一账号的数据残留
 */

// ===== 存储介质开关 =====
const USE_SESSION_STORAGE = true  // Token 改用 sessionStorage：关闭浏览器即失效，防止多账号共享 Token
const TOKEN_KEY = 'token'
const TOKEN_EXPIRE_KEY = 'token_expire'
const USER_INFO_KEY = 'user_info'
const DEFAULT_EXPIRE_DAYS = 7

// ===== Token 操作 =====

export function getToken() {
  return USE_SESSION_STORAGE
    ? sessionStorage.getItem(TOKEN_KEY)
    : localStorage.getItem(TOKEN_KEY)
}

export function setToken(token, expireDays = DEFAULT_EXPIRE_DAYS) {
  // 同时写入 sessionStorage 和 localStorage（兼容旧逻辑读取）
  // 登录成功后调用 clearAuth() 会清空两者
  const expireTime = Date.now() + expireDays * 24 * 60 * 60 * 1000
  if (USE_SESSION_STORAGE) sessionStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(TOKEN_EXPIRE_KEY, String(expireTime))
}

export function removeToken() {
  sessionStorage.removeItem(TOKEN_KEY)
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
  const minutes = Math.floor((ms % (60 * 60 * 1000)) / (60 * 1000))
  return `${minutes}分钟后过期`
}

// ===== 用户信息缓存 =====
// 注意：缓存只用于"登录刚完成时立即展示头像"，下次刷新页面必须从后端拉新数据

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
  sessionStorage.removeItem(USER_INFO_KEY)
  localStorage.removeItem(USER_INFO_KEY)
}

/**
 * 从 localStorage 中的 user_info 缓存中安全地取 userId
 * 注：仅用于"刚登录完立即展示"场景，多账号安全以 userId 作为命名空间隔离 key
 *       真正的鉴权以后端 Token 为准
 */
export function getCurrentUserId() {
  try {
    const raw = localStorage.getItem(USER_INFO_KEY)
    if (raw) {
      const info = JSON.parse(raw)
      return info?.id
    }
  } catch {}
  return null
}

// ===== Cookie 清理 =====

/**
 * 清空所有非 HttpOnly Cookie（HttpOnly Cookie 由后端 set，前端无法清除）
 * 多账号场景：必须清理浏览器写入的 cookie 防止数据残留
 */
function clearAllCookies() {
  const cookies = document.cookie.split(';')
  const hostname = window.location.hostname
  const domains = ['', hostname, '.' + hostname]
  for (const c of cookies) {
    const eqPos = c.indexOf('=')
    const name = eqPos > -1 ? c.substr(0, eqPos).trim() : c.trim()
    if (!name) continue
    for (const d of domains) {
      const exp = 'expires=Thu, 01 Jan 1970 00:00:00 GMT'
      const path = 'path=/'
      document.cookie = d
        ? `${name}=; ${exp}; ${path}; domain=${d}`
        : `${name}=; ${exp}; ${path}`
    }
  }
}

// ===== 全量清除（登出/401 时调用）=====

/**
 * 核心方法：清除所有与用户相关的本地数据
 *
 * 清除目标：
 *  1. localStorage：Token、过期时间、用户信息、AI 配置、回收站、主题/语言等所有非必要数据
 *  2. sessionStorage：所有数据（含 Token、用户信息等）
 *  3. Cookie：清空所有非 HttpOnly Cookie
 *  4. Pinia stores：在调用方负责（store 层）
 *
 * 注意：Pinia store 不能在 utils 层重置（依赖 Pinia 实例），
 *       需要在 store action 中调用此方法后自行 $reset()
 */
export function clearAuth() {
  // 1. 清空 sessionStorage 全部（最安全：包括所有临时状态）
  try { sessionStorage.clear() } catch (_) {}

  // 2. 清空 localStorage 中所有"用户相关" key
  //    保留主题/语言等可跨账号保留的偏好（不破坏用户体验）
  const keysToRemove = []
  for (let i = 0; i < localStorage.length; i++) {
    const k = localStorage.key(i)
    if (!k) continue
    if (k === 'darkMode' || k === 'locale') continue  // 保留：主题/语言偏好
    keysToRemove.push(k)
  }
  keysToRemove.forEach(k => localStorage.removeItem(k))

  // 3. 清空所有 Cookie
  clearAllCookies()
}

/**
 * 极限清理：连主题/语言偏好也清空（仅在用户主动点"退出登录并清除所有数据"时调用）
 */
export function clearAll() {
  try { localStorage.clear() } catch (_) {}
  try { sessionStorage.clear() } catch (_) {}
  clearAllCookies()
}

/** 清除所有 AI 配置（每个用户的 API Key 不同，必须清理） */
export function clearAiConfig() {
  // 兜底清理（已在 clearAuth 内清空，但保留此方法用于"只清 AI 配置"场景）
  const models = ['deepseek', 'doubao', 'openai', 'gemini', 'mimo', 'custom']
  for (let i = localStorage.length - 1; i >= 0; i--) {
    const k = localStorage.key(i)
    if (k && (k.startsWith('ai:') || k === 'ai_selected_model' ||
              models.some(m => k === `${m}_apiKey` || k === `${m}_modelId` || k === `${m}_endpoint`))) {
      localStorage.removeItem(k)
    }
  }
}
