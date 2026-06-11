<template>
  <section class="hero">
    <div class="hero-bg"></div>
    <div class="hero-content">
      <div class="hero-badge">
        <span>✨ AI 驱动的智能简历平台</span>
      </div>
      <h1 class="hero-title">
        创建令人印象深刻的<br />专业简历
      </h1>
      <p class="hero-desc">
        告别繁琐的排版，用 AI 和精美模板，只需几分钟即可打造一份完美的求职简历
      </p>
      <div class="hero-actions">
        <el-button type="primary" size="large" class="hero-btn-primary" @click="$router.push('/register')">
          立即免费使用
        </el-button>
        <el-button size="large" class="hero-btn-secondary" @click="$router.push('/templates')">
          浏览模板
        </el-button>
      </div>

      <!-- 数据指标 -->
      <div class="hero-stats">
        <div class="hero-stat">
          <span class="hs-num">8+</span>
          <span class="hs-label">精美模板</span>
        </div>
        <div class="hero-stat">
          <span class="hs-num">AI</span>
          <span class="hs-label">智能辅助</span>
        </div>
        <div class="hero-stat">
          <span class="hs-num">PDF</span>
          <span class="hs-label">一键导出</span>
        </div>
      </div>
    </div>

    <!-- 装饰元素 -->
    <div class="hero-decor hero-decor-1"></div>
    <div class="hero-decor hero-decor-2"></div>
  </section>
</template>

<style scoped>
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 40%, #0f3460 70%, #1a1a2e 100%);
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 720px;
  padding: 0 24px;
}

.hero-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  background: rgba(102, 126, 234, 0.15);
  border: 1px solid rgba(102, 126, 234, 0.3);
  color: #a8b4ff;
  font-size: 13px;
  margin-bottom: 24px;
}

.hero-title {
  font-size: 52px;
  font-weight: 800;
  color: #fff;
  line-height: 1.2;
  margin: 0 0 20px;
  letter-spacing: -0.5px;
}

.hero-desc {
  font-size: 18px;
  color: rgba(255,255,255,0.65);
  line-height: 1.7;
  margin: 0 auto 36px;
  max-width: 520px;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 14px;
  margin-bottom: 60px;
}

.hero-btn-primary {
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
}

.hero-btn-secondary {
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
  border-radius: 10px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  color: #fff;
}

.hero-btn-secondary:hover {
  background: rgba(255,255,255,0.14);
  border-color: rgba(255,255,255,0.25);
}

/* 数据指标 */
.hero-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
}

.hero-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.hs-num {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.hs-label {
  font-size: 13px;
  color: rgba(255,255,255,0.5);
}

/* 装饰 */
.hero-decor {
  position: absolute;
  border-radius: 50%;
  opacity: 0.06;
  pointer-events: none;
}

.hero-decor-1 {
  width: 500px;
  height: 500px;
  background: #667eea;
  top: -250px;
  right: -100px;
}

.hero-decor-2 {
  width: 300px;
  height: 300px;
  background: #764ba2;
  bottom: -80px;
  left: -100px;
}

@media (max-width: 640px) {
  .hero-title { font-size: 32px; }
  .hero-stats { gap: 30px; }
  .hs-num { font-size: 22px; }
}
</style>
