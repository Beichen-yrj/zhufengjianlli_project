package com.zhufeng.interceptor;

import com.zhufeng.common.JwtUtil;
import com.zhufeng.common.Result;
import com.zhufeng.common.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component  // 让 Spring 管理这个类
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 第1步：从请求头获取 Token（格式：Bearer <token>）
        String authHeader = request.getHeader("Authorization");
        
        // 第2步：检查 Token 是否存在
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedResponse(response, "请先登录");
            return false;  // 拦截请求，不往下执行
        }
        
        // 去掉 "Bearer " 前缀，得到真正的 Token
        String token = authHeader.substring(7);
        
        try {
            // 第3步：验证 Token 是否过期
            if (JwtUtil.isTokenExpired(token)) {
                sendUnauthorizedResponse(response, "登录已过期，请重新登录");
                return false;
            }
            
            // 第4步：解析 Token，获取用户 ID
            Long userId = JwtUtil.getUserId(token);
            
            // 第5步：把用户 ID 存入 UserContext（类似把身份证暂时存前台）
            UserContext.setUserId(userId);
            
            // 验证通过，放行
            return true;
            
        } catch (Exception e) {
            // Token 解析失败（被篡改或格式错误）
            sendUnauthorizedResponse(response, "Token 无效");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 第6步：请求处理完成后，清理 UserContext（类似退房时退身份证）
        // 防止内存泄漏
        UserContext.remove();
    }

    // 返回 401 错误响应
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);  // HTTP 401 状态码
        Result<?> result = Result.error(401, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}