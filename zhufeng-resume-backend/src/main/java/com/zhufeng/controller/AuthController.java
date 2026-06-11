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
     * @param expireDays Token 有效天数（前端传入，默认7天）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto,
                                  @RequestParam(defaultValue = "7") int expireDays) {
        LoginVO loginVO = authService.login(dto, Math.max(expireDays, 1));
        return Result.success(loginVO);
    }

    /**
     * 登出（从 Redis 删除 Token，使其立即失效）
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            authService.logout(token);
        }
        return Result.success("已退出登录");
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
