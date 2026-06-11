<template>
  <div class="settings-page">
    <div class="page-header">
      <h1 class="page-title">通用设置</h1>
    </div>

    <div class="settings-sections">
      <!-- 备份设置 -->
      <div class="setting-card">
        <div class="card-header">
          <div class="card-icon-wrap">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M4 7a4 4 0 014-4h4a4 4 0 014 4v6a4 4 0 01-4 4H8a4 4 0 01-4-4V7z" stroke="#3B82F6" stroke-width="1.4" fill="none"/>
              <path d="M10 6v4M10 13v.5" stroke="#3B82F6" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <div>
            <h3 class="card-title">数据备份</h3>
            <p class="card-desc">配置简历备份文件夹，防止数据丢失</p>
          </div>
        </div>
        <div class="card-body">
          <div class="form-field">
            <label class="field-label">备份路径</label>
            <div class="input-with-btn">
              <input
                type="text"
                class="field-input"
                placeholder="选择备份文件夹路径..."
                v-model="backupPath"
              />
              <button class="btn-browse" @click="browseFolder">浏览</button>
            </div>
          </div>
          <div class="form-field">
            <label class="field-label">自动备份</label>
            <div class="toggle-row">
              <span class="toggle-desc">每次编辑后自动保存备份文件</span>
              <label class="toggle-switch">
                <input type="checkbox" v-model="autoBackup" />
                <span class="toggle-slider"></span>
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- 账户设置 -->
      <div class="setting-card">
        <div class="card-header">
          <div class="card-icon-wrap" style="background:#EEF0FE;">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <circle cx="10" cy="7" r="3.5" stroke="#6366F1" stroke-width="1.4" fill="none"/>
              <path d="M4 17c0-3.3 2.7-6 6-6s6 2.7 6 6" stroke="#6366F1" stroke-width="1.4" stroke-linecap="round" fill="none"/>
            </svg>
          </div>
          <div>
            <h3 class="card-title">账户信息</h3>
            <p class="card-desc">管理你的账户基本信息</p>
          </div>
        </div>
        <div class="card-body">
          <div class="info-row">
            <span class="info-label">用户名</span>
            <span class="info-value">{{ userName || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">邮箱</span>
            <span class="info-value">{{ userEmail || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">账户类型</span>
            <span class="info-value tag-value">个人版</span>
          </div>
        </div>
      </div>

      <!-- 外观设置 -->
      <div class="setting-card">
        <div class="card-header">
          <div class="card-icon-wrap" style="background:#F0FDF4;">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <circle cx="10" cy="10" r="6" stroke="#22C55E" stroke-width="1.4" fill="none"/>
              <path d="M10 2v2M10 16v2M2 10h2M16 10h2" stroke="#22C55E" stroke-width="1.4" stroke-linecap="round"/>
            </svg>
          </div>
          <div>
            <h3 class="card-title">外观与显示</h3>
            <p class="card-desc">自定义界面外观偏好</p>
          </div>
        </div>
        <div class="card-body">
          <div class="form-field">
            <label class="field-label">语言</label>
            <select class="field-select" v-model="language">
              <option value="zh-CN">简体中文</option>
              <option value="en-US">English</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()

const backupPath = ref('')
const autoBackup = ref(false)
const language = ref('zh-CN')
const userName = ref('')
const userEmail = ref('')

onMounted(async () => {
  // 多账号安全：强制从后端拉取最新用户信息，不信任本地缓存
  try {
    await userStore.fetchCurrentUser(true)
  } catch (_) {
    // 401 已由 request.js 拦截器统一处理
    return
  }
  userName.value = userStore.userInfo?.username || ''
  userEmail.value = userStore.userInfo?.email || ''

  // 从 localStorage 恢复设置
  backupPath.value = localStorage.getItem('backup_path') || ''
  autoBackup.value = localStorage.getItem('auto_backup') === 'true'
  language.value = localStorage.getItem('app_language') || 'zh-CN'
})

const browseFolder = () => {
  ElMessage.info('请在系统对话框中选择备份文件夹')
}

// 监听变化并保存
const saveSetting = () => {
  localStorage.setItem('backup_path', backupPath.value)
  localStorage.setItem('auto_backup', String(autoBackup.value))
  localStorage.setItem('app_language', language.value)
}
</script>

<style scoped>
.settings-page {
  width: 80%;
  max-width: 900px;
  margin: 0 auto;
  padding: 3% 3%;
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
  letter-spacing: -0.5px;
}

.settings-sections {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* ===== 设置卡片 ===== */
.setting-card {
  background: #fff;
  border: 1px solid #E5E7EB;
  border-radius: 14px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 24px;
  border-bottom: 1px solid #F0F0F0;
}

.card-icon-wrap {
  width: 42px;
  height: 42px;
  border-radius: 11px;
  background: #EFF6FF;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-title {
  margin: 0 0 3px;
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.card-desc {
  margin: 0;
  font-size: 13px;
  color: #9CA3AF;
}

.card-body {
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* ===== 表单字段 ===== */
.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.input-with-btn {
  display: flex;
  gap: 8px;
}

.field-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #D1D5DB;
  border-radius: 9px;
  font-size: 14px;
  color: #1F2937;
  outline: none;
  background: #FAFBFC;
  transition: all 0.15s;
}

.field-input:focus {
  border-color: #5B8DEF;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(91, 141, 239, 0.1);
}

.btn-browse {
  padding: 10px 18px;
  border: 1px solid #D1D5DB;
  border-radius: 9px;
  background: #fff;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s;
}

.btn-browse:hover {
  background: #F3F4F6;
}

.field-select {
  padding: 10px 14px;
  border: 1px solid #D1D5DB;
  border-radius: 9px;
  font-size: 14px;
  color: #1F2937;
  outline: none;
  background: #FAFBFC;
  cursor: pointer;
  transition: all 0.15s;
}

.field-select:focus {
  border-color: #5B8DEF;
  box-shadow: 0 0 0 3px rgba(91, 141, 239, 0.1);
}

/* ===== Toggle开关 ===== */
.toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toggle-desc {
  font-size: 13px;
  color: #6B7280;
}

.toggle-switch {
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;
  cursor: pointer;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  inset: 0;
  background: #D1D5DB;
  border-radius: 12px;
  transition: all 0.25s;
}

.toggle-slider::before {
  content: '';
  position: absolute;
  left: 2px;
  top: 2px;
  width: 20px;
  height: 20px;
  background: #fff;
  border-radius: 50%;
  transition: all 0.25s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.15);
}

.toggle-switch input:checked + .toggle-slider {
  background: #3B82F6;
}

.toggle-switch input:checked + .toggle-slider::before {
  transform: translateX(20px);
}

/* ===== 信息行 ===== */
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.info-label {
  font-size: 14px;
  color: #6B7280;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #1F2937;
}

.tag-value {
  display: inline-block;
  padding: 2px 10px;
  background: #F3F4F6;
  border-radius: 6px;
  font-size: 12px;
  color: #6B7280;
}
</style>
