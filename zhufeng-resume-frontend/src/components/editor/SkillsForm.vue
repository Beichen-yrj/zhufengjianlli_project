<template>
  <div class="form-container">
    <el-form label-width="80px" size="small">
      <el-form-item label="技能标签">
        <div class="tags">
          <el-tag
            v-for="(skill, index) in list"
            :key="index"
            closable
            @close="remove(index)"
            style="margin: 0 5px 5px 0"
          >
            {{ skill }}
          </el-tag>
        </div>
        <el-input
          v-model="newSkill"
          placeholder="输入技能后按回车添加"
          style="margin-top: 10px"
          @keyup.enter="addSkill"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const list = props.modelValue || []
const newSkill = ref('')

const addSkill = () => {
  if (newSkill.value.trim() && !list.includes(newSkill.value.trim())) {
    list.push(newSkill.value.trim())
    emit('update:modelValue', list)
  }
  newSkill.value = ''
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
.tags {
  min-height: 30px;
}
</style>