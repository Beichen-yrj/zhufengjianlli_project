import { defineStore } from 'pinia'
import { DEFAULT_GLOBAL_SETTINGS, DEFAULT_BASIC_INFO } from '../config/constants'

function genId() {
  return Date.now().toString(36) + Math.random().toString(36).slice(2)
}

export const useResumeStore = defineStore('resume', {
  state: () => ({
    resumes: {},
    activeResumeId: null
  }),

  getters: {
    activeResume: (state) => state.resumes[state.activeResumeId] || null
  },

  actions: {
    // ===== Resume CRUD =====
    createResume(templateId = 'classic', title = '新建简历') {
      const id = genId()
      const defaults = {
        basic: { ...DEFAULT_BASIC_INFO },
        education: [],
        experience: [],
        projects: [],
        skillContent: '',
        selfEvaluationContent: '',
        customData: {},
        certificates: [],
        menuSections: [
          { id: 'basic', title: '基本信息', icon: '👤', enabled: true, order: 0 },
          { id: 'education', title: '教育经历', icon: '🎓', enabled: true, order: 1 },
          { id: 'experience', title: '工作经历', icon: '💼', enabled: true, order: 2 },
          { id: 'projects', title: '项目经历', icon: '🚀', enabled: true, order: 3 },
          { id: 'skills', title: '技能特长', icon: '⚡', enabled: true, order: 4 },
          { id: 'selfEvaluation', title: '自我评价', icon: '💬', enabled: true, order: 5 },
        ],
        globalSettings: { ...DEFAULT_GLOBAL_SETTINGS },
        activeSection: 'basic',
        templateId,
      }
      this.resumes[id] = {
        id,
        title,
        ...defaults,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      this.activeResumeId = id
      return id
    },

    setActiveResume(id) {
      this.activeResumeId = id
    },

    deleteResume(id) {
      delete this.resumes[id]
      if (this.activeResumeId === id) this.activeResumeId = null
    },

    updateResumeTitle(title) {
      if (!this.activeResume) return
      this.activeResume.title = title
      this.activeResume.updatedAt = new Date().toISOString()
    },

    // ===== Basic Info =====
    updateBasicInfo(data) {
      if (!this.activeResume) return
      Object.assign(this.activeResume.basic, data)
      this.activeResume.updatedAt = new Date().toISOString()
    },

    // ===== Education =====
    updateEducation(education) {
      if (!this.activeResume) return
      const idx = this.activeResume.education.findIndex(e => e.id === education.id)
      if (idx >= 0) {
        this.activeResume.education[idx] = education
      } else {
        this.activeResume.education.push(education)
      }
      this.activeResume.updatedAt = new Date().toISOString()
    },

    updateEducationBatch(educations) {
      if (!this.activeResume) return
      this.activeResume.education = educations
      this.activeResume.updatedAt = new Date().toISOString()
    },

    deleteEducation(id) {
      if (!this.activeResume) return
      this.activeResume.education = this.activeResume.education.filter(e => e.id !== id)
    },

    // ===== Experience =====
    updateExperience(experience) {
      if (!this.activeResume) return
      const idx = this.activeResume.experience.findIndex(e => e.id === experience.id)
      if (idx >= 0) {
        this.activeResume.experience[idx] = experience
      } else {
        this.activeResume.experience.push(experience)
      }
      this.activeResume.updatedAt = new Date().toISOString()
    },

    updateExperienceBatch(experiences) {
      if (!this.activeResume) return
      this.activeResume.experience = experiences
      this.activeResume.updatedAt = new Date().toISOString()
    },

    deleteExperience(id) {
      if (!this.activeResume) return
      this.activeResume.experience = this.activeResume.experience.filter(e => e.id !== id)
    },

    // ===== Projects =====
    updateProject(project) {
      if (!this.activeResume) return
      const idx = this.activeResume.projects.findIndex(p => p.id === project.id)
      if (idx >= 0) {
        this.activeResume.projects[idx] = project
      } else {
        this.activeResume.projects.push(project)
      }
      this.activeResume.updatedAt = new Date().toISOString()
    },

    updateProjectsBatch(projects) {
      if (!this.activeResume) return
      this.activeResume.projects = projects
      this.activeResume.updatedAt = new Date().toISOString()
    },

    deleteProject(id) {
      if (!this.activeResume) return
      this.activeResume.projects = this.activeResume.projects.filter(p => p.id !== id)
    },

    // ===== Skills =====
    updateSkillContent(content) {
      if (!this.activeResume) return
      this.activeResume.skillContent = content
      this.activeResume.updatedAt = new Date().toISOString()
    },

    // ===== Self Evaluation =====
    updateSelfEvaluationContent(content) {
      if (!this.activeResume) return
      this.activeResume.selfEvaluationContent = content
      this.activeResume.updatedAt = new Date().toISOString()
    },

    // ===== Certificates =====
    addCertificate(cert) {
      if (!this.activeResume) return
      const idx = this.activeResume.certificates.findIndex(c => c.id === cert.id)
      if (idx >= 0) {
        this.activeResume.certificates[idx] = cert
      } else {
        this.activeResume.certificates.push(cert)
      }
      this.activeResume.updatedAt = new Date().toISOString()
    },

    updateCertificate(id, updates) {
      if (!this.activeResume) return
      const cert = this.activeResume.certificates.find(c => c.id === id)
      if (cert) Object.assign(cert, updates)
    },

    updateCertificatesBatch(certificates) {
      if (!this.activeResume) return
      this.activeResume.certificates = certificates
      this.activeResume.updatedAt = new Date().toISOString()
    },

    removeCertificate(id) {
      if (!this.activeResume) return
      this.activeResume.certificates = this.activeResume.certificates.filter(c => c.id !== id)
    },

    // ===== Custom Data =====
    addCustomData(sectionId) {
      if (!this.activeResume) return
      if (!this.activeResume.customData[sectionId]) {
        this.activeResume.customData[sectionId] = []
      }
      this.activeResume.customData[sectionId].push({
        id: genId(),
        title: '未命名',
        subtitle: '',
        dateRange: '',
        description: '',
        visible: true,
      })
    },

    updateCustomData(sectionId, items) {
      if (!this.activeResume) return
      this.activeResume.customData[sectionId] = items
      this.activeResume.updatedAt = new Date().toISOString()
    },

    updateCustomItem(sectionId, itemId, updates) {
      if (!this.activeResume) return
      const items = this.activeResume.customData[sectionId]
      if (items) {
        const item = items.find(i => i.id === itemId)
        if (item) Object.assign(item, updates)
      }
    },

    removeCustomItem(sectionId, itemId) {
      if (!this.activeResume) return
      if (this.activeResume.customData[sectionId]) {
        this.activeResume.customData[sectionId] = this.activeResume.customData[sectionId].filter(i => i.id !== itemId)
      }
    },

    removeCustomSection(sectionId) {
      if (!this.activeResume) return
      delete this.activeResume.customData[sectionId]
      this.activeResume.menuSections = this.activeResume.menuSections.filter(s => s.id !== sectionId)
    },

    // ===== Menu Sections =====
    setActiveSection(sectionId) {
      if (!this.activeResume) return
      this.activeResume.activeSection = sectionId
    },

    toggleSectionVisibility(sectionId) {
      if (!this.activeResume) return
      const s = this.activeResume.menuSections.find(s => s.id === sectionId)
      if (s) s.enabled = !s.enabled
    },

    updateMenuSections(sections) {
      if (!this.activeResume) return
      this.activeResume.menuSections = sections
    },

    reorderSections(newOrder) {
      if (!this.activeResume) return
      const basicSection = this.activeResume.menuSections.find(s => s.id === 'basic')
      this.activeResume.menuSections = [
        basicSection,
        ...newOrder.filter(s => s.id !== 'basic')
      ].filter(Boolean).map((s, i) => ({ ...s, order: i }))
    },

    // ===== Global Settings =====
    updateGlobalSettings(settings) {
      if (!this.activeResume) return
      Object.assign(this.activeResume.globalSettings, settings)
      this.activeResume.updatedAt = new Date().toISOString()
    },

    setThemeColor(color) {
      this.updateGlobalSettings({ themeColor: color })
    },

    setTemplate(templateId) {
      if (!this.activeResume) return
      this.activeResume.templateId = templateId
      this.activeResume.updatedAt = new Date().toISOString()
    },
  }
})
