/**
 * Timeline 模板配置 — 时间线风格
 */
export const timelineConfig = {
  id: 'timeline',
  name: '时间线风格',
  description: '时间线布局，突出经历的时间顺序',
  thumbnail: 'timeline',
  layout: 'timeline',
  colorScheme: {
    primary: '#18181b',
    secondary: '#64748b',
    background: '#ffffff',
    text: '#212529'
  },
  spacing: {
    sectionGap: 1,
    itemGap: 12,
    contentPadding: 24
  },
  basic: {
    layout: 'left'
  },
  availableSections: ['skills', 'experience', 'projects', 'education', 'selfEvaluation', 'certificates']
}
