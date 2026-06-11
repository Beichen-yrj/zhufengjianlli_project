<template>
  <div class="classic-template" :style="rootStyle">
    <template v-for="section in enabledSections" :key="section.id">
      <SectionWrapper v-if="section.id === 'basic'" :sectionId="section.id">
        <BaseInfoSection :basic="resumeData.basic" :globalSettings="gs" :template="template" />
      </SectionWrapper>

      <SectionWrapper v-else :sectionId="section.id" :style="{ marginTop: sectionSpacingPx }">
        <SectionTitle :sectionTitle="section.title" :globalSettings="gs" />
        <EducationSection v-if="section.id === 'education'" :items="resumeData.education" :globalSettings="gs" :paragraphSpacing="ps" />
        <ExperienceSection v-else-if="section.id === 'experience'" :items="resumeData.experience" :globalSettings="gs" :paragraphSpacing="ps" />
        <ProjectSection v-else-if="section.id === 'projects'" :items="resumeData.projects" :globalSettings="gs" :paragraphSpacing="ps" />
        <SkillSection v-else-if="section.id === 'skills'" :content="resumeData.skillContent" :globalSettings="gs" />
        <SelfEvalSection v-else-if="section.id === 'selfEvaluation'" :content="resumeData.selfEvaluationContent" :globalSettings="gs" />
        <CertificatesSection v-else-if="section.id === 'certificates'" :certificates="resumeData.certificates" />
      </SectionWrapper>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import BaseInfoSection from './sections/BaseInfoSection.vue'
import EducationSection from './sections/EducationSection.vue'
import ExperienceSection from './sections/ExperienceSection.vue'
import ProjectSection from './sections/ProjectSection.vue'
import SkillSection from './sections/SkillSection.vue'
import SelfEvalSection from './sections/SelfEvalSection.vue'
import CertificatesSection from '../shared/CertificatesSection.vue'
import SectionWrapper from '../shared/SectionWrapper.vue'
import SectionTitle from '../shared/SectionTitle.vue'

const props = defineProps({
  resumeData: { type: Object, required: true },
  template: { type: Object, default: () => ({}) }
})

const gs = computed(() => props.resumeData.globalSettings || {})
const ps = computed(() => gs.value.paragraphSpacing || props.template.spacing?.itemGap || 12)

const enabledSections = computed(() => {
  const sections = props.resumeData.menuSections || []
  return sections.filter(s => s.enabled).sort((a, b) => a.order - b.order)
})

const sectionSpacingPx = computed(() => `${gs.value.sectionSpacing || props.template.spacing?.sectionGap || 16}px`)

const rootStyle = computed(() => ({
  backgroundColor: props.template.colorScheme?.background || '#fff',
  color: props.template.colorScheme?.text || '#212529',
  padding: `${gs.value.pagePadding || props.template.spacing?.contentPadding || 32}px`,
  minHeight: '100%', width: '100%'
}))
</script>

<style scoped>
.classic-template { width: 100%; max-width: 794px; margin: 0 auto; }
</style>
