<template>
  <div class="item-list-panel">
    <draggable
      :model-value="items"
      @update:model-value="store.updateProjectsBatch($event)"
      :animation="200"
      handle=".item-drag"
      item-key="id"
    >
      <template #item="{ element: item }">
        <div class="il-item">
          <div class="il-header">
            <span class="item-drag">⠿</span>
            <input class="il-title-input" v-model="item.name" placeholder="项目名称" />
            <el-button text size="small" class="il-del" @click="store.deleteProject(item.id)">🗑</el-button>
          </div>
          <div class="il-body">
            <Field v-model="item.role" label="角色" placeholder="前端开发 / 负责人" />
            <Field v-model="item.date" label="时间" placeholder="2022-01 ~ 2022-12" />
            <Field v-model="item.link" label="项目链接" placeholder="https://..." />
            <Field v-model="item.description" label="项目描述" kind="editor" placeholder="技术栈、功能亮点、成果" />
            <label class="visible-toggle">
              <el-switch v-model="item.visible" size="small" /> 在简历中显示
            </label>
          </div>
        </div>
      </template>
    </draggable>

    <el-button text type="primary" class="il-add" @click="addItem">
      + 添加项目经历
    </el-button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import draggable from 'vuedraggable'
import Field from './Field.vue'
import { useResumeStore } from '@/stores/resume'

const store = useResumeStore()
const items = computed(() => store.activeResume?.projects || [])

function addItem() {
  store.updateProject({
    id: Date.now().toString(36),
    name: '', role: '', date: '', link: '', description: '', visible: true
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
