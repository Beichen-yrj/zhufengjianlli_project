<template>
  <div class="dashboard-page">
    <!-- 通知横幅 -->
    <div class="notice-banner">
      <div class="notice-icon">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <circle cx="8" cy="8" r="7" stroke="#E57373" stroke-width="1.2" fill="none"/>
          <path d="M8 4v5M8 11v1" stroke="#E57373" stroke-width="1.5" stroke-linecap="round"/>
        </svg>
      </div>
      <div class="notice-content">
        <span class="notice-label">注意</span>
        <span class="notice-text">建议在设置里配置简历备份文件夹，防止您的数据可能会在浏览器清除缓存后丢失</span>
      </div>
      <a href="/settings" class="notice-link">前往设置</a>
    </div>

    <!-- 页面标题区 -->
    <div class="page-header">
      <h1 class="page-title">我的简历</h1>
      <div class="header-actions">
        <button class="btn-import" @click="handleImport">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
            <path d="M7 2v6M4.5 5L7 2.5L9.5 5" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 10v1.5A1.5 1.5 0 003.5 13h7A1.5 1.5 0 0012 11.5V10" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
          </svg>
          导入简历
        </button>
        <button class="btn-primary" @click="$router.push('/templates')">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
            <path d="M7 2v10M2 7h10" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
          </svg>
          新建简历
        </button>
        <button v-if="resumes.length > 0" class="btn-clear" @click="handleClearAll">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
            <path d="M2 4h10M5 4V3a1 1 0 011-1h2a1 1 0 011 1v1M12 4v8a1 1 0 01-1 1H3a1 1 0 01-1-1V4" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          一键清理
        </button>
      </div>
    </div>

    <!-- 回收站区域 -->
    <div v-if="recycleBin.length > 0" class="recycle-section">
      <div class="recycle-header" @click="showRecycleBin = !showRecycleBin">
        <div class="recycle-left">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M3 5h10M6 5V4a1 1 0 011-1h2a1 1 0 011 1v1M13 5v8a1 1 0 01-1 1H4a1 1 0 01-1-1V5" stroke="#9CA3AF" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>回收站</span>
          <span class="recycle-count">{{ recycleBin.length }} 份简历</span>
          <span class="recycle-hint">（{{ autoCleanDays }}天后自动清除）</span>
        </div>
        <svg :class="{ 'arrow-rotated': showRecycleBin }" class="arrow-icon" width="12" height="12" viewBox="0 0 12 12" fill="none">
          <path d="M3 4.5l3 3 3-3" stroke="#9CA3AF" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <transition name="slide">
        <div v-show="showRecycleBin" class="recycle-list">
          <div v-for="item in recycleBin" :key="item.id" class="recycle-item">
            <div class="recycle-item-info">
              <img v-if="item.templateImg" :src="item.templateImg" alt="" class="recycle-thumb" />
              <div class="recycle-detail">
                <span class="recycle-name">{{ item.title }}</span>
                <span class="recycle-time">{{ formatRecycleTime(item.deletedAt) }}</span>
              </div>
            </div>
            <div class="recycle-actions">
              <button class="btn-restore" @click="handleRestore(item)">恢复</button>
              <button class="btn-perm-delete" @click="handlePermDelete(item)">永久删除</button>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- 简历卡片列表 -->
    <div class="resume-grid">
      <!-- 新建简历卡片 -->
      <div class="resume-card new-card" @click="$router.push('/templates')">
        <div class="new-card-content">
          <div class="plus-icon">+</div>
          <span class="new-card-title">新建简历</span>
          <span class="new-card-desc">选择模板，创建一份新简历</span>
        </div>
      </div>

      <!-- 已有简历卡片（从API加载） -->
      <div
        v-for="resume in resumes"
        :key="resume.id"
        class="resume-card"
        @click="editResume(resume.id)"
      >
        <!-- 缩略图预览 -->
        <div class="card-preview">
          <img v-if="resume.templateImg" :src="resume.templateImg" alt="" class="preview-img" />
          <div v-else class="preview-mock">
            <div class="mock-avatar"></div>
            <div class="mock-info">
              <div class="mock-name"></div>
              <div class="mock-detail"></div>
            </div>
            <div class="mock-sections">
              <div class="mock-line"></div>
              <div class="mock-line short"></div>
              <div class="mock-line"></div>
              <div class="mock-line short"></div>
            </div>
          </div>
        </div>

        <!-- 卡片信息 -->
        <div class="card-body">
          <h3 class="card-title">{{ resume.title }}</h3>
          <p class="card-meta">{{ formatDate(resume.updatedAt) }}</p>
        </div>

        <!-- 操作按钮 -->
        <div class="card-actions">
          <button class="btn-edit" @click.stop="editResume(resume.id)">编辑</button>
          <button class="btn-delete" @click.stop="handleDelete(resume)">删除</button>
        </div>
      </div>
    </div>

    <!-- 完全空状态提示 -->
    <div v-if="resumes.length === 0 && !loading && recycleBin.length === 0" class="empty-hint">
      <p>还没有简历，点击上方「新建简历」选择一个喜欢的模板开始吧</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getResumes, deleteResume as apiDeleteResume } from '../api/resume'
import { ElMessage, ElMessageBox } from 'element-plus'

const RECYCLE_BIN_KEY = 'resume_recycle_bin'
const AUTO_CLEAN_DAYS = 5

const router = useRouter()
const userStore = useUserStore()
const resumes = ref([])
const loading = ref(true)
const recycleBin = ref([])
const showRecycleBin = ref(false)
const autoCleanDays = ref(AUTO_CLEAN_DAYS)

onMounted(async () => {
  if (!userStore.userInfo) await userStore.getUserInfo()
  await loadResumes()
  loadRecycleBin()
})

// ===== 回收站 =====
function loadRecycleBin() {
  try {
    const raw = localStorage.getItem(RECYCLE_BIN_KEY)
    const list = raw ? JSON.parse(raw) : []
    // 自动清理超过5天的数据
    const now = Date.now()
    const valid = list.filter(item => {
      const elapsed = now - new Date(item.deletedAt).getTime()
      return elapsed < AUTO_CLEAN_DAYS * 24 * 60 * 60 * 1000
    })
    if (valid.length !== list.length) {
      saveRecycleBin(valid)
    }
    // 补充模板图片
    recycleBin.value = valid.map(r => ({ ...r, templateImg: getTemplateImage(r.templateId || 116) }))
  } catch (_) {
    recycleBin.value = []
  }
}

function saveRecycleBin(list) {
  localStorage.setItem(RECYCLE_BIN_KEY, JSON.stringify(list))
}

function moveToRecycleBin(resume) {
  const list = getRawRecycleBin()
  list.push({
    ...resume,
    deletedAt: new Date().toISOString(),
  })
  saveRecycleBin(list)
  loadRecycleBin()
}

function getRawRecycleBin() {
  try {
    const raw = localStorage.getItem(RECYCLE_BIN_KEY)
    return raw ? JSON.parse(raw) : []
  } catch { return [] }
}

// 恢复简历（重新通过API创建）
async function handleRestore(item) {
  try {
    // 从回收站移除
    const list = getRawRecycleBin().filter(r => r.deletedAt !== item.deletedAt)
    saveRecycleBin(list)
    loadRecycleBin()
    ElMessage.success(`「${item.title}」已从回收站恢复，请重新选择模板创建`)
    // 跳转到模板页并带上恢复的数据提示
    router.push(`/templates?restore=${encodeURIComponent(item.title)}`)
  } catch (e) {
    ElMessage.error('恢复失败')
  }
}

// 永久删除
function handlePermDelete(item) {
  ElMessageBox.confirm(
    `确定要永久删除「${item.title}」吗？此操作不可恢复。`,
    '永久删除',
    { type: 'error', confirmButtonText: '永久删除', cancelButtonText: '取消', confirmButtonClass: 'el-button--danger' }
  ).then(() => {
    const list = getRawRecycleBin().filter(r => r.deletedAt !== item.deletedAt)
    saveRecycleBin(list)
    loadRecycleBin()
    ElMessage.success('已永久删除')
  }).catch(() => {})
}

// 一键清理所有简历到回收站
async function handleClearAll() {
  const count = resumes.value.length
  try {
    await ElMessageBox.confirm(
      `确定要将全部 ${count} 份简历移入回收站吗？\n\n数据不会真正删除，可在回收站恢复。\n超过 ${AUTO_CLEAN_DAYS} 天未恢复将自动清除。`,
      '一键清理',
      { type: 'warning', confirmButtonText: '确认清理', cancelButtonText: '取消' }
    )
    // 将每份简历备份到回收站后调用API删除
    for (const resume of resumes.value) {
      moveToRecycleBin(resume)
      try { await apiDeleteResume(resume.id) } catch (_) {}
    }
    ElMessage.success(`已将 ${count} 份简历移入回收站`)
    await loadResumes()
    showRecycleBin.value = true
  } catch (_) {} // 取消
}

// 单个删除（移入回收站）
async function handleDelete(resume) {
  try {
    await ElMessageBox.confirm(
      `确定要删除「${resume.title}」吗？\n\n数据将移入回收站，${AUTO_CLEAN_DAYS}天内可恢复。`,
      '确认删除',
      { type: 'warning', confirmButtonText: '移入回收站', cancelButtonText: '取消' }
    )
    // 先备份到回收站
    moveToRecycleBin(resume)
    // 再调API删除
    await apiDeleteResume(resume.id)
    ElMessage.success('已移入回收站')
    await loadResumes()
  } catch (_) {}
}

function formatRecycleTime(deletedAt) {
  if (!deletedAt) return ''
  const diff = Date.now() - new Date(deletedAt).getTime()
  const days = Math.floor(diff / (24 * 60 * 60 * 1000))
  const hours = Math.floor((diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000))
  if (days > 0) return `已删除 ${days}天前`
  if (hours > 0) return `已删除 ${hours}小时前`
  return '刚刚删除'
}

// ===== 简历列表 =====
const loadResumes = async () => {
  loading.value = true
  try {
    const res = await getResumes()
    const list = res.data || []
    resumes.value = list.map(r => {
      const tplId = r.templateId || 116
      return { ...r, templateImg: getTemplateImage(tplId) }
    })
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function getTemplateImage(templateId) {
  const map = {
    116: 'https://aka.doubaocdn.com/s/3WQr1wZYl3',
    117: 'https://aka.doubaocdn.com/s/y4oR1wZYl3',
    118: 'https://aka.doubaocdn.com/s/1rg11wZYl3',
    119: 'https://aka.doubaocdn.com/s/dz5J1wZYl3',
    120: 'https://aka.doubaocdn.com/s/264S1wZYl3',
    121: 'https://aka.doubaocdn.com/s/XlGV1wZYl3',
    122: 'https://aka.doubaocdn.com/s/08m61wZYl3',
    123: 'https://aka.doubaocdn.com/s/tXgU1wZYl3',
    124: 'https://aka.doubaocdn.com/s/CTVV1wZYl3',
    125: 'https://aka.doubaocdn.com/s/O1MC1wZYl3',
    126: 'https://aka.doubaocdn.com/s/UkOM1wZYl3',
    127: 'https://aka.doubaocdn.com/s/EyE01wZYl3',
    128: 'https://aka.doubaocdn.com/s/7DXJ1wZYl3',
    129: 'https://aka.doubaocdn.com/s/gWHi1wZYl3',
    130: 'https://aka.doubaocdn.com/s/FUKr1wZYl3',
    131: 'https://aka.doubaocdn.com/s/COa31wZYl3',
    132: 'https://aka.doubaocdn.com/s/BuZY1wZYl3',
    133: 'https://aka.doubaocdn.com/s/GyMX1wZYl3',
    134: 'https://aka.doubaocdn.com/s/KUXY1wZYl3',
    135: 'https://aka.doubaocdn.com/s/jGm61wZYl3',
    136: 'https://aka.doubaocdn.com/s/O3SJ1wZYl3',
    137: 'https://aka.doubaocdn.com/s/NCFh1wZYl3',
    138: 'https://aka.doubaocdn.com/s/H9O21wZYl3',
    139: 'https://aka.doubaocdn.com/s/NDBR1wZYl3',
    140: 'https://aka.doubaocdn.com/s/bd1C1wZYl3',
    141: 'https://aka.doubaocdn.com/s/98eH1wZYl3',
    142: 'https://aka.doubaocdn.com/s/B2zP1wZYl3',
    143: 'https://aka.doubaocdn.com/s/hR221wZYl3',
    144: 'https://aka.doubaocdn.com/s/kwVk1wZYl3',
    145: 'https://aka.doubaocdn.com/s/XxDR1wZYl3',
    146: 'https://aka.doubaocdn.com/s/rtpX1wZYl3',
    147: 'https://aka.doubaocdn.com/s/Uk0U1wZYl3',
    148: 'https://aka.doubaocdn.com/s/IbrW1wZYl3',
    149: 'https://aka.doubaocdn.com/s/NuLI1wZYl3',
    150: 'https://aka.doubaocdn.com/s/UgUx1wZYl3',
    151: 'https://aka.doubaocdn.com/s/sUJ21wZYl3',
  }
  return map[templateId] || map[116]
}

const editResume = (id) => router.push(`/editor/${id}`)

const handleImport = () => ElMessage.info('导入功能开发中...')

const formatDate = (d) => {
  if (!d) return ''
  return new Date(d).toLocaleDateString('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}
</script>

<style scoped>
.dashboard-page {
  width: 94%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 3% 3%;
}

/* ===== 通知横幅 ===== */
.notice-banner {
  display: flex; align-items: flex-start; gap: 12px;
  background: #FEF2F2; border: 1px solid #FECACA; border-radius: 12px;
  padding: 16px 20px; margin-bottom: 28px;
}
.notice-icon { margin-top: 1px; flex-shrink: 0; }
.notice-content { display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.notice-label { font-size: 13px; font-weight: 600; color: #DC2626; }
.notice-text { font-size: 13px; color: #991B1B; line-height: 1.5; }
.notice-link {
  font-size: 13px; color: #DC2626; text-decoration: none; white-space: nowrap;
  font-weight: 500; flex-shrink: 0;
}
.notice-link:hover { text-decoration: underline; }

/* ===== 页面标题 ===== */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.page-title { font-size: 24px; font-weight: 700; color: #1a1a2e; margin: 0; letter-spacing: -0.5px; }
.header-actions { display: flex; gap: 10px; }

.btn-import {
  display: inline-flex; align-items: center; gap: 6px; padding: 9px 18px;
  border: 1px solid #D1D5DB; border-radius: 8px; background: #fff; color: #374151;
  font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.15s ease;
}
.btn-import:hover { border-color: #9CA3AF; background: #F9FAFB; }

.btn-primary {
  display: inline-flex; align-items: center; gap: 6px; padding: 9px 20px;
  border: none; border-radius: 8px; background: #1F2937; color: #fff;
  font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.15s ease;
}
.btn-primary:hover { background: #111827; }

.btn-clear {
  display: inline-flex; align-items: center; gap: 6px; padding: 9px 18px;
  border: 1px solid #FECACA; border-radius: 8px; background: #FEF2F2; color: #DC2626;
  font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.15s ease;
}
.btn-clear:hover { background: #FEE2E2; border-color: #FCA5A5; }

/* ===== 回收站 ===== */
.recycle-section {
  background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 12px;
  margin-bottom: 28px; overflow: hidden;
}
.recycle-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 18px; cursor: pointer; user-select: none;
  transition: background 0.15s;
}
.recycle-header:hover { background: #F3F4F6; }
.recycle-left {
  display: flex; align-items: center; gap: 8px;
  font-size: 14px; color: #6B7280;
}
.recycle-count {
  background: #E5E7EB; color: #374151; padding: 2px 8px;
  border-radius: 10px; font-size: 12px; font-weight: 600;
}
.recycle-hint { font-size: 12px; color: #9CA3AF; }
.arrow-icon { transition: transform 0.25s; flex-shrink: 0; }
.arrow-rotated { transform: rotate(180deg); }

.recycle-list { padding: 0 18px 14px; }
.recycle-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 14px; background: #fff; border: 1px solid #E5E7EB;
  border-radius: 8px; margin-top: 8px;
}
.recycle-item:first-child { margin-top: 0; }
.recycle-item-info { display: flex; align-items: center; gap: 12px; min-width: 0; }
.recycle-thumb {
  width: 48px; height: 64px; object-fit: cover; border-radius: 4px;
  border: 1px solid #EEE; flex-shrink: 0;
}
.recycle-detail { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.recycle-name { font-size: 14px; font-weight: 600; color: #374151; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.recycle-time { font-size: 12px; color: #9CA3AF; }
.recycle-actions { display: flex; gap: 8px; flex-shrink: 0; }
.btn-restore {
  padding: 5px 14px; border: 1px solid #D1D5DB; border-radius: 6px;
  background: #fff; color: #374151; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.15s;
}
.btn-restore:hover { background: #ECFDF5; border-color: #34D399; color: #059669; }
.btn-perm-delete {
  padding: 5px 14px; border: 1px solid #FECACA; border-radius: 6px;
  background: #fff; color: #DC2626; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.15s;
}
.btn-perm-delete:hover { background: #FEF2F2; color: #B91C1C; }

/* 折叠动画 */
.slide-enter-active, .slide-leave-active { transition: all 0.25s ease; overflow: hidden; }
.slide-enter-from, .slide-leave-to { opacity: 0; max-height: 0; }
.slide-enter-to, .slide-leave-from { opacity: 1; max-height: 1000px; }

/* ===== 简历卡片网格 ===== */
.resume-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(22%, 1fr)); gap: 1.5%; }

/* ===== 新建简历卡片 ===== */
.new-card {
  background: #fff; border: 2px dashed #D1D5DB; border-radius: 14px;
  cursor: pointer; transition: all 0.2s ease; display: flex; align-items: center;
  justify-content: center; min-height: 38vh;
}
.new-card:hover { border-color: #5B8DEF; background: #FAFCFF; }
.new-card-content { display: flex; flex-direction: column; align-items: center; gap: 10px; text-align: center; }
.plus-icon {
  width: 48px; height: 48px; border-radius: 50%; background: #F3F4F6;
  display: flex; align-items: center; justify-content: center; font-size: 24px;
  color: #6B7280; transition: all 0.2s;
}
.new-card:hover .plus-icon { background: #EBF1FE; color: #3B6EF5; }
.new-card-title { font-size: 16px; font-weight: 600; color: #374151; }
.new-card-desc { font-size: 13px; color: #9CA3AF; }

/* ===== 简历卡片 ===== */
.resume-card {
  background: #fff; border-radius: 14px; overflow: hidden; cursor: pointer;
  border: 1px solid #E5E7EB; transition: all 0.25s ease; display: flex; flex-direction: column;
}
.resume-card:hover { transform: translateY(-4px); box-shadow: 0 12px 28px rgba(0,0,0,0.08); border-color: #C7D7EE; }

.card-preview {
  aspect-ratio: 210 / 297; background: #FAFBFC; padding: 4px;
  display: flex; align-items: center; justify-content: center;
  border-bottom: 1px solid #F0F0F0; overflow: hidden;
}
.preview-img { width: 100%; height: 100%; object-fit: cover; border-radius: 4px; }

.preview-mock {
  width: 85%; height: 90%; background: #fff; border: 1px solid #EEE;
  border-radius: 6px; padding: 14px; display: flex; flex-direction: column; gap: 10px;
}
.mock-avatar { width: 32px; height: 32px; border-radius: 50%; background: #E5E7EB; flex-shrink: 0; }
.mock-info { display: flex; flex-direction: column; gap: 4px; }
.mock-name { width: 50%; height: 8px; background: #374151; border-radius: 2px; }
.mock-detail { width: 70%; height: 5px; background: #E5E7EB; border-radius: 2px; }
.mock-sections { display: flex; flex-direction: column; gap: 5px; flex: 1; padding-top: 6px; }
.mock-line { width: 100%; height: 4px; background: #F3F4F6; border-radius: 1px; }
.mock-line.short { width: 65%; }

/* 卡片信息 */
.card-body { padding: 16px; }
.card-title { margin: 0 0 6px; font-size: 15px; font-weight: 600; color: #1F2937; }
.card-meta { margin: 0; font-size: 12px; color: #9CA3AF; }

/* 操作按钮 */
.card-actions { display: flex; gap: 0; padding: 0 16px 14px; border-top: 1px solid #F5F5F5; margin-top: auto; }
.btn-edit, .btn-delete {
  flex: 1; padding: 8px 0; border: none; background: transparent;
  font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.15s; border-radius: 6px;
}
.btn-edit { color: #374151; }
.btn-edit:hover { background: #F3F4F6; color: #111827; }
.btn-delete { color: #9CA3AF; }
.btn-delete:hover { background: #FEF2F2; color: #DC2626; }

/* 空状态提示 */
.empty-hint {
  text-align: center; padding: 60px 20px; color: #9CA3AF; font-size: 14px;
}
</style>
