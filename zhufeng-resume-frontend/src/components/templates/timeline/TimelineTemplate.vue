<template>
  <div class="tl-template" :style="rootStyle">
    <template v-for="section in enabledSections" :key="section.id">
      <div v-if="section.id==='basic'" class="tl-basic">
        <SectionWrapper sectionId="basic">
          <BaseInfoSection :basic="resumeData.basic" :globalSettings="gs" :template="template" />
        </SectionWrapper>
      </div>
      <div v-else class="tl-item">
        <div class="tl-line"></div>
        <div class="tl-dot" :style="{backgroundColor:tc}"></div>
        <div class="tl-header" :style="{color:tc,fontSize:hs}">{{ section.title }}</div>
        <div class="tl-content">
          <SectionWrapper :sectionId="section.id">
            <EducationSection v-if="section.id==='education'" :items="resumeData.education" :globalSettings="gs" :showTitle="false" />
            <ExperienceSection v-else-if="section.id==='experience'" :items="resumeData.experience" :globalSettings="gs" :showTitle="false" />
            <ProjectSection v-else-if="section.id==='projects'" :items="resumeData.projects" :globalSettings="gs" :showTitle="false" />
            <SkillSection v-else-if="section.id==='skills'" :content="resumeData.skillContent" :globalSettings="gs" :showTitle="false" />
            <SelfEvalSection v-else-if="section.id==='selfEvaluation'" :content="resumeData.selfEvaluationContent" :globalSettings="gs" :showTitle="false" />
            <CertificatesSection v-else-if="section.id==='certificates'" :certificates="resumeData.certificates" />
          </SectionWrapper>
        </div>
      </div>
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

const props = defineProps({ resumeData: Object, template: Object })
const gs = computed(() => props.resumeData?.globalSettings || {})
const tc = computed(() => gs.value.themeColor || props.template?.colorScheme?.primary || '#000')
const hs = computed(() => `${gs.value.headerSize||20}px`)
const pp = computed(() => gs.value.pagePadding || props.template?.spacing?.contentPadding || 24)
const enabledSections = computed(() => (props.resumeData?.menuSections||[]).filter(s=>s.enabled).sort((a,b)=>a.order-b.order))
const rootStyle = computed(()=>({backgroundColor:props.template?.colorScheme?.background||'#fff',color:props.template?.colorScheme?.text||'#212529',padding:`${pp.value}px`,paddingLeft:`${pp.value+6}px`,minHeight:'100%',width:'100%'}))
</script>

<style scoped>
.tl-template{width:100%;max-width:794px;margin:0 auto}
.tl-basic{margin-bottom:16px}
.tl-item{position:relative;padding-left:24px;margin-bottom:16px}
.tl-line{position:absolute;left:0;top:10px;height:calc(100% - 10px);width:2px;background-color:#e5e7eb}
.tl-dot{position:absolute;left:-6px;top:10px;width:12px;height:12px;border-radius:50%}
.tl-header{font-size:20px;font-weight:700;margin-bottom:16px}
</style>
