# 逐风 AI 简历平台 — 开发笔记（第七部分）

> 更新日期：2026-05-12
> 内容：AI 生成简历功能

---

## 一、模块概述

AI 生成简历是平台的特色功能，用户输入提示词，AI 自动生成完整的简历内容。

### 流程

```
用户输入提示词
    ↓
前端 POST /api/v1/ai/generate
    ↓
AiController 接收请求
    ↓
AiService 处理
    ↓（当前：模拟数据 | 后续：真实 AI 接口）
生成简历 JSON 数据
    ↓
存入 t_resume 表
    ↓
返回 resumeId 给前端
    ↓
前端跳转到编辑器页面
```

---

## 二、后端实现

### 2.1 文件结构

```
后端 - AI 模块
├── dto/
│   └── AiGenerateDTO.java      # AI 生成请求参数
├── vo/
│   └── AiGenerationVO.java     # AI 生成返回结果
├── service/
│   └── AiService.java          # AI 业务逻辑
└── controller/
    └── AiController.java        # AI 接口
```

### 2.2 核心接口

| 方法 | 路径 | 说明 | 是否需要登录 |
|------|------|------|-------------|
| POST | `/api/v1/ai/generate` | AI 生成简历 | ✅ |

### 2.3 请求参数

```json
{
  "prompt": "我是计算机专业应届毕业生，熟悉Java和SpringBoot",
  "templateId": 1
}
```

### 2.4 返回数据

```json
{
  "code": 200,
  "message": "AI 生成成功",
  "data": {
    "resumeId": 5,
    "title": "AI 生成的简历",
    "resumeData": "{ \"basicInfo\": {...}, \"education\": [...] }"
  }
}
```

### 2.5 当前实现（模拟数据）

```java
// AiService.java 中的模拟生成
private String generateMockResumeData(String prompt) {
    JSONObject data = JSONUtil.createObj();
    data.set("basicInfo", ...);
    data.set("education", ...);
    data.set("experience", ...);
    data.set("projects", ...);
    data.set("skills", ...);
    data.set("selfEvaluation", ...);
    return data.toStringPretty();
}
```

### 2.6 接入真实 AI 的方式

替换 `generateMockResumeData()` 方法：

```java
private String callRealAiApi(String prompt) {
    // 构造请求
    JSONObject requestBody = JSONUtil.createObj()
        .set("model", "gpt-3.5-turbo")
        .set("messages", JSONUtil.createArray().add(
            JSONUtil.createObj()
                .set("role", "system")
                .set("content", "你是专业简历生成助手，返回JSON格式简历数据")
        ).add(
            JSONUtil.createObj()
                .set("role", "user")
                .set("content", prompt)
        ));

    // 调用 API
    String response = HttpUtil.createPost(AI_API_URL)
        .header("Authorization", "Bearer " + AI_API_KEY)
        .body(requestBody.toString())
        .execute()
        .body();

    // 解析返回
    JSONObject result = JSONUtil.parseObj(response);
    return result.getJSONArray("choices")
        .getJSONObject(0)
        .getJSONObject("message")
        .getStr("content");
}
```

---

## 三、前端实现

### 3.1 文件结构

```
前端 - AI 模块
├── api/
│   └── ai.js                  # AI 接口
└── pages/
    └── AiGenerate.vue         # AI 生成页面
```

### 3.2 页面功能

| 功能 | 说明 |
|------|------|
| 模板选择 | 下拉选择简历模板 |
| 提示词输入 | 描述个人情况 |
| AI 生成 | 点击按钮调用接口 |
| 结果展示 | 显示成功状态 |
| 跳转编辑 | 点击"去编辑"进入编辑器 |

### 3.3 核心代码

```javascript
// 调用 AI 生成接口
const handleGenerate = async () => {
  const res = await aiGenerate({
    prompt: form.value.prompt,
    templateId: form.value.templateId
  })
  result.value = res.data
}

// 跳转到编辑器
const editResume = () => {
  router.push(`/editor/${result.value.resumeId}`)
}
```

---

## 四、可接入的 AI 模型

| 模型 | 接口地址 | 特点 |
|------|----------|------|
| OpenAI GPT | `api.openai.com` | 效果好，需付费 |
| 通义千问 | `dashscope.aliyuncs.com` | 国内可用，有免费额度 |
| DeepSeek | `api.deepseek.com` | 性价比高 |
| 百度文心 | `aip.baidubce.com` | 国内免费额度多 |

---

## 五、后续优化

| 优化项 | 说明 |
|--------|------|
| 接入真实 AI | 替换模拟数据 |
| AI 优化内容 | 已有简历内容让 AI 润色 |
| 流式输出 | 使用 SSE 实时显示生成过程 |
| Token 记录 | 记录每次调用消耗的 Token |
| 生成历史 | 用户可查看历史生成记录 |

---

## 六、后续开发计划

- [x] 第 7 步：AI 生成简历功能
- [ ] 第 8 步：简历导出 PDF
- [ ] 第 9 步：前端界面优化
