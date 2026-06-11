<template>
  <div class="editor-page">
    <!-- 顶部工具栏 -->
    <div class="editor-toolbar">
      <div class="toolbar-left">
        <button class="toolbar-btn back-btn" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M10 12L6 8l4-4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          返回工作台
        </button>
        <span class="toolbar-brand">链速resume</span>
        <span class="toolbar-divider"></span>
        <input v-model="resumeTitle" class="title-input" placeholder="简历标题" @blur="saveTitle" />
        <span class="auto-save-hint">自动保存中...</span>
      </div>
      <div class="toolbar-right">
        <button class="toolbar-btn save-btn" :disabled="saving" @click="handleSave">
          <svg v-if="!saving" width="15" height="15" viewBox="0 0 15 15" fill="none"><path d="M12 3.75L5.25 10.5L3 8.25" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          <span v-else class="spin"></span>
          保存
        </button>
        <button class="toolbar-btn export-btn" @click="handleExport">
          <svg width="15" height="15" viewBox="0 0 15 15" fill="none"><path d="M7.5 2v7m0 0l-2.5-2.5M7.5 9l2.5-2.5M2.5 12h10" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round"/></svg>
          导出
        </button>
      </div>
    </div>

    <!-- 主内容区：左表单 + 右预览 -->
    <div class="editor-body">
      <!-- 左侧：表单编辑面板 -->
      <transition name="slide-left">
        <EditorFormPanel
          v-if="showForm"
          ref="formPanelRef"
          :resumeData="formData"
          :activeSection="activeSection"
          :enabledModuleIds="enabledModuleIds"
          @update="onFormDataUpdate"
          @sectionChange="onSectionChangeFromPanel"
          @setColor="(c) => themeColor = c"
          @setFont="(f) => fontFamily = f"
          @setLineHeight="(lh) => lineHeight = lh"
          @setFontSize="(fs) => fontSize = fs"
          @addModule="onAddModule"
          @toggleVisibility="onToggleVisibility"
          @deleteModule="onDeleteModule"
          @aiPolish="onAiPolish"
          @aiExtract="onAiExtract"
          @avatarUpload="onAvatarUpload"
          @reorderModules="onReorderModulesFromPanel"
        />
      </transition>

      <!-- 拖拽分割线 -->
      <div v-if="showForm" class="resize-divider" @mousedown="startDragResize"></div>

      <!-- 右侧：A4 预览区 -->
      <div class="preview-area" :class="{ 'full-width': !showForm }">
        <div class="preview-scroll" ref="previewScrollRef">
          <div class="preview-wrapper" :style="{ transform: `scale(${zoom / 100})`, transformOrigin: 'top center' }">
            <JianlibenTemplate
              ref="templateRef"
              :resumeData="formData"
              :themeColor="themeColor"
              :activeSection="activeSection"
              :hiddenSections="[...hiddenSections]"
              :avatarUrl="avatarUrl"
              :fontFamily="fontFamily"
              :lineHeight="lineHeight"
              :fontSize="fontSize"
              :enabledModuleIds="enabledModuleIds"
              :fieldStyles="fieldStyles"
              :nonBasicFieldStyles="nonBasicFieldStyles"
              :basicFieldOrder="basicFieldOrder"
              @sectionClick="onSectionClickFromPreview"
              @reorderSections="onReorderSectionsFromCanvas"
            />
          </div>
        </div>
        <!-- 缩放控制（底部浮动） -->
        <div class="zoom-bar">
          <button class="zb-btn" @click="zoomOut" title="缩小">−</button>
          <span class="zb-val">{{ zoom }}%</span>
          <button class="zb-btn" @click="zoomIn" title="放大">+</button>
          <span class="zb-sep"></span>
          <button class="zb-btn zb-reset" @click="zoom = 90; zoom = 100" title="重置">⟲</button>
        </div>
      </div>
    </div>

    <!-- 右侧浮动工具栏 -->
    <EditorFloatingBar
      :showForm="showForm"
      @toggle-form="showForm = !showForm"
      @switch-template="showTemplateDialog = true"
      @ai-analyze="onAiAnalyze"
      @export-pdf="handleExport"
      @copy-resume="handleCopyResume"
      @print="handlePrint"
      @fullscreen="toggleFullscreen"
      @back-to-dashboard="$router.push('/dashboard')"
    />

    <!-- 切换模板弹窗 -->
    <el-dialog v-model="showTemplateDialog" title="切换模板" width="720px" top="6vh" destroy-on-close>
      <div class="tpl-dialog-grid">
        <div
          v-for="tpl in templateOptions"
          :key="tpl.id"
          class="tpl-dialog-card"
          :class="{ selected: currentTemplateId === String(tpl.id) }"
          @click="switchTemplate(tpl)"
        >
          <img :src="tpl.img" :alt="tpl.name" loading="lazy" />
          <span class="tpl-d-name">{{ tpl.name }}</span>
        </div>
      </div>
    </el-dialog>

    <!-- AI 分析结果弹窗 -->
    <el-dialog v-model="showAiResult" title="AI 简历分析" width="600px" top="6vh">
      <div class="ai-result-content" v-html="aiResultHtml"></div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getResumeById, updateResume } from '../api/resume'
import { aiPolish as apiAiPolish, aiGenerate as apiAiGenerate } from '../api/ai'
import { useResumeStore } from '../stores/resume'
import { useAiConfigStore } from '../stores/aiConfig'
import { exportToPdf } from '../utils/exportPdf'
import { JIANLIBEN_TEMPLATES } from '../data/jianlibenTemplates.js'
import EditorFormPanel from '../components/editor/EditorFormPanel.vue'
import JianlibenTemplate from '../components/templates/jianliben/JianlibenTemplate.vue'
import EditorFloatingBar from '../components/editor/EditorFloatingBar.vue'

const route = useRoute()
const router = useRouter()
const store = useResumeStore()
const aiStore = useAiConfigStore()
const resumeId = route.params.id

// ===== UI 状态 =====
const saving = ref(false)
const showForm = ref(true)
const zoom = ref(90)
const themeColor = ref('#333')
const resumeTitle = ref('我的简历')
const activeSection = ref('basic')
const hiddenSections = ref(new Set())
const avatarUrl = ref('')
const fontFamily = ref('default')
const lineHeight = ref(1.5)
const fontSize = ref(10)
const formPanelRef = ref(null)
const templateRef = ref(null)

// 已启用的模块ID列表（与 EditorFormPanel 同步）
const enabledModuleIds = ref(['basic', 'intention', 'education', 'experience', 'projects', 'skills', 'selfEvaluation'])

// 弹窗状态
const showTemplateDialog = ref(false)
const showAiResult = ref(false)
const aiResultHtml = ref('')
const currentTemplateId = ref('116')

// 模板选项（用于切换）
const templateOptions = JIANLIBEN_TEMPLATES.slice(0, 5)

// ===== 表单数据 =====
// 各模板的预填充示例数据
const TEMPLATE_DEFAULTS = {
  // 模板116：简小历（经典深蓝 #1a73e8）
  '116': () => ({
    basic: {
      name: '简小历', gender: '男', status: '应届生', birthday: '2006/09',
      email: 'user@jianliben.com', phone: '13366668888', avatar: '',
      age: '20岁', education: '本科', targetJob: '前端开发工程师', workYears: '', city: '深圳市',
      avatarWidth: 76, avatarHeight: 92, avatarRadius: 6, avatarPosition: 'right-top',
    },
    intention: { expectedSalary: '10K～11K', jobType: '全职', availableDate: '' },
    education: [
      { _id: uid(), school: '深圳职业技术大学', major: '软件技术', degree: '本科', gpa: '',
        startDate: '2024/09', endDate: '', isCurrent: true,
        description: '主修课程：Web前端开发、JavaScript程序设计、UI设计基础、数据库原理', courses: '' }
    ],
    experience: [],
    projects: [
      { _id: uid(), name: '在线简历编辑器', role: '前端开发',
        description: '一款基于Vue3的在线简历制作平台，支持多模板切换与实时预览',
        startDate: '2025/03', endDate: '', link: '',
        content: '• 使用 Vue3 + Element Plus 构建响应式编辑界面\n• 实现拖拽排序模块、字体样式调节等交互功能\n• 集成 AI 提取功能，自动解析文本填充简历字段\n• 支持导出 PDF 与打印功能' }
    ],
    skills: `• 前端框架：熟练使用 Vue3、React，了解 Next.js
• 开发语言：JavaScript (ES6+)、TypeScript、HTML5/CSS3
• 工具库：Element Plus、Ant Design、Tailwind CSS
• 工程化：Vite、Webpack、Git、npm/yarn
• 其他：有 Node.js 基础，能编写简单后端接口`,
    selfEvaluation: `• 热爱前端技术，关注行业动态，保持每周学习新技术习惯
• 具备良好的代码规范意识，注重组件复用与性能优化
• 性格开朗，善于团队沟通，能快速融入开发团队`,
    certificates: [], customModules: [], themeColor: '#1a73e8',
  }),
  // 模板117：简约简历（商务蓝 #2563EB，简洁大气风格）
  '117': () => ({
    basic: {
      name: '简小历', gender: '男', status: '在职', birthday: '1999/08',
      email: 'jianxl@sina.com', phone: '13777778888', avatar: '',
      age: '26岁', education: '本科', targetJob: '产品经理', workYears: '3年', city: '广州天河',
      avatarWidth: 76, avatarHeight: 92, avatarRadius: 6, avatarPosition: 'right-top',
    },
    intention: { expectedSalary: '15K～20K', jobType: '全职', availableDate: '' },
    education: [
      { _id: uid(), school: '华南理工大学', major: '信息管理与信息系统', degree: '本科', gpa: '',
        startDate: '2018/09', endDate: '2022/06', isCurrent: false,
        description: '主修课程：产品设计与原型、数据分析、用户体验研究、项目管理、商业模式分析', courses: '' }
    ],
    experience: [
      { _id: uid(), company: '腾讯科技', position: '产品经理',
        startDate: '2022/07', endDate: '',
        description: '负责微信支付商户平台的迭代优化与商家增长策略',
        content: '> 主导商户自助后台改版，将核心操作路径从7步缩减至3步，操作效率提升55%\n> 搭建商户分层运营体系，针对不同规模商家制定差异化服务方案\n> 协调设计、研发、运营等12人跨职能团队推进季度OKR落地\n> 通过用户调研与数据分析，推动3个核心功能的体验优化，NPS提升18分' }
    ],
    projects: [
      { _id: uid(), name: '智能客服中台', role: '产品负责人',
        description: '面向全业务的统一客服解决方案，整合多渠道接入与AI能力',
        startDate: '2024/02', endDate: '2024/10', link: '',
        content: '> 从0到1规划产品架构，覆盖工单系统、知识库、智能机器人三大模块\n> 接入大模型能力实现意图识别准确率92%，自动解决率提升至65%\n> 项目上线后日均处理咨询量50万+，人工成本降低40%\n> 输出PRD文档30+份，建立产品需求评审SOP流程' }
    ],
    skills: `> 产品工具：精通 Axure、Figma、XMind、Visio，能高效输出原型与文档
> 数据分析：熟练使用 SQL、Excel 透视表、Tableau 做业务数据分析
> 用户研究：掌握用户访谈、可用性测试、A/B Test 等研究方法
> 项目管理：熟悉 Scrum 敏捷开发流程，有 Jira/飞书项目管理经验`,
    selfEvaluation: `> 逻辑思维清晰，擅长从复杂业务场景中提炼核心问题与解决方案
> 数据驱动决策，习惯用数据验证假设而非凭直觉做判断
> 跨部门沟通能力强，能在技术与业务之间做好翻译与协调`,
    certificates: [
      { _id: uid(), name: 'NPDP 产品经理认证', issuer: 'PDMA', date: '2023/11' },
    ], customModules: [], themeColor: '#2563EB',
  }),
  // 模板129：深色简历
  '129': () => ({
    basic: {
      name: '简小历', gender: '男', status: '应届生', birthday: '2006/09',
      email: 'user@jianliben.com', phone: '13366668888', avatar: '',
      age: '20岁', education: '本科', targetJob: '意向岗位', workYears: '', city: '深圳市',
      avatarWidth: 76, avatarHeight: 92, avatarRadius: 6, avatarPosition: 'right-top',
    },
    intention: { expectedSalary: '10K～11K', jobType: '全职', availableDate: '' },
    education: [
      { _id: uid(), school: 'XX大学', major: 'XX专业', degree: '本科', gpa: '成绩优异的话可在这里填写成绩排名及GPA信息',
        startDate: '2014/09', endDate: '2017/06', isCurrent: false,
        description: '主修课程：尽量填写和意向岗位相关的主修课程', courses: '' }
    ],
    experience: [
      { _id: uid(), company: 'XX公司', position: 'XX岗位',
        startDate: '2017/03', endDate: '2018/03',
        description: '如有必要，这里可对公司做简单的介绍',
        content: '• 你负责做了什么事情，运用了哪些工具和方法，取得了什么成果，分条陈列' }
    ],
    projects: [
      { _id: uid(), name: 'XX项目', role: 'XX角色',
        description: '如有必要，这里可对项目做简单的介绍',
        startDate: '2016/03', endDate: '2017/03', link: '',
        content: '• 你负责做了什么事情，运用了哪些工具和方法，取得了什么成果，分条陈列' }
    ],
    skills: `1、能熟练使用XX工具做XX工作
2、有XX技能实践经验，能完成XX工作
3、取得XX证书，能熟练处理XX工作`,
    selfEvaluation: `1、个人技能、知识描述
2、个人能力、特长描述
3、个人性格、特质描述`,
    certificates: [], customModules: [], themeColor: '#212121',
  }),
  // 模板118：清新简历（绿色 #4CAF50，清爽简洁风格）
  '118': () => ({
    basic: {
      name: '简小历', gender: '男', status: '在校', birthday: '2005/06',
      email: 'jianxiaoli@163.com', phone: '13888886666', avatar: '',
      age: '21岁', education: '本科', targetJob: 'UI设计师', workYears: '', city: '杭州',
      avatarWidth: 76, avatarHeight: 92, avatarRadius: 6, avatarPosition: 'right-top',
    },
    intention: { expectedSalary: '8K～10K', jobType: '全职', availableDate: '随时到岗' },
    education: [
      { _id: uid(), school: '浙江理工大学', major: '视觉传达设计', degree: '本科', gpa: 'GPA 3.7/4.0',
        startDate: '2022/09', endDate: '2026/06', isCurrent: true,
        description: '主修课程：平面构成、色彩设计、交互设计、品牌形象设计、网页设计', courses: '' }
    ],
    experience: [],
    projects: [
      { _id: uid(), name: '某电商平台UI改版', role: 'UI设计实习生',
        description: '负责移动端App界面视觉升级，提升用户体验',
        startDate: '2025/01', endDate: '2025/06', link: '',
        content: '- 独立完成首页、商品详情页等20+页面视觉设计\n- 建立统一的设计规范（字体/颜色/间距/组件库）\n- 配合开发团队完成设计还原，还原度达95%以上\n- 用户满意度提升15%，页面停留时长增加20%' }
    ],
    skills: `- 熟练掌握 Figma、Sketch、Adobe Photoshop、Illustrator 等设计工具
- 具备良好的审美能力和色彩搭配能力，熟悉 Material Design 设计规范
- 了解 HTML/CSS 基础，能与前端开发高效协作
- 有动效设计经验，能使用 After Effects 制作简单交互动效`,
    selfEvaluation: `- 热爱设计行业，对视觉趋势保持敏感度，定期浏览 Dribbble、Behance 获取灵感
- 性格开朗，善于沟通协作，具备良好的团队合作精神
- 学习能力强，能快速适应新工具和新流程，注重细节和用户体验`,
    certificates: [],
    customModules: [
      { _id: uid(), title: '兴趣爱好', content: '- 摄影：擅长人像与风景摄影，作品曾在校园影展展出\n- 手绘：坚持每周练习速写，有扎实的手绘功底\n- 旅行：已走过12个城市，喜欢用镜头记录生活' }
    ], themeColor: '#059669',
  }),
  // 模板119：稳重简历（蓝灰色 #607D8B，专业正式风格）
  '119': () => ({
    basic: {
      name: '简小历', gender: '男', status: '在职', birthday: '1998/03',
      email: 'jianxiaoli@outlook.com', phone: '13900001111', avatar: '',
      age: '28岁', education: '硕士', targetJob: '高级Java工程师', workYears: '5年', city: '上海浦东',
      avatarWidth: 76, avatarHeight: 92, avatarRadius: 6, avatarPosition: 'right-top',
    },
    intention: { expectedSalary: '25K～30K', jobType: '全职', availableDate: '' },
    education: [
      { _id: uid(), school: '华中科技大学', major: '软件工程', degree: '硕士', gpa: 'GPA 3.85/4.0 专业前10%',
        startDate: '2019/09', endDate: '2022/06', isCurrent: false,
        description: '研究方向：分布式系统与微服务架构 | 核心课程：高级软件工程、分布式计算、云计算架构', courses: '' }
    ],
    experience: [
      { _id: uid(), company: '蚂蚁集团', position: '后端开发工程师',
        startDate: '2022/07', endDate: '',
        description: '负责支付核心链路的系统设计与开发，支撑日均亿级交易流量',
        content: '1. 主导核心支付模块的重构工作，将系统响应时间从200ms优化至50ms以内\n2. 设计并实现分布式事务解决方案，保障资金交易的一致性与可靠性\n3. 参与双十一大促技术保障，系统可用性达到99.99%\n4. 带领3人小组完成新业务线的后端架构搭建与落地' }
    ],
    projects: [
      { _id: uid(), name: '统一网关平台', role: '技术负责人',
        description: '面向全公司的API网关服务，支持路由、限流、熔断、鉴权等能力',
        startDate: '2023/05', endDate: '2024/02', link: '',
        content: '1. 基于Netty框架自研高性能网关，QPS峰值突破50万\n2. 实现动态路由配置与热更新机制，配置变更秒级生效\n3. 接入 Sentinel 实现多维度限流与熔断降级策略\n4. 输出技术文档与最佳实践，推动20+业务团队接入使用' }
    ],
    skills: `1. 精通 Java / Spring Boot / Spring Cloud / MyBatis 等主流后端技术栈
2. 熟悉 MySQL、Redis、RocketMQ、Kafka 等中间件的原理与调优
3. 具备 Kubernetes / Docker 容器化部署与 CI/CD 流水线搭建经验
4. 熟悉高并发、高可用系统的架构设计，有大型分布式项目实战经验`,
    selfEvaluation: `1. 5年后端研发经验，具备扎实的计算机基础与优秀的工程能力
2. 有技术团队管理经验，善于跨团队沟通协调与技术方案评审
3. 对新技术保持热情，持续关注云原生与AI工程化领域的发展`,
    certificates: [
      { _id: uid(), name: '软件设计师（高级）', issuer: '中国计算机技术职业资格网', date: '2021/05' },
      { _id: uid(), name: 'CET-6（580分）', issuer: '教育部考试中心', date: '2019/12' },
    ], customModules: [], themeColor: '#374151',
  }),
  // 模板120：创意简历（粉色 #E91E63，活泼个性风格）
  '120': () => ({
    basic: {
      name: '简小历', gender: '女', status: '应届生', birthday: '2003/04',
      email: 'hello@jianliben.com', phone: '13666668888', avatar: '',
      age: '23岁', education: '本科', targetJob: '新媒体运营', workYears: '', city: '北京朝阳',
      avatarWidth: 76, avatarHeight: 92, avatarRadius: 6, avatarPosition: 'right-top',
    },
    intention: { expectedSalary: '9K～12K', jobType: '全职', availableDate: '一周内到岗' },
    education: [
      { _id: uid(), school: '中国传媒大学', major: '网络与新媒体', degree: '本科', gpa: '',
        startDate: '2020/09', endDate: '2024/06', isCurrent: false,
        description: '主修课程：数字营销、内容创作、短视频制作、用户运营、数据分析', courses: '' }
    ],
    experience: [
      { _id: uid(), company: '字节跳动', position: '内容运营实习生',
        startDate: '2023/07', endDate: '2024/01',
        description: '负责抖音教育垂类账号的内容策划与粉丝增长',
        content: '◆ 独立策划并执行30+条短视频内容，单条最高播放量500W+\n◆ 6个月内将账号粉丝从2万增长至25万，增长率1150%\n◆ 搭建内容SOP流程，输出选题库与脚本模板供团队复用\n◆ 协助完成3场直播活动策划，场均GMV突破10万' }
    ],
    projects: [
      { _id: uid(), name: '校园自媒体矩阵', role: '创始人 & 主理人',
        description: '从0到1打造覆盖公众号+小红书+B站的大学生活IP',
        startDate: '2021/09', endDate: '2024/06', link: '',
        content: '◆ 全平台累计粉丝8万+，公众号阅读量均篇3000+\n◆ 与10+品牌达成商业合作，总变现金额超5万元\n◆ 擅长热点追踪与创意文案，多篇笔记登上小红书热搜榜\n◆ 具备完整的拍摄、剪辑、排版、运营闭环能力' }
    ],
    skills: `★ 内容创作：擅长爆款文案撰写、短视频脚本策划、海报设计
★ 平台运营：深度用户 of 抖音 / 小红书 / 公众号 / B站，熟悉各平台算法机制
★ 工具技能：熟练使用剪映、PR、PS、Canva、秀米等创作工具
★ 数据分析：能用蝉妈妈、新抖等工具做竞品分析与复盘优化`,
    selfEvaluation: `★ 00后Z世代原住民，懂年轻人语言，网感强、脑洞大
★ 执行力MAX，说干就干，不拖延，结果导向
★ 社牛属性，善于与人打交道，采访过50+位校园人物
★ 相信"内容为王"，每一条产出都用心打磨`,
    certificates: [],
    customModules: [
      { _id: uid(), title: '个人作品', content: '📱 抖音号：@简小历在折腾（教育垂类 2.5万粉）\n📕 小红书：@简小历的日常（生活Vlog 3万粉）\n🎨 公众号：简小历手记（原创文章 阅读均篇3000+）\n🎬 B站：简小历剪辑室（混剪视频 播放总量100W+）' }
    ], themeColor: '#D946EF',
  }),
}

// 根据模板ID获取默认数据（默认使用116）
function getTemplateDefaults(templateId) {
  const tid = String(templateId)
  console.log('[getTemplateDefaults] requesting:', tid, 'available:', Object.keys(TEMPLATE_DEFAULTS))
  const fn = TEMPLATE_DEFAULTS[tid] || TEMPLATE_DEFAULTS['116']
  const result = fn ? fn() : TEMPLATE_DEFAULTS['116']()
  console.log('[getTemplateDefaults] result keys:', Object.keys(result), 'name:', result.basic?.name)
  return result
}

const defaultFormData = () => getTemplateDefaults('116')
const formData = reactive(defaultFormData())
const fieldStyles = ref({}) // 字段级字体样式（基础信息）
const nonBasicFieldStyles = ref({}) // 非基础字段字体样式
const basicFieldOrder = ref([]) // 基础字段排序（来自编辑区拖拽）

// ===== 数据加载 =====
function parseJson(raw, fallback) {
  if (!raw) return fallback
  if (typeof raw === 'object') return raw
  try { return JSON.parse(raw) } catch { return fallback }
}

onMounted(() => loadResume())

async function loadResume() {
  try {
    const res = await getResumeById(resumeId)
    const d = res.data
    resumeTitle.value = d.title || '我的简历'
    // 优先使用URL参数中的模板ID（从模板页创建时传递），其次用后端返回的
    const urlTplId = route.query.tpl
    const apiTemplateId = String(urlTplId || d.templateId || '116')
    currentTemplateId.value = apiTemplateId

    const basic = parseJson(d.basic, {})
    // 检测是否为新建空白简历（无任何有效内容）
    const isEmptyResume = !basic.name && !basic.phone && !basic.email &&
      (!d.education || parseJson(d.education, []).length === 0) &&
      (!d.experience || parseJson(d.experience, []).length === 0)

    console.log('[loadResume] templateId:', apiTemplateId, 'urlTpl:', urlTplId, 'apiTpl:', d.templateId, 'isEmpty:', isEmptyResume)

    // 始终用当前模板ID对应的预填充数据作为基础
    const tplDefaults = getTemplateDefaults(apiTemplateId)

    if (isEmptyResume) {
      // 新建空白简历：直接使用模板预填充数据
      Object.assign(formData.basic, tplDefaults.basic)
      Object.assign(formData.intention, tplDefaults.intention)
      formData.education = tplDefaults.education
      formData.experience = tplDefaults.experience
      formData.projects = tplDefaults.projects
      formData.skills = tplDefaults.skills
      formData.selfEvaluation = tplDefaults.selfEvaluation
      formData.certificates = tplDefaults.certificates
      formData.customModules = tplDefaults.customModules
      if (basic.photo) avatarUrl.value = basic.photo
      // 应用模板主题色
      if (tplDefaults.themeColor) themeColor.value = tplDefaults.themeColor
    } else {
      // 已有数据的简历：正常全覆盖
      Object.assign(formData.basic, {
        name: basic.name || '', gender: basic.gender || '', age: basic.age || '',
        phone: basic.phone || '', email: basic.email || '',
        targetJob: basic.title || basic.targetJob || '', workYears: basic.workYears || '',
        city: basic.location || basic.city || '', status: basic.status || '',
        birthday: basic.birthday || '',
      })
      if (basic.photo) avatarUrl.value = basic.photo

      const intention = parseJson(d.intention, {})
      formData.intention.expectedSalary = intention.expectedSalary || ''
      formData.intention.jobType = intention.jobType || '全职'
      formData.intention.availableDate = intention.availableDate || ''

      formData.education = (parseJson(d.education, [])).map(e => ({
        _id: e._id || uid(), school: e.school || '', degree: e.degree || e.level || '',
        major: e.major || '', startDate: e.startDate || e.startTime || '',
        endDate: e.endDate || e.endTime || '', courses: e.courses || '',
        gpa: e.gpa || e.gradeRank || '', description: e.description || '',
      }))
      formData.experience = (parseJson(d.experience, [])).map(e => ({
        _id: e._id || uid(), company: e.company || '', position: e.position || e.title || '',
        startDate: e.startDate || e.startTime || '', endDate: e.endDate || e.endTime || '',
        content: e.content || e.description || '', description: e.intro || '',
      }))
      formData.projects = (parseJson(d.projects, [])).map(p => ({
        _id: p._id || uid(), name: p.name || p.projectName || '', role: p.role || '',
        startDate: p.startDate || p.startTime || '', endDate: p.endDate || p.endTime || '',
        description: p.description || p.intro || '', content: p.content || p.workContent || '',
        link: p.link || '',
      }))
      formData.skills = d.skillContent || ''
      formData.selfEvaluation = d.selfEvaluationContent || ''
      formData.certificates = (parseJson(d.certificates, [])).map(c => ({
        _id: c._id || uid(), name: c.name || '', issuer: c.issuer || c.organization || '',
        date: c.date || c.issueDate || '',
      }))
      formData.customModules = parseJson(d.customModules, []).map(cm => ({
        _id: cm._id || uid(), title: cm.title || '', content: cm.content || '',
      }))
    }

    // 隐藏的区块（新旧简历都处理）
    if (d.hiddenSections) hiddenSections.value = new Set(parseJson(d.hiddenSections, []))

    const settings = parseJson(d.globalSettings, {})
    if (settings.themeColor) themeColor.value = settings.themeColor
    if (settings.fontFamily) fontFamily.value = settings.fontFamily
    if (settings.lineHeight) lineHeight.value = settings.lineHeight
    if (settings.fontSize) fontSize.value = settings.fontSize

    store.activeResumeId = resumeId
  } catch (_) {
    if (store.resumes[resumeId]) {
      store.activeResumeId = resumeId
      const local = store.resumes[resumeId]
      const b = local.basic || {}
      // 检测本地数据是否也是空的新建简历
      const isEmptyLocal = !b.name && !b.phone && !b.email &&
        (!local.education || local.education.length === 0) &&
        (!local.experience || local.experience.length === 0)
      if (!isEmptyLocal) {
        mapLocalToForm(local)
      }
      // 空简历时保留 defaultFormData 预填充数据，不做任何覆盖
    } else {
      ElMessage.error('简历不存在')
      router.push('/dashboard')
    }
  }
}

function mapLocalToForm(local) {
  if (!local) return
  const b = local.basic || {}
  Object.assign(formData.basic, {
    name: b.name||'', gender:b.gender||'', age:b.age||'', phone:b.phone||'', email:b.email||'',
    targetJob:b.title||'', workYears:b.workYears||'', city:b.location||'',
  })
  formData.education = (local.education||[]).map(e=>({...e,_id:e._id||uid()}))
  formData.experience = (local.experience||[]).map(e=>({...e,_id:e._id||uid()}))
  formData.projects = (local.projects||[]).map(p=>({...p,_id:p._id||uid()}))
  formData.skills = local.skillContent||''
  formData.selfEvaluation = local.selfEvaluationContent||''
  formData.certificates = (local.certificates||[]).map(c=>({...c,_id:c._id||uid()}))
}
function uid(){ return Date.now().toString(36)+Math.random().toString(36).slice(2,7) }

// ===== 数据更新 =====
function onFormDataUpdate(data) {
  Object.assign(formData, data)
  // 同步元数据
  if (data._meta) {
    if (data._meta.enabledModuleIds) {
      enabledModuleIds.value = data._meta.enabledModuleIds
    }
    if (data._meta.fieldStyles) {
      fieldStyles.value = data._meta.fieldStyles
    }
    if (data._meta.basicFieldKeys) {
      basicFieldOrder.value = data._meta.basicFieldKeys
    }
    if (data._meta.nonBasicFieldStyles) {
      nonBasicFieldStyles.value = data._meta.nonBasicFieldStyles
    }
  }
  // 同步头像：表单上传的 basic.avatar → avatarUrl
  if (data.basic?.avatar) {
    avatarUrl.value = data.basic.avatar
  }
}

// ===== 画布拖拽排序 =====
function onReorderSectionsFromCanvas({ fromId, fromIdx, toIdx }) {
  // 从画布拖拽排序 → 更新 enabledModuleIds
  const ids = [...enabledModuleIds.value]
  const fromIdxReal = ids.indexOf(fromId)
  if (fromIdxReal === -1) return
  const [moved] = ids.splice(fromIdxReal, 1)
  // 找到目标位置（toIdx 是画布上的视觉索引，需要映射到 enabledModuleIds 的索引）
  let targetRealIdx = toIdx - 1 // 减去 header 区域
  if (targetRealIdx < 0) targetRealIdx = 0
  if (targetRealIdx > ids.length) targetRealIdx = ids.length
  ids.splice(targetRealIdx, 0, moved)
  enabledModuleIds.value = ids
}

// ===== 左侧面板模块排序 =====
function onReorderModulesFromPanel(newOrder) {
  enabledModuleIds.value = newOrder
}

// ===== 区块点击交互 =====
function onSectionClickFromPreview(sectionId) {
  activeSection.value = sectionId
  if (!showForm.value) showForm.value = true
  scrollToTop()
}
function onSectionChangeFromPanel(sectionId) {
  activeSection.value = sectionId
}
function scrollToTop() {
  nextTick(() => {
    const el = formPanelRef.value?.$el
    if (el) el.scrollTop = 0
  })
}

// ===== 模块显隐/删除 =====
function onToggleVisibility(sectionId) {
  if (hiddenSections.value.has(sectionId)) {
    hiddenSections.value.delete(sectionId)
  } else {
    hiddenSections.value.add(sectionId)
  }
  hiddenSections.value = new Set(hiddenSections.value) // 触发响应式更新
}
function onDeleteModule(sectionId) {
  ElMessageBox.confirm(`确定要删除「${getModuleName(sectionId)}」模块吗？`, '确认删除', {
    type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消',
  }).then(() => {
    // 清空该模块数据
    if (sectionId === 'education') formData.education = []
    else if (sectionId === 'experience') formData.experience = []
    else if (sectionId === 'projects') formData.projects = []
    else if (sectionId === 'skills') formData.skills = ''
    else if (sectionId === 'selfEvaluation') formData.selfEvaluation = ''
    else if (sectionId === 'certificates') formData.certificates = []
    else if (sectionId === 'intention') formData.intention = { expectedSalary:'', jobType:'全职', availableDate:'' }
    else if (sectionId === 'basic') formData.basic = defaultFormData().basic
    ElMessage.success('已删除')
  }).catch(()=>{})
}
function getModuleName(id) {
  const map = { basic:'基本信息', intention:'求职意向', education:'教育经历', experience:'工作经历', projects:'项目经历', skills:'专业技能', selfEvaluation:'自我评价', certificates:'证书荣誉' }
  return map[id] || id
}
function onAddModule() {
  ElMessage.info('可通过左侧导航添加标准模块')
}

// ===== 头像上传 =====
function onAvatarUpload(base64Data) {
  avatarUrl.value = base64Data
  formData.basic.photo = base64Data
}

// ===== AI 润色 =====
async function onAiPolish({ text, field }) {
  if (!aiStore.isConfigured) {
    ElMessage.warning('请先在 AI 服务商页面配置 API Key')
    router.push('/ai-settings')
    return
  }

  const model = aiStore.currentModel
  ElMessage.info(`正在使用 ${model.name} 润色「${field}」...`)

  try {
    const res = await apiAiPolish({
      content: text,
      type: field,
      model: aiStore.selectedModel,
      modelId: model.modelId,
      endpoint: model.endpoint,
      apiKey: model.apiKey,
    })

    let polishedText = res.data?.polishedContent || res.data?.result || res.data?.content || ''

    // 根据字段类型回填到对应位置
    if (field === 'skills') formData.skills = polishedText
    else if (field === 'selfEvaluation') formData.selfEvaluation = polishedText
    else if (field.startsWith('experience.')) {
      const [_, idx] = field.split('.')
      if (formData.experience[idx]) formData.experience[idx].content = polishedText
    } else if (field.startsWith('projects.')) {
      const [_, idx] = field.split('.')
      if (formData.projects[idx]) formData.projects[idx].content = polishedText
    } else if (field.startsWith('education.')) {
      const [_, idx] = field.split('.')
      if (formData.education[idx]) formData.education[idx].description = polishedText
    }

    ElMessage.success('AI 润色完成，内容已替换')
  } catch (e) {
    console.error('AI润色失败:', e)
    ElMessage.error('AI 润色失败，请检查 API 配置')
  }
}

// ===== AI 智能提取 =====
async function onAiExtract({ text }) {
  if (!aiStore.isConfigured) {
    ElMessage.warning('请先配置 AI 服务商')
    router.push('/ai-settings')
    formPanelRef.value?.resetAiExtract(false)
    return false
  }
  ElMessage.info('AI 正在提取信息...')
  try {
    const model = aiStore.currentModel
    const res = await apiAiPolish({
      content: text,
      type: 'extract',
      model: aiStore.selectedModel,
      modelId: model.modelId,
      endpoint: model.endpoint,
      apiKey: model.apiKey,
    })

    let rawText = res.data?.polishedContent || res.data?.result || res.data?.content || ''

    // 尝试从返回文本中提取 JSON
    const jsonMatch = rawText.match(/\{[\s\S]*\}/)
    const extracted = jsonMatch ? JSON.parse(jsonMatch[0]) : null
    let data
    if (typeof extracted === 'string') {
      const jsonStr = extracted.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim()
      data = JSON.parse(jsonStr)
    } else {
      data = extracted
    }
    if (data) {
      applyExtractedData(data)
      ElMessage.success('AI 提取完成，信息已填入各板块')
      formPanelRef.value?.resetAiExtract(true)
      return true
    } else {
      ElMessage.error('AI 返回数据为空，请重试')
      formPanelRef.value?.resetAiExtract(false)
      return false
    }
  } catch (e) {
    console.error('AI提取失败:', e)
    ElMessage.error('AI 提取失败，请检查 AI 配置')
    formPanelRef.value?.resetAiExtract(false)
    return false
  }
}

// 将 AI 提取的结构化数据写入 formData
function applyExtractedData(data) {
  // 基本信息
  if (data.basic || data.name || data.email || data.phone) {
    const b = data.basic || data
    if (b.name) formData.basic.name = b.name
    if (b.targetJob || b.job) formData.basic.targetJob = b.targetJob || b.job
    if (b.email) formData.basic.email = b.email
    if (b.phone) formData.basic.phone = b.phone
    if (b.city) formData.basic.city = b.city
    if (b.gender) formData.basic.gender = b.gender
    if (b.age) formData.basic.age = b.age
    if (b.education) formData.basic.education = b.education
    if (b.workYears) formData.basic.workYears = b.workYears
    if (b.birthday) formData.basic.birthday = b.birthday
    if (b.status) formData.basic.status = b.status
    // 求职意向
    if (b.expectedSalary || data.intention?.expectedSalary) {
      if (!formData.intention) formData.intention = { expectedSalary: '', jobType: '全职', availableDate: '' }
      formData.intention.expectedSalary = b.expectedSalary || data.intention?.expectedSalary || formData.intention.expectedSalary
    }
    if (b.jobType || data.intention?.jobType) {
      if (!formData.intention) formData.intention = { expectedSalary: '', jobType: '全职', availableDate: '' }
      formData.intention.jobType = b.jobType || data.intention?.jobType || formData.intention.jobType
    }
    if (b.availableDate || data.intention?.availableDate) {
      if (!formData.intention) formData.intention = { expectedSalary: '', jobType: '全职', availableDate: '' }
      formData.intention.availableDate = b.availableDate || data.intention?.availableDate || formData.intention.availableDate
    }
  }
  // 求职意向（独立字段）
  if (data.intention) {
    if (!formData.intention) formData.intention = { expectedSalary: '', jobType: '全职', availableDate: '' }
    const i = data.intention
    if (i.expectedSalary) formData.intention.expectedSalary = i.expectedSalary
    if (i.jobType) formData.intention.jobType = i.jobType
    if (i.availableDate) formData.intention.availableDate = i.availableDate
  }
  // 专业技能
  if (data.skills) {
    const s = Array.isArray(data.skills) ? data.skills.join('\n') : data.skills
    formData.skills = (formData.skills ? formData.skills + '\n' : '') + s
  }
  // 工作经历
  if (data.experience && Array.isArray(data.experience)) {
    if (!formData.experience) formData.experience = []
    data.experience.forEach(exp => {
      formData.experience.push({
        _id: uid(),
        company: exp.company || '',
        position: exp.position || exp.title || '',
        startDate: exp.startDate || '',
        endDate: exp.endDate || '',
        content: exp.content || exp.description || exp.detail || '',
        description: exp.summary || ''
      })
    })
  }
  // 项目经历
  if (data.projects && Array.isArray(data.projects)) {
    if (!formData.projects) formData.projects = []
    data.projects.forEach(proj => {
      formData.projects.push({
        _id: uid(),
        name: proj.name || proj.title || '',
        role: proj.role || '',
        description: proj.description || proj.summary || '',
        startDate: proj.startDate || '',
        endDate: proj.endDate || '',
        link: proj.link || '',
        content: proj.content || proj.detail || ''
      })
    })
  }
  // 教育经历
  if (data.education && Array.isArray(data.education)) {
    if (!formData.education) formData.education = []
    data.education.forEach(edu => {
      formData.education.push({
        _id: uid(),
        school: edu.school || '',
        major: edu.major || '',
        degree: edu.degree || '',
        gpa: edu.gpa || '',
        startDate: edu.startDate || '',
        endDate: edu.endDate || '',
        description: edu.description || '',
        courses: edu.courses || ''
      })
    })
  }
  // 证书荣誉
  if (data.certificates && Array.isArray(data.certificates)) {
    if (!formData.certificates) formData.certificates = []
    data.certificates.forEach(cert => {
      formData.certificates.push({
        _id: uid(),
        name: cert.name || '',
        issuer: cert.issuer || '',
        date: cert.date || ''
      })
    })
  }
  // 自我评价
  if (data.selfEvaluation) {
    formData.selfEvaluation = (formData.selfEvaluation ? formData.selfEvaluation + '\n' : '') + data.selfEvaluation
  }
  // 同步已启用模块列表
  syncEnabledModules()
}

// 根据已有数据同步 enabledModuleIds
function syncEnabledModules() {
  const ids = new Set(enabledModuleIds.value)
  if (formData.skills && String(formData.skills).trim()) ids.add('skills')
  if (formData.experience && formData.experience.length) ids.add('experience')
  if (formData.projects && formData.projects.length) ids.add('projects')
  if (formData.education && formData.education.length) ids.add('education')
  if (formData.selfEvaluation && String(formData.selfEvaluation).trim()) ids.add('selfEvaluation')
  if (formData.certificates && formData.certificates.length) ids.add('certificates')
  if (formData.intention?.expectedSalary || formData.intention?.jobType !== '全职') ids.add('intention')
  enabledModuleIds.value = [...ids]
}

// ===== AI 整体分析 =====
async function onAiAnalyze() {
  if (!aiStore.isConfigured) {
    ElMessage.warning('请先配置 AI 服务商')
    router.push('/ai-settings')
    return
  }

  ElMessage.info('正在分析简历质量...')
  try {
    // 将整份简历文本化
    const resumeText = buildResumePlainText()
    const res = await apiAiGenerate({
      type: 'analyze',
      content: resumeText,
      model: aiStore.selectedModel,
      modelId: aiStore.currentModel.modelId,
      endpoint: aiStore.currentModel.endpoint,
      apiKey: aiStore.currentModel.apiKey,
    })
    aiResultHtml.value = formatAiResult(res.data?.result || res.data?.content || JSON.stringify(res.data, null, 2))
    showAiResult.value = true
  } catch (e) {
    ElMessage.error('AI 分析失败')
  }
}

function buildResumePlainText() {
  const d = formData
  let text = `【简历】${d.basic.name || ''}\n`
  text += `职位：${d.basic.targetJob || ''}\n\n`
  text += `【专业技能】\n${d.skills || '(未填写)'}\n\n`
  if (d.experience.length) {
    text += `【工作经历】\n`
    d.experience.forEach((ex,i) => {
      text += `${i+1}. ${ex.company || ''} - ${ex.position || ''}\n${ex.content || ''}\n\n`
    })
  }
  if (d.projects.length) {
    text += `【项目经历】\n`
    d.projects.forEach((p,i) => {
      text += `${i+1}. ${p.name || ''}\n${p.content || ''}\n\n`
    })
  }
  if (d.education.length) {
    text += `【教育背景】\n`
    d.education.forEach(e => {
      text += `${e.school || ''} ${e.degree || ''} ${e.major || ''}\n`
    })
  }
  text += `\n【自我评价】\n${d.selfEvaluation || '(未填写)'}`
  return text
}

function formatAiResult(text) {
  return text.replace(/\n/g, '<br>').replace(/【([^】]+)】/g, '<strong style="color:#667eea;font-size:14px;">【$1】</strong>')
}

// ===== 切换模板 =====
function switchTemplate(tpl) {
  const tid = String(tpl.id)
  console.log('[switchTemplate] 切换到模板:', tid, tpl.name)
  currentTemplateId.value = tid
  // 重新应用该模板的预填充数据
  const tplDefaults = getTemplateDefaults(tid)
  Object.assign(formData.basic, tplDefaults.basic)
  Object.assign(formData.intention, tplDefaults.intention)
  formData.education = tplDefaults.education
  formData.experience = tplDefaults.experience
  formData.projects = tplDefaults.projects
  formData.skills = tplDefaults.skills
  formData.selfEvaluation = tplDefaults.selfEvaluation
  formData.certificates = tplDefaults.certificates
  formData.customModules = tplDefaults.customModules
  // 优先使用模板预定义主题色，其次用模板配置的颜色
  themeColor.value = tplDefaults.themeColor || tpl.color || '#333'
  console.log('[switchTemplate] 已应用数据, name:', formData.basic.name, 'targetJob:', formData.basic.targetJob, 'themeColor:', themeColor.value)
  showTemplateDialog.value = false
  ElMessage.success(`已切换到「${tpl.name}」模板`)
}

// ===== 缩放 =====
function zoomIn() { zoom.value = Math.min(200, zoom.value + 10) }
function zoomOut() { zoom.value = Math.max(30, zoom.value - 10) }

// ===== 拖拽调整 =====
let draggingResize = false
function startDragResize(e) {
  draggingResize = true
  const startX = e.clientX
  const panelEl = formPanelRef.value?.$el
  if (!panelEl) return
  const startWidth = panelEl.offsetWidth
  const containerEl = panelEl.parentElement

  function onMove(ev) {
    if (!draggingResize) return
    const dx = ev.clientX - startX
    const newW = startWidth + dx
    const pct = (newW / containerEl.offsetWidth) * 100
    if (pct >= 25 && pct <= 55) panelEl.style.width = pct + '%'
  }
  function onUp() { draggingResize = false; document.removeEventListener('mousemove', onMove); document.removeEventListener('mouseup', onUp) }
  document.addEventListener('mousemove', onMove); document.addEventListener('mouseup', onUp)
  e.preventDefault()
}

// ===== 保存 =====
let autoSaveTimer = null
watch(formData, () => {
  clearTimeout(autoSaveTimer)
  autoSaveTimer = setTimeout(handleSave, 3000) // 3秒自动保存
}, { deep: true })

async function saveTitle() {}
async function handleSave() {
  saving.value = true
  try {
    await updateResume(resumeId, {
      title: resumeTitle.value,
      templateId: currentTemplateId.value,
      basic: JSON.stringify({ ...formData.basic, title: formData.basic.targetJob, location: formData.basic.city }),
      intention: JSON.stringify(formData.intention),
      education: JSON.stringify(formData.education),
      experience: JSON.stringify(formData.experience),
      projects: JSON.stringify(formData.projects),
      skillContent: formData.skills,
      selfEvaluationContent: formData.selfEvaluation,
      certificates: JSON.stringify(formData.certificates),
      customModules: JSON.stringify(formData.customModules || []),
      hiddenSections: JSON.stringify([...hiddenSections.value]),
      globalSettings: JSON.stringify({
        themeColor: themeColor.value, fontFamily: fontFamily.value, lineHeight: lineHeight.value, fontSize: fontSize.value
      }),
    })
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.warning('保存失败')
  } finally {
    saving.value = false
  }
}

// ===== 导出 PDF =====
async function handleExport() {
  try {
    const el = templateRef.value?.$el
    if (!el) { ElMessage.warning('预览未加载'); return }
    await exportToPdf(el, resumeTitle.value || '我的简历')
    ElMessage.success('导出成功')
  } catch (e) { ElMessage.error('导出失败') }
}

// ===== 复制简历 =====
async function handleCopyResume() {
  try {
    const text = buildResumePlainText()
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  } catch (_) { ElMessage.error('复制失败') }
}

// ===== 打印 =====
function handlePrint() { window.print() }

// ===== 全屏 =====
function toggleFullscreen() {
  if (!document.fullscreenElement) document.documentElement.requestFullscreen()
  else document.exitFullscreen()
}
</script>

<style scoped>
.editor-page {
  display: flex; flex-direction: column; height: 100vh; overflow: hidden;
  background: #f0f2f5;
}

/* ===== 顶部工具栏 ===== */
.editor-toolbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 20px; height: 50px; background: #fff;
  border-bottom: 1px solid #E5E7EB; flex-shrink: 0; z-index: 100;
}
.toolbar-left { display: flex; align-items: center; gap: 10px; }
.toolbar-right { display: flex; align-items: center; gap: 6px; }
.toolbar-divider { width: 1px; height: 18px; background: #E5E7EB; }
.toolbar-brand { font-size: 13px; font-weight: 700; color: #667eea; letter-spacing: 0.5px; }
.auto-save-hint { font-size: 11px; color: #bbb; margin-left: 4px; }

.back-btn {
  display: inline-flex; align-items: center; gap: 4px; padding: 6px 10px;
  border: 1px solid #E5E7EB; border-radius: 6px; background: #fff;
  color: #374151; font-size: 13px; cursor: pointer; transition: all 0.15s;
}
.back-btn:hover { background: #F9FAFB; border-color: #D1D5DB; }

.title-input {
  border: none; outline: none; font-size: 15px; font-weight: 600;
  color: #1a1a2e; background: transparent; min-width: 120px;
  text-align: left; padding: 4px 8px; border-radius: 6px;
}
.title-input:focus { background: #F3F4F6; }

.toolbar-btn {
  display: inline-flex; align-items: center; gap: 5px; padding: 6px 14px;
  border: 1px solid #E5E7EB; border-radius: 6px; background: #fff;
  color: #374151; font-size: 13px; cursor: pointer; transition: all 0.15s;
  white-space: nowrap;
}
.toolbar-btn:hover { background: #F9FAFB; border-color: #D1D5DB; }
.save-btn { background: #1F2937 !important; color: #fff !important; border-color: #1F2937 !important; }
.save-btn:hover { background: #111827 !important; }
.save-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.export-btn:hover { background: #FEF2F2 !important; border-color: #FCA5A5 !important; color: #DC2626 !important; }
.spin { animation: spin 1s linear infinite; display: inline-block; }
@keyframes spin { to { transform: rotate(360deg); } }

/* ===== 主内容区 ===== */
.editor-body {
  flex: 1; display: flex; overflow: hidden; position: relative;
  min-height: 0; /* 关键：让flex子元素可以正确收缩 */
}

.slide-left-enter-active,.slide-left-leave-active{ transition:all .25s ease; }
.slide-left-enter-from,.slide-left-leave-to{ margin-left:-100%; opacity:0; }

.resize-divider {
  width: 6px; cursor: col-resize; background: #E5E7EB;
  flex-shrink: 0; position: relative; z-index: 10; transition:background .2s;
}
.resize-divider:hover,.resize-divider:active{ background:#667eea; }

/* ===== 右侧预览区 ===== */
.preview-area {
  flex: 1; overflow: hidden; display: flex; flex-direction: column;
  background: #e8eaed; min-width: 0; position: relative;
  min-height: 0; /* 关键：允许收缩 */
}
.preview-area.full-width{ flex:none; width:100%; }

.preview-scroll{
  flex:1; overflow:auto; display:flex; justify-content:center;
  padding:24px 20px; align-items:flex-start;
  min-height: 0; /* 关键 */
}
.preview-wrapper{
  transform-origin:top center; transition:transform .15s ease;
  box-shadow:0 4px 24px rgba(0,0,0,.12);
  max-width: 100%; /* 防止缩放后超出容器 */
}

/* 底部缩放条 */
.zoom-bar{
  position:absolute; bottom:16px; left:50%; transform:translateX(-50%);
  display:flex; align-items:center; gap:2px; padding:4px 10px;
  background:rgba(255,255,255,.9); backdrop-filter:blur(8px);
  border:1px solid #E5E7EB; border-radius:20px; z-index:10;
  box-shadow:0 2px 8px rgba(0,0,0,.08);
}
.zb-btn{
  width:28px;height:28px;border:1px solid transparent;border-radius:6px;
  background:transparent;color:#555;font-size:14px;cursor:pointer;display:flex;
  align-items:center;justify-content:center;transition:all .12s;
}
.zb-btn:hover{ background:#f3f4f6; border-color:#ddd; }
.zb-val{ font-size:12px; color:#333; font-weight:600; min-width:36px; text-align:center; }
.zb-sep{ width:1px; height:16px; background:#ddd; }
.zb-reset{ font-size:14px; width:auto; padding:0 6px; }

/* ===== 模板选择弹窗 ===== */
.tpl-dialog-grid{
  display:grid; grid-template-columns:repeat(4,1fr); gap:12px; max-height:60vh; overflow-y:auto;
  padding:4px;
}
.tpl-dialog-card{
  border:2px solid transparent; border-radius:8px; overflow:hidden;
  cursor:pointer; transition:all .2s; position:relative;
}
.tpl-dialog-card:hover{ border-color:#667eea; transform:translateY(-2px); box-shadow:0 4px 12px rgba(102,126,234,.15); }
.tpl-dialog-card.selected{ border-color:#667eea; background:#f8f6ff; }
.tpl-dialog-card img{ width:100%; aspect-ratio:210/297; object-fit:contain; background:#fafafa; }
.tpl-d-name{
  display:block; text-align:center; font-size:12px; color:#444; padding:6px; font-weight:500;
}

/* ===== AI 结果 ===== */
.ai-result-content{
  font-size:14px; line-height:1.8; color:#333; max-height:60vh; overflow-y:auto;
  padding:8px 0;
}
.ai-result-content :deep(strong){ display:block; margin:12px 0 6px; }
</style>
