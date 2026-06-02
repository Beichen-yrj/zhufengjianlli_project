<template>
  <div class="dashboard">
    <div class="header">
      <h2>我的简历</h2>
      <div class="user-info">
        <span>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
      </div>
    </div>
    <div class="content">
      <p>欢迎使用逐风 AI 简历平台！</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.getUserInfo()
  }
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: white;
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.content {
  padding: 40px;
  text-align: center;
  font-size: 18px;
  color: #666;
}
</style>