<template>
  <div class="form-container">
    <el-form label-width="80px" size="small">
      <el-form-item label="自我评价">
        <el-input
          v-model="value"
          type="textarea"
          :rows="6"
          placeholder="请输入自我评价"
          @input="update"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const value = ref(props.modelValue || '')

const update = () => {
  emit('update:modelValue', value.value)
}

watch(() => props.modelValue, (newVal) => {
  value.value = newVal || ''
})
</script>