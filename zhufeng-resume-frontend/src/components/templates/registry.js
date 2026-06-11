// ===== 模板配置 =====
import { classicConfig } from './classic/config.js'
import ClassicTemplate from './classic/ClassicTemplate.vue'
import { modernConfig } from './modern/config.js'
import ModernTemplate from './modern/ModernTemplate.vue'
import { leftRightConfig } from './left-right/config.js'
import LeftRightTemplate from './left-right/LeftRightTemplate.vue'
import { timelineConfig } from './timeline/config.js'
import TimelineTemplate from './timeline/TimelineTemplate.vue'
import { minimalistConfig } from './minimalist/config.js'
import MinimalistTemplate from './minimalist/MinimalistTemplate.vue'
import { elegantConfig } from './elegant/config.js'
import ElegantTemplate from './elegant/ElegantTemplate.vue'
import { creativeConfig } from './creative/config.js'
import CreativeTemplate from './creative/CreativeTemplate.vue'
import { editorialConfig } from './editorial/config.js'
import EditorialTemplate from './editorial/EditorialTemplate.vue'

/**
 * 统一模板注册表
 * 新增模板只需：
 * 1. 在 templates/ 下创建目录 with config.js + TemplateName.vue
 * 2. 在此注册
 */
export const TEMPLATE_REGISTRY = [
  { config: classicConfig, component: ClassicTemplate },
  { config: modernConfig, component: ModernTemplate },
  { config: leftRightConfig, component: LeftRightTemplate },
  { config: timelineConfig, component: TimelineTemplate },
  { config: minimalistConfig, component: MinimalistTemplate },
  { config: elegantConfig, component: ElegantTemplate },
  { config: creativeConfig, component: CreativeTemplate },
  { config: editorialConfig, component: EditorialTemplate },
]

/** 所有模板配置列表 */
export const DEFAULT_TEMPLATES = TEMPLATE_REGISTRY.map(entry => entry.config)

/** 根据 layout id 查找模板组件 */
export function getTemplateComponent(layout) {
  return TEMPLATE_REGISTRY.find(e => e.config.layout === layout)?.component ?? ClassicTemplate
}

/** 根据 layout id 查找模板配置 */
export function getTemplateConfig(layout) {
  return TEMPLATE_REGISTRY.find(e => e.config.layout === layout)?.config ?? classicConfig
}
