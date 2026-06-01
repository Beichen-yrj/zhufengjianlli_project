package com.zhufeng.controller;

import com.zhufeng.common.Result;
import com.zhufeng.service.UserService;
import com.zhufeng.vo.UserVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 获取当前登录用户的信息
    // 路径：GET /api/v1/user/profile
    // 需要登录才能访问
    @GetMapping("/profile")
    public Result<UserVO> getCurrentUser() {
        UserVO userVO = userService.getCurrentUser();
        return Result.success(userVO);
    }
}