# 逐风 AI 简历生成与制作网站 — 全栈架构设计文档

> 版本：v1.0  
> 更新日期：2026-05-08

---

## 一、项目定位

智能 AI 驱动的在线简历制作平台，支持以下核心能力：

- AI 一键生成简历
- 多模板选择与切换
- 可视化实时编辑
- PDF 格式导出
- 个人账号管理

---

## 二、技术栈

| 类别 | 技术选型 | 说明 |
|------|----------|------|
| 后端语言 & 框架 | Java + SpringBoot | 企业级后端框架 |
| 前端框架 | Vue3 | 组合式 API，更灵活 |
| 数据库 | PostgreSQL | 支持 JSONB，适合简历灵活数据 |
| 数据库操作工具 | MyBatis | SQL 映射，灵活可控 |
| 构建工具 | Vite | 前端构建，启动极快 |
| 状态管理 | Pinia | Vue3 官方推荐 |
| 路由 | Vue Router 4 | Vue3 配套路由 |
| UI 组件库 | Element Plus | Vue3 最成熟的 UI 库 |
| HTTP 请求 | Axios | 前后端通信 |
| CSS 方案 | SCSS | CSS 预处理器 |
| 认证方案 | JWT | 无状态 Token 认证 |
| PDF 导出 | html2canvas + jsPDF / iText | 前端/后端双方案 |

---

## 三、项目目录结构

```
zhufengaijianli-project/
│
├── zhufeng-resume-backend/                    # 后端 SpringBoot 项目
│   ├── src/main/java/com/zhufeng/
│   │   ├── ZhufengResumeApplication.java      # 启动类
│   │   ├── config/                            # 配置层
│   │   │   ├── CorsConfig.java                # 跨域配置
│   │   │   ├── MyBatisConfig.java             # MyBatis 配置
│   │   │   ├── WebMvcConfig.java              # Web 配置（拦截器注册）
│   │   │   └── AiApiConfig.java               # AI 接口配置
│   │   ├── common/                            # 公共模块
│   │   │   ├── Result.java                    # 统一响应体
│   │   │   ├── ResultCode.java                # 响应状态码枚举
│   │   │   ├── BusinessException.java         # 自定义业务异常
│   │   │   └── GlobalExceptionHandler.java    # 全局异常处理器
│   │   ├── controller/                        # 控制层
│   │   │   ├── AuthController.java            # 认证接口
│   │   │   ├── UserController.java            # 用户接口
│   │   │   ├── ResumeController.java          # 简历接口
│   │   │   ├── TemplateController.java        # 模板接口
│   │   │   └── AiController.java              # AI 接口
│   │   ├── service/                           # 业务逻辑层
│   │   │   ├── AuthService.java
│   │   │   ├── UserService.java
│   │   │   ├── ResumeService.java
│   │   │   ├── TemplateService.java
│   │   │   └── AiService.java
│   │   ├── mapper/                            # MyBatis Mapper 接口
│   │   │   ├── UserMapper.java
│   │   │   ├── ResumeMapper.java
│   │   │   ├── TemplateMapper.java
│   │   │   └── AiGenerationMapper.java
│   │   ├── entity/                            # 实体类（对应数据库表）
│   │   │   ├── User.java
│   │   │   ├── Resume.java
│   │   │   ├── Template.java
│   │   │   └── AiGeneration.java
│   │   ├── dto/                               # 数据传输对象（接收前端参数）
│   │   │   ├── LoginDTO.java
│   │   │   ├── RegisterDTO.java
│   │   │   ├── CreateResumeDTO.java
│   │   │   ├── UpdateResumeDTO.java
│   │   │   └── AiGenerateDTO.java
│   │   ├── vo/                                # 视图对象（返回给前端的数据）
│   │   │   ├── UserInfoVO.java
│   │   │   ├── ResumeListVO.java
│   │   │   ├── ResumeDetailVO.java
│   │   │   └── TemplateVO.java
│   │   └── interceptor/                       # 拦截器
│   │       └── JwtAuthInterceptor.java        # JWT 登录校验
│   ├── src/main/resources/
│   │   ├── application.yml                    # 主配置文件
│   │   ├── application-dev.yml                # 开发环境配置
│   │   ├── application-prod.yml               # 生产环境配置
│   │   └── mapper/                            # MyBatis XML 映射文件
│   │       ├── UserMapper.xml
│   │       ├── ResumeMapper.xml
│   │       ├── TemplateMapper.xml
│   │       └── AiGenerationMapper.xml
│   └── pom.xml                                # Maven 依赖管理
│
├── zhufeng-resume-frontend/                   # 前端 Vue3 项目
│   ├── src/
│   │   ├── api/                               # 接口请求封装
│   │   │   ├── auth.js                        # 认证接口
│   │   │   ├── user.js                        # 用户接口
│   │   │   ├── resume.js                      # 简历接口
│   │   │   ├── template.js                    # 模板接口
│   │   │   └── ai.js                          # AI 接口
│   │   ├── assets/                            # 静态资源
│   │   │   ├── images/
│   │   │   ├── fonts/
│   │   │   └── styles/
│   │   ├── components/                        # 公共组件
│   │   │   ├── AppHeader.vue
│   │   │   ├── AppFooter.vue
│   │   │   └── LoadingSpinner.vue
│   │   ├── composables/                       # 组合式函数
│   │   │   ├── useAuth.js                     # 认证相关
│   │   │   └── useResume.js                   # 简历操作相关
│   │   ├── layouts/                           # 布局组件
│   │   │   ├── DefaultLayout.vue              # 默认布局（首页等）
│   │   │   └── EditorLayout.vue               # 编辑器布局（全屏）
│   │   ├── pages/                             # 页面视图
│   │   │   ├── Login.vue
│   │   │   ├── Register.vue
│   │   │   ├── Home.vue
│   │   │   ├── Dashboard.vue
│   │   │   ├── Editor/
│   │   │   │   ├── ResumeEditor.vue
│   │   │   │   ├── EditorSidebar.vue
│   │   │   │   ├── EditorPreview.vue
│   │   │   │   └── EditorToolbar.vue
│   │   │   ├── Templates.vue
│   │   │   ├── AiGenerate.vue
│   │   │   └── Profile.vue
│   │   ├── router/                            # 路由配置
│   │   │   └── index.js
│   │   ├── stores/                            # Pinia 状态管理
│   │   │   ├── user.js                        # 用户状态
│   │   │   ├── resume.js                      # 简历状态
│   │   │   └── editor.js                      # 编辑器状态
│   │   ├── styles/                            # 全局样式
│   │   │   ├── variables.scss
│   │   │   ├── reset.scss
│   │   │   └── global.scss
│   │   ├── utils/                             # 工具函数
│   │   │   ├── request.js                     # Axios 封装
│   │   │   ├── auth.js                        # Token 管理
│   │   │   └── export.js                      # PDF 导出工具
│   │   ├── App.vue                            # 根组件
│   │   └── main.js                            # 入口文件
│   ├── public/
│   └── package.json
│
└── docs/                                      # 项目文档
    ├── architecture-design.md                 # 架构设计文档
    └── development-mindmap.md                 # 开发流程思维导图
```

---

## 四、后端架构分层设计

### 4.1 四层架构

```
请求流程：前端 → Controller → Service → Mapper → PostgreSQL
                                                  ↓
返回流程：前端 ← Controller ← Service ← Mapper ← PostgreSQL
```

### 4.2 各层职责

| 层级 | 包名 | 职责 | 举例 |
|------|------|------|------|
| **Controller** | `controller` | 接收 HTTP 请求，参数校验，调用 Service，返回响应 | `UserController` |
| **Service** | `service` | 核心业务逻辑，事务管理，调用 Mapper 和外部 API | `UserService` |
| **Mapper** | `mapper` | 数据库操作，MyBatis 接口 + XML | `UserMapper` |
| **Entity** | `entity` | 数据库表对应的 Java 类 | `User` |

### 4.3 辅助包说明

| 包名 | 职责 |
|------|------|
| `dto` | **Data Transfer Object** — 前端传给后端的参数封装，如 `LoginDTO`、`CreateResumeDTO` |
| `vo` | **View Object** — 后端返回给前端的数据封装，如 `UserInfoVO`、`ResumeDetailVO` |
| `common` | 统一响应体 `Result<T>`、自定义异常 `BusinessException`、全局异常处理器 |
| `config` | 跨域配置、MyBatis 配置、AI 接口配置等 |
| `interceptor` | JWT 登录校验拦截器 |

### 4.4 统一响应格式

```java
public class Result<T> {
    private Integer code;    // 状态码：200成功，其他失败
    private String message;  // 提示信息
    private T data;          // 具体数据（泛型）
}
```

前端只需判断 `code === 200` 即可知道请求是否成功，`data` 中获取具体数据。

---

## 五、数据库设计（PostgreSQL）

### 5.1 用户表

```sql
CREATE TABLE t_user (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,          -- BCrypt 加密存储
    email           VARCHAR(100),
    phone           VARCHAR(20),
    avatar          VARCHAR(500),                   -- 头像 URL
    nickname        VARCHAR(50),
    status          SMALLINT DEFAULT 1,             -- 1正常 0禁用
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5.2 模板表

```sql
CREATE TABLE t_template (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    description     TEXT,
    preview_image   VARCHAR(500),                   -- 模板预览图 URL
    template_config JSONB NOT NULL,                 -- 模板布局配置（JSON）
    category        VARCHAR(50),                    -- 分类：简约/商务/创意
    sort_order      INT DEFAULT 0,
    status          SMALLINT DEFAULT 1,             -- 1上架 0下架
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5.3 简历表

```sql
CREATE TABLE t_resume (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES t_user(id),
    title           VARCHAR(100) NOT NULL,          -- 简历标题
    template_id     BIGINT REFERENCES t_template(id),
    resume_data     JSONB NOT NULL,                 -- 简历内容（JSON 格式存储）
    ai_prompt       TEXT,                           -- AI 生成时的提示词
    status          SMALLINT DEFAULT 1,             -- 1草稿 2已完成
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5.4 AI 生成记录表

```sql
CREATE TABLE t_ai_generation (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES t_user(id),
    resume_id       BIGINT REFERENCES t_resume(id),
    prompt          TEXT NOT NULL,                  -- 用户输入的提示词
    ai_model        VARCHAR(50),                    -- 使用的 AI 模型
    result_data     JSONB,                          -- AI 返回的简历数据
    token_usage     INT,                            -- 消耗的 token 数
    status          SMALLINT DEFAULT 1,             -- 1成功 0失败
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5.5 关键设计决策

- `resume_data` 和 `template_config` 使用 PostgreSQL 的 **JSONB** 类型
- JSONB 既能存储灵活的 JSON 结构，又支持索引和查询
- 适合简历这种字段不固定、结构可能随模板变化的数据

### 5.6 resume_data JSON 结构示例

```json
{
  "basicInfo": {
    "name": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "avatar": "https://xxx/avatar.jpg",
    "jobIntention": "Java后端开发工程师"
  },
  "education": [
    {
      "school": "XX大学",
      "major": "计算机科学与技术",
      "degree": "本科",
      "startDate": "2020-09",
      "endDate": "2024-06",
      "description": "GPA 3.8/4.0"
    }
  ],
  "workExperience": [
    {
      "company": "XX科技有限公司",
      "position": "Java开发实习生",
      "startDate": "2023-06",
      "endDate": "2023-12",
      "description": "负责后端API开发..."
    }
  ],
  "projects": [
    {
      "name": "电商后台管理系统",
      "role": "后端开发",
      "startDate": "2023-03",
      "endDate": "2023-06",
      "description": "使用SpringBoot+Vue开发...",
      "techStack": "Java, SpringBoot, MySQL, Redis"
    }
  ],
  "skills": ["Java", "SpringBoot", "MySQL", "Redis", "Git", "Docker"],
  "selfEvaluation": "具备扎实的Java基础，熟悉主流框架..."
}
```

---

## 六、API 接口设计

### 6.1 接口规范

- 基础路径：`/api/v1`
- 认证方式：JWT Token（请求头 `Authorization: Bearer <token>`）
- 请求格式：`application/json`
- 响应格式：统一 `Result<T>` 结构

### 6.2 认证模块

| 方法 | 路径 | 说明 | 是否需登录 |
|------|------|------|-----------|
| POST | `/api/v1/auth/register` | 用户注册 | ❌ |
| POST | `/api/v1/auth/login` | 用户登录 | ❌ |
| POST | `/api/v1/auth/logout` | 退出登录 | ✅ |

### 6.3 用户模块

| 方法 | 路径 | 说明 | 是否需登录 |
|------|------|------|-----------|
| GET | `/api/v1/user/profile` | 获取个人信息 | ✅ |
| PUT | `/api/v1/user/profile` | 修改个人信息 | ✅ |
| PUT | `/api/v1/user/password` | 修改密码 | ✅ |

### 6.4 简历模块

| 方法 | 路径 | 说明 | 是否需登录 |
|------|------|------|-----------|
| GET | `/api/v1/resumes` | 获取简历列表 | ✅ |
| POST | `/api/v1/resumes` | 创建简历 | ✅ |
| GET | `/api/v1/resumes/{id}` | 获取简历详情 | ✅ |
| PUT | `/api/v1/resumes/{id}` | 更新简历 | ✅ |
| DELETE | `/api/v1/resumes/{id}` | 删除简历 | ✅ |
| POST | `/api/v1/resumes/{id}/export` | 导出简历(PDF) | ✅ |

### 6.5 AI 模块

| 方法 | 路径 | 说明 | 是否需登录 |
|------|------|------|-----------|
| POST | `/api/v1/ai/generate` | AI 生成简历 | ✅ |
| POST | `/api/v1/ai/optimize` | AI 优化简历内容 | ✅ |

### 6.6 模板模块

| 方法 | 路径 | 说明 | 是否需登录 |
|------|------|------|-----------|
| GET | `/api/v1/templates` | 获取模板列表 | ❌ |
| GET | `/api/v1/templates/{id}` | 获取模板详情 | ❌ |

---

## 七、核心业务流程

### 7.1 AI 生成简历流程

```
用户输入提示词（如："帮我生成一份Java开发工程师的简历"）
    ↓
前端 → POST /api/v1/ai/generate { prompt, userId }
    ↓
Controller 接收请求，校验参数
    ↓
Service 调用 AI 接口（如 OpenAI / 国内大模型 API）
    ↓
AI 返回结构化简历数据（JSON）
    ↓
Service 解析 AI 结果，组装 resume_data
    ↓
存入 t_resume 表，记录 t_ai_generation
    ↓
返回简历 ID 给前端 → 自动跳转到编辑器页面
```

### 7.2 简历编辑流程

```
用户进入编辑器页面
    ↓
前端 → GET /api/v1/resumes/{id}
    ↓
后端返回 resume_data（JSON）+ template_config
    ↓
前端根据 template_config 渲染简历模板
    ↓
用户修改任意字段 → 实时更新预览
    ↓
用户点击保存 → PUT /api/v1/resumes/{id} { resumeData }
    ↓
后端更新 t_resume.resume_data
```

### 7.3 简历导出流程

```
方案A — 前端导出（推荐起步）：
    用户点击"导出PDF"
    → html2canvas 将简历预览区域截图
    → jsPDF 将截图转为 PDF
    → 触发浏览器下载

方案B — 后端导出（更专业）：
    用户点击"导出PDF"
    → POST /api/v1/resumes/{id}/export
    → 后端使用 iText / Puppeteer 生成 PDF
    → 返回文件流，前端触发下载
```

---

## 八、前端页面路由规划

| 路径 | 页面 | 说明 |
|------|------|------|
| `/login` | 登录页 | 用户登录 |
| `/register` | 注册页 | 用户注册 |
| `/home` | 首页 | 产品介绍、功能展示 |
| `/dashboard` | 控制台 | 简历列表、快捷操作 |
| `/editor/:id` | 简历编辑器 | 核心页面：编辑+预览 |
| `/templates` | 模板中心 | 浏览和选择模板 |
| `/ai-generate` | AI 生成页 | 输入提示词生成简历 |
| `/profile` | 个人设置 | 修改信息、密码 |

---

## 九、简历编辑器组件拆解

简历编辑器是整个项目最复杂的页面，采用三栏布局：

```
ResumeEditor（简历编辑页）
│
├── EditorToolbar（顶部工具栏）
│   ├── TemplateSwitcher       → 切换模板
│   ├── AIOptimize             → AI 优化按钮
│   ├── SaveButton             → 保存
│   └── ExportButton           → 导出 PDF
│
├── EditorSidebar（左侧：编辑面板）
│   ├── BasicInfoForm          → 基本信息（姓名、联系方式、求职意向）
│   ├── EducationForm          → 教育经历（可多条）
│   ├── WorkExperienceForm     → 工作经历（可多条）
│   ├── ProjectForm            → 项目经历（可多条）
│   ├── SkillForm              → 专业技能（标签式）
│   └── SelfEvalForm           → 自我评价
│
└── EditorPreview（中间/右侧：实时预览）
    └── ResumePreview          → 根据模板配置实时渲染简历
```

---

## 十、后端核心依赖

```xml
<dependencies>
    <!-- SpringBoot 基础 -->
    <dependency>spring-boot-starter-web</dependency>
    <dependency>spring-boot-starter-validation</dependency>

    <!-- 数据库 -->
    <dependency>mybatis-spring-boot-starter</dependency>
    <dependency>postgresql</dependency>

    <!-- 工具库 -->
    <dependency>lombok</dependency>
    <dependency>hutool-all</dependency>

    <!-- 认证 -->
    <dependency>jjwt-api</dependency>
    <dependency>jjwt-impl</dependency>
    <dependency>jjwt-jackson</dependency>

    <!-- AI 接口调用 -->
    <dependency>okhttp3</dependency>

    <!-- PDF 导出 -->
    <dependency>itextpdf</dependency>

    <!-- 开发工具 -->
    <dependency>spring-boot-devtools</dependency>
</dependencies>
```

---

## 十一、开发步骤建议

| 步骤 | 内容 | 涉及技术 |
|------|------|----------|
| 第1步 | 搭建后端项目骨架 | SpringBoot 初始化 + 数据库连接 + 通一个 CRUD |
| 第2步 | 完成用户认证模块 | 注册/登录 + JWT + 拦截器 |
| 第3步 | 搭建前端项目骨架 | Vite 创建 Vue3 项目 + 路由 + Axios |
| 第4步 | 完成模板模块 | 模板列表展示 + 模板详情 |
| 第5步 | 完成简历 CRUD | 简历增删改查 + JSONB 数据存取 |
| 第6步 | 开发简历编辑器 | 左右分栏 + 实时预览 + 数据绑定 |
| 第7步 | 接入 AI 能力 | 调用大模型 API + 解析返回结果 |
| 第8步 | 简历导出 | PDF 导出功能 |
| 第9步 | 打磨优化 | 界面美化 + 性能优化 + 部署上线 |
