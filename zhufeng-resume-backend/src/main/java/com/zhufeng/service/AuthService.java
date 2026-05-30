package com.zhufeng.service;

import com.zhufeng.common.BusinessException;
import com.zhufeng.common.JwtUtil;
import com.zhufeng.common.ResultCode;
import com.zhufeng.dto.LoginDTO;
import com.zhufeng.dto.RegisterDTO;
import com.zhufeng.entity.User;
import com.zhufeng.mapper.UserMapper;
import com.zhufeng.vo.LoginVO;
import com.zhufeng.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 注册
    public void register(RegisterDTO dto) {
        // 检查用户名是否存在
        User existUser = userMapper.findByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名已存在");
        }

        // 密码加密
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());

        userMapper.insert(user);
    }

    // 登录
    public LoginVO login(LoginDTO dto) {
        // 查找用户
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "账号已被禁用");
        }

        // 生成 Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建返回
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setToken(token);

        return LoginVO.builder()
                .token(token)
                .user(userVO)
                .build();
    }
}