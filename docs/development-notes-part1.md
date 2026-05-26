# 逐风 AI 简历平台 — 开发笔记

> 项目进度：第一部分（项目骨架 + 用户认证模块）
> 更新时间：2026-05-12

---

## 一、项目概述

### 1.1 项目简介

逐风 AI 简历生成与制作网站是一个智能 AI 驱动的在线简历制作平台。

### 1.2 技术栈

| 类别 | 技术选型 |
|------|----------|
| 后端框架 | Java 17 + Spring Boot 3.4.0 |
| 前端框架 | Vue3 |
| 数据库 | PostgreSQL 18 |
| ORM 框架 | MyBatis |
| 认证方案 | JWT + BCrypt |
| 构建工具 | Maven |

---

## 二、项目结构

```
zhufeng-resume-backend/
├── src/main/java/com/zhufeng/
│   ├── ZhufengResumeBackendApplication.java   # 启动类
│   ├── common/                                # 公共模块
│   │   ├── Result.java                       # 统一响应体
│   │   ├── ResultCode.java                   # 状态码枚举
│   │   ├── BusinessException.java            # 自定义异常
│   │   ├── GlobalExceptionHandler.java      # 全局异常处理
│   │   └── JwtUtil.java                      # JWT 工具类
│   ├── controller/                           # 控制层
│   │   ├── TestController.java              # 测试接口
│   │   └── AuthController.java              # 认证接口
│   ├── service/                              # 业务层
│   │   └── AuthService.java                 # 认证业务
│   ├── mapper/                               # 数据层
│   │   └── UserMapper.java                  # 用户 Mapper
│   ├── entity/                               # 实体类
│   │   └── User.java                        # 用户实体
│   ├── dto/                                  # 数据传输对象
│   │   ├── LoginDTO.java                    # 登录 DTO
│   │   └── RegisterDTO.java                 # 注册 DTO
│   └── vo/                                   # 视图对象
│       ├── UserVO.java                      # 用户 VO
│       └── LoginVO.java                     # 登录 VO
├── src/main/resources/
│   ├── application.yml                       # 配置文件
│   └── mapper/                               # MyBatis XML（预留）
└── pom.xml                                   # Maven 配置
```

---

## 三、数据库设计

### 3.1 用户表

```sql
CREATE TABLE t_user (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    email           VARCHAR(100),
    phone           VARCHAR(20),
    avatar          VARCHAR(500),
    nickname        VARCHAR(50),
    status          SMALLINT DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 四、接口设计

### 4.1 测试接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/test/hello` | 测试接口 |
| GET | `/api/v1/test/user/{id}` | 获取用户（测试路径参数） |

### 4.2 认证接口

| 方法 | 路径 | 说明 | 是否需登录 |
|------|------|------|-----------|
| POST | `/api/v1/auth/register` | 用户注册 | ❌ |
| POST | `/api/v1/auth/login` | 用户登录 | ❌ |

---

## 五、核心代码说明

### 5.1 统一响应体 (Result)

所有接口返回统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {...}
}
```

### 5.2 密码加密

使用 BCrypt 加密用户密码，登录时自动校验：

```java
// 加密
String encoded = passwordEncoder.encode(rawPassword);

// 校验
boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
```

### 5.3 JWT Token

登录成功后生成 Token，后续请求携带 Token 访问受保护接口：

```
Authorization: Bearer <token>
```

---

## 六、环境配置

### 6.1 配置文件 (application.yml)

```yaml
server:
  port: 8081

spring:
  application:
    name: zhufeng-resume-backend
  
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/zhufeng_resume
    username: postgres
    password: <your_password>

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.zhufeng.entity
  configuration:
    map-underscore-to-camel-case: true

logging:
  level:
    com.zhufeng: debug
```

### 6.2 启动命令

```bash
mvn clean compile
mvn spring-boot:run
```

---

## 七、开发问题记录

### 7.1 常见问题

| 问题 | 解决方案 |
|------|----------|
| Controller 404 | 检查 `@RequestMapping` 路径是否正确 |
| 数据库连接失败 | 检查 PostgreSQL 服务是否启动，配置是否正确 |
| Maven 命令找不到 | 使用 `mvnw.cmd` 或确保 Maven 已安装 |
| 端口被占用 | 修改 `server.port` 为其他端口 |

### 7.2 路径问题

- 访问路径：`/api/v1/xxx`
- Controller 路径：`@RequestMapping("/api/v1/xxx")`
- 确保两者一致

---

## 八、后续开发计划

- [ ] 第 2 步：JWT 拦截器（保护需要登录的接口）
- [ ] 第 3 步：搭建前端 Vue3 项目骨架
- [ ] 第 4 步：模板管理模块
- [ ] 第 5 步：简历 CRUD 模块
- [ ] 第 6 步：简历编辑器
- [ ] 第 7 步：AI 生成简历功能
- [ ] 第 8 步：简历导出 PDF