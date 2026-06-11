<template>
  <div class="home-page">
    <LandingHeader />
    <HeroSection />
    <FeaturesSection />
    <FAQSection />
    <CTASection />
    <Footer />
  </div>
</template>

<script setup>
import LandingHeader from '../components/home/LandingHeader.vue'
import HeroSection from '../components/home/HeroSection.vue'
import FeaturesSection from '../components/home/FeaturesSection.vue'
import FAQSection from '../components/home/FAQSection.vue'
import CTASection from '../components/home/CTASection.vue'
import Footer from '../components/home/Footer.vue'
</script>

<style scoped>
.home-page {
  background: #fafbfc;
}
</style>
