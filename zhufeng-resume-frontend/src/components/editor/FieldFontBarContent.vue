<template>
  <div class="ff-compact">
    <div class="ff-row">
      <label>字号</label>
      <input type="range" min="8" max="24" step="0.5" v-model.number="styleObj.fontSize" @input="$emit('change', fk)" />
      <span class="range-val">{{ styleObj.fontSize }}pt</span>
      <label>样式</label>
      <button class="ff-style-btn" :class="{ active: styleObj.fontStyle === 'italic' }" @click="styleObj.fontStyle = (styleObj.fontStyle === 'italic' ? '' : 'italic'); $emit('change', fk)" title="斜体"><i>I</i></button>
      <button class="ff-style-btn" :class="{ active: styleObj.textDecoration === 'underline' }" @click="styleObj.textDecoration = (styleObj.textDecoration === 'underline' ? '' : 'underline'); $emit('change', fk)" title="下划线"><u>U</u></button>
      <label>字重</label>
      <select v-model="styleObj.fontWeight" @change="$emit('change', fk)" class="ff-select-sm">
        <option value="normal">常规</option><option value="bold">加粗</option><option value="lighter">细体</option><option value="900">特粗</option>
      </select>
      <label>颜色</label>
      <input type="color" v-model="styleObj.color" @input="$emit('change', fk)" class="ff-color" />
      <label>字体</label>
      <select v-model="styleObj.fontFamily" @change="$emit('change', fk)" class="ff-select-sm">
        <option value="">默认</option>
        <option value="Microsoft YaHei, sans-serif">微软雅黑</option>
        <option value="SimSun, serif">宋体</option>
        <option value="SimHei, sans-serif">黑体</option>
        <option value="KaiTi, serif">楷体</option>
        <option value="FangSong, serif">仿宋</option>
        <option value="Arial, sans-serif">Arial</option>
        <option value="Helvetica Neue, sans-serif">Helvetica</option>
        <option value="Georgia, serif">Georgia</option>
        <option value="Times New Roman, serif">Times New Roman</option>
        <option value="PingFang SC, sans-serif">苹方</option>
      </select>
    </div>
    <div class="ff-row">
      <label>行高</label>
      <input type="range" min="1" max="3" step="0.1" v-model.number="styleObj.lineHeight" @input="$emit('change', fk)" />
      <span class="range-val">{{ styleObj.lineHeight }}</span>
    </div>
    <div class="ff-row">
      <label>字间距</label>
      <input type="range" min="-2" max="6" step="0.5" v-model.number="styleObj.letterSpacing" @input="$emit('change', fk)" />
      <span class="range-val">{{ styleObj.letterSpacing }}px</span>
    </div>
    <div class="ff-row">
      <label>上边距</label>
      <input type="number" min="0" max="20" v-model.number="styleObj.marginTop" @input="$emit('change', fk)" class="ff-num-input" /><span class="style-unit">px</span>
      <label>下边距</label>
      <input type="number" min="0" max="20" v-model.number="styleObj.marginBottom" @input="$emit('change', fk)" class="ff-num-input" /><span class="style-unit">px</span>
      <label>左缩进</label>
      <input type="number" min="0" max="30" v-model.number="styleObj.paddingLeft" @input="$emit('change', fk)" class="ff-num-input" /><span class="style-unit">px</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  fk: { type: String, required: true },
  getStyle: { type: Function, required: true },
})

defineEmits(['change'])

const styleObj = computed(() => props.getStyle(props.fk))
</script>

<style scoped>
.ff-compact{background:#f0f2ff;border:1px solid #d0daf9;border-radius:6px;padding:5px 8px;margin-top:4px;}
.ff-row{display:flex;align-items:center;gap:4px;font-size:10px;color:#666;}
.ff-row + .ff-row{margin-top:3px;}
.ff-row label{white-space:nowrap;min-width:24px;font-weight:500;color:#888;}
.ff-row input[type="range"]{flex:1;height:2px;accent-color:#667eea;max-width:none;}
.ff-select-sm{border:1px solid #ddd;border-radius:3px;padding:1px 3px;font-size:10px;background:#fff;color:#444;outline:none;width:auto;min-width:36px;}
.ff-select-sm:focus{border-color:#667eea;}
.ff-color{width:18px;height:16px;border:1px solid #ddd;border-radius:3px;cursor:pointer;padding:0;}
.ff-style-btn{width:20px;height:18px;border:1px solid #ddd;border-radius:3px;background:#fff;cursor:pointer;font-size:11px;font-family:serif;color:#666;display:flex;align-items:center;justify-content:center;transition:all .12s;padding:0;}
.ff-style-btn:hover{border-color:#667eea;color:#667eea;}
.ff-style-btn.active{background:#667eea;color:#fff;border-color:#667eea;}
.ff-style-btn i{font-style:italic;}
.range-val{font-size:10px;color:#999;min-width:20px;text-align:right;font-variant-numeric:tabular-nums;}
.style-unit{font-size:9px;color:#999;}
.ff-num-input{width:34px;border:1px solid #ddd;border-radius:3px;padding:1px 2px;font-size:10px;text-align:center;outline:none;height:18px;line-height:1;}
.ff-num-input:focus{border-color:#667eea;}
</style>
