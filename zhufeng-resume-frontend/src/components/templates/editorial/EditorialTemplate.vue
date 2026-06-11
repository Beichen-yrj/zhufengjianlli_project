<template>
  <div class="ed-template" :style="rootStyle">
    <template v-for="section in enabledSections" :key="section.id">
      <SectionWrapper :sectionId="section.id" :style="{ marginTop: section.id==='basic'?'0':sectionSpacingPx }">
        <SectionTitle v-if="section.id!=='basic'" :sectionTitle="section.title" :globalSettings="gs" />
        <BaseInfoSection v-if="section.id==='basic'" :basic="resumeData.basic" :globalSettings="gs" :template="template" />
        <EducationSection v-else-if="section.id==='education'" :items="resumeData.education" :globalSettings="gs" />
        <ExperienceSection v-else-if="section.id==='experience'" :items="resumeData.experience" :globalSettings="gs" />
        <ProjectSection v-else-if="section.id==='projects'" :items="resumeData.projects" :globalSettings="gs" />
        <SkillSection v-else-if="section.id==='skills'" :content="resumeData.skillContent" :globalSettings="gs" />
        <SelfEvalSection v-else-if="section.id==='selfEvaluation'" :content="resumeData.selfEvaluationContent" :globalSettings="gs" />
        <CertificatesSection v-else-if="section.id==='certificates'" :certificates="resumeData.certificates" />
      </SectionWrapper>
    </template>
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
const pp = computed(() => gs.value.pagePadding || props.template?.spacing?.contentPadding || 36)
const enabledSections = computed(() => (props.resumeData?.menuSections||[]).filter(s=>s.enabled).sort((a,b)=>a.order-b.order))
const sectionSpacingPx = computed(()=>`${gs.value.sectionSpacing||32}px`)
const rootStyle = computed(()=>({backgroundColor:props.template?.colorScheme?.background||'#FAF8F5',color:props.template?.colorScheme?.text||'#1a1a1a',minHeight:'100%',width:'100%',margin:`-${pp.value}px`,padding:`${pp.value}px`,paddingTop:`${pp.value+16}px`}))
</script>
<style scoped>.ed-template{width:100%;max-width:794px;margin:0 auto;font-family:Georgia,'Times New Roman',serif}</style>
