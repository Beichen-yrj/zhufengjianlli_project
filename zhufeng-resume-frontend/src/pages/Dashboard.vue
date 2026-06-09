<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>我的简历</h2>
      <el-button type="primary" @click="$router.push('/templates')">
        新建简历
      </el-button>
    </div>

    <div v-if="resumes.length > 0" class="resume-list">
      <el-card v-for="resume in resumes" :key="resume.id" shadow="hover" class="resume-card">
        <template #header>
          <div class="card-header">
            <span>{{ resume.title }}</span>
            <el-tag :type="resume.status === 1 ? 'info' : 'success'" size="small">
              {{ resume.status === 1 ? '草稿' : '已完成' }}
            </el-tag>
          </div>
        </template>
        <p>模板：{{ resume.templateName || '默认模板' }}</p>
        <p>更新：{{ formatDate(resume.updatedAt) }}</p>
        <div class="card-actions">
          <el-button type="primary" size="small" @click="editResume(resume.id)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(resume.id)">删除</el-button>
        </div>
      </el-card>
    </div>

    <el-empty v-else description="暂无简历">
      <el-button type="primary" @click="$router.push('/templates')">创建第一份简历</el-button>
    </el-empty>
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
  if (!userStore.userInfo) await userStore.getUserInfo()
  await loadResumes()
})

const loadResumes = async () => {
  const res = await getResumes()
  resumes.value = res.data
}

const editResume = (id) => router.push(`/editor/${id}`)

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
    await deleteResume(id)
    ElMessage.success('删除成功')
    await loadResumes()
  } catch (e) {}
}

const formatDate = (d) => new Date(d).toLocaleDateString('zh-CN')
</script>

<style scoped>
.dashboard { padding: 30px; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-header h2 { margin: 0; font-size: 24px; }

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

.resume-card p {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
}

.card-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}
</style>