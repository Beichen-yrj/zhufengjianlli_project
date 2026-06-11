package com.zhufeng.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 敏感响应头拦截器：所有 API 响应禁止浏览器/代理缓存
 *
 * 多账号登录场景：防止浏览器"返回"按钮展示上一个用户的数据
 */
@Component
public class NoCacheInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 禁止任何形式的缓存
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        // 防止 referrer 泄漏
        response.setHeader("Referrer-Policy", "no-referrer");
        return true;
    }
}
