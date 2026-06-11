<template>
  <section id="features" class="features">
    <div class="f-inner">
      <div class="f-header">
        <h2>强大功能，助你脱颖而出</h2>
        <p>从模板选择到 AI 辅助，一站式解决简历制作所有痛点</p>
      </div>

      <div class="f-grid">
        <div v-for="feat in featureList" :key="feat.title" class="f-card">
          <div class="f-card-icon" :style="{ background: feat.bg }">
            <span class="f-icon">{{ feat.icon }}</span>
          </div>
          <h3>{{ feat.title }}</h3>
          <p>{{ feat.desc }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const featureList = [
  {
    icon: '🤖',
    title: 'AI 智能生成',
    desc: '输入一句话需求，AI 自动生成完整简历内容，大幅节省时间',
    bg: 'linear-gradient(135deg, #667eea, #764ba2)'
  },
  {
    icon: '🎨',
    title: '多款精美模板',
    desc: '8 套专业设计模板，覆盖经典、现代、极简等多种风格，满足不同行业需求',
    bg: 'linear-gradient(135deg, #43e97b, #38f9d7)'
  },
  {
    icon: '🔍',
    title: 'AI 语法检查',
    desc: '智能检测简历中的错别字和标点错误，确保内容零失误',
    bg: 'linear-gradient(135deg, #fa709a, #fee140)'
  },
  {
    icon: '✨',
    title: 'AI 内容润色',
    desc: '基于 STAR 法则自动优化经历描述，让简历更具竞争力',
    bg: 'linear-gradient(135deg, #a18cd1, #fbc2eb)'
  },
  {
    icon: '📱',
    title: '实时预览',
    desc: '三栏编辑器，编辑同时实时查看效果，所见即所得',
    bg: 'linear-gradient(135deg, #f7971e, #ffd200)'
  },
  {
    icon: '📄',
    title: 'PDF 一键导出',
    desc: '一键生成高质量 PDF，支持多种页面尺寸，随时打印或发送',
    bg: 'linear-gradient(135deg, #4facfe, #00f2fe)'
  }
]
</script>

<style scoped>
.features {
  padding: 100px 24px;
  background: #fafbfc;
}

.f-inner {
  max-width: 1100px;
  margin: 0 auto;
}

.f-header {
  text-align: center;
  margin-bottom: 60px;
}

.f-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 12px;
}

.f-header p {
  font-size: 16px;
  color: #888;
  margin: 0;
}

.f-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.f-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px 24px;
  border: 1px solid #f0f0f0;
  transition: transform 0.2s, box-shadow 0.2s;
}

.f-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0,0,0,0.06);
}

.f-card-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.f-icon {
  font-size: 24px;
}

.f-card h3 {
  font-size: 17px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.f-card p {
  font-size: 14px;
  color: #888;
  line-height: 1.6;
  margin: 0;
}

@media (max-width: 768px) {
  .f-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .f-grid {
    grid-template-columns: 1fr;
  }
}
</style>
