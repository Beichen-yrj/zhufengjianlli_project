/** 主题色列表 */
export const THEME_COLORS = [
  '#000000', '#1A1A1A', '#333333', '#4D4D4D',
  '#666666', '#808080', '#999999',
  '#0047AB', '#8B0000', '#FF4500', '#4B0082', '#2E8B57'
]

/** 简历模板 ID */
export const TEMPLATE_IDS = {
  CLASSIC: 'classic',
  MODERN: 'modern',
  LEFT_RIGHT: 'left-right',
  TIMELINE: 'timeline',
  MINIMALIST: 'minimalist',
  ELEGANT: 'elegant',
  CREATIVE: 'creative',
  EDITORIAL: 'editorial'
}

/** 默认全局设置 */
export const DEFAULT_GLOBAL_SETTINGS = {
  themeColor: '#000000',
  fontFamily: 'default',
  baseFontSize: 14,
  pagePadding: 40,
  paragraphSpacing: 8,
  lineHeight: 1.6,
  sectionSpacing: 24,
  headerSize: 24,
  subheaderSize: 16,
  useIconMode: false,
  centerSubtitle: false,
  flexibleHeaderLayout: false,
  autoOnePage: false
}

/** 默认头像配置 */
export const DEFAULT_PHOTO_CONFIG = {
  width: 90,
  height: 120,
  aspectRatio: '1:1',
  borderRadius: 'none',
  customBorderRadius: 0,
  visible: true
}

/** 默认基本信息 */
export const DEFAULT_BASIC_INFO = {
  name: '',
  title: '',
  email: '',
  phone: '',
  location: '',
  birthDate: '',
  employementStatus: '',
  photo: '',
  photoConfig: { ...DEFAULT_PHOTO_CONFIG },
  customFields: [],
  fieldOrder: [],
  layout: 'center',
  githubKey: '',
  githubUseName: '',
  githubContributionsVisible: false,
  icons: {}
}
