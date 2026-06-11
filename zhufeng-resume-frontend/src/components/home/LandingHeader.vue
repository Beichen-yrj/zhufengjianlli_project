<template>
  <nav :class="['landing-nav', { scrolled: isScrolled }]">
    <div class="ln-inner">
      <div class="ln-logo" @click="$router.push('/')">
        <span class="ln-logo-icon">📄</span>
        <span class="ln-logo-text">逐风简历</span>
      </div>

      <div class="ln-links">
        <a href="#features" class="ln-link">功能介绍</a>
        <a href="#faq" class="ln-link">常见问题</a>
        <el-button v-if="hasToken" type="primary" @click="$router.push('/dashboard')">
          进入工作台
        </el-button>
        <template v-else>
          <el-button text class="ln-login-btn" @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">免费注册</el-button>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isScrolled = ref(false)
const hasToken = ref(!!localStorage.getItem('token'))

// 路由切换时重新检查 token（登录/登出后更新）
watch(() => route.path, () => {
  hasToken.value = !!localStorage.getItem('token')
})

function onScroll() {
  isScrolled.value = window.scrollY > 60
}

onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.landing-nav {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: 64px;
  transition: background 0.3s, box-shadow 0.3s;
  background: transparent;
}

.landing-nav.scrolled {
  position: fixed;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(12px);
  box-shadow: 0 1px 8px rgba(0,0,0,0.06);
}

.ln-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.ln-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.ln-logo-icon {
  font-size: 24px;
}

.ln-logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  transition: color 0.3s;
}

.scrolled .ln-logo-text { color: #1a1a1a; }

.ln-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ln-link {
  font-size: 14px;
  color: rgba(255,255,255,0.85);
  text-decoration: none;
  padding: 6px 12px;
  border-radius: 6px;
  transition: color 0.3s, background 0.3s;
}

.ln-link:hover {
  color: #fff;
  background: rgba(255,255,255,0.1);
}

.scrolled .ln-link {
  color: #555;
}

.scrolled .ln-link:hover {
  color: #1a1a1a;
  background: #f0f0f0;
}

.scrolled .ln-login-btn {
  color: #555;
}
</style>
