<template>
  <div class="form-container">
    <el-form label-width="80px" :model="form" size="small">
      <el-form-item label="姓名">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="求职意向">
        <el-input v-model="form.jobIntention" placeholder="请输入求职意向" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const form = reactive({
  name: '',
  phone: '',
  email: '',
  jobIntention: ''
})

// 同步初始值
if (props.modelValue) {
  Object.assign(form, props.modelValue)
}

// 监听变化，同步到父组件
watch(form, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })
</script>

<style scoped>
.form-container {
  padding: 15px;
}
</style>