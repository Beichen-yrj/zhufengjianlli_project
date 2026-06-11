<template>
  <div class="cr-template" :style="rootStyle">
    <div v-if="basicSection" class="cr-header" :style="headerStyle">
      <SectionWrapper sectionId="basic">
        <BaseInfoSection :basic="resumeData.basic" :globalSettings="gs" :template="template" />
      </SectionWrapper>
    </div>
    <div class="cr-body">
      <template v-for="section in otherSections" :key="section.id">
        <SectionWrapper :sectionId="section.id" :style="{ marginTop: sectionSpacingPx }">
          <SectionTitle :sectionTitle="section.title" :globalSettings="gs" />
          <EducationSection v-if="section.id==='education'" :items="resumeData.education" :globalSettings="gs" />
          <ExperienceSection v-else-if="section.id==='experience'" :items="resumeData.experience" :globalSettings="gs" />
          <ProjectSection v-else-if="section.id==='projects'" :items="resumeData.projects" :globalSettings="gs" />
          <SkillSection v-else-if="section.id==='skills'" :content="resumeData.skillContent" :globalSettings="gs" />
          <SelfEvalSection v-else-if="section.id==='selfEvaluation'" :content="resumeData.selfEvaluationContent" :globalSettings="gs" />
          <CertificatesSection v-else-if="section.id==='certificates'" :certificates="resumeData.certificates" />
        </SectionWrapper>
      </template>
    </div>
  </div>
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
const otherSections = computed(()=>sections.value.filter(s=>s.id!=='basic'))
const sectionSpacingPx = computed(()=>`${gs.value.sectionSpacing||16}px`)
const rootStyle = computed(()=>({backgroundColor:props.template?.colorScheme?.background||'#fff',color:props.template?.colorScheme?.text||'#1e293b',minHeight:'100%',width:'100%'}))
const headerStyle = computed(()=>({backgroundColor:tc.value,color:'#fff',padding:'32px 16px',paddingRight:'0',borderBottomLeftRadius:'24px',borderBottomRightRadius:'24px'}))
</script>
<style scoped>.cr-template{width:100%;max-width:794px;margin:0 auto}.cr-header{width:100%}.cr-body{width:100%;max-width:720px;margin:0 auto;padding:0 16px}</style>
