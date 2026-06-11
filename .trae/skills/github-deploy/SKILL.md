---
name: "github-deploy"
description: "自动初始化 Git 仓库并推送到 GitHub 远程仓库。当用户要求部署到 GitHub、推送代码、创建远程仓库或发布项目时调用此 Skill。"
---

# GitHub 自动部署（Git 推送）

## 功能说明

将当前项目自动初始化 Git 仓库、关联远程 GitHub 仓库并推送代码。适用于首次部署或重新配置远程仓库。

## 项目结构

本项目为前后端分离架构：

```
zhufengaijianli-project/
├── zhufeng-resume-frontend/   # Vue 3 + Vite 前端
├── zhufeng-resume-backend/    # Spring Boot Java 后端
└── docs/                       # 项目文档
```

## 执行流程

### 第一步：检查 Git 状态

```bash
git status
```

- 如果已有 `.git` 目录，跳到第三步
- 如果没有，执行初始化

### 第二步：初始化 Git 仓库

```bash
git init
```

### 第三步：检查/创建 .gitignore

确保以下文件被忽略（不应提交到仓库）：

**根目录 .gitignore 应包含：**
```
node_modules/
dist/
.DS_Store
*.log
.env
.env.local
.trae/
```

**后端目录 `zhufeng-resume-backend/.gitignore` 已存在，检查是否包含：**
```
target/
.mvn/
*.class
*.jar
*.war
*.log
```

如果 `.gitignore` 不存在或不完整，立即创建或补充。

### 第四步：添加所有文件

```bash
git add .
```

### 第五步：创建初始提交

```bash
git commit -m "feat: 初始化简历编辑器项目

- 前端: Vue3 + Element Plus + Pinia + Vite
- 后端: Spring Boot + MyBatis + MySQL
- 功能: 多模板简历编辑、AI提取润色、字体样式调节"
```

### 第六步：在 GitHub 创建远程仓库

**方式 A：使用 GitHub CLI（推荐，如已安装）**
```bash
gh repo create zhufengaijianli-project --public --source=. --push --description="在线AI智能简历编辑器"
```

**方式 B：手动创建后关联**

1. 引导用户在 https://github.com/new 手动创建空仓库
2. 建议仓库名：`zhufengaijianli-project`
3. 建议描述：`在线AI智能简历编辑器 - Vue3 + Spring Boot`
4. **不要勾选** README / .gitignore / License（本地已有）
5. 创建后执行：

```bash
git remote add origin https://github.com/<用户名>/zhufengaijianli-project.git
git branch -M main
git push -u origin main
```

### 第七步：验证推送结果

```bash
git remote -v
git log --oneline -5
```

确认输出显示正确的远程 URL 和提交记录。

## 后续操作提示

推送成功后告知用户：
1. 仓库地址：`https://github.com/<用户名>/zhufengaijianli-project`
2. 如需后续每次开发完自动推送，可设置 alias 或使用 IDE 的 Git 面板
3. 建议后续开启 GitHub Actions 实现自动化 CI/CD

## 注意事项

1. **敏感信息检查**：推送前必须扫描代码中的硬编码密钥/API Key，如有则：
   - 将其移至环境变量
   - 添加到 `.gitignore`
   - 提醒用户已在 GitHub Settings > Secrets 中配置
   
2. **大文件警告**：如果有超过 50MB 的文件，需要安装 Git LFS 或将其加入 .gitignore

3. **分支策略**：默认使用 `main` 分支，遵循 GitHub 现代约定

4. **权限问题**：如果 push 失败提示认证错误，引导用户：
   - 配置 SSH key：`ssh-keygen -t ed25519 -C "邮箱"` 并添加到 GitHub
   - 或使用 HTTPS + Personal Access Token (PAT)
