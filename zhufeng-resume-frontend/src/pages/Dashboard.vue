<template>
  <div class="dashboard">
    <div class="header">
      <h2>我的简历</h2>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/templates')">
          新建简历
        </el-button>
        <el-button @click="handleLogout">退出</el-button>
      </div>
    </div>

    <div class="content">
      <!-- 简历列表 -->
      <div v-if="resumes.length > 0" class="resume-list">
        <el-card 
          v-for="resume in resumes" 
          :key="resume.id" 
          class="resume-card"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span>{{ resume.title }}</span>
              <el-tag :type="resume.status === 1 ? 'info' : 'success'" size="small">
                {{ resume.status === 1 ? '草稿' : '已完成' }}
              </el-tag>
            </div>
          </template>
          
          <div class="card-body">
            <p>模板：{{ resume.templateName || '默认模板' }}</p>
            <p>更新时间：{{ formatDate(resume.updatedAt) }}</p>
          </div>
          
          <div class="card-footer">
            <el-button type="primary" size="small" @click="editResume(resume.id)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(resume.id)">
              删除
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无简历">
        <el-button type="primary" @click="$router.push('/templates')">
          创建第一份简历
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getResumes, deleteResume } from '../api/resume'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const resumes = ref([])

onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.getUserInfo()
  }
  await loadResumes()
})

const loadResumes = async () => {
  try {
    const res = await getResumes()
    resumes.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const editResume = (id) => {
  router.push(`/editor/${id}`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这份简历吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteResume(id)
    ElMessage.success('删除成功')
    await loadResumes()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

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

.actions {
  display: flex;
  gap: 10px;
}

.content {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.resume-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-body p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}

.card-footer {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}
</style>