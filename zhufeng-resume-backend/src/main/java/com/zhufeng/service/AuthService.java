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
    private final RedisService redisService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserMapper userMapper, RedisService redisService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
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

    /**
     * 登录（集成 Redis Token 存储 + 登录限流）
     *
     * @param dto       登录信息
     * @param expireDays Token 有效天数（前端传入，记住登录=7，否则=3小时）
     */
    public LoginVO login(LoginDTO dto, int expireDays) {
        String username = dto.getUsername();

        // 检查是否被限流（15分钟内失败5次）
        if (redisService.isLoginLimited(username)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(),
                "登录尝试次数过多，请15分钟后再试");
        }

        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            handleLoginFail(username); // 记录失败
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            handleLoginFail(username); // 记录失败
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "账号已被禁用");
        }

        // 登录成功 → 清除限流计数
        redisService.clearLoginFail(username);

        // 生成 JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 存储到 Redis（Token 黑名单/白名单机制）
        redisService.saveToken(token, user.getId(), user.getUsername(), expireDays);

        // 存储用户 Session 到 Redis
        redisService.saveSession(user.getId(), user, java.time.Duration.ofDays(expireDays));

        // 构建返回
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return LoginVO.builder()
                .token(token)
                .user(userVO)
                .build();
    }

    /**
     * 处理登录失败（记录+限流检查）
     */
    private void handleLoginFail(String username) {
        long failCount = redisService.incrementLoginFail(username);
        int remaining = 5 - (int) failCount;
        if (remaining > 0 && remaining <= 2) {
            // 接近限流阈值时提示剩余次数
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                "用户名或密码错误，还可尝试 " + remaining + " 次");
        }
    }

    /**
     * 登出（从 Redis 删除 Token + Session，使登录态彻底失效）
     */
    public void logout(String token) {
        // 1. 从 Token 解析出 userId，用于删除 Session
        Long userId = null;
        try {
            userId = JwtUtil.getUserId(token);
        } catch (Exception ignore) {
            // Token 解析失败也继续走删除流程
        }

        // 2. 删除 Token 白名单（让 Token 立即失效）
        redisService.deleteToken(token);

        // 3. 删除用户 Session（避免脏数据残留）
        if (userId != null) {
            redisService.deleteSession(userId);
        }
    }
}
