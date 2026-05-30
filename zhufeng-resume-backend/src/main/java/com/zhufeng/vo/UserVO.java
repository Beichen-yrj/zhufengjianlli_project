package com.zhufeng.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String nickname;
    private String token;  // 登录成功后返回 Token
}