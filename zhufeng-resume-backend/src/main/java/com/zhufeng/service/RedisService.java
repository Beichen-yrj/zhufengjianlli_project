package com.zhufeng.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务 - 统一管理 Token / Session / 限流 等缓存操作
 *
 * Key 命名规范：
 *   token:{tokenValue}     → 用户ID (用于校验Token是否有效)
 *   session:userId         → 用户Session信息JSON
 *   login_limit:{username} → 登录失败次数 (防暴力破解)
 */
@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    // 注册 JavaTimeModule，支持 LocalDateTime / LocalDate 序列化
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ===== Token 管理 =====

    /**
     * 存储 Token 到 Redis（登录时调用）
     *
     * @param token      JWT Token
     * @param userId     用户ID
     * @param username   用户名
     * @param expireDays 过期天数
     */
    public void saveToken(String token, Long userId, String username, int expireDays) {
        String key = "token:" + token;
        String value = userId + ":" + username;
        redisTemplate.opsForValue().set(key, value, Duration.ofDays(expireDays));
    }

    /**
     * 校验 Token 是否有效（拦截器中调用）
     *
     * @return 有效返回 "userId:username"，无效/不存在返回 null
     */
    public String validateToken(String token) {
        String key = "token:" + token;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除 Token（登出时调用，使Token立即失效）
     */
    public void deleteToken(String token) {
        String key = "token:" + token;
        redisTemplate.delete(key);
    }

    /**
     * 获取 Token 剩余过期时间（秒），-2 表示 key 不存在
     */
    public long getTokenTtl(String token) {
        String key = "token:" + token;
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // ===== Session 管理 =====

    /**
     * 存储用户 Session
     */
    public void saveSession(Long userId, Object sessionData, Duration ttl) {
        try {
            String key = "session:" + userId;
            String json = objectMapper.writeValueAsString(sessionData);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (Exception e) {
            throw new RuntimeException("序列化 Session 失败", e);
        }
    }

    /**
     * 获取用户 Session
     */
    public <T> T getSession(Long userId, Class<T> clazz) {
        String key = "session:" + userId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("反序列化 Session 失败", e);
        }
    }

    /**
     * 删除用户 Session（踢人下线）
     */
    public void deleteSession(Long userId) {
        redisTemplate.delete("session:" + userId);
    }

    // ===== 登录限流（防暴力破解）=====

    /**
     * 记录一次登录失败，返回当前累计失败次数
     */
    public long incrementLoginFail(String username) {
        String key = "login_limit:" + username;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            // 首次失败，设置15分钟过期窗口
            redisTemplate.expire(key, Duration.ofMinutes(15));
        }
        return count != null ? count : 0;
    }

    /**
     * 登录成功后清除失败计数
     */
    public void clearLoginFail(String username) {
        redisTemplate.delete("login_limit:" + username);
    }

    /**
     * 检查是否被限流（15分钟内失败超过5次）
     */
    public boolean isLoginLimited(String username) {
        String key = "login_limit:" + username;
        String countStr = redisTemplate.opsForValue().get(key);
        if (countStr == null) return false;
        return Long.parseLong(countStr) >= 5;
    }

    // ===== 通用操作 =====

    /**
     * 删除指定 Key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 检查 Key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
