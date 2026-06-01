# 逐风 AI 简历平台 — 开发笔记（第二部分）

> 更新日期：2026-05-12
> 内容：JWT 拦截器 + 用户接口

---

## 一、新增文件清单

### 1.1 文件总览

| 文件名 | 路径 | 作用 | 重要性 |
|--------|------|------|--------|
| UserContext.java | common/ | 用户上下文（ThreadLocal） | ⭐⭐⭐⭐⭐ |
| JwtAuthInterceptor.java | interceptor/ | JWT 认证拦截器 | ⭐⭐⭐⭐⭐ |
| WebMvcConfig.java | config/ | 拦截器配置 + 跨域配置 | ⭐⭐⭐⭐⭐ |
| UserService.java | service/ | 用户业务逻辑 | ⭐⭐⭐⭐ |
| UserController.java | controller/ | 用户接口 | ⭐⭐⭐⭐ |

### 1.2 新增接口

| 方法 | 路径 | 说明 | 是否需登录 |
|------|------|------|-----------|
| GET | `/api/v1/user/profile` | 获取当前用户信息 | ✅ |

---

## 二、核心概念解释

### 2.1 什么是拦截器（Interceptor）？

拦截器是 **请求过滤器**，类似机场安检：

```
旅客（请求） → 安检口（拦截器） → 登机口（Controller）
     ↓              ↓
  检查行李        验证 Token
  检查身份证      验证登录状态
     ↓              ↓
  通过 → 放行     失败 → 返回 401
```

### 2.2 什么是 ThreadLocal？

ThreadLocal 是 **线程本地存储**，每个线程都有独立的变量空间。

**作用**：在拦截器中存储用户ID，在 Controller/Service 中获取，线程之间互不影响。

**类比**：机场的临时储物柜，每个旅客（线程）都有自己独立的柜子。

```
请求1（用户ID=1） → ThreadLocal: {userId=1}    → Controller 读取
请求2（用户ID=2） → ThreadLocal: {userId=2}    → Controller 读取
                  ↓（请求结束后清理，防止内存泄漏）
              ThreadLocal.remove()
```

### 2.3 什么是跨域（CORS）？

跨域是浏览器安全机制，前端和后端在不同端口时需要配置。

**本项目配置**：
- 前端：localhost:5173（Vite 默认）
- 后端：localhost:8081（Spring Boot）
- 需要配置跨域才能正常通信

---

## 三、文件详细说明

### 3.1 UserContext.java（用户上下文）

**文件路径**：`src/main/java/com/zhufeng/common/UserContext.java`

**作用**：在请求处理过程中存储当前用户信息

**核心代码**：
```java
private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

public static void setUserId(Long userId) {
    USER_ID.set(userId);
}

public static Long getUserId() {
    return USER_ID.get();
}

public static void remove() {
    USER_ID.remove();  // 清理，防止内存泄漏
}
```

**使用场景**：
1. 拦截器验证 Token 后，存入 UserContext
2. Service 从 UserContext 获取当前用户 ID
3. 请求结束后，拦截器 afterCompletion 清理

---

### 3.2 JwtAuthInterceptor.java（JWT拦截器）

**文件路径**：`src/main/java/com/zhufeng/interceptor/JwtAuthInterceptor.java`

**作用**：验证用户登录状态，保护需要登录的接口

**核心流程**：
```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // 1. 获取请求头中的 Token
    String authHeader = request.getHeader("Authorization");

    // 2. 检查 Token 是否存在
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        sendUnauthorizedResponse(response, "请先登录");
        return false;
    }

    // 3. 提取 Token
    String token = authHeader.substring(7);

    // 4. 验证 Token 是否过期
    if (JwtUtil.isTokenExpired(token)) {
        sendUnauthorizedResponse(response, "登录已过期，请重新登录");
        return false;
    }

    // 5. 解析 Token 获取用户 ID
    Long userId = JwtUtil.getUserId(token);
    UserContext.setUserId(userId);

    // 6. 放行
    return true;
}

@Override
public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    // 请求完成后清理 ThreadLocal
    UserContext.remove();
}
```

**返回 401 的情况**：
| 情况 | 返回消息 |
|------|----------|
| 没有携带 Token | "请先登录" |
| Token 格式错误 | "请先登录" |
| Token 已过期 | "登录已过期，请重新登录" |
| Token 被篡改 | "Token 无效" |

---

### 3.3 WebMvcConfig.java（拦截器配置）

**文件路径**：`src/main/java/com/zhufeng/config/WebMvcConfig.java`

**作用**：
1. 注册拦截器到 Spring MVC
2. 配置拦截路径和排除路径
3. 配置跨域

**核心配置**：
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtAuthInterceptor)
            .addPathPatterns("/api/v1/**")           // 拦截 /api/v1/** 的请求
            .excludePathPatterns(
                    "/api/v1/auth/**",              // 认证接口（登录/注册）
                    "/api/v1/test/**",              // 测试接口
                    "/api/v1/templates/**"          // 模板列表
            );
}
```

**路径匹配说明**：

| 路径 | 是否拦截 | 说明 |
|------|----------|------|
| `/api/v1/auth/login` | ❌ 不拦截 | 登录接口公开 |
| `/api/v1/auth/register` | ❌ 不拦截 | 注册接口公开 |
| `/api/v1/test/hello` | ❌ 不拦截 | 测试接口公开 |
| `/api/v1/templates` | ❌ 不拦截 | 模板列表公开 |
| `/api/v1/user/profile` | ✅ 拦截 | 需要登录 |
| `/api/v1/resumes` | ✅ 拦截 | 需要登录 |
| `/api/v1/resumes/{id}` | ✅ 拦截 | 需要登录 |

---

### 3.4 UserService.java（用户业务）

**文件路径**：`src/main/java/com/zhufeng/service/UserService.java`

**作用**：处理用户相关业务逻辑

**核心代码**：
```java
public UserVO getCurrentUser() {
    // 从 UserContext 获取当前用户 ID
    Long userId = UserContext.getUserId();

    // 检查是否登录
    if (userId == null) {
        throw new BusinessException(ResultCode.UNAUTHORIZED);
    }

    // 查询数据库
    User user = userMapper.findById(userId);

    // 检查用户是否存在
    if (user == null) {
        throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
    }

    // 复制属性到 VO
    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);

    return userVO;
}
```

---

### 3.5 UserController.java（用户接口）

**文件路径**：`src/main/java/com/zhufeng/controller/UserController.java`

**作用**：提供用户相关的 HTTP 接口

**接口信息**：
| 项目 | 值 |
|------|-----|
| 请求方法 | GET |
| 请求路径 | `/api/v1/user/profile` |
| 请求头 | `Authorization: Bearer <token>` |
| 返回 | 用户信息 |

---

## 四、完整请求流程

```
用户未登录访问 /api/v1/user/profile
    ↓
拦截器 preHandle() 执行
    ↓
从请求头获取 Authorization: Bearer xxx
    ↓
检查 Token 存在？→ 不存在 → 返回 401 "请先登录"
    ↓
Token 格式正确？→ 否 → 返回 401 "请先登录"
    ↓
Token 过期？→ 是 → 返回 401 "登录已过期"
    ↓
解析 Token 获取 userId = 1
    ↓
存入 UserContext.userId = 1
    ↓
preHandle() 返回 true，放行
    ↓
Controller 收到请求
    ↓
调用 Service.getCurrentUser()
    ↓
Service 从 UserContext 获取 userId = 1
    ↓
Service 查询数据库获取用户信息
    ↓
Service 返回 UserVO 给 Controller
    ↓
Controller 返回 JSON 给前端
    ↓
拦截器 afterCompletion() 执行
    ↓
清理 UserContext.remove()
```

---

## 五、后续开发计划

- [ ] 第 3 步：搭建前端 Vue3 项目骨架
- [ ] 第 4 步：模板管理模块
- [ ] 第 5 步：简历 CRUD 模块
- [ ] 第 6 步：简历编辑器
- [ ] 第 7 步：AI 生成简历功能
- [ ] 第 8 步：简历导出 PDF