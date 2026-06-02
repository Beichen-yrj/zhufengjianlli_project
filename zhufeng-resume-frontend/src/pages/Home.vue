<template>
  <div class="home">
    <div class="hero">
      <h1>逐风 AI 简历平台</h1>
      <p>智能 AI 驱动的在线简历制作平台</p>
      <div class="buttons">
        <el-button type="primary" size="large" @click="$router.push('/register')">
          立即开始
        </el-button>
        <el-button size="large" @click="$router.push('/login')">
          已有账号登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.hero {
  text-align: center;
  color: white;
}

.hero h1 {
  font-size: 48px;
  margin-bottom: 20px;
}

.hero p {
  font-size: 20px;
  margin-bottom: 40px;
}

.buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}
</style>