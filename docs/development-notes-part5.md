# 逐风 AI 简历平台 — 开发笔记（第五部分）

> 更新日期：2026-05-12
> 内容：简历 CRUD 模块（增删改查）

---

## 一、模块概述

简历 CRUD 模块是平台的核心功能，实现用户对简历的完整生命周期管理：
- **C**reate - 创建简历
- **R**ead - 查看简历列表和详情
- **U**pdate - 编辑简历内容
- **D**elete - 删除简历

---

## 二、数据库设计

### 2.1 简历表 (t_resume)

```sql
CREATE TABLE t_resume (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES t_user(id),  -- 所属用户
    title           VARCHAR(100) NOT NULL,                   -- 简历标题
    template_id     BIGINT,                                 -- 模板ID
    resume_data     JSONB NOT NULL,                         -- 简历内容（JSON格式）
    status          SMALLINT DEFAULT 1,                     -- 1草稿 2已完成
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_resume_user_id ON t_resume(user_id);
```

### 2.2 resume_data 结构示例

```json
{
  "basicInfo": {
    "name": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "jobIntention": "Java后端开发工程师"
  },
  "education": [
    {
      "school": "XX大学",
      "major": "计算机科学与技术",
      "degree": "本科",
      "startDate": "2020-09",
      "endDate": "2024-06"
    }
  ],
  "experience": [
    {
      "company": "XX科技公司",
      "position": "Java开发实习生",
      "startDate": "2023-06",
      "endDate": "2023-12",
      "description": "负责后端API开发"
    }
  ],
  "projects": [
    {
      "name": "电商后台管理系统",
      "role": "后端开发",
      "description": "使用SpringBoot+Vue开发"
    }
  ],
  "skills": ["Java", "SpringBoot", "MySQL", "Redis"],
  "selfEvaluation": "具备扎实的Java基础..."
}
```

---

## 三、后端实现

### 3.1 文件结构

```
后端 - 简历模块
├── entity/
│   └── Resume.java              # 简历实体
├── dto/
│   ├── CreateResumeDTO.java     # 创建简历参数
│   └── UpdateResumeDTO.java     # 更新简历参数
├── mapper/
│   └── ResumeMapper.java         # 数据访问层
├── service/
│   └── ResumeService.java        # 业务逻辑层
├── vo/
│   ├── ResumeVO.java             # 简历详情视图
│   └── ResumeListVO.java         # 简历列表视图
└── controller/
    └── ResumeController.java     # 控制器
```

### 3.2 核心接口

| 方法 | 路径 | 说明 | 是否需要登录 |
|------|------|------|-------------|
| POST | `/api/v1/resumes` | 创建简历 | ✅ |
| GET | `/api/v1/resumes` | 获取简历列表 | ✅ |
| GET | `/api/v1/resumes/{id}` | 获取简历详情 | ✅ |
| PUT | `/api/v1/resumes/{id}` | 更新简历 | ✅ |
| DELETE | `/api/v1/resumes/{id}` | 删除简历 | ✅ |

### 3.3 权限控制

简历操作需要校验用户身份：

```java
// 在 ResumeService 中校验
public ResumeVO getResumeById(Long id) {
    Long userId = UserContext.getUserId();
    Resume resume = resumeMapper.findById(id);
    
    if (resume == null) {
        throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
    }
    
    // 校验是否是本人的简历
    if (!resume.getUserId().equals(userId)) {
        throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权访问该简历");
    }
    
    return convertToVO(resume);
}
```

### 3.4 ResumeMapper SQL 查询

```java
// 获取用户的所有简历（带模板名称）
@Select("SELECT r.*, t.name as template_name " +
        "FROM t_resume r " +
        "LEFT JOIN t_template t ON r.template_id = t.id " +
        "WHERE r.user_id = #{userId} " +
        "ORDER BY r.updated_at DESC")
List<Resume> findByUserId(Long userId);

// 更新简历
@Update("UPDATE t_resume SET title = #{title}, template_id = #{templateId}, " +
        "resume_data = #{resumeData}, status = #{status}, updated_at = CURRENT_TIMESTAMP " +
        "WHERE id = #{id}")
void update(Resume resume);
```

---

## 四、前端实现

### 4.1 文件结构

```
前端 - 简历模块
├── api/
│   └── resume.js                # 简历接口
└── pages/
    └── Dashboard.vue             # 控制台（简历列表）
```

### 4.2 简历接口 (api/resume.js)

```javascript
import request from './request'

// 获取简历列表
export function getResumes() {
  return request({
    url: '/api/v1/resumes',
    method: 'GET'
  })
}

// 获取简历详情
export function getResumeById(id) {
  return request({
    url: `/api/v1/resumes/${id}`,
    method: 'GET'
  })
}

// 创建简历
export function createResume(data) {
  return request({
    url: '/api/v1/resumes',
    method: 'POST',
    data
  })
}

// 更新简历
export function updateResume(id, data) {
  return request({
    url: `/api/v1/resumes/${id}`,
    method: 'PUT',
    data
  })
}

// 删除简历
export function deleteResume(id) {
  return request({
    url: `/api/v1/resumes/${id}`,
    method: 'DELETE'
  })
}
```

### 4.3 控制台页面功能

```javascript
// 加载简历列表
const loadResumes = async () => {
  const res = await getResumes()
  resumes.value = res.data
}

// 编辑简历
const editResume = (id) => {
  router.push(`/editor/${id}`)
}

// 删除简历（带确认）
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定要删除这份简历吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await deleteResume(id)
  ElMessage.success('删除成功')
  await loadResumes()
}
```

---

## 五、创建简历流程

```
用户选择模板
    ↓
点击模板卡片
    ↓
前端调用 createResume() 接口
    ↓
后端创建简历记录，返回 resumeId
    ↓
前端跳转到编辑器 /editor/{resumeId}
    ↓
加载简历数据进行编辑
```

---

## 六、后续开发计划

- [ ] 第 6 步：简历编辑器（核心）
  - 左侧编辑面板
  - 右侧实时预览
  - 数据保存
- [ ] 第 7 步：AI 生成简历功能
- [ ] 第 8 步：简历导出 PDF
- [ ] 第 9 步：前端界面优化