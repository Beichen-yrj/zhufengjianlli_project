package com.zhufeng.config;

import com.zhufeng.interceptor.JwtAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // 标记为配置类
public class WebMvcConfig implements WebMvcConfigurer {

    // 注入拦截器
    private final JwtAuthInterceptor jwtAuthInterceptor;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                // 拦截所有 API
                .addPathPatterns("/api/v1/**")
                // 排除：登录、注册（不需要认证）
                .excludePathPatterns(
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
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