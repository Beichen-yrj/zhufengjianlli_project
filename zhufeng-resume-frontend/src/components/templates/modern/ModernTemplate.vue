<template>
  <table class="modern-template" style="width:100%;border-collapse:collapse;table-layout:fixed">
    <tbody><tr>
      <td class="modern-sidebar" :style="sidebarStyle">
        <div v-if="basicSection">
          <BaseInfoSection :basic="resumeData.basic" :globalSettings="gs" :template="template" />
        </div>
        <div v-if="educationSection" class="modern-sidebar-edu">
          <SectionTitle sectionTitle="教育经历" :globalSettings="gs" variant="sidebar" />
          <EducationSection :items="resumeData.education" :globalSettings="gs" variant="sidebar" />
        </div>
      </td>
      <td class="modern-content" :style="contentStyle">
        <template v-for="section in otherSections" :key="section.id">
          <SectionWrapper :sectionId="section.id" :style="{ marginTop: sectionSpacingPx }">
            <SectionTitle :sectionTitle="section.title" :globalSettings="gs" />
            <ExperienceSection v-if="section.id==='experience'" :items="resumeData.experience" :globalSettings="gs" />
            <ProjectSection v-else-if="section.id==='projects'" :items="resumeData.projects" :globalSettings="gs" />
            <SkillSection v-else-if="section.id==='skills'" :content="resumeData.skillContent" :globalSettings="gs" />
            <SelfEvalSection v-else-if="section.id==='selfEvaluation'" :content="resumeData.selfEvaluationContent" :globalSettings="gs" />
            <CertificatesSection v-else-if="section.id==='certificates'" :certificates="resumeData.certificates" />
          </SectionWrapper>
        </template>
      </td>
    </tr></tbody>
  </table>
</template>

<script setup>
import { computed } from 'vue'
import BaseInfoSection from '../classic/sections/BaseInfoSection.vue'
import EducationSection from '../classic/sections/EducationSection.vue'
import ExperienceSection from '../classic/sections/ExperienceSection.vue'
import ProjectSection from '../classic/sections/ProjectSection.vue'
import SkillSection from '../classic/sections/SkillSection.vue'
import SelfEvalSection from '../classic/sections/SelfEvalSection.vue'
import CertificatesSection from '../shared/CertificatesSection.vue'
import SectionWrapper from '../shared/SectionWrapper.vue'
import SectionTitle from '../shared/SectionTitle.vue'

const props = defineProps({ resumeData: Object, template: Object })
const gs = computed(() => props.resumeData?.globalSettings || {})
const tc = computed(() => gs.value.themeColor || props.template?.colorScheme?.primary || '#000')
const sections = computed(() => (props.resumeData?.menuSections||[]).filter(s=>s.enabled).sort((a,b)=>a.order-b.order))
const basicSection = computed(()=>sections.value.find(s=>s.id==='basic'))
const educationSection = computed(()=>sections.value.find(s=>s.id==='education'))
const otherSections = computed(()=>sections.value.filter(s=>s.id!=='basic'&&s.id!=='education'))
const sectionSpacingPx = computed(()=>`${gs.value.sectionSpacing||8}px`)
const sidebarStyle = computed(()=>({width:'33.33%',backgroundColor:tc.value,color:'#fff',padding:'16px',paddingTop:`${gs.value.sectionSpacing||8}px`,verticalAlign:'top'}))
const contentStyle = computed(()=>({width:'66.67%',backgroundColor:props.template?.colorScheme?.background||'#fff',color:props.template?.colorScheme?.text||'#212529',padding:'16px',paddingTop:'0',verticalAlign:'top'}))
</script>

<style scoped>
.modern-template { width: 100%; max-width: 794px; margin: 0 auto; }
.modern-sidebar { word-break: break-word; }
.modern-sidebar-edu { margin-top: 24px; }
.modern-content { word-break: break-word; }
</style>
