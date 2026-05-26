# 逐风 AI 简历平台 — 开发流程思维导图

> 使用 Mermaid 语法绘制，支持在 VS Code、GitHub、Typora 等工具中直接渲染

---

## 一、项目总览思维导图

```mermaid
mindmap
  root((逐风AI简历平台))
    前端 Vue3
      项目骨架
        Vite 初始化
        路由配置
        Axios 封装
        Pinia 状态管理
      页面开发
        首页 Home
        登录注册 Login/Register
        控制台 Dashboard
        模板中心 Templates
        AI生成页 AiGenerate
        简历编辑器 Editor
        个人设置 Profile
      核心组件
        EditorToolbar 工具栏
        EditorSidebar 编辑面板
        EditorPreview 实时预览
        ResumePreview 简历渲染
      工具与样式
        SCSS 预处理
        Element Plus 组件库
        PDF导出工具
    后端 SpringBoot
      项目骨架
        SpringBoot 初始化
        数据库连接配置
        跨域配置
        统一响应封装
      分层架构
        Controller 控制层
        Service 业务层
        Mapper 数据层
        Entity 实体层
      认证模块
        JWT Token 生成
        登录校验拦截器
        BCrypt 密码加密
      业务模块
        用户管理
        简历 CRUD
        模板管理
        AI 接口调用
        PDF 导出
    数据库 PostgreSQL
      表设计
        t_user 用户表
        t_resume 简历表
        t_template 模板表
        t_ai_generation AI记录表
      核心特性
        JSONB 灵活存储
        外键关联
        时间戳自动维护
    AI 能力
      接入方式
        OpenAI API
        国内大模型 API
      功能
        AI 一键生成简历
        AI 优化简历内容
        AI 润色描述
```

---

## 二、开发流程思维导图

```mermaid
mindmap
  root((开发流程))
    第1步 后端骨架
      SpringBoot 初始化
      pom.xml 依赖配置
      application.yml 数据库连接
      统一响应体 Result
      全局异常处理
      通一个 CRUD 接口验证
    第2步 用户认证
      t_user 建表
      UserMapper + XML
      UserService 注册登录逻辑
      AuthController 接口
      JWT 工具类
      登录拦截器
      密码 BCrypt 加密
    第3步 前端骨架
      Vite 创建 Vue3 项目
      安装依赖
        Element Plus
        Vue Router
        Pinia
        Axios
        SCSS
      路由配置
      Axios 请求封装
      Pinia Store 结构
      布局组件
    第4步 模板模块
      t_template 建表
      后端模板 CRUD
      前端模板列表页
      前端模板详情页
      模板预览图展示
    第5步 简历CRUD
      t_resume 建表
      后端简历增删改查
      JSONB 数据读写
      前端控制台页面
      简历列表展示
      新建/删除简历
    第6步 简历编辑器
      编辑器三栏布局
      左侧编辑面板
        基本信息表单
        教育经历表单
        工作经历表单
        项目经历表单
        技能标签编辑
        自我评价编辑
      中间实时预览
        模板渲染组件
        数据双向绑定
        实时更新预览
      顶部工具栏
        模板切换
        保存按钮
        导出按钮
    第7步 AI能力
      t_ai_generation 建表
      AI API 配置
      AiService 调用逻辑
      AI 生成简历接口
      AI 优化内容接口
      前端 AI 生成页面
      前端 AI 优化交互
    第8步 简历导出
      前端导出方案
        html2canvas 截图
        jsPDF 生成
      后端导出方案
        iText PDF 生成
        文件流返回
    第9步 打磨优化
      界面美化
      表单校验完善
      错误提示优化
      性能优化
      部署上线
```

---

## 三、后端请求流转流程图

```mermaid
flowchart LR
    A[前端 Vue3] -->|HTTP 请求| B[Controller]
    B -->|调用| C[Service]
    C -->|调用| D[Mapper]
    D -->|SQL| E[(PostgreSQL)]
    E -->|查询结果| D
    D -->|返回Entity| C
    C -->|组装VO| B
    B -->|Result&lt;VO&gt;| A

    C -->|调用AI API| F[大模型服务]
    F -->|返回JSON| C
```

---

## 四、AI 生成简历流程图

```mermaid
flowchart TD
    A[用户输入提示词] --> B[前端发送 POST /ai/generate]
    B --> C[Controller 接收请求]
    C --> D[Service 组装 Prompt]
    D --> E[调用大模型 API]
    E --> F{AI 返回结果}
    F -->|成功| G[解析 JSON 结果]
    G --> H[组装 resume_data]
    H --> I[存入 t_resume 表]
    I --> J[记录 t_ai_generation]
    J --> K[返回简历 ID]
    K --> L[前端跳转编辑器页面]
    F -->|失败| M[返回错误信息]
    M --> N[前端提示用户重试]
```

---

## 五、简历编辑与导出流程图

```mermaid
flowchart TD
    A[用户进入编辑器] --> B[GET /resumes/id 获取简历数据]
    B --> C[加载 resume_data + template_config]
    C --> D[渲染简历模板]
    D --> E[用户编辑字段]
    E --> F[实时更新预览]
    F --> E

    E -->|点击保存| G[PUT /resumes/id 更新数据]
    G --> H[后端更新 resume_data]
    H --> I[返回成功]

    E -->|点击导出| J{导出方案}
    J -->|前端导出| K[html2canvas 截图]
    K --> L[jsPDF 生成 PDF]
    L --> M[浏览器下载]
    J -->|后端导出| N[POST /resumes/id/export]
    N --> O[iText 生成 PDF]
    O --> P[返回文件流]
    P --> M
```

---

## 六、用户认证流程图

```mermaid
flowchart TD
    subgraph 注册流程
        R1[用户填写注册信息] --> R2[POST /auth/register]
        R2 --> R3[校验用户名唯一性]
        R3 -->|已存在| R4[返回用户名已占用]
        R3 -->|不存在| R5[BCrypt 加密密码]
        R5 --> R6[存入 t_user]
        R6 --> R7[返回注册成功]
    end

    subgraph 登录流程
        L1[用户输入账号密码] --> L2[POST /auth/login]
        L2 --> L3[查询用户]
        L3 -->|不存在| L4[返回账号或密码错误]
        L3 -->|存在| L5[BCrypt 校验密码]
        L5 -->|不匹配| L4
        L5 -->|匹配| L6[生成 JWT Token]
        L6 --> L7[返回 Token + 用户信息]
    end

    subgraph 请求鉴权
        A1[前端携带 Token 请求] --> A2[JwtAuthInterceptor 拦截]
        A2 --> A3[解析验证 Token]
        A3 -->|无效或过期| A4[返回 401 未授权]
        A3 -->|有效| A5[放行到 Controller]
    end
```

---

## 七、数据库 ER 关系图

```mermaid
erDiagram
    t_user {
        BIGSERIAL id PK
        VARCHAR username UK
        VARCHAR password
        VARCHAR email
        VARCHAR phone
        VARCHAR avatar
        VARCHAR nickname
        SMALLINT status
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    t_template {
        BIGSERIAL id PK
        VARCHAR name
        TEXT description
        VARCHAR preview_image
        JSONB template_config
        VARCHAR category
        INT sort_order
        SMALLINT status
        TIMESTAMP created_at
    }

    t_resume {
        BIGSERIAL id PK
        BIGINT user_id FK
        VARCHAR title
        BIGINT template_id FK
        JSONB resume_data
        TEXT ai_prompt
        SMALLINT status
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    t_ai_generation {
        BIGSERIAL id PK
        BIGINT user_id FK
        BIGINT resume_id FK
        TEXT prompt
        VARCHAR ai_model
        JSONB result_data
        INT token_usage
        SMALLINT status
        TIMESTAMP created_at
    }

    t_user ||--o{ t_resume : "一个用户拥有多份简历"
    t_template ||--o{ t_resume : "一个模板被多份简历使用"
    t_user ||--o{ t_ai_generation : "一个用户有多条AI记录"
    t_resume ||--o{ t_ai_generation : "一份简历可能多次AI生成"
```

---

## 八、前端路由与页面结构图

```mermaid
flowchart TD
    subgraph 公开页面
        HOME[/home 首页]
        LOGIN[/login 登录]
        REGISTER[/register 注册]
        TEMPLATES[/templates 模板中心]
    end

    subgraph 需要登录
        DASHBOARD[/dashboard 控制台]
        EDITOR[/editor/:id 简历编辑器]
        AI[/ai-generate AI生成]
        PROFILE[/profile 个人设置]
    end

    HOME --> LOGIN
    HOME --> REGISTER
    HOME --> TEMPLATES
    LOGIN --> DASHBOARD
    REGISTER --> DASHBOARD
    DASHBOARD --> EDITOR
    DASHBOARD --> AI
    DASHBOARD --> PROFILE
    AI -->|生成成功| EDITOR
    TEMPLATES -->|选择模板| EDITOR

    subgraph 编辑器内部
        EDITOR --> TOOLBAR[EditorToolbar 工具栏]
        EDITOR --> SIDEBAR[EditorSidebar 编辑面板]
        EDITOR --> PREVIEW[EditorPreview 实时预览]
    end
```
