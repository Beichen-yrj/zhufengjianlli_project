<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <div class="logo">
        <span v-if="!isCollapse">逐风简历</span>
        <span v-else>逐</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        :collapse="isCollapse"
        background-color="#1d1e2c"
        text-color="#8b8fa3"
        active-text-color="#667eea"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Document /></el-icon>
          <span>我的简历</span>
        </el-menu-item>
        <el-menu-item index="/templates">
          <el-icon><Grid /></el-icon>
          <span>模板中心</span>
        </el-menu-item>
        <el-menu-item index="/ai-generate">
          <el-icon><MagicStick /></el-icon>
          <span>AI 生成</span>
        </el-menu-item>
      </el-menu>
      
      <div class="sidebar-footer">
        <el-button @click="handleLogout" text>
          <el-icon><SwitchButton /></el-icon>
        </el-button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<script>
import { ref } from 'vue'
</script>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  width: 220px;
  background: #1d1e2c;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 1px solid #2d2e3c;
}

.sidebar-footer {
  margin-top: auto;
  padding: 15px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #2d2e3c;
}

.main-content {
  flex: 1;
  overflow-y: auto;
}
</style>