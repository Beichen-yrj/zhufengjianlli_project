<template>
  <div class="form-container">
    <div v-for="(item, index) in list" :key="index" class="form-item">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>工作经历 {{ index + 1 }}</span>
            <el-button type="danger" size="small" @click="remove(index)">
              删除
            </el-button>
          </div>
        </template>
        <el-form label-width="70px" size="small">
          <el-form-item label="公司">
            <el-input v-model="item.company" placeholder="公司名称" />
          </el-form-item>
          <el-form-item label="职位">
            <el-input v-model="item.position" placeholder="职位" />
          </el-form-item>
          <el-form-item label="时间">
            <el-date-picker
              v-model="item.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM"
            />
          </el-form-item>
          <el-form-item label="工作描述">
            <el-input
              v-model="item.description"
              type="textarea"
              :rows="3"
              placeholder="描述工作内容"
            />
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    <el-button type="primary" plain style="width: 100%; margin-top: 10px" @click="add">
      添加工作经历
    </el-button>
  </div>
</template>

<script setup>
import { watch } from 'vue'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const list = props.modelValue || []

const add = () => {
  list.push({ company: '', position: '', dateRange: [], description: '' })
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