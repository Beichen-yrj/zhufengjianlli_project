package com.zhufeng.interceptor;

import com.zhufeng.common.JwtUtil;
import com.zhufeng.common.Result;
import com.zhufeng.common.UserContext;
import com.zhufeng.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器（Redis 增强版）
 *
 * 校验流程：
 * 1. 解析请求头 Token
 * 2. 先查 Redis 白名单（Token 是否存在且未过期）
 * 3. 再解析 JWT 获取用户ID
 * 4. 双重校验通过后放行
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final RedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthInterceptor(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 第1步：从请求头获取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "请先登录");
            return false;
        }

        String token = authHeader.substring(7);

        try {
            // 第2步：查 Redis 白名单（Token 是否在 Redis 中存在）
            String tokenData = redisService.validateToken(token);
            if (tokenData == null) {
                // Redis 中不存在 → Token 可能被吊销/已过期/不存在
                sendUnauthorized(response, "登录已失效，请重新登录");
                return false;
            }

            // 第3步：解析 JWT 获取用户 ID（双重校验）
            Long userId = JwtUtil.getUserId(token);

            // 第4步：设置用户上下文
            UserContext.setUserId(userId);

            // 放行
            return true;

        } catch (Exception e) {
            sendUnauthorized(response, "Token 无效或已过期");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        Result<?> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
