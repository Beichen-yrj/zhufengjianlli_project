package com.zhufeng.config;

import com.zhufeng.interceptor.JwtAuthInterceptor;
import com.zhufeng.interceptor.NoCacheInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // 标记为配置类
public class WebMvcConfig implements WebMvcConfigurer {

    // 注入拦截器
    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final NoCacheInterceptor noCacheInterceptor;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor, NoCacheInterceptor noCacheInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.noCacheInterceptor = noCacheInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 禁缓存拦截器（优先级最高，先于鉴权执行）
        registry.addInterceptor(noCacheInterceptor)
                .addPathPatterns("/api/v1/**");

        // 2. JWT 鉴权拦截器
        registry.addInterceptor(jwtAuthInterceptor)
                // 拦截所有 API
                .addPathPatterns("/api/v1/**")
                // 排除：登录、注册（不需要认证）；登出无需先登录（已登录用户调）
                .excludePathPatterns(
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/api/v1/auth/logout",
                        "/api/v1/auth/logout-all",
                        "/api/v1/test/**",
                        "/api/v1/templates/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许跨域：让前端能访问后端接口
        registry.addMapping("/**")
                .allowedOriginPatterns("*")     // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")             // 允许所有请求头
                .allowCredentials(true)         // 允许携带凭证
                .maxAge(3600);                  // 预检请求缓存1小时
    }
}