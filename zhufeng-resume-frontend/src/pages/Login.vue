<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>逐风简历 - 登录</h2>
      </template>

      <!-- 过期/失效提示 -->
      <div v-if="loginReason" class="login-reason" :class="'reason-' + loginReason">
        {{ reasonText }}
      </div>

      <el-form :model="loginForm" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <div class="login-options">
            <el-checkbox v-model="rememberMe">记住登录状态（7天）</el-checkbox>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginForm = ref({
  username: '',
  password: ''
})

const rememberMe = ref(true)
const loading = ref(false)

// 解析跳转原因
const loginReason = computed(() => route.query.reason || '')
const reasonText = computed(() => {
  switch (loginReason.value) {
    case 'expired': return '登录已过期，请重新登录'
    case 'unauthorized': return '登录状态已失效，请重新登录'
    default: return ''
  }
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const formRef = ref(null)

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    // 记住登录：7天有效；不记住：3小时有效
    // 后端支持 double 类型参数，0.125 = 3 小时
    const expireDays = rememberMe.value ? 7 : 0.125
    await userStore.login(loginForm.value, expireDays)
    ElMessage.success('登录成功')
    // 有 redirect 参数则跳回目标页
    const redirect = route.query.redirect || '/dashboard'
    router.push(redirect)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.footer {
  text-align: center;
  color: #666;
}

.footer a {
  color: #409eff;
  text-decoration: none;
}

/* 跳转原因提示 */
.login-reason {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 16px;
}
.reason-expired,
.reason-unauthorized {
  background: #FEF2F2;
  color: #DC2626;
  border: 1px solid #FECACA;
}

/* 记住我选项 */
.login-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
