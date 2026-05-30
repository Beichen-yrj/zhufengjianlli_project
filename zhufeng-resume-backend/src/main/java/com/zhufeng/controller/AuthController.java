package com.zhufeng.controller;

import com.zhufeng.common.Result;
import com.zhufeng.dto.LoginDTO;
import com.zhufeng.dto.RegisterDTO;
import com.zhufeng.service.AuthService;
import com.zhufeng.vo.LoginVO;
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

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return Result.success(loginVO);
    }
}