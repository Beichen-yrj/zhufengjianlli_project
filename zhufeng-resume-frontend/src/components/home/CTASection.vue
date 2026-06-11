<template>
  <section class="cta">
    <div class="cta-bg"></div>
    <div class="cta-content">
      <h2>准备好打造你的专业简历了吗？</h2>
      <p>免费注册，几分钟即可创建一份令人印象深刻的简历</p>
      <el-button type="primary" size="large" class="cta-btn" @click="$router.push('/register')">
        免费开始使用
      </el-button>
    </div>

    <!-- 底部页脚 -->
    <footer class="cta-footer">
      <div class="cf-inner">
        <div class="cf-brand">
          <span class="cf-logo">📄 逐风简历</span>
          <p>AI 驱动的智能简历制作平台</p>
        </div>
        <div class="cf-links">
          <div class="cf-col">
            <span class="cf-col-title">产品</span>
            <a href="#features">功能介绍</a>
            <router-link to="/templates">模板市场</router-link>
          </div>
          <div class="cf-col">
            <span class="cf-col-title">资源</span>
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </div>
        </div>
      </div>
      <div class="cf-bottom">
        <span>&copy; 2026 逐风简历. All rights reserved.</span>
      </div>
    </footer>
  </section>
</template>

<style scoped>
.cta {
  position: relative;
  padding: 100px 24px 0;
  overflow: hidden;
}

.cta-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
}

.cta-content {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.cta-content h2 {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
}

.cta-content p {
  font-size: 16px;
  color: rgba(255,255,255,0.6);
  margin: 0 0 32px;
}

.cta-btn {
  height: 48px;
  padding: 0 40px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
}

/* 页脚 */
.cta-footer {
  position: relative;
  z-index: 2;
  margin-top: 80px;
  padding: 40px 24px 30px;
  border-top: 1px solid rgba(255,255,255,0.08);
}

.cf-inner {
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  gap: 40px;
}

.cf-brand {
  max-width: 240px;
}

.cf-logo {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
}

.cf-brand p {
  font-size: 13px;
  color: rgba(255,255,255,0.4);
  margin: 8px 0 0;
}

.cf-links {
  display: flex;
  gap: 60px;
}

.cf-col {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cf-col-title {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255,255,255,0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.cf-col a {
  font-size: 13px;
  color: rgba(255,255,255,0.55);
  text-decoration: none;
}

.cf-col a:hover {
  color: #fff;
}

.cf-bottom {
  max-width: 1100px;
  margin: 30px auto 0;
  padding-top: 20px;
  border-top: 1px solid rgba(255,255,255,0.06);
  font-size: 12px;
  color: rgba(255,255,255,0.3);
  text-align: center;
}

@media (max-width: 640px) {
  .cta-content h2 { font-size: 26px; }
  .cf-inner { flex-direction: column; }
  .cf-links { gap: 30px; }
}
</style>
