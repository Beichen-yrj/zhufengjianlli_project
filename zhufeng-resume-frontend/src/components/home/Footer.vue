<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <span class="footer-logo">📄</span>
        <span class="footer-name">逐风resume</span>
      </div>
      <div class="footer-text">
        <p>© {{ currentYear }} 逐风resume. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
const currentYear = new Date().getFullYear()
</script>

<style scoped>
.app-footer {
  padding: 48px 24px;
  border-top: 1px solid rgba(0,0,0,0.06);
  background: rgba(0,0,0,0.01);
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}
.footer-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}
.footer-logo { font-size: 28px; }
.footer-name { font-size: 18px; font-weight: 600; color: #555; }
.footer-text p { margin: 0; font-size: 13px; color: #999; }
</style>
