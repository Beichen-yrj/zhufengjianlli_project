package com.zhufeng.controller;

import com.zhufeng.common.Result;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, 逐风简历平台!");
    }

    @GetMapping("/user/{id}")
    public Result<String> getUser(@PathVariable Long id) {
        if(id <= 0) {
            return Result.error(400, "用户ID必须大于0");
        }
        return Result.success("获取用户ID成功，用户ID：" + id);
    }
}
