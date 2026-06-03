# 逐风 AI 简历平台 — 开发笔记（第四部分）

> 更新日期：2026-05-12
> 内容：模板管理模块

---

## 一、模块概述

模板模块是简历平台的基础，用户选择模板后创建简历。主要功能：
- 模板列表展示
- 模板分类筛选
- 模板详情查看
- 选择模板创建简历

---

## 二、数据库设计

### 2.1 模板表 (t_template)

```sql
CREATE TABLE t_template (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,           -- 模板名称
    description     TEXT,                              -- 模板描述
    preview_image   VARCHAR(500),                      -- 预览图 URL
    template_config JSONB NOT NULL,                    -- 模板配置（颜色、字体等）
    category        VARCHAR(50),                       -- 分类：简约/商务/创意
    sort_order      INT DEFAULT 0,                    -- 排序
    status          SMALLINT DEFAULT 1,               -- 1上架 0下架
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.2 模板配置示例

`template_config` 字段存储模板的样式配置：

```json
{
  "primaryColor": "#667eea",
  "secondaryColor": "#764ba2",
  "fontSize": 14,
  "fontFamily": "Microsoft YaHei",
  "layout": "single"  // single | two-column | modern
}
```

---

## 三、后端实现

### 3.1 文件结构

```
后端 - 模板模块
├── entity/
│   └── Template.java           # 模板实体
├── mapper/
│   └── TemplateMapper.java      # 数据访问层
├── service/
│   └── TemplateService.java     # 业务逻辑层
├── vo/
│   └── TemplateVO.java          # 视图对象
└── controller/
    └── TemplateController.java  # 控制器
```

### 3.2 核心接口

| 方法 | 路径 | 说明 | 是否需要登录 |
|------|------|------|-------------|
| GET | `/api/v1/templates` | 获取所有模板 | ❌ |
| GET | `/api/v1/templates/{id}` | 获取模板详情 | ❌ |
| GET | `/api/v1/templates/category/{category}` | 按分类获取模板 | ❌ |

### 3.3 TemplateMapper 关键代码

```java
@Mapper
public interface TemplateMapper {

    // 获取所有上架模板，按排序字段排序
    @Select("SELECT * FROM t_template WHERE status = 1 ORDER BY sort_order ASC")
    List<Template> findAll();

    // 根据ID查询
    @Select("SELECT * FROM t_template WHERE id = #{id}")
    Template findById(Long id);

    // 按分类查询
    @Select("SELECT * FROM t_template WHERE status = 1 AND category = #{category} ORDER BY sort_order ASC")
    List<Template> findByCategory(String category);
}
```

### 3.4 TemplateService 关键代码

```java
@Service
public class TemplateService {

    private final TemplateMapper templateMapper;

    public TemplateService(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    // 获取所有模板
    public List<TemplateVO> getAllTemplates() {
        List<Template> templates = templateMapper.findAll();
        // 转换为 VO 返回
        return templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    // 获取单个模板
    public TemplateVO getTemplateById(Long id) {
        Template template = templateMapper.findById(id);
        if (template == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "模板不存在");
        }
        return convertToVO(template);
    }
}
```

---

## 四、前端实现

### 4.1 文件结构

```
前端 - 模板模块
├── api/
│   └── template.js             # 模板接口
└── pages/
    └── Templates.vue            # 模板列表页
```

### 4.2 模板接口 (api/template.js)

```javascript
import request from './request'

// 获取所有模板
export function getTemplates() {
  return request({
    url: '/api/v1/templates',
    method: 'GET'
  })
}

// 获取模板详情
export function getTemplateById(id) {
  return request({
    url: `/api/v1/templates/${id}`,
    method: 'GET'
  })
}

// 按分类获取模板
export function getTemplatesByCategory(category) {
  return request({
    url: `/api/v1/templates/category/${category}`,
    method: 'GET'
  })
}
```

### 4.3 模板列表页 (Templates.vue)

**核心功能**：
1. 页面加载时调用 `getTemplates()` 获取模板列表
2. 展示模板卡片（预览图、名称、分类、描述）
3. 点击模板跳转到简历编辑器

**关键代码**：
```javascript
import { getTemplates } from '../api/template'

const templates = ref([])

onMounted(async () => {
  const res = await getTemplates()
  templates.value = res.data
})

const selectTemplate = (template) => {
  // 跳转到编辑器页面，传递模板ID
  router.push(`/editor?templateId=${template.id}`)
}
```

**样式特点**：
- 网格布局：`grid-template-columns: repeat(auto-fill, minmax(280px, 1fr))`
- 卡片悬浮效果：`transform: translateY(-5px)`
- 响应式设计，适配不同屏幕

---

## 五、模板分类

| 分类 | 英文 | 特点 | 适用人群 |
|------|------|------|----------|
| 简约 | simple | 简洁大方，留白多 | 应届毕业生、技术岗位 |
| 商务 | business | 专业正式，结构清晰 | 职场人士、管理岗位 |
| 创意 | creative | 设计感强，色彩丰富 | 设计、运营、市场岗位 |

---

## 六、模板数据示例

```sql
-- 经典简约
INSERT INTO t_template (name, description, preview_image, template_config, category, sort_order) 
VALUES ('经典简约', '简洁大方的经典简历模板', 'https://example.com/simple.jpg', 
'{"primaryColor": "#667eea", "layout": "single"}', '简约', 1);

-- 专业商务
INSERT INTO t_template (name, description, preview_image, template_config, category, sort_order) 
VALUES ('专业商务', '适合职场人士的商务风格', 'https://example.com/business.jpg', 
'{"primaryColor": "#764ba2", "layout": "two-column"}', '商务', 2);

-- 创意活力
INSERT INTO t_template (name, description, preview_image, template_config, category, sort_order) 
VALUES ('创意活力', '年轻化的创意设计', 'https://example.com/creative.jpg', 
'{"primaryColor": "#f093fb", "layout": "modern"}', '创意', 3);
```

---

## 七、后续开发计划

- [ ] 第 5 步：简历 CRUD 模块（增删改查）
- [ ] 第 6 步：简历编辑器（核心）
  - 左侧编辑面板
  - 右侧实时预览
  - 数据保存
- [ ] 第 7 步：AI 生成简历功能
- [ ] 第 8 步：简历导出 PDF
- [ ] 第 9 步：前端界面优化