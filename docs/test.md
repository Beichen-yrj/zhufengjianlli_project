# 逐风 AI 简历平台 — API 测试文档

> 更新时间：2026-05-12
> 测试工具：Postman
> 基础地址：http://localhost:8081

---

## 一、环境配置

### 1.1 Postman 环境变量

在 Postman 中创建环境，添加变量：

| 变量名 | 初始值 | 说明 |
|--------|--------|------|
| `baseUrl` | http://localhost:8081 | 基础地址 |
| `token` | （留空） | 登录后自动更新 |

---

## 二、认证模块测试

### 2.1 用户注册

**接口信息**

| 项目 | 值 |
|------|-----|
| 请求方法 | POST |
| 请求路径 | `{{baseUrl}}/api/v1/auth/register` |
| Content-Type | application/json |

**请求体**

```json
{
    "username": "testuser",
    "password": "123456",
    "email": "test@example.com",
    "nickname": "测试用户"
}
```

**成功响应**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": "注册成功"
}
```

**失败响应（用户名已存在）**

```json
{
    "code": 400,
    "message": "用户名已存在",
    "data": null
}
```

**失败响应（参数校验失败）**

```json
{
    "code": 400,
    "message": "用户名不能为空",
    "data": null
}
```

---

### 2.2 用户登录

**接口信息**

| 项目 | 值 |
|------|-----|
| 请求方法 | POST |
| 请求路径 | `{{baseUrl}}/api/v1/auth/login` |
| Content-Type | application/json |

**请求体**

```json
{
    "username": "testuser",
    "password": "123456"
}
```

**成功响应**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "user": {
            "id": 1,
            "username": "testuser",
            "email": "test@example.com",
            "phone": null,
            "avatar": null,
            "nickname": "测试用户",
            "password": null
        }
    }
}
```

**失败响应（用户名不存在）**

```json
{
    "code": 400,
    "message": "用户名或密码错误",
    "data": null
}
```

**失败响应（密码错误）**

```json
{
    "code": 400,
    "message": "用户名或密码错误",
    "data": null
}
```

**后续测试操作**

1. 复制响应中 `data.token` 的值
2. 粘贴到环境变量 `token` 中
3. 后续需要登录的接口会自动使用这个 Token

---

## 三、测试接口（公开）

### 3.1 测试接口 - Hello

**接口信息**

| 项目 | 值 |
|------|-----|
| 请求方法 | GET |
| 请求路径 | `{{baseUrl}}/api/v1/test/hello` |
| 是否需要登录 | ❌ 不需要 |

**成功响应**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": "Hello, 逐风简历平台!"
}
```

---

### 3.2 测试接口 - 获取用户（路径参数）

**接口信息**

| 项目 | 值 |
|------|-----|
| 请求方法 | GET |
| 请求路径 | `{{baseUrl}}/api/v1/test/user/{id}` |
| 是否需要登录 | ❌ 不需要 |

**路径参数**

| 参数名 | 值 | 说明 |
|--------|-----|------|
| id | 1 | 用户 ID |

**成功响应（id > 0）**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": "获取用户ID成功，用户ID：1"
}
```

**失败响应（id <= 0）**

```json
{
    "code": 400,
    "message": "用户ID必须大于0",
    "data": null
}
```

---

## 四、用户模块测试

### 4.1 获取当前用户信息

**接口信息**

| 项目 | 值 |
|------|-----|
| 请求方法 | GET |
| 请求路径 | `{{baseUrl}}/api/v1/user/profile` |
| 是否需要登录 | ✅ 需要 |
| 请求头 | `Authorization: Bearer {{token}}` |

**成功响应**

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phone": null,
        "avatar": null,
        "nickname": "测试用户"
    }
}
```

**失败响应（未携带 Token）**

```json
{
    "code": 401,
    "message": "请先登录",
    "data": null
}
```

**失败响应（Token 过期）**

```json
{
    "code": 401,
    "message": "登录已过期，请重新登录",
    "data": null
}
```

---

## 五、Postman Collection 导入

可以直接导入以下 JSON 到 Postman：

```json
{
    "info": {
        "name": "逐风简历平台 API",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "variable": [
        {
            "key": "baseUrl",
            "value": "http://localhost:8081"
        },
        {
            "key": "token",
            "value": ""
        }
    ],
    "item": [
        {
            "name": "认证模块",
            "item": [
                {
                    "name": "用户注册",
                    "request": {
                        "method": "POST",
                        "url": "{{baseUrl}}/api/v1/auth/register",
                        "header": [
                            {
                                "key": "Content-Type",
                                "value": "application/json"
                            }
                        ],
                        "body": {
                            "mode": "raw",
                            "raw": "{\n    \"username\": \"testuser\",\n    \"password\": \"123456\",\n    \"email\": \"test@example.com\",\n    \"nickname\": \"测试用户\"\n}"
                        }
                    }
                },
                {
                    "name": "用户登录",
                    "event": [
                        {
                            "listen": "test",
                            "script": {
                                "exec": [
                                    "var jsonData = pm.response.json();",
                                    "if (jsonData.code === 200 && jsonData.data.token) {",
                                    "    pm.collectionVariables.set('token', jsonData.data.token);",
                                    "}"
                                ]
                            }
                        }
                    ],
                    "request": {
                        "method": "POST",
                        "url": "{{baseUrl}}/api/v1/auth/login",
                        "header": [
                            {
                                "key": "Content-Type",
                                "value": "application/json"
                            }
                        ],
                        "body": {
                            "mode": "raw",
                            "raw": "{\n    \"username\": \"testuser\",\n    \"password\": \"123456\"\n}"
                        }
                    }
                }
            ]
        },
        {
            "name": "测试接口",
            "item": [
                {
                    "name": "Hello",
                    "request": {
                        "method": "GET",
                        "url": "{{baseUrl}}/api/v1/test/hello"
                    }
                },
                {
                    "name": "获取用户",
                    "request": {
                        "method": "GET",
                        "url": "{{baseUrl}}/api/v1/test/user/1"
                    }
                }
            ]
        },
        {
            "name": "用户模块",
            "item": [
                {
                    "name": "获取当前用户信息",
                    "request": {
                        "method": "GET",
                        "url": "{{baseUrl}}/api/v1/user/profile",
                        "header": [
                            {
                                "key": "Authorization",
                                "value": "Bearer {{token}}"
                            }
                        ]
                    }
                }
            ]
        }
    ]
}
```

---

## 六、测试流程

### 6.1 完整测试流程

```
1. 注册用户
   → POST /api/v1/auth/register

2. 登录获取 Token
   → POST /api/v1/auth/login
   → 复制 token 到环境变量

3. 测试公开接口
   → GET /api/v1/test/hello

4. 测试需要登录的接口
   → GET /api/v1/user/profile
   → Header: Authorization: Bearer {{token}}

5. 测试 Token 验证
   → 不带 Token 访问 /api/v1/user/profile
   → 应该返回 401
```

### 6.2 快速测试清单

| 序号 | 接口 | 方法 | 路径 | 需要 Token | 测试结果 |
|------|------|------|------|------------|----------|
| 1 | 注册 | POST | /api/v1/auth/register | ❌ | ☐ |
| 2 | 登录 | POST | /api/v1/auth/login | ❌ | ☐ |
| 3 | 测试-Hello | GET | /api/v1/test/hello | ❌ | ☐ |
| 4 | 测试-获取用户 | GET | /api/v1/test/user/1 | ❌ | ☐ |
| 5 | 用户信息 | GET | /api/v1/user/profile | ✅ | ☐ |
| 6 | 用户信息（无Token） | GET | /api/v1/user/profile | ❌ | ☐ |

---

## 七、常见错误

### 7.1 错误码说明

| 错误码 | 说明 | 解决方法 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 参数错误 | 检查请求参数 |
| 401 | 未登录 | 先登录获取 Token |
| 403 | 禁止访问 | 账号被禁用 |
| 404 | 资源不存在 | 检查请求路径 |
| 500 | 服务器错误 | 查看后端日志 |

### 7.2 常见问题

**Q: 返回 401 "请先登录"**
A: 需要在请求头中添加 `Authorization: Bearer {{token}}`

**Q: 返回 401 "登录已过期"**
A: Token 过期，需要重新登录

**Q: 返回 500 "服务器异常"**
A: 查看后端控制台日志，常见原因：数据库连接失败

**Q: Postman 无法访问 localhost**
A: 确保 Spring Boot 应用已启动，端口 8081 未被占用