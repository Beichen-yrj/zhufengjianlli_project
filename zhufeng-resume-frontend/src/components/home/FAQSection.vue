<template>
  <section class="faq-section">
    <!-- 背景装饰 -->
    <div class="faq-bg">
      <div class="faq-orb faq-orb--amber"></div>
      <div class="faq-orb faq-orb--blue"></div>
      <div class="faq-grid"></div>
    </div>

    <div class="faq-container">
      <!-- 标题区 -->
      <div class="faq-header">
        <div class="faq-label">
          <span class="faq-dot"></span>
          <span class="faq-label-text">FAQ</span>
          <span class="faq-label-line"></span>
        </div>
        <h2 class="faq-title">常见问题</h2>
        <p class="faq-subtitle">关于逐风resume的常见问题解答</p>
        <div class="faq-divider">
          <span class="faq-divider-line"></span>
          <span class="faq-divider-diamond">◆</span>
          <span class="faq-divider-line"></span>
        </div>
      </div>

      <!-- FAQ 手风琴 -->
      <div class="faq-list">
        <div
          v-for="(item, index) in faqItems"
          :key="index"
          class="faq-item"
          :class="{ 'faq-item--open': openIndex === index }"
        >
          <div class="faq-question" @click="toggleFaq(index)">
            <div class="faq-q-number">
              <span>{{ String(index + 1).padStart(2, '0') }}</span>
            </div>
            <span class="faq-q-text">{{ item.question }}</span>
            <svg
              class="faq-chevron"
              :class="{ 'faq-chevron--open': openIndex === index }"
              width="20" height="20" viewBox="0 0 24 24"
              fill="none" stroke="currentColor"
              stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            >
              <path d="m6 9 6 6 6-6"></path>
            </svg>
          </div>
          <div class="faq-answer" v-show="openIndex === index">
            <div class="faq-answer-inner">
              <div class="faq-answer-line"></div>
              <div class="faq-answer-text">{{ item.answer }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

const openIndex = ref(0)

const faqItems = [
  {
    question: '逐风resume 是免费的吗？',
    answer: '是的，逐风resume 完全免费使用。您可以无限制地创建简历、使用所有模板、导出 PDF。'
  },
  {
    question: '支持导出 PDF 格式吗？',
    answer: '支持！您可以在编辑器中一键将简历导出为高质量的 PDF 文件，格式完美适配 A4 纸张。'
  },
  {
    question: '可以使用自定义模板吗？',
    answer: '我们内置了 8 种精美模板，涵盖经典、现代、极简等多种风格。您可以通过修改模板配置来实现深度自定义。'
  },
  {
    question: 'AI 功能如何使用？',
    answer: '您需要在 AI 设置页面配置您的 API Key（支持豆包、DeepSeek、OpenAI 等多种模型），配置完成后即可使用语法检查和内容润色功能。'
  },
  {
    question: '我的数据安全吗？',
    answer: '非常安全。您的简历数据存储在您的账户下，我们不会将您的数据用于任何其他用途。AI 功能的数据仅用于实时处理，不会被存储。'
  },
  {
    question: '支持移动端编辑吗？',
    answer: '逐风resume 支持响应式布局，您可以在平板和手机端查看简历。为了获得最佳的编辑体验，建议在桌面端使用。'
  }
]

function toggleFaq(index) {
  openIndex.value = openIndex.value === index ? -1 : index
}
</script>

<style scoped>
.faq-section {
  position: relative;
  padding: 100px 0 120px;
  background: #fafbfc;
  overflow: hidden;
}

/* 背景装饰 */
.faq-bg { position: absolute; inset: 0; pointer-events: none; overflow: hidden; }
.faq-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  animation: spin-slow 20s linear infinite;
}
.faq-orb--amber {
  top: 10%; right: 5%;
  width: 450px; height: 450px;
  background: linear-gradient(135deg, rgba(251,191,36,0.06), rgba(249,115,22,0.03), transparent);
}
.faq-orb--blue {
  bottom: 15%; left: 8%;
  width: 380px; height: 380px;
  background: linear-gradient(135deg, rgba(59,130,246,0.04), rgba(99,102,241,0.02), transparent);
  animation-direction: reverse;
}
.faq-grid {
  position: absolute; inset: 0; opacity: 0.015;
  background-image: radial-gradient(circle at 1px 1px, #1b1b18 1px, transparent 0);
  background-size: 40px 40px;
}

.faq-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
  z-index: 10;
}

/* 标题 */
.faq-header { text-align: center; margin-bottom: 64px; }
.faq-label {
  display: inline-flex; align-items: center; gap: 12px;
  padding: 10px 20px;
  background: rgba(102,126,234,0.03);
  border: 1px solid rgba(102,126,234,0.1);
  border-radius: 999px;
  margin-bottom: 24px;
  backdrop-filter: blur(4px);
}
.faq-dot { width: 8px; height: 8px; background: #f59e0b; border-radius: 50%; animation: pulse 2s infinite; }
.faq-label-text { font-size: 14px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.1em; color: #555; }
.faq-label-line { width: 24px; height: 1px; background: linear-gradient(to right, rgba(102,126,234,0.2), transparent); }
.faq-title {
  font-size: 42px; font-weight: 700; color: #111;
  margin: 0 0 12px; letter-spacing: -0.02em;
}
.faq-subtitle { font-size: 16px; color: #888; margin: 0; }
.faq-divider {
  display: flex; align-items: center; justify-content: center; gap: 12px; margin-top: 32px;
}
.faq-divider-line { width: 40px; height: 1px; background: #e0e0e0; }
.faq-divider-diamond { font-size: 8px; color: #f59e0b33; }

/* FAQ 项 */
.faq-list { display: flex; flex-direction: column; gap: 16px; }
.faq-item {
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.5);
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
  overflow: hidden;
  transition: all 0.3s ease;
}
.faq-item:hover { box-shadow: 0 8px 24px rgba(0,0,0,0.06); }
.faq-item--open {
  border-color: rgba(102,126,234,0.2);
  box-shadow: 0 8px 24px rgba(102,126,234,0.08);
}

.faq-question {
  display: flex; align-items: center; gap: 16px;
  padding: 20px 24px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}
.faq-question:hover { background: rgba(0,0,0,0.01); }

.faq-q-number {
  width: 44px; height: 44px;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, rgba(102,126,234,0.05), rgba(102,126,234,0.02));
  border: 1px solid rgba(102,126,234,0.1);
  border-radius: 10px;
  flex-shrink: 0;
  font-family: monospace;
  font-weight: 700;
  font-size: 15px;
  color: rgba(102,126,234,0.6);
  transition: all 0.3s;
}
.faq-item--open .faq-q-number {
  background: linear-gradient(135deg, rgba(245,158,11,0.12), rgba(249,115,22,0.08));
  border-color: rgba(245,158,11,0.3);
  color: #d97706;
}

.faq-q-text { flex: 1; font-size: 16px; font-weight: 500; color: #333; line-height: 1.5; }

.faq-chevron {
  flex-shrink: 0; color: #ccc;
  transition: transform 0.3s ease;
}
.faq-chevron--open { transform: rotate(180deg); color: #f59e0b; }

/* 答案 */
.faq-answer { overflow: hidden; }
.faq-answer-inner {
  display: flex; gap: 0;
  padding: 0 24px 20px 84px;
}
.faq-answer-line {
  width: 1px;
  background: linear-gradient(to bottom, rgba(245,158,11,0.4), rgba(245,158,11,0.1), transparent);
  flex-shrink: 0;
  margin-right: 16px;
}
.faq-answer-text { font-size: 14px; color: #666; line-height: 1.8; }

@keyframes spin-slow { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }
</style>
