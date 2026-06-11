<template>
  <div class="editor-form-panel" :style="{ width: panelWidth + '%' }">
    <div class="resize-handle" @mousedown="onResizeStart"></div>

    <!-- ========== 上半：导航 + 主题色（可收起）========== -->
    <div class="module-nav">
      <div class="collapsible-header" @click="topCollapsed = !topCollapsed">
        <span class="collapse-title">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/></svg>
          导航与主题
        </span>
        <span class="collapse-arrow" :class="{ rotated: topCollapsed }">&#9660;</span>
      </div>
      <div v-show="!topCollapsed" class="collapse-body">

      <!-- 动态模块列表：只显示已启用(画布上有)的模块 -->
      <div
        v-for="(m, mi) in visibleNavModules"
        :key="m.id"
        class="nav-item"
        :class="{ active: currentSection === m.id }"
        :draggable="true"
        @click="switchSection(m.id)"
        @dragstart="onDragStart($event, m.id, mi)"
        @dragover.prevent="onDragOver($event, mi)"
        @drop="onDrop($event, mi)"
      >
        <!-- 拖拽手柄 -->
        <span class="drag-handle" title="拖拽排序">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><circle cx="9" cy="6" r="1.5"/><circle cx="15" cy="6" r="1.5"/><circle cx="9" cy="12" r="1.5"/><circle cx="15" cy="12" r="1.5"/><circle cx="9" cy="18" r="1.5"/><circle cx="15" cy="18" r="1.5"/></svg>
        </span>
        <!-- 图标 -->
        <span class="nav-icon" v-html="m.icon"></span>
        <!-- 名称 -->
        <span class="nav-label">{{ m.label }}</span>
        <!-- 操作按钮：基本信息不可删，其他可隐藏+删除 -->
        <template v-if="m.id !== 'basic'">
          <span class="nav-action eye-btn" @click.stop="toggleVisibility(m.id)" title="显隐">
            <svg v-if="!hiddenModules.has(m.id)" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
            <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19m-6.72-1.07a3 3 0 11-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
          </span>
          <span class="nav-action del-btn" @click.stop="removeModuleFromNav(m.id)" title="从布局移除">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
          </span>
        </template>
      </div>

      <!-- 添加模块按钮（带下拉）—— 显示所有未启用的标准模块 -->
      <div class="add-module-wrapper">
        <button class="nav-add-module" @click.stop="showAddMenu = !showAddMenu">+ 添加布局</button>
        <transition name="fade-down">
          <div v-if="showAddMenu" class="add-module-menu">
            <!-- 未启用的标准模块 -->
            <div
              v-for="opt in inactiveStandardModules"
              :key="opt.id"
              class="add-opt-item"
              @click="enableModule(opt)"
            >
              <span class="add-opt-icon" v-html="opt.icon"></span>
              {{ opt.label }}
            </div>
            <div v-if="inactiveStandardModules.length" class="add-opt-divider"></div>
            <div class="add-opt-item add-custom" @click="startAddCustom">
              <span class="add-opt-icon">+</span> 自定义模块
            </div>
            <div v-if="!inactiveStandardModules.length" class="add-opt-empty">所有布局已添加</div>
          </div>
        </transition>
      </div>

      <!-- 主题色 -->
      <div class="nav-section-title">主题色</div>
      <div class="color-row">
        <button v-for="c in colorOptions" :key="c" class="color-dot"
          :class="{ active: currentColor === c }" :style="{ background: c }"
          @click="onSetColor(c)"></button>
        <span class="custom-color-label">自定义</span>
      </div>

      </div>
    </div>

    <!-- ========== 下半：编辑区（可收起）========== -->
    <div class="panel-scroll" ref="scrollRef" @click.self="activeFieldKey = ''">
      <div class="collapsible-header editor-collapse-header" @click="bottomCollapsed = !bottomCollapsed">
        <span class="collapse-title">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          编辑内容
        </span>
        <span class="collapse-arrow" :class="{ rotated: bottomCollapsed }">&#9660;</span>
      </div>
      <div v-show="!bottomCollapsed" class="collapse-body">

      <!-- ====== 基本信息 ====== -->
      <section v-show="currentSection === 'basic'" class="form-section">
        <div class="form-section-header"><h3>基本信息</h3><button class="header-edit-btn"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg></button></div>
        <div class="field-group-title">资料</div>
        <div class="avatar-row">
          <div class="avatar-preview" :style="localData.basic.avatar ? { backgroundImage: 'url(' + localData.basic.avatar + ')' } : {}">
            <img v-if="localData.basic.avatar" :src="localData.basic.avatar" alt="" /><span v-else class="avatar-placeholder">暂无头像</span>
          </div>
          <div class="avatar-actions">
            <label class="avatar-upload-btn">上传头像<input type="file" accept="image/*" hidden @change="onAvatarUpload" /></label>
            <button class="icon-action-btn sm" @click="toggleFieldHidden('basic','avatar')" title="隐藏"><svg width="13"height="13"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12"cy="12"r="3"/></svg></button>
            <button class="icon-action-btn sm danger" @click="localData.basic.avatar='';emitUpdate()" title="删除"><svg width="13"height="13"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2 2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg></button>
          </div>
        </div>
        <!-- 头像样式设置 -->
        <div v-if="localData.basic.avatar" class="avatar-style-settings">
          <div class="style-setting-row">
            <label>宽度</label>
            <input type="number" min="40" max="150" v-model.number="localData.basic.avatarWidth" @input="emitUpdate" class="style-input-sm" />
            <span class="style-unit">px</span>
            <label style="margin-left:12px">高度</label>
            <input type="number" min="50" max="180" v-model.number="localData.basic.avatarHeight" @input="emitUpdate" class="style-input-sm" />
            <span class="style-unit">px</span>
          </div>
          <div class="style-setting-row">
            <label>圆角</label>
            <input type="number" min="0" max="100" v-model.number="localData.basic.avatarRadius" @input="emitUpdate" class="style-input-sm" />
            <span class="style-unit">px (0=方, 38=圆)</span>
          </div>
          <div class="style-setting-row">
            <label>位置</label>
            <select v-model="localData.basic.avatarPosition" @change="emitUpdate" class="style-select-sm">
              <option value="right-top">右上</option>
              <option value="right-center">右侧居中</option>
              <option value="right-bottom">右下</option>
              <option value="left-top">左上</option>
              <option value="left-center">左侧居中</option>
              <option value="left-bottom">左下</option>
              <option value="center-top">上方居中</option>
              <option value="center-bottom">下方居中</option>
            </select>
          </div>
        </div>
        <div class="field-group-title">
          基础字段
          <button class="add-field-btn" @click="showFieldPicker = !showFieldPicker" title="添加字段">+ 添加</button>
        </div>
        <!-- 字段选择器弹窗（含自定义字段入口） -->
        <transition name="fade-down">
          <div v-if="showFieldPicker" class="field-picker">
            <div v-for="af in availableFields" :key="af.key" class="picker-item" @click="restoreBasicField(af)">
              <span class="picker-icon" v-html="af.icon || '+'"></span> {{ af.label }}
            </div>
            <div v-if="availableFields.length" class="add-opt-divider"></div>
            <div class="picker-item add-custom" @click="startAddCustomField">
              <span class="picker-icon">+</span> 自定义字段
            </div>
            <div v-if="!availableFields.length && customFields.length === 0 && removedCustomFields.length === 0" class="picker-empty">所有字段已添加</div>
            <template v-if="removedCustomFields.length">
              <div v-if="availableFields.length || customFields.length" class="add-opt-divider"></div>
              <div v-for="cf in removedCustomFields" :key="'rm-'+cf.key" class="picker-item" @click="restoreCustomField(cf)">
                <span class="picker-icon">&#8617;</span> {{ cf.label }} (自定义)
              </div>
            </template>
          </div>
        </transition>
        <!-- 基础字段两列网格 + 聚焦字体调整 -->
        <div class="field-grid">
          <template v-for="(bf, bfi) in visibleBasicFields" :key="bf.key">
            <div class="field-cell"
              :class="{ focused: activeFieldKey === bf.key }"
              draggable="true"
              @dragstart="onFieldDragStart($event, bf.key)"
              @dragover.prevent
              @drop="onFieldDrop($event, bf.key)">
              <span class="field-drag-handle">&#9776;</span>
              <span class="field-label">{{ bf.label }}</span>
              <component :is="bf.component||'el-input'"
                v-model="localData.basic[bf.key]"
                :placeholder="'请输入'+bf.label"
                size="small"
                :type="bf.type||'text'"
                v-bind="bf.props||{}"
                @focus="activeFieldKey = bf.key"
                @input="emitUpdate"
                @change="emitUpdate"/>
              <button class="icon-action-btn xs" @click="deleteBasicField(bf.key)" title="移除">
                <svg width="12"height="12"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2 2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
              </button>
            </div>
            <transition name="fade-down">
              <div v-if="activeFieldKey === bf.key" class="field-font-bar">
                <!-- 第一行：字号 + 字体样式 -->
                <div class="ff-row">
                  <label>字号</label>
                  <input type="range" min="8" max="24" step="0.5" v-model.number="bf.fontSize" @input="onFieldFontChange(bf)" />
                  <span class="range-val">{{ bf.fontSize || 10 }}pt</span>
                  <label style="margin-left:10px">样式</label>
                  <button class="ff-style-btn" :class="{ active: bf.fontStyle === 'italic' }" @click="bf.fontStyle = (bf.fontStyle === 'italic' ? '' : 'italic'); onFieldFontChange(bf)" title="斜体"><i>I</i></button>
                  <button class="ff-style-btn" :class="{ active: bf.textDecoration === 'underline' }" @click="bf.textDecoration = (bf.textDecoration === 'underline' ? '' : 'underline'); onFieldFontChange(bf)" title="下划线"><u>U</u></button>
                </div>
                <!-- 第二行：字重 + 颜色 -->
                <div class="ff-row">
                  <label>字重</label>
                  <select v-model="bf.fontWeight" @change="onFieldFontChange(bf)" class="ff-select-sm">
                    <option value="normal">常规</option><option value="bold">加粗</option>
                    <option value="lighter">细体</option><option value="900">特粗</option>
                  </select>
                  <label style="margin-left:8px">颜色</label>
                  <input type="color" v-model="bf.color" @input="onFieldFontChange(bf)" class="ff-color" />
                  <label>字体</label>
                  <select v-model="bf.fontFamily" @change="onFieldFontChange(bf)" class="ff-select-sm">
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
                <!-- 第三行：行高 + 行距 -->
                <div class="ff-row">
                  <label>行高</label>
                  <input type="range" min="1" max="3" step="0.1" v-model.number="bf.lineHeight" @input="onFieldFontChange(bf)" />
                  <span class="range-val">{{ bf.lineHeight || 1.6 }}</span>
                  <label style="margin-left:10px">字间距</label>
                  <input type="range" min="-2" max="6" step="0.5" v-model.number="bf.letterSpacing" @input="onFieldFontChange(bf)" />
                  <span class="range-val">{{ (bf.letterSpacing || 0) }}px</span>
                </div>
                <!-- 第四行：内边距（与边框距离） -->
                <div class="ff-row">
                  <label>上边距</label>
                  <input type="number" min="0" max="20" v-model.number="bf.marginTop" @input="onFieldFontChange(bf)" class="ff-num-input" /><span class="style-unit">px</span>
                  <label style="margin-left:8px">下边距</label>
                  <input type="number" min="0" max="20" v-model.number="bf.marginBottom" @input="onFieldFontChange(bf)" class="ff-num-input" /><span class="style-unit">px</span>
                  <label style="margin-left:8px">左缩进</label>
                  <input type="number" min="0" max="30" v-model.number="bf.paddingLeft" @input="onFieldFontChange(bf)" class="ff-num-input" /><span class="style-unit">px</span>
                </div>
              </div>
            </transition>
          </template>
        </div>
      </section>

      <!-- ====== 求职意向（可编辑）====== -->
      <section v-show="currentSection === 'intention'" class="form-section">
        <div class="form-section-header"><h3>求职意向</h3></div>
        <div class="form-item" style="margin-bottom:12px;">
          <div class="field-cell" :class="{ focused: activeFieldKey === 'intention-targetJob' }">
            <span class="field-label"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 7h-4V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v3H4a2 2 0 00-2 2v11a2 2 0 002 2h16a2 2 0 002-2V9a2 2 0 00-2-2z"/><path d="M10 4h4"/></svg>期望岗位</span>
            <el-input v-model="localData.basic.targetJob" placeholder="如：前端开发工程师" size="small" clearable @input="emitUpdate" @focus="activeFieldKey = 'intention-targetJob'"/>
            <transition name="font-bar-fade"><div v-if="activeFieldKey === 'intention-targetJob'" class="field-font-bar"><FieldFontBarContent :fk="'intention-targetJob'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div></transition>
          </div>
        </div>
        <div class="form-item" style="margin-bottom:12px;">
          <div class="field-cell" :class="{ focused: activeFieldKey === 'intention-city' }">
            <span class="field-label"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/><circle cx="12" cy="10" r="3"/></svg>工作地点</span>
            <el-input v-model="localData.basic.city" placeholder="如：北京/上海" size="small" clearable @input="emitUpdate" @focus="activeFieldKey = 'intention-city'"/>
            <transition name="font-bar-fade"><div v-if="activeFieldKey === 'intention-city'" class="field-font-bar"><FieldFontBarContent :fk="'intention-city'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div></transition>
          </div>
        </div>
        <div class="form-item" style="margin-bottom:12px;">
          <div class="field-cell" :class="{ focused: activeFieldKey === 'intention-salary' }">
            <span class="field-label"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>期望薪资</span>
            <el-input v-model="localData.intention.expectedSalary" placeholder="如：15k-25k" size="small" clearable @input="emitUpdate" @focus="activeFieldKey = 'intention-salary'"/>
            <transition name="font-bar-fade"><div v-if="activeFieldKey === 'intention-salary'" class="field-font-bar"><FieldFontBarContent :fk="'intention-salary'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div></transition>
          </div>
        </div>
        <div class="form-item" style="margin-bottom:12px;">
          <div class="field-cell" :class="{ focused: activeFieldKey === 'intention-jobType' }">
            <span class="field-label"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v16"/></svg>工作性质</span>
            <el-select v-model="localData.intention.jobType" size="small" style="width:100%;" @change="emitUpdate" @focus="activeFieldKey = 'intention-jobType'">
              <el-option label="全职" value="全职"/>
              <el-option label="兼职" value="兼职"/>
              <el-option label="实习" value="实习"/>
              <el-option label="远程" value="远程"/>
            </el-select>
            <transition name="font-bar-fade"><div v-if="activeFieldKey === 'intention-jobType'" class="field-font-bar"><FieldFontBarContent :fk="'intention-jobType'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div></transition>
          </div>
        </div>
        <div class="form-item" style="margin-bottom:0;">
          <div class="field-cell" :class="{ focused: activeFieldKey === 'intention-date' }">
            <span class="field-label"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>到岗时间</span>
            <el-input v-model="localData.intention.availableDate" placeholder="如：随时到岗 / 一个月内" size="small" clearable @input="emitUpdate" @focus="activeFieldKey = 'intention-date'"/>
            <transition name="font-bar-fade"><div v-if="activeFieldKey === 'intention-date'" class="field-font-bar"><FieldFontBarContent :fk="'intention-date'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div></transition>
          </div>
        </div>
      </section>

      <!-- ====== 专业技能 ====== -->
      <section v-show="currentSection === 'skills'" class="form-section">
        <div class="form-section-header"><h3>专业技能</h3><button class="header-edit-btn"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg></button></div>
        <div class="rich-toolbar"><div class="toolbar-left"><button class="tb-btn" v-for="t in richToolsLeft" :key="t.label" :title="t.label"><span v-html="t.icon"></span></button></div></div>
        <div class="rich-toolbar toolbar-second">
          <div class="toolbar-left"><button class="tb-btn" title="撤销">&#8617;</button><button class="tb-btn" title="重做">&#8618;</button></div>
          <button class="ai-polish-btn" @click="$emit('aiPolish',{text:localData.skills,field:'skills'})"><span class="ai-icon">&#10024;</span> AI润色</button>
        </div>
        <div class="form-item" style="margin-bottom:0;">
          <div class="field-cell" :class="{ focused: activeFieldKey === 'skills' }">
            <el-input v-model="localData.skills" type="textarea" :rows="10" placeholder="&#10;• 前端框架：熟悉 React、Vue.js，熟悉 Next.js、Nuxt.js 等 SSR 框架&#10;• 开发语言：TypeScript、JavaScript(ES6+)、HTML5、CSS3&#10;• UI/组件式：熟悉 TailwindCSS、Sass/Less、CSS Module、Styled-components&#10;• 状态管理：Redux、Vuex、Zustand、Jotai、React Query&#10;• 工程化工具：Webpack、Vite、Rollup、Babel、ESLint&#10;• 测试工具：Jest、React Testing Library、Cypress" resize="vertical" size="small" @input="emitUpdate" @focus="activeFieldKey = 'skills'"/>
          </div>
          <transition name="fade-down">
            <div v-if="activeFieldKey === 'skills'" class="field-font-bar">
              <div class="ff-row">
                <label>字号</label>
                <input type="range" min="8" max="24" step="0.5" v-model.number="getNonBasicFieldStyle('skills').fontSize" @input="onNonBasicFieldFontChange('skills')" />
                <span class="range-val">{{ getNonBasicFieldStyle('skills').fontSize }}pt</span>
                <label style="margin-left:10px">样式</label>
                <button class="ff-style-btn" :class="{ active: getNonBasicFieldStyle('skills').fontStyle === 'italic' }" @click="getNonBasicFieldStyle('skills').fontStyle = (getNonBasicFieldStyle('skills').fontStyle === 'italic' ? '' : 'italic'); onNonBasicFieldFontChange('skills')" title="斜体"><i>I</i></button>
                <button class="ff-style-btn" :class="{ active: getNonBasicFieldStyle('skills').textDecoration === 'underline' }" @click="getNonBasicFieldStyle('skills').textDecoration = (getNonBasicFieldStyle('skills').textDecoration === 'underline' ? '' : 'underline'); onNonBasicFieldFontChange('skills')" title="下划线"><u>U</u></button>
              </div>
              <div class="ff-row">
                <label>字重</label>
                <select v-model="getNonBasicFieldStyle('skills').fontWeight" @change="onNonBasicFieldFontChange('skills')" class="ff-select-sm">
                  <option value="normal">常规</option><option value="bold">加粗</option>
                  <option value="lighter">细体</option><option value="900">特粗</option>
                </select>
                <label style="margin-left:8px">颜色</label>
                <input type="color" v-model="getNonBasicFieldStyle('skills').color" @input="onNonBasicFieldFontChange('skills')" class="ff-color" />
              </div>
              <div class="ff-row">
                <label>行高</label>
                <input type="range" min="1" max="3" step="0.1" v-model.number="getNonBasicFieldStyle('skills').lineHeight" @input="onNonBasicFieldFontChange('skills')" />
                <span class="range-val">{{ getNonBasicFieldStyle('skills').lineHeight }}</span>
                <label style="margin-left:10px">字间距</label>
                <input type="range" min="-2" max="6" step="0.5" v-model.number="getNonBasicFieldStyle('skills').letterSpacing" @input="onNonBasicFieldFontChange('skills')" />
                <span class="range-val">{{ getNonBasicFieldStyle('skills').letterSpacing }}px</span>
              </div>
              <div class="ff-row">
                <label>上边距</label>
                <input type="number" min="0" max="20" v-model.number="getNonBasicFieldStyle('skills').marginTop" @input="onNonBasicFieldFontChange('skills')" class="ff-num-input" /><span class="style-unit">px</span>
                <label style="margin-left:8px">下边距</label>
                <input type="number" min="0" max="20" v-model.number="getNonBasicFieldStyle('skills').marginBottom" @input="onNonBasicFieldFontChange('skills')" class="ff-num-input" /><span class="style-unit">px</span>
                <label style="margin-left:8px">左缩进</label>
                <input type="number" min="0" max="30" v-model.number="getNonBasicFieldStyle('skills').paddingLeft" @input="onNonBasicFieldFontChange('skills')" class="ff-num-input" /><span class="style-unit">px</span>
              </div>
            </div>
          </transition>
        </div>
      </section>

      <!-- ====== 工作经历 ====== -->
      <section v-show="currentSection === 'experience'" class="form-section">
        <div class="form-section-header">
          <h3>工作经历</h3>
          <div class="header-right-btns">
            <button class="header-edit-btn"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg></button>
            <button class="header-del-btn" @click="removeModuleFromNav('experience')"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2 2V6m3 0V4a2 2 0 012-2h4a2 2 0 01 2 2v2"/></svg></button>
          </div>
        </div>
        <div class="current-item-name" v-if="localData.experience.length">{{ localData.experience[0].company || '未命名公司' }}</div>
        <button class="btn-add-card-dark" @click="addItem('experience')"><span>&#10024;</span> 添加工作经历</button>
        <div v-for="(item,idx) in localData.experience" :key="item._id" class="list-card">
          <div class="list-card-head"><span class="card-index">#{{idx+1}}</span><button class="card-del" @click="removeItem('experience',idx)">删除此条</button></div>
          <div class="form-item" style="margin-bottom:0;">
            <label>公司名称</label>
            <div class="field-cell" :class="{ focused: activeFieldKey === 'exp-'+idx+'-company' }">
              <el-input v-model="item.company" placeholder="如：字节跳动" size="small" @input="emitUpdate" @focus="activeFieldKey = 'exp-'+idx+'-company'"/>
            </div>
            <transition name="fade-down">
              <div v-if="activeFieldKey === 'exp-'+idx+'-company'" class="field-font-bar"><field-font-bar-content :fk="'exp-'+idx+'-company'" :active-field-key="activeFieldKey" :get-style="getNonBasicFieldStyle" :on-change="onNonBasicFieldFontChange"/></div>
            </transition>
          </div>
          <div class="form-item" style="margin-bottom:0;">
            <label>职位</label>
            <div class="field-cell" :class="{ focused: activeFieldKey === 'exp-'+idx+'-position' }">
              <el-input v-model="item.position" placeholder="如：前端工程师" size="small" @input="emitUpdate" @focus="activeFieldKey = 'exp-'+idx+'-position'"/>
            </div>
            <transition name="fade-down">
              <div v-if="activeFieldKey === 'exp-'+idx+'-position'" class="field-font-bar"><field-font-bar-content :fk="'exp-'+idx+'-position'" :active-field-key="activeFieldKey" :get-style="getNonBasicFieldStyle" :on-change="onNonBasicFieldFontChange"/></div>
            </transition>
          </div>
          <div class="form-row">
            <div class="form-item form-half" style="margin-bottom:0;">
              <label>开始时间</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'exp-'+idx+'-startDate' }">
                <el-date-picker v-model="item.startDate" type="month" placeholder="开始" size="small" style="width:100%" value-format="YYYY-MM" @change="emitUpdate" @focus="activeFieldKey = 'exp-'+idx+'-startDate'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'exp-'+idx+'-startDate'" class="field-font-bar"><field-font-bar-content :fk="'exp-'+idx+'-startDate'" :active-field-key="activeFieldKey" :get-style="getNonBasicFieldStyle" :on-change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="form-item form-half" style="margin-bottom:0;">
              <label>结束时间</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'exp-'+idx+'-endDate' }">
                <el-date-picker v-model="item.endDate" type="month" placeholder="结束" size="small" style="width:100%" value-format="YYYY-MM" @change="emitUpdate" @focus="activeFieldKey = 'exp-'+idx+'-endDate'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'exp-'+idx+'-endDate'" class="field-font-bar"><field-font-bar-content :fk="'exp-'+idx+'-endDate'" :active-field-key="activeFieldKey" :get-style="getNonBasicFieldStyle" :on-change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
          </div>
          <div class="form-item" style="margin-bottom:0;">
            <label>工作内容</label>
            <div class="field-cell" :class="{ focused: activeFieldKey === 'exp-'+idx+'-content' }">
              <el-input v-model="item.content" type="textarea" :rows="3" placeholder="你负责做了什么事情，运用了哪些工具和方法，取得了什么成果" size="small" resize="vertical" @input="emitUpdate" @focus="activeFieldKey = 'exp-'+idx+'-content'"/>
            </div>
            <transition name="fade-down">
              <div v-if="activeFieldKey === 'exp-'+idx+'-content'" class="field-font-bar"><field-font-bar-content :fk="'exp-'+idx+'-content'" :active-field-key="activeFieldKey" :get-style="getNonBasicFieldStyle" :on-change="onNonBasicFieldFontChange"/></div>
            </transition>
          </div>
          <div class="form-item" style="margin-bottom:0;">
            <label>公司介绍</label>
            <div class="field-cell" :class="{ focused: activeFieldKey === 'exp-'+idx+'-description' }">
              <el-input v-model="item.description" type="textarea" :rows="2" placeholder="如有必要，这里可对公司做简单的介绍" size="small" resize="vertical" @input="emitUpdate" @focus="activeFieldKey = 'exp-'+idx+'-description'"/>
            </div>
            <transition name="fade-down">
              <div v-if="activeFieldKey === 'exp-'+idx+'-description'" class="field-font-bar"><field-font-bar-content :fk="'exp-'+idx+'-description'" :active-field-key="activeFieldKey" :get-style="getNonBasicFieldStyle" :on-change="onNonBasicFieldFontChange"/></div>
            </transition>
          </div>
        </div>
      </section>

      <!-- ====== 项目经历 ====== -->
      <section v-show="currentSection === 'projects'" class="form-section">
        <div class="form-section-header">
          <h3>项目经历</h3>
          <div class="header-right-btns">
            <button class="header-edit-btn"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg></button>
            <button class="header-del-btn" @click="removeModuleFromNav('projects')"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2 2V6m3 0V4a2 2 0 012-2h4a2 2 0 01 2 2v2"/></svg></button>
          </div>
        </div>
        <button class="btn-add-card-dark" @click="addItem('projects')"><span>&#10024;</span> 添加项目经历</button>
        <div v-for="(item,idx) in localData.projects" :key="item._id" class="list-card collapsible">
          <div class="list-card-head clickable" @click="toggleCardExpand('projects',idx)">
            <span class="card-expand-icon" :class="{expanded:expandedCards.has(`projects-${idx}`)}">&#9660;</span>
            <span class="card-title-text">{{ item.name || `项目 #${idx+1}` }}</span>
            <button class="card-del" @click.stop="removeItem('projects',idx)">删除</button>
          </div>
          <div class="card-body" v-show="expandedCards.has(`projects-${idx}`)">
            <div class="form-item" style="margin-bottom:0;">
              <label>项目名称</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'proj-'+idx+'-name' }">
                <el-input v-model="item.name" placeholder="项目名称" size="small" @input="emitUpdate" @focus="activeFieldKey = 'proj-'+idx+'-name'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'proj-'+idx+'-name'" class="field-font-bar"><FieldFontBarContent :fk="'proj-'+idx+'-name'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="form-item" style="margin-bottom:0;">
              <label>项目角色</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'proj-'+idx+'-role' }">
                <el-input v-model="item.role" placeholder="你在项目中的角色" size="small" @input="emitUpdate" @focus="activeFieldKey = 'proj-'+idx+'-role'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'proj-'+idx+'-role'" class="field-font-bar"><FieldFontBarContent :fk="'proj-'+idx+'-role'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="form-item" style="margin-bottom:0;">
              <label>项目描述</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'proj-'+idx+'-description' }">
                <el-input v-model="item.description" type="textarea" :rows="2" placeholder="简要描述项目背景和目标" size="small" resize="vertical" @input="emitUpdate" @focus="activeFieldKey = 'proj-'+idx+'-description'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'proj-'+idx+'-description'" class="field-font-bar"><FieldFontBarContent :fk="'proj-'+idx+'-description'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="form-row">
              <div class="form-item form-half" style="margin-bottom:0;">
                <label>开始时间</label>
                <div class="field-cell" :class="{ focused: activeFieldKey === 'proj-'+idx+'-startDate' }">
                  <el-date-picker v-model="item.startDate" type="month" placeholder="开始" size="small" style="width:100%" value-format="YYYY-MM" @change="emitUpdate" @focus="activeFieldKey = 'proj-'+idx+'-startDate'"/>
                </div>
                <transition name="fade-down">
                  <div v-if="activeFieldKey === 'proj-'+idx+'-startDate'" class="field-font-bar"><FieldFontBarContent :fk="'proj-'+idx+'-startDate'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
                </transition>
              </div>
              <div class="form-item form-half" style="margin-bottom:0;">
                <label>结束时间</label>
                <div class="field-cell" :class="{ focused: activeFieldKey === 'proj-'+idx+'-endDate' }">
                  <el-date-picker v-model="item.endDate" type="month" placeholder="结束" size="small" style="width:100%" value-format="YYYY-MM" @change="emitUpdate" @focus="activeFieldKey = 'proj-'+idx+'-endDate'"/>
                </div>
                <transition name="fade-down">
                  <div v-if="activeFieldKey === 'proj-'+idx+'-endDate'" class="field-font-bar"><FieldFontBarContent :fk="'proj-'+idx+'-endDate'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
                </transition>
              </div>
            </div>
            <div class="form-item" style="margin-bottom:0;">
              <label>项目链接</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'proj-'+idx+'-link' }">
                <el-input v-model="item.link" placeholder="https://" size="small" @input="emitUpdate" @focus="activeFieldKey = 'proj-'+idx+'-link'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'proj-'+idx+'-link'" class="field-font-bar"><FieldFontBarContent :fk="'proj-'+idx+'-link'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="rich-toolbar compact"><div class="toolbar-left"><button class="tb-btn" v-for="t in richToolsLeft" :key="'p-'+t.label" :title="t.label"><span v-html="t.icon"></span></button></div><button class="ai-polish-btn small" @click="$emit('aiPolish',{text:item.content,field:'project-'+idx})"><span class="ai-icon">&#10024;</span> AI润色</button></div>
            <div class="form-item" style="margin-top:8px;margin-bottom:0;">
              <div class="field-cell" :class="{ focused: activeFieldKey === 'proj-'+idx+'-content' }">
                <el-input v-model="item.content" type="textarea" :rows="4" placeholder="详细描述你的工作和成果..." size="small" resize="vertical" @input="emitUpdate" @focus="activeFieldKey = 'proj-'+idx+'-content'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'proj-'+idx+'-content'" class="field-font-bar"><FieldFontBarContent :fk="'proj-'+idx+'-content'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
          </div>
        </div>
      </section>

      <!-- ====== 教育经历 ====== -->
      <section v-show="currentSection === 'education'" class="form-section">
        <div class="form-section-header">
          <h3>教育经历</h3>
          <div class="header-right-btns">
            <button class="header-edit-btn"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg></button>
            <button class="header-del-btn" @click="removeModuleFromNav('education')"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2 2V6m3 0V4a2 2 0 012-2h4a2 2 0 01 2 2v2"/></svg></button>
          </div>
        </div>
        <button class="btn-add-card-dark" @click="addItem('education')"><span>&#10024;</span> 添加教育经历</button>
        <div v-for="(item,idx) in localData.education" :key="item._id" class="list-card collapsible edu-card">
          <div class="list-card-head clickable" @click="toggleCardExpand('education',idx)">
            <span class="card-expand-icon" :class="{expanded:expandedCards.has(`education-${idx}`)}">&#9660;</span>
            <span class="card-title-text">{{ item.school || '未命名学校' }}{{ item.major ? '  '+item.major : '' }}</span>
            <button class="card-del" @click.stop="removeItem('education',idx)">删除</button>
          </div>
          <div class="card-body" v-show="expandedCards.has(`education-${idx}`)">
            <div class="form-item" style="margin-bottom:0;">
              <label>学校名称</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'edu-'+idx+'-school' }">
                <el-input v-model="item.school" placeholder="学校名称" size="small" @input="emitUpdate" @focus="activeFieldKey = 'edu-'+idx+'-school'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'edu-'+idx+'-school'" class="field-font-bar"><FieldFontBarContent :fk="'edu-'+idx+'-school'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="form-item" style="margin-bottom:0;">
              <label>专业</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'edu-'+idx+'-major' }">
                <el-input v-model="item.major" placeholder="专业名称" size="small" @input="emitUpdate" @focus="activeFieldKey = 'edu-'+idx+'-major'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'edu-'+idx+'-major'" class="field-font-bar"><FieldFontBarContent :fk="'edu-'+idx+'-major'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="form-row">
              <div class="form-item form-half" style="margin-bottom:0;">
                <label>学历</label>
                <div class="field-cell" :class="{ focused: activeFieldKey === 'edu-'+idx+'-degree' }">
                  <el-select v-model="item.degree" size="small" @change="emitUpdate" style="width:100%" @focus="activeFieldKey = 'edu-'+idx+'-degree'"><el-option label="大专"value="大专"/><el-option label="本科"value="本科"/><el-option label="硕士"value="硕士"/><el-option label="博士"value="博士"/></el-select>
                </div>
                <transition name="fade-down">
                  <div v-if="activeFieldKey === 'edu-'+idx+'-degree'" class="field-font-bar"><FieldFontBarContent :fk="'edu-'+idx+'-degree'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
                </transition>
              </div>
              <div class="form-item form-half" style="margin-bottom:0;">
                <label>GPA</label>
                <div class="field-cell" :class="{ focused: activeFieldKey === 'edu-'+idx+'-gpa' }">
                  <el-input v-model="item.gpa" placeholder="如：3.8/4.0" size="small" @input="emitUpdate" @focus="activeFieldKey = 'edu-'+idx+'-gpa'"/>
                </div>
                <transition name="fade-down">
                  <div v-if="activeFieldKey === 'edu-'+idx+'-gpa'" class="field-font-bar"><FieldFontBarContent :fk="'edu-'+idx+'-gpa'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
                </transition>
              </div>
            </div>
            <div class="form-row">
              <div class="form-item form-half" style="margin-bottom:0;">
                <label>开始时间</label>
                <div class="field-cell" :class="{ focused: activeFieldKey === 'edu-'+idx+'-startDate' }">
                  <el-date-picker v-model="item.startDate" type="month" placeholder="开始" size="small" style="width:100%" value-format="YYYY-MM" @change="emitUpdate" @focus="activeFieldKey = 'edu-'+idx+'-startDate'"/>
                </div>
                <transition name="fade-down">
                  <div v-if="activeFieldKey === 'edu-'+idx+'-startDate'" class="field-font-bar"><FieldFontBarContent :fk="'edu-'+idx+'-startDate'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
                </transition>
              </div>
              <div class="form-item form-half" style="margin-bottom:0;">
                <label>结束时间</label>
                <div class="field-cell" :class="{ focused: activeFieldKey === 'edu-'+idx+'-endDate' }">
                  <div class="date-with-switch">
                    <el-date-picker v-model="item.endDate" type="month" placeholder="结束" size="small" style="width:100%;flex:1;" value-format="YYYY-MM" :disabled="item.isCurrent" @change="emitUpdate" @focus="activeFieldKey = 'edu-'+idx+'-endDate'"/>
                    <label class="now-switch"><input type="checkbox" v-model="item.isCurrent" @change="if(item.isCurrent){item.endDate=''};emitUpdate()"/> 至今</label>
                  </div>
                </div>
                <transition name="fade-down">
                  <div v-if="activeFieldKey === 'edu-'+idx+'-endDate'" class="field-font-bar"><FieldFontBarContent :fk="'edu-'+idx+'-endDate'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
                </transition>
              </div>
            </div>
            <div class="form-item" style="margin-bottom:0;">
              <label>学校介绍</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'edu-'+idx+'-description' }">
                <el-input v-model="item.description" type="textarea" :rows="2" placeholder="如有必要可填写学校简介" size="small" resize="vertical" @input="emitUpdate" @focus="activeFieldKey = 'edu-'+idx+'-description'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'edu-'+idx+'-description'" class="field-font-bar"><FieldFontBarContent :fk="'edu-'+idx+'-description'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="rich-toolbar compact"><div class="toolbar-left"><button class="tb-btn" v-for="t in richToolsLeft" :key="'e-'+t.label" :title="t.label"><span v-html="t.icon"></span></button></div><button class="ai-polish-btn small" @click="$emit('aiPolish',{text:item.courses,field:`edu-${idx}`})"><span class="ai-icon">&#10024;</span> AI润色</button></div>
            <div class="form-item" style="margin-top:8px;margin-bottom:0;">
              <div class="field-cell" :class="{ focused: activeFieldKey === 'edu-'+idx+'-courses' }">
                <el-input v-model="item.courses" type="textarea" :rows="3" placeholder="主修课程 / 获奖情况等" size="small" resize="vertical" @input="emitUpdate" @focus="activeFieldKey = 'edu-'+idx+'-courses'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'edu-'+idx+'-courses'" class="field-font-bar"><FieldFontBarContent :fk="'edu-'+idx+'-courses'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
          </div>
        </div>
      </section>

      <!-- ====== 自我评价 ====== -->
      <section v-show="currentSection === 'selfEvaluation'" class="form-section">
        <div class="form-section-header">
          <h3>自我评价</h3>
          <div class="header-right-btns">
            <button class="header-edit-btn"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg></button>
            <button class="header-del-btn" @click="removeModuleFromNav('selfEvaluation')"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2 2V6m3 0V4a2 2 0 012-2h4a2 2 0 01 2 2v2"/></svg></button>
          </div>
        </div>
        <div class="rich-toolbar"><div class="toolbar-left"><button class="tb-btn" v-for="t in richToolsLeft" :key="'se-'+t.label" :title="t.label"><span v-html="t.icon"></span></button></div></div>
        <div class="rich-toolbar toolbar-second">
          <div class="toolbar-left"><button class="tb-btn" title="撤销">&#8617;</button><button class="tb-btn" title="重做">&#8618;</button></div>
          <button class="ai-polish-btn" @click="$emit('aiPolish',{text:localData.selfEvaluation,field:'selfEvaluation'})"><span class="ai-icon">&#10024;</span> AI润色</button>
        </div>
        <div class="form-item" style="margin-bottom:0;">
          <div class="field-cell" :class="{ focused: activeFieldKey === 'selfEvaluation' }">
            <el-input v-model="localData.selfEvaluation" type="textarea" :rows="6" placeholder="&#10;1. 个人技能、知识描述&#10;2. 个人能力、特长描述&#10;3. 个人性格、特质描述" resize="vertical" size="small" @input="emitUpdate" @focus="activeFieldKey = 'selfEvaluation'"/>
          </div>
          <transition name="fade-down">
            <div v-if="activeFieldKey === 'selfEvaluation'" class="field-font-bar"><FieldFontBarContent :fk="'selfEvaluation'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
          </transition>
        </div>
      </section>

      <!-- ====== 证书荣誉 ====== -->
      <section v-show="currentSection === 'certificates'" class="form-section">
        <div class="form-section-header">
          <h3>证书荣誉</h3>
          <div class="header-right-btns">
            <button class="header-edit-btn"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg></button>
            <button class="header-del-btn" @click="removeModuleFromNav('certificates')"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2 2V6m3 0V4a2 2 0 012-2h4a2 2 0 01 2 2v2"/></svg></button>
          </div>
        </div>
        <button class="btn-add-card-dark" @click="addItem('certificates')"><span>&#10024;</span> 添加证书</button>
        <div v-for="(item,idx) in localData.certificates" :key="item._id" class="list-card compact">
          <div class="list-card-head"><span class="card-index">#{{idx+1}}</span><button class="card-del" @click="removeItem('certificates',idx)">删除此条</button></div>
          <div class="form-row">
            <div class="form-item form-half" style="margin-bottom:0;">
              <label>证书名称</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'cert-'+idx+'-name' }">
                <el-input v-model="item.name" placeholder="如：PMP项目管理认证" size="small" @input="emitUpdate" @focus="activeFieldKey = 'cert-'+idx+'-name'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'cert-'+idx+'-name'" class="field-font-bar"><FieldFontBarContent :fk="'cert-'+idx+'-name'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
            <div class="form-item form-half" style="margin-bottom:0;">
              <label>颁发机构</label>
              <div class="field-cell" :class="{ focused: activeFieldKey === 'cert-'+idx+'-issuer' }">
                <el-input v-model="item.issuer" placeholder="如：PMI" size="small" @input="emitUpdate" @focus="activeFieldKey = 'cert-'+idx+'-issuer'"/>
              </div>
              <transition name="fade-down">
                <div v-if="activeFieldKey === 'cert-'+idx+'-issuer'" class="field-font-bar"><FieldFontBarContent :fk="'cert-'+idx+'-issuer'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
              </transition>
            </div>
          </div>
          <div class="form-item" style="margin-bottom:0;">
            <label>获取时间</label>
            <div class="field-cell" :class="{ focused: activeFieldKey === 'cert-'+idx+'-date' }">
              <el-date-picker v-model="item.date" type="month" size="small" style="width:100%" value-format="YYYY-MM" @change="emitUpdate" @focus="activeFieldKey = 'cert-'+idx+'-date'"/>
            </div>
            <transition name="fade-down">
              <div v-if="activeFieldKey === 'cert-'+idx+'-date'" class="field-font-bar"><FieldFontBarContent :fk="'cert-'+idx+'-date'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
            </transition>
          </div>
          <div class="form-item" style="margin-bottom:0;">
            <label>获奖经历</label>
            <div class="field-cell" :class="{ focused: activeFieldKey === 'cert-'+idx+'-desc' }">
              <el-input v-model="item.description" type="textarea" :rows="3" placeholder="如：在全国大学生数学建模竞赛中获得省级一等奖，负责模型构建与算法实现..." size="small" resize="none" @input="emitUpdate" @focus="activeFieldKey = 'cert-'+idx+'-desc'"/>
            </div>
            <transition name="fade-down">
              <div v-if="activeFieldKey === 'cert-'+idx+'-desc'" class="field-font-bar"><FieldFontBarContent :fk="'cert-'+idx+'-desc'" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
            </transition>
          </div>
        </div>
      </section>

      <!-- ====== 自定义模块（动态渲染）====== -->
      <section v-for="cm in localData.customModules" :key="cm._id" v-show="currentSection === cm._id" class="form-section">
        <div class="form-section-header">
          <h3>{{ cm.title || '自定义模块' }}</h3>
          <div class="header-right-btns">
            <button class="header-edit-btn" @click="editCustomTitle(cm)"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg></button>
            <button class="header-del-btn" @click="removeCustomModule(cm._id)"><svg width="14"height="14"viewBox="0 0 24 24"fill="none"stroke="currentColor"stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2 2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg></button>
          </div>
        </div>
        <div class="rich-toolbar"><div class="toolbar-left"><button class="tb-btn" v-for="t in richToolsLeft" :key="'cm-'+t.label" :title="t.label"><span v-html="t.icon"></span></button></div></div>
        <div class="rich-toolbar toolbar-second">
          <div class="toolbar-left"><button class="tb-btn" title="撤销">&#8617;</button><button class="tb-btn" title="重做">&#8618;</button></div>
          <button class="ai-polish-btn" @click="$emit('aiPolish',{text:cm.content,field:'custom-'+cm._id})"><span class="ai-icon">&#10024;</span> AI润色</button>
        </div>
        <div class="form-item" style="margin-bottom:0;">
          <div class="field-cell" :class="{ focused: activeFieldKey === 'custom-'+cm._id }">
            <el-input v-model="cm.content" type="textarea" :rows="8" placeholder="输入该模块的内容..." resize="vertical" size="small" @input="emitUpdate" @focus="activeFieldKey = 'custom-'+cm._id"/>
          </div>
          <transition name="fade-down">
            <div v-if="activeFieldKey === 'custom-'+cm._id" class="field-font-bar"><FieldFontBarContent :fk="'custom-'+cm._id" :get-style="getNonBasicFieldStyle" @change="onNonBasicFieldFontChange"/></div>
          </transition>
        </div>
      </section>

      </div><!-- /.collapse-body -->

      <!-- ====== AI 智能提取 ====== -->
      <div class="ai-extract-box">
        <div class="ai-extract-header" @click="aiExtractExpanded = !aiExtractExpanded">
          <span class="ai-extract-title">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2a4 4 0 014 4c0 2-2 3-2 5h-4c0-2-2-3-2-5a4 4 0 014-4z"/><path d="M9 18h6"/><path d="M10 22h4"/></svg>
            AI 智能提取
          </span>
          <span class="ai-extract-arrow" :class="{ rotated: aiExtractExpanded }">&#9660;</span>
        </div>
        <div v-show="aiExtractExpanded" class="ai-extract-body">
          <p class="ai-extract-tip">粘贴一段简历文字或职位描述，AI 将自动提取信息并填入对应板块。<br><span style="color:#6B7280;font-size:11px;">提示：可在文本开头加 <b>[工作经历]</b> / <b>[教育经历]</b> / <b>[项目经历]</b> / <b>[获奖经历]</b> 等标记限定提取到指定板块</span></p>
          <el-input
            v-model="aiExtractInput"
            type="textarea"
            :rows="10"
            placeholder="【全量提取 — 不加标记】&#10;张三，5年前端开发经验，熟悉 React 和 Vue.js，曾在字节跳动担任高级前端工程师。毕业于北京大学计算机专业...&#10;&#10;【限定板块 — 加标记】&#10;[获奖经历]&#10;2025.10.16 一带一路金砖暨金砖国家技术与创新大赛 核心成员&#10;区块链 Linux 运维核心工作：负责区块链节点环境搭建...&#10;2026.3.8 广东省职业院校技能大赛 后端核心负责人..."
            resize="vertical"
            size="small"
          />
          <button
            class="ai-extract-btn"
            :disabled="!aiExtractInput.trim() || aiExtracting"
            @click="onAiExtractClick"
          >
            <span v-if="aiExtracting" class="ai-extract-loading"></span>
            {{ aiExtracting ? '提取中...' : '✨ 开始提取' }}
          </button>
        </div>
      </div>

    </div><!-- /.panel-scroll -->
  </div>
</template>

<script setup>
import { reactive, ref, computed, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import FieldFontBarContent from './FieldFontBarContent.vue'

const props = defineProps({
  resumeData: { type: Object, default: () => ({}) },
  activeSection: { type: String, default: 'basic' },
  enabledModuleIds: { type: Array, default: () => [] }, // 从 Editor.vue 同步的模块列表
})

const emit = defineEmits([
  'update','sectionChange','setColor','setFont','setLineHeight','setFontSize',
  'toggleVisibility','deleteModule','aiPolish','aiExtract','avatarUpload',
  'reorderModules',
])

// ---- 状态 ----
const panelWidth = ref(38)
const scrollRef = ref(null)
const currentSection = ref(props.activeSection || 'basic')
const currentColor = ref('#333')
const lineHeightVal = ref(1.5)
const fontSizeVal = ref(10)
const basicLayout = ref(0)
const hiddenModules = ref(new Set())
const expandedCards = ref(new Set())
const showAddMenu = ref(false)
const showFieldPicker = ref(false)
const aiExtractExpanded = ref(false)
const aiExtractInput = ref('')
const aiExtracting = ref(false)

// 收起状态
const topCollapsed = ref(false)
const bottomCollapsed = ref(false)
const activeFieldKey = ref('')
const customFields = ref([])
const removedCustomFields = ref([])
let customFieldCounter = 0

// ---- 跨板块字段聚焦：非基础信息字段的字体样式存储 ----
const nonBasicFieldStyles = ref({})

// 获取某个非基础字段的字体样式（不存在则用默认值初始化）
function getNonBasicFieldStyle(fieldKey) {
  if (!nonBasicFieldStyles.value[fieldKey]) {
    nonBasicFieldStyles.value[fieldKey] = {
      fontSize: 10, fontWeight: 'normal', color: '#333333',
      lineHeight: 1.6, letterSpacing: 0, fontStyle: '', textDecoration: '',
      marginTop: 0, marginBottom: 0, paddingLeft: 0,
    }
  }
  return nonBasicFieldStyles.value[fieldKey]
}

// 非基础字段字体样式变更时调用
function onNonBasicFieldFontChange(fieldKey) {
  emitUpdate()
}

// ---- 全部标准模块定义（含图标）----
const ALL_STANDARD_MODULES = [
  { id: 'basic',       label: '基本信息', icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#667eea"stroke-width="2"stroke-linecap="round"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12"cy="7"r="4"/></svg>' },
  { id: 'intention',   label: '求职意向', icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#4CAF50"stroke-width="2"stroke-linecap="round"><circle cx="11"cy="11"r="8"/><path d="M21 21l-4.35-4.35"/></svg>' },
  { id: 'skills',      label: '专业技能', icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#FF9800"stroke-width="2"stroke-linecap="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>' },
  { id: 'experience',  label: '工作经历', icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#1976D2"stroke-width="2"stroke-linecap="round"><rect x="2"y="7"width="20"height="14"rx="2"/><path d="M16 21V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v16"/></svg>' },
  { id: 'projects',    label: '项目经历', icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#9C27B0"stroke-width="2"stroke-linecap="round"><path d="M22 19a2 2 0 01-2 2H4a2 2 0 01-2-2V5a2 2 0 012-2h5l2 3h9a2 2 0 012 2z"/></svg>' },
  { id: 'education',   label: '教育经历', icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#D32F2F"stroke-width="2"stroke-linecap="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>' },
  { id: 'selfEvaluation', label: '自我评价', icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#607D8B"stroke-width="2"stroke-linecap="round"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>' },
  { id: 'certificates', label: '证书作品', icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#F57C00"stroke-width="2"stroke-linecap="round"><circle cx="12"cy="8"r="6"/><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11"/></svg>' },
]

// ---- 已启用的模块ID列表（控制左侧导航和画布显示）----
// 以 Editor.vue 传来的 prop 为准，本地只做临时修改后 emit 回去同步
const enabledModuleIds = ref(['basic'])

// 从 Editor.vue 同步 prop 到本地状态（Editor 是唯一可信源）
watch(() => props.enabledModuleIds, (ids) => {
  if (ids && ids.length > 0) {
    // 始终信任 Editor 的值，确保左右完全一致
    enabledModuleIds.value = [...ids]
  }
}, { immediate: true })

// ---- 导航中可见的模块 = 已启用的标准模块(按顺序) + 自定义模块 ----
const visibleNavModules = computed(() => {
  const result = []
  // 按enabledModuleIds的顺序排列标准模块
  for (const id of enabledModuleIds.value) {
    const found = ALL_STANDARD_MODULES.find(m => m.id === id)
    if (found) result.push(found)
  }
  // 追加自定义模块
  if (localData.customModules && localData.customModules.length) {
    localData.customModules.forEach(cm => {
      result.push({
        id: cm._id,
        label: cm.title || '自定义模块',
        icon: '<svg width="16"height="16"viewBox="0 0 24 24"fill="none"stroke="#7B68EE"stroke-width="2"stroke-linecap="round"><rect x="3"y="3"width="18"height="18"rx="2"/><path d="M12 8v8M8 12h8"/></svg>'
      })
    })
  }
  return result
})

// ---- 未启用的标准模块（用于"添加布局"菜单）----
const inactiveStandardModules = computed(() => {
  return ALL_STANDARD_MODULES.filter(m => !enabledModuleIds.value.includes(m.id))
})

// ---- 基础字段完整列表（含所有可用字段）----
const ALL_BASIC_FIELDS = [
  { key:'name',     label:'姓名' },
  { key:'gender',   label:'性别' },
  { key:'status',   label:'状态' },
  { key:'birthday', label:'生日' },
  { key:'email',    label:'邮箱' },
  { key:'phone',    label:'电话' },
  { key:'age',      label:'年龄' },
  { key:'education',label:'学历' },
  { key:'targetJob',label:'期望岗位' },
  { key:'workYears',label:'工作年限' },
  { key:'city',     label:'所在城市' },
]

// 当前可见的基础字段（用户可以删除/恢复）
const basicFields = ref([
  { key:'name',label:'姓名' },{ key:'gender',label:'性别' },{ key:'status',label:'状态' },
  { key:'birthday',label:'生日' },{ key:'email',label:'邮箱' },{ key:'phone',label:'电话' },
])

// 可恢复的字段（在ALL_BASIC_FIELDS中但不在basicFields中的）
const availableFields = computed(() => {
  return ALL_BASIC_FIELDS.filter(af => !basicFields.value.some(bf => bf.key === af.key))
})

const colorOptions = ['#000000','#1a1a2e','#1976D2','#D32F2F','#FF9800','#9C27B0','#607D8B']

const layoutOptions = [
  { svg: '<svg width="28"height="20"viewBox="0 0 28 20"><circle cx="6"cy="6"r="3"fill="#667eea"/><circle cx="14"cy="6"r="3"fill="#ccc"/><circle cx="22"cy="6"r="3"fill="#ccc"/></svg>' },
  { svg: '<svg width="28"height="20"viewBox="0 0 28 20"><rect x="3"y="3"width="6"height="4"rx="1"fill="#667eea"/><rect x="3"y="10"width="22"height="3"rx="1"fill="#eee"/><rect x="3"y="15"width="18"height="3"rx="1"fill="#eee"/></svg>' },
  { svg: '<svg width="28"height="20"viewBox="0 0 28 20"><rect x="3"y="3"width="10"height="14"rx="2"fill="#667eea"opacity=".25"stroke="#667eea"stroke-width="1"/><rect x="16"y="3"width="9"height="6"rx="1"fill="#eee"/><rect x="16"y="11"width="9"height="6"rx="1"fill="#eee"/></svg>' },
]

const richToolsLeft = [
  { label:'加粗',icon:'<b style="font-family:serif;font-weight:700;">B</b>' },
  { label:'斜体',icon:"<i style='font-family:serif;font-style:italic;'>I</i>" },
  { label:'下划线',icon:'<u style="font-family:serif;text-decoration:underline;">U</u>' },
  { label:'链接',icon:'&#128279;' },
  { label:'无序列表',icon:'&#8226;&#8226;&#8226;' },
  { label:'有序列表',icon:'1.2.3.' },
  { label:'缩进',icon:'&#8594;&#8594;' },
  { label:'减缩进',icon:'&#8592;&#8592;' },
  { label:'清除格式',icon:'&#9003;' },
  { label:'插入图片',icon:'&#128247;' },
]

// ---- 数据 ----
function uid(){ return Date.now().toString(36)+Math.random().toString(36).slice(2,7) }

const defaultData = () => ({
  basic:{ name:'',gender:'',status:'',birthday:'',email:'',phone:'',avatar:'',age:'',education:'',targetJob:'',workYears:'',city:'',avatarWidth:76,avatarHeight:92,avatarRadius:6,avatarPosition:'right-top' },
  intention:{ expectedSalary:'',jobType:'全职',availableDate:'' },
  skills:'', experience:[], projects:[], education:[],
  selfEvaluation:'', certificates:[], customModules:[],
})

const itemTemplates = {
  experience: () => ({ _id:uid(),company:'',position:'',startDate:'',endDate:'',description:'',content:'' }),
  projects: () => ({ _id:uid(),name:'',role:'',description:'',startDate:'',endDate:'',link:'',startText:'',content:'' }),
  education: () => ({ _id:uid(),school:'',major:'',degree:'',gpa:'',startDate:'',endDate:'',isCurrent:false,description:'',courses:'' }),
  certificates: () => ({ _id:uid(),name:'',issuer:'',date:'',description:'' }),
}

const localData = reactive(deepMerge(defaultData(), props.resumeData))
let isInternalUpdate = false // 标记：当前更新是否来自表单自身编辑

// 初始化 customModules 如果不存在
if (!localData.customModules) localData.customModules = []

// 从 resumeData 中恢复已启用的模块（仅初始化时执行一次）
function initEnabledModules() {
  // 增量模式：只根据数据内容补充模块，不覆盖已手动启用的模块
  const existing = new Set(enabledModuleIds.value)
  const d = localData
  if (!existing.has('skills') && d.skills && String(d.skills).trim()) enabledModuleIds.value.push('skills')
  if (!existing.has('experience') && d.experience && d.experience.length) enabledModuleIds.value.push('experience')
  if (!existing.has('projects') && d.projects && d.projects.length) enabledModuleIds.value.push('projects')
  if (!existing.has('education') && d.education && d.education.length) enabledModuleIds.value.push('education')
  if (!existing.has('selfEvaluation') && d.selfEvaluation && String(d.selfEvaluation).trim()) enabledModuleIds.value.push('selfEvaluation')
  if (!existing.has('certificates') && d.certificates && d.certificates.length) enabledModuleIds.value.push('certificates')
  enabledModuleIds.value = [...new Set(enabledModuleIds.value)]
}
// 仅在组件初始化时执行一次（prop watcher 的 immediate 已处理 Editor 下发的值，这里做数据驱动的补充）
initEnabledModules()

// ---- 监听外部变化 ----
watch(()=>props.activeSection,(val)=>{ if(val&&val!==currentSection.value) switchSection(val) })
watch(()=>props.resumeData,(val)=>{
  // 跳过自身触发的更新，防止循环覆盖
  if (isInternalUpdate) return
  Object.assign(localData,deepMerge(defaultData(),val))
  // 注意：不再调用 initEnabledModules()，避免外部数据变化时重置已手动添加的模块顺序
},{deep:true})

// ---- 方法 ----
function switchSection(id){
  currentSection.value=id
  emit('sectionChange',id)
  if(['experience','projects','education'].includes(id)) ensureFirstExpanded(id)
  nextTick(()=>{ if(scrollRef.value) scrollRef.value.scrollTop=0 })
}
function ensureFirstExpanded(sec){ const arr=getArray(sec); if(arr&&arr.length>0&&!expandedCards.value.has(`${sec}-0`)) expandedCards.value.add(`${sec}-0`) }
function getArray(k){ return localData[k] }

function onSetColor(c){ currentColor.value=c; emit('setColor',c) }
function onAiExtractClick(){
  if (!aiExtractInput.value.trim() || aiExtracting.value) return
  aiExtracting.value = true
  emit('aiExtract', { text: aiExtractInput.value })
}
function resetAiExtract(success){
  aiExtracting.value = false
  if (success) {
    aiExtractInput.value = ''
    aiExtractExpanded.value = false
  }
}
function toggleVisibility(id){
  const s=hiddenModules.value
  s.has(id)?s.delete(id):s.add(id)
  emit('toggleVisibility',id,!s.has(id))
}

// ---- 模块管理：启用/禁用/移除 ----
function enableModule(opt){
  // 启用模块：加入已启用列表
  if (!enabledModuleIds.value.includes(opt.id)) {
    enabledModuleIds.value.push(opt.id)
  }
  // 初始化该模块的默认数据（确保画布能显示）
  initModuleDefaultData(opt.id)
  switchSection(opt.id)
  showAddMenu.value=false
  ElMessage.success(`已添加「${opt.label}」到布局`)
  emitUpdate()
}

// 为新启用的模块初始化默认数据（确保 shouldShow 通过数据条件）
function initModuleDefaultData(moduleId) {
  const d = localData
  switch (moduleId) {
    case 'experience':
      if (!d.experience || !d.experience.length) {
        d.experience = [{ _id: uid(), company: '', position: '', startDate: '', endDate: '', content: '', description: '' }]
      }
      break
    case 'projects':
      if (!d.projects || !d.projects.length) {
        d.projects = [{ _id: uid(), name: '', role: '', description: '', startDate: '', endDate: '', link: '', content: '' }]
      }
      break
    case 'education':
      if (!d.education || !d.education.length) {
        d.education = [{ _id: uid(), school: '', major: '', degree: '', gpa: '', startDate: '', endDate: '', isCurrent: false, description: '', courses: '' }]
      }
      break
    case 'certificates':
      if (!d.certificates || !d.certificates.length) {
        d.certificates = [{ _id: uid(), name: '', issuer: '', date: '', description: '' }]
      }
      break
    case 'skills':
      if (!d.skills || !String(d.skills).trim()) {
        d.skills = ''
      }
      break
    case 'selfEvaluation':
      if (!d.selfEvaluation || !String(d.selfEvaluation).trim()) {
        d.selfEvaluation = ''
      }
      break
    case 'intention':
      if (!d.intention) {
        d.intention = { expectedSalary: '', jobType: '全职', availableDate: '' }
      }
      break
  }
}
function removeModuleFromNav(id){
  // 从导航移除（不删除数据，只是不在画布上显示）
  if (id === 'basic') {
    ElMessage.warning('基本信息不能移除')
    return
  }
  const idx = enabledModuleIds.value.indexOf(id)
  if (idx !== -1) {
    enabledModuleIds.value.splice(idx, 1)
    if (currentSection.value === id) switchSection('basic')
    emitUpdate()
    ElMessage.info('已从布局中移除')
  }
}

// ---- 基础字段管理 ----
function toggleFieldHidden(sec,fk){}

// 可见的基础字段列表（标准字段 + 自定义字段）
const visibleBasicFields = computed(() => {
  return [...basicFields.value, ...customFields.value]
})

// 求职意向是否有内容
const hasIntentionContent = computed(() => {
  const d = localData
  return !!(d.basic?.targetJob || d.basic?.city || d.intention?.expectedSalary || d.intention?.jobType || d.intention?.availableDate)
})

function deleteBasicField(key){
  // 先检查是否是自定义字段
  const cIdx = customFields.value.findIndex(f => f.key === key)
  if (cIdx !== -1) {
    const removed = customFields.value.splice(cIdx, 1)[0]
    removedCustomFields.value.push(removed)
    delete localData.basic[key]
    emitUpdate()
    return
  }
  // 标准字段
  const idx=basicFields.value.findIndex(f=>f.key===key)
  if(idx!==-1) basicFields.value.splice(idx,1)
  // 清理数据，让画布上的 v-if 判断为 false
  delete localData.basic[key]
  if(activeFieldKey.value === key) activeFieldKey.value = ''
  emitUpdate()
}

function restoreBasicField(fieldDef){
  basicFields.value.push({
    key: fieldDef.key, label: fieldDef.label,
    fontSize: 10, fontWeight: 'normal', color: '#333333',
    lineHeight: 1.6, letterSpacing: 0, fontStyle: '', textDecoration: '',
    marginTop: 0, marginBottom: 0, paddingLeft: 0,
  })
  showFieldPicker.value=false
  emitUpdate()
}

// 自定义字段
function startAddCustomField(){
  ElMessageBox.prompt('请输入自定义字段名称','添加自定义字段',{
    confirmButtonText:'添加',cancelButtonText:'取消',inputPlaceholder:'如：籍贯、爱好等',
    inputValidator:v=>(!v||!v.trim())?'名称不能为空':true,
  }).then(({value})=>{
    const key = '_custom_' + (++customFieldCounter)
    const cf = {
      key, label: value.trim(),
      fontSize: 10, fontWeight: 'normal', color: '#333333',
      lineHeight: 1.6, letterSpacing: 0, fontStyle: '', textDecoration: '',
      marginTop: 0, marginBottom: 0, paddingLeft: 0,
    }
    customFields.value.push(cf)
    localData.basic[key] = ''
    showFieldPicker.value=false
    emitUpdate()
    ElMessage.success(`已添加自定义字段「${value.trim()}」`)
  }).catch(()=>{})
}

function restoreCustomField(cf){
  customFields.value.push({ ...cf })
  removedCustomFields.value = removedCustomFields.value.filter(r => r.key !== cf.key)
  if (!localData.basic[cf.key]) localData.basic[cf.key] = ''
  showFieldPicker.value=false
  emitUpdate()
}

function onFieldFontChange(bf){
  emitUpdate()
}

// 头像上传
function onAvatarUpload(e){
  const f=e.target.files?.[0]; if(!f)return
  const r=new FileReader(); r.onload=(ev)=>{ localData.basic.avatar=ev.target.result; emitUpdate() }; r.readAsDataURL(f); e.target.value=''
}

// 添加/删除条目
function addItem(section){
  // 如果该模块还没启用，先启用
  if (!enabledModuleIds.value.includes(section)) {
    enabledModuleIds.value.push(section)
  }
  localData[section].push(itemTemplates[section]())
  if(currentSection.value!==section) switchSection(section)
  const idx=localData[section].length-1
  if(['experience','projects','education'].includes(section)) expandedCards.value.add(`${section}-${idx}`)
  emitUpdate()
}
function removeItem(section,index){
  localData[section].splice(index,1)
  const newSet=new Set()
  expandedCards.value.forEach(k=>{
    const p=k.split('-'), sec=p[0], i=parseInt(p[1])
    if(sec===section&&i>index) newSet.add(`${sec}-${i-1}`)
    else if(sec!==section||i<index) newSet.add(k)
  })
  expandedCards.value=newSet
  emitUpdate()
}
function toggleCardExpand(sec,idx){
  const k=`${sec}-${idx}`
  expandedCards.value.has(k)?expandedCards.value.delete(k):expandedCards.value.add(k)
}

// ---- 自定义模块 ----
function startAddCustom(){
  showAddMenu.value=false
  ElMessageBox.prompt('请输入自定义模块名称','添加自定义模块',{
    confirmButtonText:'添加',cancelButtonText:'取消',inputPlaceholder:'如：获奖经历、志愿服务等',
    inputValidator:v=>(!v||!v.trim())?'名称不能为空':true,
  }).then(({value})=>{
    const cm={ _id:uid(), title:value.trim(), content:'' }
    localData.customModules.push(cm)
    switchSection(cm._id)
    emitUpdate()
    ElMessage.success(`已创建自定义模块「${cm.title}」`)
  }).catch(()=>{})
}
function editCustomTitle(cm){
  ElMessageBox.prompt('修改模块名称','编辑',{
    confirmButtonText:'保存',cancelButtonText:'取消',inputValue:cm.title||'',
  }).then(({value})=>{
    cm.title=value.trim(); emitUpdate()
  }).catch(()=>{})
}
function removeCustomModule(cmId){
  const idx=localData.customModules.findIndex(c=>c._id===cmId)
  if(idx!==-1){
    const cm=localData.customModules[idx]
    ElMessageBox.confirm(`确定要删除自定义模块「${cm.title||'未命名'}」吗？`,`确认删除`,{
      type:'warning',confirmButtonText:'删除',cancelButtonText:'取消',
    }).then(()=>{
      localData.customModules.splice(idx,1)
      if(currentSection.value===cmId) switchSection('basic')
      emitUpdate()
      ElMessage.success('已删除')
    }).catch(()=>{})
  }
}

// ---- 拖拽排序：模块 ----
let dragModId=null, dragModIdx=-1
function onDragStart(e, id, idx){
  dragModId=id; dragModIdx=idx
  e.dataTransfer.effectAllowed='move'
  e.dataTransfer.setData('text/plain', id)
  // 区分是标准模块还是自定义模块
  const isCustom = localData.customModules.some(cm => cm._id === id)
  e.dataTransfer.setData('application/is-custom', String(isCustom))
}
function onDragOver(e, idx){
  e.currentTarget.classList['drag-over']=true
}
function onDrop(e, toIdx){
  e.preventDefault()
  e.currentTarget.classList['drag-over']=false
  if (dragModId===null || dragModIdx===toIdx) return

  const isCustom = e.dataTransfer.getData('application/is-custom') === 'true'

  if (isCustom) {
    // 自定义模块在 customModules 数组内移动
    const arr = localData.customModules
    const fromIdx = arr.findIndex(cm => cm._id === dragModId)
    if (fromIdx === -1) return
    // 计算在 visibleNavModules 中的位置映射
    const customStartIdx = enabledModuleIds.value.length // 标准模块之后的起始位置
    const realFrom = fromIdx
    // 找到目标位置对应的customModules索引
    const visibleCustomCount = arr.length
    // 在visibleNavModules中的toIdx对应customModules中的哪个位置？
    // 需要重新计算
    const [moved] = arr.splice(fromIdx, 1)
    // toIdx 是在 visibleNavModules 中的位置，需要转换
    let targetRealIdx = toIdx - customStartIdx
    if (targetRealIdx < 0) targetRealIdx = 0
    if (targetRealIdx > arr.length) targetRealIdx = arr.length
    arr.splice(targetRealIdx, 0, moved)
  } else {
    // 标准模块在 enabledModuleIds 内移动
    const [moved] = enabledModuleIds.value.splice(dragModIdx, 1)
    enabledModuleIds.value.splice(toIdx, 0, moved)
    emit('reorderModules', [...enabledModuleIds.value])
  }

  dragModId=null; dragModIdx=-1
  emitUpdate()
}

// ---- 拖拽排序：基础字段 ----
let dragFieldKey=null
function onFieldDragStart(e, key){
  dragFieldKey=key
  e.dataTransfer.effectAllowed='move'
  e.dataTransfer.setData('text/plain', key)
}
function onFieldDrop(e, targetKey){
  e.preventDefault()
  if (!dragFieldKey || dragFieldKey===targetKey) return
  const fromIdx=basicFields.value.findIndex(f=>f.key===dragFieldKey)
  const toIdx=basicFields.value.findIndex(f=>f.key===targetKey)
  if(fromIdx!==-1&&toIdx!==-1){
    const [moved]=basicFields.value.splice(fromIdx,1)
    basicFields.value.splice(toIdx,0,moved)
    emitUpdate()
  }
  dragFieldKey=null
}

function emitUpdate(){
  // 标记为内部更新，防止 watcher 循环覆盖
  isInternalUpdate = true
  nextTick(() => { isInternalUpdate = false })

  // 将 enabledModuleIds、basicFields 和字段级字体样式一起传出去
  const payload = JSON.parse(JSON.stringify(localData))
  payload._meta = {
    enabledModuleIds: [...enabledModuleIds.value],
    basicFieldKeys: [...basicFields.value, ...customFields.value].map(f => f.key),
    // 字段级字体样式：{ name: { fontSize, fontWeight, color }, email: {...}, ... }
    fieldStyles: {},
    // 非基础信息字段的字体样式
    nonBasicFieldStyles: {},
  }
  for (const bf of [...basicFields.value, ...customFields.value]) {
    if (bf.fontSize || bf.fontWeight || bf.color || bf.lineHeight || bf.letterSpacing ||
        bf.fontStyle || bf.textDecoration || bf.marginTop || bf.marginBottom || bf.paddingLeft) {
      payload._meta.fieldStyles[bf.key] = {
        fontSize: bf.fontSize || 10,
        fontWeight: bf.fontWeight || 'normal',
        color: bf.color || '#333333',
        lineHeight: bf.lineHeight || 1.6,
        letterSpacing: bf.letterSpacing || 0,
        fontStyle: bf.fontStyle || '',
        textDecoration: bf.textDecoration || '',
        marginTop: bf.marginTop || 0,
        marginBottom: bf.marginBottom || 0,
        paddingLeft: bf.paddingLeft || 0,
      }
    }
  }
  // 非基础信息字段的字体样式
  for (const [fk, fs] of Object.entries(nonBasicFieldStyles.value)) {
    payload._meta.nonBasicFieldStyles[fk] = { ...fs }
  }
  emit('update', payload)
}

function deepMerge(t,s){
  const r={...t}
  for(const k in s){
    if(s[k]&&typeof s[k]==='object'&&!Array.isArray(s[k])) r[k]=deepMerge(r[k]||{},s[k])
    else if(Array.isArray(s[k])) r[k]=s[k].map(i=>i&&typeof i==='object'?{...i}:i)
    else r[k]=s[k]
  }
  return r
}

// 拖拽调整宽度
let resizing=false,sx=0,sw=0
function onResizeStart(e){
  resizing=true;sx=e.clientX;sw=panelWidth.value
  document.addEventListener('mousemove',onMove);document.addEventListener('mouseup',onUp);e.preventDefault()
}
function onMove(e){
  if(!resizing)return
  const d=((e.clientX-sx)/window.innerWidth)*100
  panelWidth.value=Math.round(Math.min(Math.max(sw+d,25),55))
}
function onUp(){
  resizing=false;document.removeEventListener('mousemove',onMove);document.removeEventListener('mouseup',onUp)}

defineExpose({ resetAiExtract })
</script>

<style scoped>
/* ==================== 主容器 ==================== */
.editor-form-panel{position:relative;height:100%;background:#fff;border-right:1px solid #e8e8e8;display:flex;flex-direction:column;min-width:280px;max-width:55%;user-select:none;overflow-y:auto;overflow-x:hidden;}
.editor-form-panel::-webkit-scrollbar{width:5px;}
.editor-form-panel::-webkit-scrollbar-thumb{background:#ddd;border-radius:3px;}
.editor-form-panel::-webkit-scrollbar-thumb:hover{background:#bbb;}
.resize-handle{position:absolute;top:0;right:-4px;width:8px;height:100%;cursor:col-resize;z-index:10;}
.resize-handle:hover{background:rgba(102,126,234,0.25);}

/* ==================== 导航区 ==================== */
.module-nav{padding:0;border-bottom:1px solid #e0e0e0;background:#fafbfc;}
.nav-header{display:flex;align-items:center;gap:5px;font-size:11px;color:#999;font-weight:600;letter-spacing:1px;margin-bottom:8px;padding-left:2px;}

/* 可收起栏通用样式 */
.collapsible-header{
  display:flex;align-items:center;justify-content:space-between;
  padding:10px 14px;cursor:pointer;user-select:none;
  background:#f5f6fa;border-bottom:1px solid #e8e8e8;
  transition:background .15s;
}
.collapsible-header:hover{background:#eef0f5;}
.collapse-title{
  display:flex;align-items:center;gap:6px;
  font-size:12px;font-weight:700;color:#555;letter-spacing:.5px;
}
.collapse-arrow{
  font-size:10px;color:#aaa;transition:transform .2s ease;display:inline-block;
}
.collapse-arrow.rotated{transform:rotate(-90deg);}
.collapse-body{padding:12px;}

/* 编辑区收起栏特有样式 */
.editor-collapse-header{
  background:#fff;border-bottom:1px solid #e8e8e8;
  position:sticky;top:0;z-index:5;
}

/* 导航项 */
.nav-item{display:flex;align-items:center;gap:5px;padding:7px 8px;border-radius:6px;cursor:pointer;transition:all .15s ease;font-size:13px;color:#444;margin-bottom:2px;border:1.5px solid transparent;position:relative;}
.nav-item:hover{background:#f0f2ff;border-color:#d0daf9;}
.nav-item.active{background:#eef2ff;border-color:#667eea;color:#1a1a2e;font-weight:600;box-shadow:0 1px 3px rgba(102,126,234,.1);}
.nav-item[draggable="true"]{cursor:grab;}

/* 拖拽手柄 */
.drag-handle{width:18px;height:18px;display:flex;align-items:center;justify-content:center;color:#bbb;cursor:grab;flex-shrink:0;transition:color .12s;}
.drag-handle:hover{color:#999;}
.drag-handle:active{cursor:grabbing;}

/* 图标+标签 */
.nav-icon{width:20px;height:20px;display:flex;align-items:center;justify-content:center;flex-shrink:0;}
.nav-item.active .nav-icon :deep(svg){stroke:#667eea;}
.nav-item:hover .nav-icon{opacity:.85;}
.nav-label{flex:1;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}

/* 操作按钮 */
.nav-action{width:26px;height:26px;border:none;border-radius:5px;background:none;cursor:pointer;display:flex;align-items:center;justify-content:center;opacity:0;transition:all .15s ease;color:#888;flex-shrink:0;}
.nav-item:hover .nav-action{opacity:1;}
.eye-btn:hover{background:#e3f2fd;color:#1976D2;}
.del-btn:hover{background:#fef0f0;color:#f56c6c;}

/* 添加模块 */
.add-module-wrapper{position:relative;margin-top:6px;}
.nav-add-module{width:100%;padding:6px 0;border:1.5px dashed #ddd;border-radius:6px;background:none;font-size:12px;color:#999;cursor:pointer;transition:all .15s ease;}
.nav-add-module:hover{border-color:#667eea;color:#667eea;background:#faf5ff;}

/* 添加下拉菜单 */
.add-module-menu{background:#fff;border:1px solid #e0e0e0;border-radius:8px;box-shadow:0 4px 16px rgba(0,0,0,.1);padding:6px 0;margin-top:4px;z-index:50;}
.add-opt-item{display:flex;align-items:center;gap:8px;padding:8px 14px;font-size:13px;color:#444;cursor:pointer;transition:background .12s;}
.add-opt-item:hover{background:#f0f2ff;color:#667eea;}
.add-opt-icon{width:18px;height:18px;display:flex;align-items:center;justify-content:center;flex-shrink:0;}
.add-opt-divider{height:1px;background:#eee;margin:4px 12px;}
.add-opt-item.add-custom{color:#667eea;font-weight:500;}
.add-opt-item.add-custom:hover{background:#f8f6ff;}
.add-opt-empty{padding:10px 14px;font-size:12px;color:#bbb;text-align:center;}

.fade-down-enter-active,.fade-down-leave-active{transition:all .2s ease;}
.fade-down-enter-from,.fade-down-leave-to{opacity:0;transform:translateY(-6px);}

/* 区域标题 */
.nav-section-title{font-size:11px;color:#999;font-weight:600;letter-spacing:.5px;margin:12px 0 6px 2px;}

/* 主题色 */
.color-row{display:flex;align-items:center;gap:6px;flex-wrap:wrap;}
.color-dot{width:20px;height:20px;border-radius:50%;border:2px solid transparent;cursor:pointer;transition:all .15s ease;flex-shrink:0;}
.color-dot.active{border-color:#333;box-shadow:0 0 0 2px #fff,0 0 0 4px #ccc;}
.color-dot:hover{transform:scale(1.15);}
.custom-color-label{font-size:11px;color:#bbb;margin-left:2px;}



/* 排版（导航区旧样式保留兼容）*/
.setting-row{display:flex;align-items:center;gap:8px;margin-bottom:8px;font-size:12px;color:#666;}
.setting-row label{white-space:nowrap;min-width:30px;font-weight:500;}
.setting-select{flex:1;border:1px solid #ddd;border-radius:5px;padding:4px 6px;font-size:12px;background:#fff;color:#444;outline:none;transition:border-color .15s;}
.setting-select:focus{border-color:#667eea;}
.setting-row input[type="range"]{flex:1;height:4px;accent-color:#667eea;border-radius:2px;}
.range-val{font-size:11px;color:#999;min-width:22px;text-align:right;font-variant-numeric:tabular-nums;}

/* ==================== 编辑区 ==================== */
.panel-scroll{padding:16px 18px;}

.form-section{animation:fadeIn .2s ease;}
@keyframes fadeIn{from{opacity:0;transform:translateY(4px);}to{opacity:1;transform:translateY(0);}}

.form-section-header{display:flex;align-items:center;justify-content:space-between;padding-bottom:10px;margin-bottom:14px;border-bottom:1px solid #f0f0f0;}
.form-section-header h3{font-size:16px;font-weight:700;color:#1a1a2e;margin:0;}
.header-right-btns{display:flex;align-items:center;gap:6px;}
.header-edit-btn{width:28px;height:28px;border:1px solid #e0e0e0;border-radius:6px;background:#fff;cursor:pointer;display:flex;align-items:center;justify-content:center;color:#888;transition:all .15s ease;}
.header-edit-btn:hover{border-color:#667eea;color:#667eea;background:#f8f6ff;}
.header-del-btn{width:28px;height:28px;border:1px solid #e0e0e0;border-radius:6px;background:#fff;cursor:pointer;display:flex;align-items:center;justify-content:center;color:#aaa;transition:all .15s ease;}
.header-del-btn:hover{border-color:#f56c6c;color:#f56c6c;background:#fef0f0;}

/* 求职意向只读展示 */
.readonly-badge{
  font-size:10px;color:#fff;background:#4CAF50;padding:1px 8px;border-radius:10px;font-weight:600;
  letter-spacing:.5px;
}
.intention-readonly{padding:4px 0;}
.ir-tip{
  font-size:11px;color:#999;line-height:1.6;margin-bottom:12px;padding:8px 10px;
  background:#fafafa;border-radius:6px;border-left:3px solid #4CAF50;
}
.ir-grid{display:grid;grid-template-columns:1fr 1fr;gap:8px;}
.ir-item{
  display:flex;align-items:center;gap:6px;padding:8px 10px;background:#f8f9fa;border-radius:6px;border:1px solid #eee;
}
.ir-label{font-size:11px;color:#888;white-space:nowrap;min-width:52px;}
.ir-value{font-size:13px;color:#333;font-weight:500;}

/* 分组标题 */
.field-group-title{font-size:12px;font-weight:700;color:#555;margin:14px 0 8px;padding-left:6px;border-left:3px solid #667eea;line-height:1.2;display:flex;align-items:center;justify-content:space-between;}

/* 添加字段按钮 */
.add-field-btn{font-size:11px;font-weight:600;color:#667eea;background:none;border:1px solid #667eea33;border-radius:10px;padding:1px 8px;cursor:pointer;transition:all .15s ease;white-space:nowrap;}
.add-field-btn:hover{background:#667eea;color:#fff;}

/* 字段选择器 */
.field-picker{border:1px solid #e0e0e0;border-radius:8px;background:#fff;padding:6px 0;margin-bottom:10px;box-shadow:0 2px 8px rgba(0,0,0,.06);}
.picker-empty{padding:8px 14px;font-size:12px;color:#bbb;text-align:center;}
.picker-item{display:flex;align-items:center;gap:8px;padding:7px 14px;font-size:12px;color:#555;cursor:pointer;transition:background .12s;}
.picker-item:hover{background:#f0f2ff;color:#667eea;}
.picker-icon{width:16px;height:16px;display:flex;align-items:center;justify-content:center;color:#667eea;font-size:14px;flex-shrink:0;}

/* 头像 */
.avatar-row{display:flex;align-items:center;gap:12px;margin-bottom:10px;}
.avatar-preview{width:64px;height:76px;border-radius:6px!important;border:2px dashed #ddd;overflow:hidden;display:flex;align-items:center;justify-content:center;background:#f8f9fa;flex-shrink:0;background-size:cover;background-position:center;}
.avatar-preview img{width:100%;height:100%;object-fit:cover;border-radius:6px;}
.avatar-placeholder{font-size:11px;color:#bbb;text-align:center;}
.avatar-actions{display:flex;align-items:center;gap:6px;flex:1;}
.avatar-upload-btn{padding:5px 12px;border:1px solid #667eea;border-radius:6px;background:#f8f6ff;color:#667eea;font-size:12px;cursor:pointer;transition:all .15s ease;white-space:nowrap;}
.avatar-upload-btn:hover{background:#667eea;color:#fff;}
.icon-action-btn{width:30px;height:30px;border:1px solid #e0e0e0;border-radius:6px;background:#fff;cursor:pointer;display:flex;align-items:center;justify-content:center;color:#888;transition:all .15s ease;flex-shrink:0;}
.icon-action-btn:hover{border-color:#667eea;color:#667eea;}
.icon-action-btn.danger:hover{border-color:#f56c6c;color:#f56c6c;background:#fef0f0;}
.icon-action-btn.sm{width:26px;height:26px;}
.icon-action-btn.xs{width:22px;height:22px;}

/* 基础字段两列网格 */
.field-grid{
  display:grid;grid-template-columns:1fr 1fr;gap:8px;
}
/* 基础信息字体编辑栏：独占整行，显示在字段下方 */
.field-grid > .field-font-bar{
  grid-column: 1 / -1;
}
.field-cell{
  display:flex;flex-wrap:wrap;align-items:center;gap:4px;padding:6px 8px;border:1px solid #e8e8e8;border-radius:6px;
  background:#fff;transition:all .15s ease;position:relative;
}
.field-cell:hover{border-color:#c0c8e0;background:#fafbff;}
.field-cell.focused{border-color:#667eea;background:#f8f6ff;box-shadow:0 0 0 2px rgba(102,126,234,.1);}
.field-cell .el-input{flex:1;min-width:0;}
.field-cell .field-label{font-size:11px;color:#888;font-weight:500;min-width:32px;flex-shrink:0;white-space:nowrap;}

/* 字段级字体调整栏 — 紧凑型，不遮挡下方输入框 */
.field-font-bar{
  width:100%;background:#f0f2ff;border:1px solid #d0daf9;border-radius:6px;
  padding:5px 8px;margin-top:4px;z-index:2;
}
.font-bar-fade-enter-active,.font-bar-fade-leave-active{transition:all .18s ease;}
.font-bar-fade-enter-from,.font-bar-fade-leave-to{opacity:0;transform:translateY(-4px);}
.ff-row{display:flex;align-items:center;gap:6px;margin-bottom:4px;font-size:11px;color:#666;}
.ff-row:last-child{margin-bottom:0;}
.ff-row label{white-space:nowrap;min-width:28px;font-weight:500;}
.ff-row input[type="range"]{flex:1;height:3px;accent-color:#667eea;}
.ff-select{border:1px solid #ddd;border-radius:4px;padding:2px 4px;font-size:11px;background:#fff;color:#444;outline:none;}
.ff-select:focus{border-color:#667eea;}
.ff-color{width:24px;height:22px;border:1px solid #ddd;border-radius:4px;cursor:pointer;padding:0;}
.ff-select-sm{border:1px solid #ddd;border-radius:4px;padding:2px 4px;font-size:11px;background:#fff;color:#444;outline:none;width:auto;}
.ff-select-sm:focus{border-color:#667eea;}
.ff-style-btn{width:24px;height:22px;border:1px solid #ddd;border-radius:4px;background:#fff;cursor:pointer;font-size:12px;font-family:serif;color:#666;display:flex;align-items:center;justify-content:center;transition:all .12s;}
.ff-style-btn:hover{border-color:#667eea;color:#667eea;}
.ff-style-btn.active{background:#667eea;color:#fff;border-color:#667eea;}
.ff-style-btn i{font-style:italic;}
.ff-num-input{width:42px;border:1px solid #ddd;border-radius:4px;padding:2px 4px;font-size:11px;text-align:center;outline:none;}
.ff-num-input:focus{border-color:#667eea;}

/* 头像样式设置 */
.avatar-style-settings{
  background:#f8f9fa;border:1px solid #e8e8e8;border-radius:8px;
  padding:10px 12px;margin-bottom:12px;
}
.style-setting-row{
  display:flex;align-items:center;gap:6px;margin-bottom:8px;font-size:12px;
}
.style-setting-row:last-child{margin-bottom:0;}
.style-setting-row label{
  font-size:12px;color:#666;font-weight:500;white-space:nowrap;min-width:32px;
}
.style-input-sm{
  width:60px;border:1px solid #ddd;border-radius:4px;padding:3px 6px;
  font-size:12px;text-align:center;outline:none;transition:border-color .15s;
}
.style-input-sm:focus{border-color:#667eea;}
.style-unit{font-size:11px;color:#999;}
.style-select-sm{
  flex:1;border:1px solid #ddd;border-radius:4px;padding:4px 6px;
  font-size:12px;color:#444;background:#fff;outline:none;cursor:pointer;transition:border-color .15s;
}
.style-select-sm:focus{border-color:#667eea;}

/* 字段行 */
.field-line{display:flex;align-items:center;gap:6px;margin-bottom:10px;}
.field-drag-handle{width:16px;font-size:12px;color:#ccc;cursor:grab;flex-shrink:0;text-align:center;user-select:none;transition:color .12s;}
.field-drag-handle:hover{color:#999;}
.field-label{font-size:12px;color:#666;font-weight:500;min-width:42px;flex-shrink:0;white-space:nowrap;}

/* 表单通用 */
.form-item{margin-bottom:12px;}
.form-item:last-child{margin-bottom:0;}
.form-item label{display:block;font-size:12px;color:#666;margin-bottom:4px;font-weight:500;}
.form-row{display:flex;gap:10px;}
.form-row .form-item{flex:1;min-width:0;}
.form-half{min-width:0;}

.current-item-name{font-size:14px;font-weight:600;color:#333;padding:8px 10px;background:#f8f9fa;border-radius:6px;margin-bottom:10px;}
.btn-add-card-dark{width:100%;padding:10px 0;border:1.5px dashed #c0c4cc;border-radius:8px;background:linear-gradient(135deg,#f8f9fa,#f0f1f3);font-size:13px;color:#555;cursor:pointer;margin-bottom:12px;transition:all .2s ease;display:flex;align-items:center;justify-content:center;gap:6px;}
.btn-add-card-dark:hover{border-color:#667eea;color:#667eea;background:linear-gradient(135deg,#f8f6ff,#ede7ff);box-shadow:0 2px 8px rgba(102,126,234,.12);}

/* 卡片 */
.list-card{border:1px solid #eaeaea;border-radius:10px;padding:14px;margin-bottom:12px;background:#fff;transition:box-shadow .15s ease;}
.list-card:hover{box-shadow:0 2px 8px rgba(0,0,0,.05);}
.list-card-head{display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;}
.list-card-head.clickable{cursor:pointer;user-select:none;}
.card-expand-icon{font-size:10px;color:#999;transition:transform .2s ease;margin-right:4px;display:inline-block;}
.card-expand-icon.expanded{transform:rotate(180deg);}
.card-title-text{flex:1;font-size:13px;font-weight:600;color:#333;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;}
.card-index{font-size:11px;color:#999;background:#f0f0f0;padding:2px 8px;border-radius:10px;font-weight:600;}
.card-del{font-size:11px;color:#f56c6c;background:none;border:1px solid #f56c6c22;border-radius:4px;padding:2px 8px;cursor:pointer;transition:all .15s ease;white-space:nowrap;}
.card-del:hover{background:#fef0f0;border-color:#f56c6c;}
.edu-card .card-title-text{font-size:13px;color:#1a1a2e;}
.card-body{padding-top:4px;border-top:1px solid #f5f5f5;margin-top:8px;}

/* 富文本工具栏 */
.rich-toolbar{display:flex;align-items:center;gap:6px;padding:7px 10px;border:1px solid #e8e8e8;border-radius:7px;margin-bottom:10px;background:#fafafa;}
.rich-toolbar.compact{padding:5px 8px;margin-bottom:8px;}
.toolbar-left{display:flex;align-items:center;gap:3px;flex:1;}
.tb-btn{width:26px;height:26px;border:1px solid #ddd;border-radius:4px;background:#fff;cursor:pointer;display:flex;align-items:center;justify-content:center;font-size:12px;color:#666;transition:all .12s ease;padding:0;}
.tb-btn:hover{border-color:#667eea;color:#667eea;background:#f8f6ff;}
.toolbar-second{margin-bottom:10px;margin-top:-4px;}
.ai-polish-btn{margin-left:auto;padding:4px 12px;border:none;border-radius:14px;background:linear-gradient(135deg,#ff9800,#f57c00);color:#fff;font-size:12px;font-weight:600;cursor:pointer;display:flex;align-items:center;gap:4px;white-space:nowrap;transition:all .2s ease;box-shadow:0 1px 4px rgba(255,152,0,.3);}
.ai-polish-btn:hover{transform:translateY(-1px);box-shadow:0 3px 8px rgba(255,152,0,.4);}
.ai-polish-btn.small{padding:3px 10px;font-size:11px;border-radius:12px;}
.ai-icon{font-size:13px;}

/* 日期+至今 */
.date-with-switch{display:flex;align-items:center;gap:6px;flex:1;min-width:0;}
.now-switch{font-size:11px;color:#666;white-space:nowrap;display:flex;align-items:center;gap:3px;cursor:pointer;user-select:none;}
.now-switch input[type="checkbox"]{accent-color:#667eea;cursor:pointer;}

/* Element Plus 覆盖 */
:deep(.el-input__wrapper){border-radius:5px;box-shadow:0 0 0 1px #dcdfe6 inset;transition:box-shadow .15s;}
:deep(.el-input__wrapper:hover){box-shadow:0 0 0 1px #c0c4cc inset;}
:deep(.el-input__wrapper.is-focus){box-shadow:0 0 0 1px #667eea inset;}
:deep(.el-textarea__inner){border-radius:5px;}
:deep(.el-select){width:100%;}
:deep(.el-input--small){--el-input-height:32px;}
:deep(.el-date-editor.el-input){--el-input-height:32px;}
:deep(.el-date-editor .el-input__wrapper){width:100%;}

/* AI 智能提取 */
.ai-extract-box{
  margin:16px 0 0;border:1px solid #e0d8f0;border-radius:8px;overflow:hidden;
  background:linear-gradient(135deg,#f8f6ff,#f0edff);
}
.ai-extract-header{
  display:flex;align-items:center;justify-content:space-between;padding:10px 14px;
  cursor:pointer;user-select:none;transition:background .15s;
}
.ai-extract-header:hover{background:rgba(102,126,234,.06);}
.ai-extract-title{
  font-size:13px;font-weight:600;color:#5b4d9a;display:flex;align-items:center;gap:6px;
}
.ai-extract-title svg{color:#7c6bc0;}
.ai-extract-arrow{
  font-size:10px;color:#999;transition:transform .2s;
}
.ai-extract-arrow.rotated{transform:rotate(180deg);}
.ai-extract-body{padding:0 14px 14px;}
.ai-extract-tip{
  font-size:11px;color:#999;margin:0 0 10px;line-height:1.5;
}
.ai-extract-btn{
  margin-top:10px;width:100%;padding:8px 0;border:none;border-radius:6px;
  background:linear-gradient(135deg,#667eea,#5a4fcf);color:#fff;
  font-size:13px;font-weight:600;cursor:pointer;display:flex;align-items:center;
  justify-content:center;gap:6px;transition:all .2s;
}
.ai-extract-btn:hover:not(:disabled){
  transform:translateY(-1px);box-shadow:0 3px 10px rgba(102,126,234,.4);
}
.ai-extract-btn:disabled{opacity:.55;cursor:not-allowed;}
.ai-extract-loading{
  width:14px;height:14px;border:2px solid rgba(255,255,255,.3);border-top-color:#fff;
  border-radius:50%;animation:spin .6s linear infinite;
}
@keyframes spin{to{transform:rotate(360deg);}}
</style>
