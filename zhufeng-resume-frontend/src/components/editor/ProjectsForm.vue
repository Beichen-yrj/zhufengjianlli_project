<template>
  <div class="form-container">
    <div v-for="(item, index) in list" :key="index" class="form-item">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>项目 {{ index + 1 }}</span>
            <el-button type="danger" size="small" @click="remove(index)">
              删除
            </el-button>
          </div>
        </template>
        <el-form label-width="70px" size="small">
          <el-form-item label="项目名称">
            <el-input v-model="item.name" placeholder="项目名称" />
          </el-form-item>
          <el-form-item label="担任角色">
            <el-input v-model="item.role" placeholder="担任角色" />
          </el-form-item>
          <el-form-item label="项目描述">
            <el-input
              v-model="item.description"
              type="textarea"
              :rows="3"
              placeholder="描述项目内容"
            />
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    <el-button type="primary" plain style="width: 100%; margin-top: 10px" @click="add">
      添加项目
    </el-button>
  </div>
</template>

<script setup>
import { watch } from 'vue'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const list = props.modelValue || []

const add = () => {
  list.push({ name: '', role: '', description: '' })
  emit('update:modelValue', list)
}

const remove = (index) => {
  list.splice(index, 1)
  emit('update:modelValue', list)
}

watch(list, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })
</script>

<style scoped>
.form-item {
  margin-bottom: 15px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>