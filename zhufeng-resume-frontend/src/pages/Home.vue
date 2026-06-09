<template>
  <div class="home">
    <nav class="navbar">
      <div class="nav-logo">逐风简历</div>
      <div class="nav-links">
        <el-button text @click="$router.push('/login')">登录</el-button>
        <el-button type="primary" @click="$router.push('/register')">免费注册</el-button>
      </div>
    </nav>
    <div class="hero">
      <h1>AI 智能简历制作平台</h1>
      <p class="subtitle">输入需求，AI 一键生成专业简历 | 多模板选择 | 实时预览 | 一键导出</p>
      <el-button type="primary" size="large" @click="$router.push('/register')">立即免费使用</el-button>
    </div>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #1d1e2c 0%, #2d3a5c 50%, #667eea 100%);
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 60px;
  height: 70px;
}

.nav-logo {
  color: white;
  font-size: 22px;
  font-weight: bold;
}

.hero {
  text-align: center;
  padding: 120px 20px 0;
  color: white;
}

.hero h1 {
  font-size: 48px;
  margin-bottom: 20px;
}

.subtitle {
  font-size: 18px;
  opacity: 0.85;
  margin-bottom: 40px;
}
</style>