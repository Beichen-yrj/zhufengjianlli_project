package com.zhufeng.controller;

import com.zhufeng.common.Result;
import com.zhufeng.dto.LoginDTO;
import com.zhufeng.dto.RegisterDTO;
import com.zhufeng.service.AuthService;
import com.zhufeng.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success("注册成功");
    }

    /**
     * 登录
     * @param dto 登录信息
     * @param expireDays Token 有效天数（前端传入，默认7天；最小支持 0.004 即 ~6 分钟）
     *                   0.125 = 3小时（不勾选"记住我"），1 = 1天，7 = 7天
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto,
                                  @RequestParam(defaultValue = "7") double expireDays) {
        LoginVO loginVO = authService.login(dto, expireDays);
        return Result.success(loginVO);
    }

    /**
     * 登出（从 Redis 删除 Token + Session，使登录态彻底失效）
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            authService.logout(token);
        }
        return Result.success("已退出登录");
    }

    /**
     * 强制下线（从 Token 中解析用户，销毁该用户所有 Token + Session）
     * 用于多设备/多账号切换时彻底清理登录态
     */
    @PostMapping("/logout-all")
    public Result<?> logoutAll(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            authService.logoutAll(token);
        }
        return Result.success("已退出所有设备");
    }

    /**
     * 校验 Token 是否有效（前端应用启动时调用）
     * 从请求头解析 Token，强制从 Token 获取当前用户，绝不信任前端传的 userId
     * 用于"刷新页面后重新验证登录态"场景
     */
    @GetMapping("/me")
    public Result<?> me(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return Result.error(401, "未登录");
        }
        return authService.validateAndGetUser(token);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
