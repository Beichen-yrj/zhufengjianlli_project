<template>
  <div class="jl-template" :style="{ '--theme': themeColor, '--font-size': fontSize + 'pt', '--name-size': (fontSize + 12) + 'pt' }">
    <!-- ====== 头部区域 ====== -->
    <div class="jl-header"
         :class="{ 'section-hover': activeSection === 'basic', 'section-active': activeSection === 'basic' }"
         @click.stop="$emit('sectionClick', 'basic')"
         draggable="true"
         @dragstart="onSectionDragStart($event, 'basic', 0)"
         @dragover.prevent="onSectionDragOver($event, 0)"
         @drop="onSectionDrop($event, 0)">
      <div class="header-main" :class="'avatar-pos-' + avatarPosition">
        <div class="header-left">
          <h1 class="jl-name" :style="getFieldStyle('name')">{{ resumeData.basic?.name || '简小历' }}</h1>
          <p class="jl-job" :style="getFieldStyle('targetJob')">{{ resumeData.basic?.targetJob || '' }}</p>
          <div class="jl-contact-list">
            <template v-for="key in orderedContactFields" :key="key">
              <div v-if="resumeData.basic?.[key]" class="contact-item" :style="getFieldStyle(key)">
                <span class="icon" v-html="FIELD_CONFIG[key]?.icon || ''"></span>
                <a v-if="FIELD_CONFIG[key]?.isLink" class="email-link">{{ resumeData.basic[key] }}</a>
                <span v-else>{{ resumeData.basic[key] }}</span>
              </div>
            </template>
            <template v-for="key in orderedCustomKeys" :key="key">
              <div v-if="resumeData.basic?.[key]" class="contact-item" :style="getFieldStyle(key)">
                <span class="icon"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z"/></svg></span>
                <span>{{ resumeData.basic[key] }}</span>
              </div>
            </template>
          </div>
        </div>
        <div class="header-right">
          <div class="avatar-box" :style="avatarBoxStyle">
            <!-- 优先使用 basic.avatar (表单上传的)，其次用 avatarUrl prop -->
            <img v-if="effectiveAvatar" :src="effectiveAvatar" alt="头像" :style="avatarImgStyle" />
            <div v-else class="avatar-placeholder">
              <svg viewBox="0 0 76 92" xmlns="http://www.w3.org/2000/svg">
                <rect width="76" height="92" rx="6" fill="#E8EEF5"/>
                <circle cx="38" cy="32" r="14" fill="#5B8DEF"/>
                <path d="M8 88 Q38 48 68 88" fill="#5B8DEF"/>
                <rect x="28" y="62" width="20" height="22" rx="4" fill="#F5A623"/>
                <rect x="35" y="68" width="6" height="10" rx="1" fill="#fff"/>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ====== 动态区块（按 renderOrder 排序，与左侧导航一致） ====== -->
    <template v-for="(secId, secIdx) in renderOrder" :key="secId">

      <!-- 求职意向 -->
      <section v-if="secId === 'intention' && isModuleEnabled('intention') && shouldShow('intention')" class="jl-section"
               :class="{ 'section-hover': activeSection === 'intention', 'section-active': activeSection === 'intention' }"
               @click.stop="$emit('sectionClick', 'intention')"
               draggable="true"
               @dragstart="onSectionDragStart($event, 'intention', secIdx)"
               @dragover.prevent
               @drop="onSectionDrop($event, secIdx)">
        <h2 class="jl-section-title"><span class="title-text">求职意向</span></h2>
        <template v-if="hasIntentionContent">
          <div class="jl-intention-grid">
            <div v-if="resumeData.basic?.targetJob" class="jl-intention-item">
              <span class="il">期望岗位：</span><strong>{{ resumeData.basic.targetJob }}</strong>
            </div>
            <div v-if="resumeData.intention?.expectedSalary" class="jl-intention-item">
              <span class="il">期望薪资：</span><strong>{{ resumeData.intention.expectedSalary }}</strong>
            </div>
            <div v-if="resumeData.basic?.city" class="jl-intention-item">
              <span class="il">工作地点：</span><strong>{{ resumeData.basic.city }}</strong>
            </div>
            <div v-if="resumeData.intention?.jobType" class="jl-intention-item">
              <span class="il">工作性质：</span><strong>{{ resumeData.intention.jobType }}</strong>
            </div>
          </div>
        </template>
        <div v-else class="jl-empty">点击添加求职意向</div>
      </section>

      <!-- 专业技能 -->
      <section v-else-if="secId === 'skills' && isModuleEnabled('skills') && shouldShow('skills')" class="jl-section"
               :class="{ 'section-hover': activeSection === 'skills', 'section-active': activeSection === 'skills' }"
               @click.stop="$emit('sectionClick', 'skills')"
               draggable="true"
               @dragstart="onSectionDragStart($event, 'skills', secIdx)"
               @dragover.prevent
               @drop="onSectionDrop($event, secIdx)">
        <h2 class="jl-section-title"><span class="title-text">专业技能</span></h2>
        <div v-if="resumeData.skills" class="jl-content" :style="getNonBasicFieldStyle('skills')" v-html="fmtText(resumeData.skills)"></div>
        <div v-else class="jl-empty">点击添加专业技能</div>
      </section>

      <!-- 工作经历 -->
      <section v-else-if="secId === 'experience' && isModuleEnabled('experience') && shouldShow('experience')" class="jl-section"
               :class="{ 'section-hover': activeSection === 'experience', 'section-active': activeSection === 'experience' }"
               @click.stop="$emit('sectionClick', 'experience')"
               draggable="true"
               @dragstart="onSectionDragStart($event, 'experience', secIdx)"
               @dragover.prevent
               @drop="onSectionDrop($event, secIdx)">
        <h2 class="jl-section-title"><span class="title-text">工作经历</span></h2>
        <template v-if="resumeData.experience && resumeData.experience.length">
          <div v-for="(exp, idx) in resumeData.experience" :key="idx" class="jl-exp-block">
            <div class="jl-block-head">
              <span class="jl-date" :style="getNonBasicFieldStyle('exp-'+idx+'-startDate')">{{ fmtDate(exp.startDate) }} - <span :style="getNonBasicFieldStyle('exp-'+idx+'-endDate')">{{ fmtDate(exp.endDate) }}</span></span>
              <span class="jl-org" :style="getNonBasicFieldStyle('exp-'+idx+'-company')">{{ exp.company || '' }}</span>
              <span class="jl-role" :style="getNonBasicFieldStyle('exp-'+idx+'-position')">{{ exp.position || '' }}</span>
            </div>
            <div v-if="exp.content" class="jl-content" :style="getNonBasicFieldStyle('exp-'+idx+'-content')" v-html="fmtText(exp.content)"></div>
          </div>
        </template>
        <div v-else class="jl-empty">点击添加工作经历</div>
      </section>

      <!-- 项目经历 -->
      <section v-else-if="secId === 'projects' && isModuleEnabled('projects') && shouldShow('projects')" class="jl-section"
               :class="{ 'section-hover': activeSection === 'projects', 'section-active': activeSection === 'projects' }"
               @click.stop="$emit('sectionClick', 'projects')"
               draggable="true"
               @dragstart="onSectionDragStart($event, 'projects', secIdx)"
               @dragover.prevent
               @drop="onSectionDrop($event, secIdx)">
        <h2 class="jl-section-title"><span class="title-text">项目经历</span></h2>
        <template v-if="resumeData.projects && resumeData.projects.length">
          <div v-for="(proj, idx) in resumeData.projects" :key="idx" class="jl-proj-block">
            <div class="jl-block-head">
              <span class="jl-date" :style="getNonBasicFieldStyle('proj-'+idx+'-startDate')">{{ fmtDate(proj.startDate) }} - <span :style="getNonBasicFieldStyle('proj-'+idx+'-endDate')">{{ fmtDate(proj.endDate) }}</span></span>
              <span class="jl-org" :style="getNonBasicFieldStyle('proj-'+idx+'-name')">{{ proj.name || '' }}</span>
              <span v-if="proj.role" class="jl-role" :style="getNonBasicFieldStyle('proj-'+idx+'-role')">{{ proj.role }}</span>
            </div>
            <p v-if="proj.description" class="jl-desc" :style="getNonBasicFieldStyle('proj-'+idx+'-description')">{{ proj.description }}</p>
            <div v-if="proj.content" class="jl-content" :style="getNonBasicFieldStyle('proj-'+idx+'-content')" v-html="fmtText(proj.content)"></div>
          </div>
        </template>
        <div v-else class="jl-empty">点击添加项目经历</div>
      </section>

      <!-- 教育背景 -->
      <section v-else-if="secId === 'education' && isModuleEnabled('education') && shouldShow('education')" class="jl-section"
               :class="{ 'section-hover': activeSection === 'education', 'section-active': activeSection === 'education' }"
               @click.stop="$emit('sectionClick', 'education')"
               draggable="true"
               @dragstart="onSectionDragStart($event, 'education', secIdx)"
               @dragover.prevent
               @drop="onSectionDrop($event, secIdx)">
        <h2 class="jl-section-title"><span class="title-text">教育背景</span></h2>
        <template v-if="resumeData.education && resumeData.education.length">
          <div v-for="(edu, idx) in resumeData.education" :key="idx" class="jl-edu-block">
            <div class="jl-block-head">
              <span class="jl-date" :style="getNonBasicFieldStyle('edu-'+idx+'-startDate')">{{ fmtDate(edu.startDate) }} - <span :style="getNonBasicFieldStyle('edu-'+idx+'-endDate')">{{ fmtDate(edu.endDate) }}</span></span>
              <span class="jl-org" :style="getNonBasicFieldStyle('edu-'+idx+'-school')">{{ edu.school || '' }}</span>
              <span class="jl-role" :style="getNonBasicFieldStyle('edu-'+idx+'-degree')">{{ edu.degree || '' }}{{ edu.major ? ' · ' + edu.major : '' }}</span>
            </div>
            <div v-if="edu.courses || edu.gpa" class="jl-edit-area" :style="getNonBasicFieldStyle('edu-'+idx+'-description')">
              <div v-if="edu.courses" class="jl-line" :style="getNonBasicFieldStyle('edu-'+idx+'-courses')">主修课程：{{ edu.courses }}</div>
              <div v-if="edu.gpa" class="jl-line" :style="getNonBasicFieldStyle('edu-'+idx+'-gpa')">成绩排名：{{ edu.gpa }}</div>
            </div>
          </div>
        </template>
        <div v-else class="jl-empty">点击添加教育背景</div>
      </section>

      <!-- 自我评价 -->
      <section v-else-if="secId === 'selfEvaluation' && isModuleEnabled('selfEvaluation') && shouldShow('selfEvaluation')" class="jl-section"
               :class="{ 'section-hover': activeSection === 'selfEvaluation', 'section-active': activeSection === 'selfEvaluation' }"
               @click.stop="$emit('sectionClick', 'selfEvaluation')"
               draggable="true"
               @dragstart="onSectionDragStart($event, 'selfEvaluation', secIdx)"
               @dragover.prevent
               @drop="onSectionDrop($event, secIdx)">
        <h2 class="jl-section-title"><span class="title-text">自我评价</span></h2>
        <div v-if="resumeData.selfEvaluation" class="jl-content" :style="getNonBasicFieldStyle('selfEvaluation')" v-html="fmtText(resumeData.selfEvaluation)"></div>
        <div v-else class="jl-empty">点击添加自我评价</div>
      </section>

      <!-- 证书荣誉 -->
      <section v-else-if="secId === 'certificates' && isModuleEnabled('certificates') && shouldShow('certificates')" class="jl-section"
               :class="{ 'section-hover': activeSection === 'certificates', 'section-active': activeSection === 'certificates' }"
               @click.stop="$emit('sectionClick', 'certificates')"
               draggable="true"
               @dragstart="onSectionDragStart($event, 'certificates', secIdx)"
               @dragover.prevent
               @drop="onSectionDrop($event, secIdx)">
        <h2 class="jl-section-title"><span class="title-text">证书荣誉</span></h2>
        <template v-if="resumeData.certificates && resumeData.certificates.length">
          <div v-for="(c, i) in resumeData.certificates" :key="i" class="jl-cert-line">
            <strong :style="getNonBasicFieldStyle('cert-'+i+'-name')">{{ c.name }}</strong>
            <span v-if="c.issuer" :style="getNonBasicFieldStyle('cert-'+i+'-issuer')">（{{ c.issuer }}）</span>
            <span v-if="c.date" class="jl-cert-date" :style="getNonBasicFieldStyle('cert-'+i+'-date')">{{ c.date }}</span>
          </div>
        </template>
        <div v-else class="jl-empty">点击添加证书荣誉</div>
      </section>
    </template>

    <!-- ====== 自定义模块（动态渲染，追加在标准模块之后）====== -->
    <template v-for="cm in (resumeData.customModules || [])" :key="cm._id">
      <section
        v-if="!hiddenSections.includes(cm._id) && (cm.content || activeSection === cm._id)"
        class="jl-section"
        :class="{ 'section-hover': activeSection === cm._id, 'section-active': activeSection === cm._id }"
        @click.stop="$emit('sectionClick', cm._id)"
        draggable="true"
        @dragstart="onSectionDragStart($event, cm._id, 99)"
        @dragover.prevent
        @drop="onSectionDrop($event, 99)"
      >
        <h2 class="jl-section-title"><span class="title-text">{{ cm.title || '自定义模块' }}</span></h2>
        <div v-if="cm.content" class="jl-content" :style="getNonBasicFieldStyle('custom-'+cm._id)" v-html="fmtText(cm.content)"></div>
        <div v-else class="jl-empty">点击编辑内容</div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  resumeData: { type: Object, default: () => ({}) },
  themeColor: { type: String, default: '#333' },
  activeSection: { type: String, default: '' },
  hiddenSections: { type: Array, default: () => [] },
  avatarUrl: { type: String, default: '' },
  enabledModuleIds: { type: Array, default: () => ['basic'] },
  fontSize: { type: Number, default: 10 },
  fontFamily: { type: String, default: '' },
  lineHeight: { type: Number, default: 1.5 },
  fieldStyles: { type: Object, default: () => ({}) }, // 字段级字体样式（基础信息）
  nonBasicFieldStyles: { type: Object, default: () => ({}) }, // 非基础字段字体样式
  basicFieldOrder: { type: Array, default: () => [] }, // 基础字段排序
})

const emit = defineEmits(['sectionClick', 'reorderSections'])

// 有效头像：优先使用 basic.avatar（表单上传的 base64），其次 avatarUrl prop
const effectiveAvatar = computed(() => {
  if (props.resumeData?.basic?.avatar) return props.resumeData.basic.avatar
  return props.avatarUrl || ''
})

// 头像位置
const avatarPosition = computed(() => props.resumeData?.basic?.avatarPosition || 'right-top')

// 头像容器样式（宽高、圆角）
const avatarBoxStyle = computed(() => {
  const w = props.resumeData?.basic?.avatarWidth || 76
  const h = props.resumeData?.basic?.avatarHeight || 92
  const r = props.resumeData?.basic?.avatarRadius ?? 6
  return {
    width: w + 'px',
    height: h + 'px',
    borderRadius: r + 'px',
  }
})

// 头像图片样式（圆角跟随容器）
const avatarImgStyle = computed(() => {
  const r = props.resumeData?.basic?.avatarRadius ?? 6
  return {
    borderRadius: r + 'px',
  }
})

// 获取字段级字体样式（用于基础信息各字段）
function getFieldStyle(fieldKey) {
  const fs = props.fieldStyles?.[fieldKey]
  if (!fs) return {}
  const style = {}
  if (fs.fontSize) style.fontSize = fs.fontSize + 'pt'
  if (fs.fontWeight) style.fontWeight = fs.fontWeight
  if (fs.color) style.color = fs.color
  if (fs.lineHeight) style.lineHeight = fs.lineHeight
  if (fs.letterSpacing) style.letterSpacing = fs.letterSpacing + 'px'
  if (fs.fontStyle) style.fontStyle = fs.fontStyle
  if (fs.textDecoration) style.textDecoration = fs.textDecoration
  if (fs.marginTop) style.marginTop = fs.marginTop + 'px'
  if (fs.marginBottom) style.marginBottom = fs.marginBottom + 'px'
  if (fs.paddingLeft) style.paddingLeft = fs.paddingLeft + 'px'
  if (fs.fontFamily) style.fontFamily = fs.fontFamily
  return style
}

// 获取非基础字段字体样式（专业技能、工作经历、项目经历等）
function getNonBasicFieldStyle(fieldKey) {
  const fs = props.nonBasicFieldStyles?.[fieldKey]
  if (!fs) return {}
  const style = {}
  if (fs.fontSize) style.fontSize = fs.fontSize + 'pt'
  if (fs.fontWeight) style.fontWeight = fs.fontWeight
  if (fs.color) style.color = fs.color
  if (fs.lineHeight) style.lineHeight = fs.lineHeight
  if (fs.letterSpacing) style.letterSpacing = fs.letterSpacing + 'px'
  if (fs.fontStyle) style.fontStyle = fs.fontStyle
  if (fs.textDecoration) style.textDecoration = fs.textDecoration
  if (fs.marginTop) style.marginTop = fs.marginTop + 'px'
  if (fs.marginBottom) style.marginBottom = fs.marginBottom + 'px'
  if (fs.paddingLeft) style.paddingLeft = fs.paddingLeft + 'px'
  if (fs.fontFamily) style.fontFamily = fs.fontFamily
  return style
}

// 字段图标和渲染配置
const FIELD_CONFIG = {
  name:       { icon: '', special: 'name' },
  targetJob:  { icon: '', special: 'job' },
  gender:     { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>', isLink: false },
  birthday:   { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>', isLink: false },
  status:     { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>', isLink: false },
  email:      { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>', isLink: true },
  phone:      { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07 19.5 19.5 0 01-6-6 19.79 19.79 0 01-3.07-8.67A2 2 0 014.11 2h3a2 2 0 012 1.72 12.84 12.84 0 00.7 2.81l-1.27 1.27a16 16 0 006.25 6.25l1.27-1.27a12.84 12.84 0 002.81.7A2 2 0 0122 16.92z"/></svg>', isLink: false },
  city:       { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/><circle cx="12" cy="10" r="3"/></svg>', isLink: false },
  age:        { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>', isLink: false },
  education:  { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>', isLink: false },
  workYears:  { icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="1.8" stroke-linecap="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>', isLink: false },
}

// 按编辑区排序的联系方式字段（排除 name 和 targetJob）
const orderedContactFields = computed(() => {
  const order = props.basicFieldOrder
  const defaultOrder = ['name','gender','status','birthday','email','phone','city']
  const keys = order.length > 0 ? order : defaultOrder
  return keys.filter(k => k !== 'name' && k !== 'targetJob' && FIELD_CONFIG[k])
})

// 自定义字段 key 列表（_custom_xxx 格式，按 basicFieldOrder 中的顺序）
const orderedCustomKeys = computed(() => {
  if (!props.basicFieldOrder.length) return []
  return props.basicFieldOrder.filter(k => k.startsWith('_custom_'))
})

// 按enabledModuleIds顺序渲染sections（完全跟随左侧导航的拖拽排序）
const renderOrder = computed(() => {
  // 直接使用 prop 中的顺序，它就是左侧拖拽后的结果
  return [...props.enabledModuleIds]
})

// 判断模块是否已启用（在左侧布局中激活）
function isModuleEnabled(sectionId) {
  // basic 始终启用
  if (sectionId === 'basic') return true
  return props.enabledModuleIds.includes(sectionId)
}

// 判断区块是否应该显示（有内容或当前选中）
function shouldShow(sectionId) {
  if (props.hiddenSections.includes(sectionId)) return false
  const data = props.resumeData
  switch (sectionId) {
    case 'basic':
      return true
    case 'intention':
      return hasIntentionContent || props.activeSection === sectionId
    case 'skills':
      return !!data.skills || props.activeSection === sectionId
    case 'experience':
      return !!(data.experience && data.experience.length) || props.activeSection === sectionId
    case 'projects':
      return !!(data.projects && data.projects.length) || props.activeSection === sectionId
    case 'education':
      return !!(data.education && data.education.length) || props.activeSection === sectionId
    case 'selfEvaluation':
      return !!data.selfEvaluation || props.activeSection === sectionId
    case 'certificates':
      return !!(data.certificates && data.certificates.length) || props.activeSection === sectionId
    default:
      return true
  }
}

// 求职意向是否有内容
const hasIntentionContent = (() => {
  const d = props.resumeData
  return !!(d?.basic?.targetJob || d?.intention?.expectedSalary || d?.basic?.city || d?.intention?.jobType)
})

// 格式化日期
function fmtDate(d) {
  if (!d) return ''
  if (String(d).includes('至今')) return '至今'
  return String(d).replace(/-/g, '/').slice(0, 7)
}

// 格式化文本
function fmtText(text) {
  if (!text) return ''
  let s = String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
  return s.split('\n').map(line => {
    const t = line.trim()
    if (!t) return '<br>'
    if (/^[-•*]\s/.test(t)) {
      return `<div class="list-item">${t.replace(/^[-•*]\s*/, '')}</div>`
    }
    if (/^\d+[\.、]\s/.test(t)) {
      return `<div class="list-item">${t.replace(/^\d+[\.、]\s*/, '')}</div>`
    }
    return `<div class="text-line">${line}</div>`
  }).join('')
}

// ---- 画布拖拽排序 ----
let dragSecId = null, dragSecIdx = -1
function onSectionDragStart(e, id, idx) {
  dragSecId = id; dragSecIdx = idx
  e.dataTransfer.effectAllowed = 'move'
  e.dataTransfer.setData('text/plain', id)
}
function onSectionDragOver(e) {
  e.currentTarget.style.outline = '2px dashed #667eea'
}
function onSectionDrop(e, targetIdx) {
  e.preventDefault()
  e.currentTarget.style.outline = ''
  if (dragSecId === null) return
  emit('reorderSections', { fromId: dragSecId, fromIdx: dragSecIdx, toIdx: targetIdx })
  dragSecId = null; dragSecIdx = -1
}
</script>

<style scoped>
.jl-template {
  --theme: #333;
  --font-size: 10pt;
  --name-size: 22pt;
  width: 210mm;
  min-height: 297mm;
  padding: 10mm 14mm;
  box-sizing: border-box;
  background: #fff;
  color: #222;
  font-family: -apple-system, "PingFang SC", "Microsoft YaHei", "Helvetica Neue", sans-serif;
  font-size: var(--font-size);
  line-height: 1.65;
  overflow: hidden;
}

/* ========== 头部区域 ========== */
.jl-header {
  cursor: pointer;
  transition: background-color 0.15s ease;
  border-radius: 4px;
  margin-bottom: 5mm;
}

.jl-header.section-hover:hover {
  background-color: rgba(102, 126, 234, 0.04);
}

.jl-header.section-active {
  background-color: rgba(102, 126, 234, 0.08);
  outline: 1.5px dashed rgba(102, 126, 234, 0.35);
  outline-offset: -1px;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-left {
  flex: 1;
  padding-right: 10mm;
}

.jl-name {
  font-size: var(--name-size);
  font-weight: 800;
  color: var(--theme);
  margin: 0 0 3px 0;
  letter-spacing: 1px;
  line-height: 1.2;
}

.jl-job {
  font-size: 11pt;
  color: #555;
  margin: 0 0 6px 0;
  line-height: 1.4;
}

.jl-contact-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2px 16px;
  margin-top: 4px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 9.5pt;
  color: #444;
  line-height: 1.6;
}

.contact-item .icon {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.contact-item .icon svg {
  width: 14px;
  height: 14px;
}

.email-link {
  color: var(--theme);
  text-decoration: underline;
  text-underline-offset: 2px;
  word-break: break-all;
}

/* 头像 —— 圆形 */
.header-main {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.header-right {
  flex-shrink: 0;
}

.avatar-box {
  border-radius: 6px;
  overflow: hidden;
  background: #f0f0f0;
  border: 2px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-placeholder svg {
  width: 48px;
  height: 60px;
}

/* 头像位置变体 */
.header-main.avatar-pos-right-top { flex-direction: row; align-items: flex-start; }
.header-main.avatar-pos-right-center { flex-direction: row; align-items: center; }
.header-main.avatar-pos-right-bottom { flex-direction: row; align-items: flex-end; }

.header-main.avatar-pos-left-top { flex-direction: row-reverse; align-items: flex-start; }
.header-main.avatar-pos-left-center { flex-direction: row-reverse; align-items: center; }
.header-main.avatar-pos-left-bottom { flex-direction: row-reverse; align-items: flex-end; }

.header-main.avatar-pos-center-top { flex-direction: column; align-items: center; }
.header-main.avatar-pos-center-bottom { flex-direction: column-reverse; align-items: center; }

/* ========== 区块通用样式 ========== */
.jl-section {
  margin-bottom: 5mm;
  padding: 2mm 0;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.15s ease;
  position: relative;
}

.jl-section[draggable="true"] {
  cursor: grab;
}

.jl-section[draggable="true"]:active {
  cursor: grabbing;
}

.jl-section.section-hover:hover {
  background-color: rgba(102, 126, 234, 0.04);
}

.jl-section.section-active {
  background-color: rgba(102, 126, 234, 0.08);
  outline: 1.5px dashed rgba(102, 126, 234, 0.35);
  outline-offset: -1px;
}

.jl-section-title {
  font-size: 13pt;
  font-weight: 800;
  color: var(--theme);
  margin: 0 0 3mm 0;
  letter-spacing: 0.5px;
  line-height: 1.3;
}

.title-text {
  display: inline-block;
  position: relative;
  padding-bottom: 2px;
}

.title-text::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 2.5px;
  background-color: var(--theme);
  border-radius: 1px;
}

/* ========== 求职意向 ========== */
.jl-intention-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2mm 8mm;
  padding-left: 2mm;
}

.jl-intention-item {
  font-size: 10pt;
  color: #444;
  line-height: 1.6;
}

.il {
  color: #888;
}

.jl-intention-item strong {
  color: #222;
}

/* ========== 内容块通用 ========== */
.jl-exp-block,
.jl-proj-block,
.jl-edu-block {
  margin-bottom: 4mm;
}

.jl-block-head {
  display: flex;
  align-items: baseline;
  gap: 6mm;
  flex-wrap: wrap;
  margin-bottom: 2mm;
}

.jl-date {
  font-size: 9.5pt;
  color: #777;
  white-space: nowrap;
  min-width: 90px;
}

.jl-org {
  font-size: 11.5pt;
  font-weight: 700;
  color: #222;
}

.jl-role {
  font-size: 10.5pt;
  color: #444;
  margin-left: auto;
}

.jl-desc {
  font-size: 9.5pt;
  color: #666;
  margin: 1.5mm 0 2mm;
  line-height: 1.5;
}

/* ========== 文本内容 ========== */
.jl-content {
  font-size: 10pt;
  color: #333;
  line-height: 1.75;
  padding-left: 2mm;
}

.jl-content :deep(.list-item) {
  position: relative;
  padding-left: 14px;
  margin-bottom: 2px;
}

.jl-content :deep(.list-item)::before {
  content: '\2022';
  position: absolute;
  left: 0;
  color: var(--theme);
  font-weight: bold;
  font-size: 10pt;
}

.jl-content :deep(.text-line) {
  margin-bottom: 2px;
}

/* ========== 教育背景详情 ========== */
.jl-edit-area {
  border: 1px solid #b8d4f0;
  border-radius: 3px;
  padding: 2.5mm 4mm;
  background: #f5f9ff;
  font-size: 9.5pt;
  color: #555;
  margin-top: 2mm;
}

.jl-line {
  line-height: 1.65;
}

/* ========== 证书荣誉 ========== */
.jl-cert-line {
  font-size: 10pt;
  color: #444;
  margin-bottom: 1.5mm;
  padding-left: 2mm;
  line-height: 1.6;
}

.jl-cert-date {
  color: #999;
  font-size: 9pt;
  margin-left: 4px;
}

/* ========== 空状态提示 ========== */
.jl-empty {
  padding: 3mm 4mm;
  border: 1px dashed #ccc;
  border-radius: 3px;
  color: #aaa;
  font-size: 9.5pt;
  text-align: center;
  font-style: italic;
  background: #fafafa;
}

/* ========== 打印样式 ========== */
@media print {
  .jl-template {
    width: 210mm;
    min-height: 297mm;
    padding: 10mm 14mm;
    box-shadow: none;
  }

  .jl-header,
  .jl-section {
    background: none !important;
    outline: none !important;
  }

  .jl-header:hover,
  .jl-section:hover {
    background: none !important;
  }

  @page {
    size: A4;
    margin: 0;
  }
}
</style>
