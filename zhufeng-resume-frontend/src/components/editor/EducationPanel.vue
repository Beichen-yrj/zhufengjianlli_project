<template>
  <div class="item-list-panel">
    <draggable
      :model-value="items"
      @update:model-value="store.updateEducationBatch($event)"
      :animation="200"
      handle=".item-drag"
      item-key="id"
    >
      <template #item="{ element: item, index }">
        <div class="il-item">
          <div class="il-header">
            <span class="item-drag">⠿</span>
            <input class="il-title-input" v-model="item.school" placeholder="学校名称" />
            <el-button text size="small" class="il-del" @click="store.deleteEducation(item.id)">🗑</el-button>
          </div>
          <div class="il-body">
            <Field v-model="item.major" label="专业" />
            <Field v-model="item.degree" label="学历" placeholder="本科/硕士/博士" />
            <Field v-model="item.startDate" label="开始" kind="date" />
            <Field v-model="item.endDate" label="结束" kind="date" />
            <Field v-model="item.gpa" label="GPA" placeholder="3.8/4.0" />
            <Field v-model="item.description" label="描述" kind="textarea" :rows="3" />
            <label class="visible-toggle">
              <el-switch v-model="item.visible" size="small" /> 在简历中显示
            </label>
          </div>
        </div>
      </template>
    </draggable>

    <el-button text type="primary" class="il-add" @click="addItem">
      + 添加教育经历
    </el-button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import draggable from 'vuedraggable'
import Field from './Field.vue'
import { useResumeStore } from '@/stores/resume'

const store = useResumeStore()
const items = computed(() => store.activeResume?.education || [])

function addItem() {
  store.updateEducation({
    id: Date.now().toString(36),
    school: '', major: '', degree: '', startDate: '', endDate: '',
    gpa: '', description: '', visible: true
  })
}
</script>

<style scoped>
.item-list-panel { padding: 6px; }
.il-item { border: 1px solid #eee; border-radius: 8px; margin-bottom: 12px; overflow: hidden; }
.il-header { display: flex; align-items: center; gap: 8px; padding: 10px 12px; background: #fafafa; border-bottom: 1px solid #f0f0f0; }
.item-drag { cursor: grab; color: #bbb; font-size: 14px; user-select: none; }
.il-title-input { flex: 1; border: none; outline: none; font-size: 14px; font-weight: 500; background: transparent; }
.il-del { color: #ccc; }
.il-del:hover { color: #f56c6c; }
.il-body { padding: 12px; display: flex; flex-direction: column; gap: 4px; }
.il-add { margin-top: 8px; }
.visible-toggle { display: flex; align-items: center; gap: 6px; font-size: 12px; color: #999; margin-top: 4px; }
</style>
