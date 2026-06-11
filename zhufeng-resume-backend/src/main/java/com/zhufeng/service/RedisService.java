package com.zhufeng.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务 - 统一管理 Token / Session / 限流 等缓存操作
 *
 * Key 命名规范：
 *   token:{tokenValue}     → 用户ID (用于校验Token是否有效)
 *   token:user:{userId}:*  → 该用户的所有 Token（用于一键清空）
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
     * @param ttl        过期时长
     */
    public void saveToken(String token, Long userId, String username, Duration ttl) {
        String value = userId + ":" + username;
        // 1. 存主 token 索引（用于校验）
        redisTemplate.opsForValue().set("token:" + token, value, ttl);
        // 2. 存反向索引（用于一键清除某用户的所有 token）
        redisTemplate.opsForSet().add("token:user:" + userId, token);
        redisTemplate.expire("token:user:" + userId, ttl);
    }

    /**
     * 校验 Token 是否有效（拦截器中调用）
     *
     * @return 有效返回 "userId:username"，无效/不存在返回 null
     */
    public String validateToken(String token) {
        return redisTemplate.opsForValue().get("token:" + token);
    }

    /**
     * 删除 Token（登出时调用，使Token立即失效）
     */
    public void deleteToken(String token) {
        // 1. 先解析 token 拿到 userId（从 token 字符串中获取）
        String value = redisTemplate.opsForValue().get("token:" + token);
        redisTemplate.delete("token:" + token);

        // 2. 从反向索引中删除
        if (value != null && value.contains(":")) {
            try {
                Long uid = Long.parseLong(value.split(":")[0]);
                redisTemplate.opsForSet().remove("token:user:" + uid, token);
            } catch (NumberFormatException ignore) {}
        }
    }

    /**
     * 删除某用户的所有 Token（强制下线）
     */
    public void deleteUserTokens(Long userId) {
        String key = "token:user:" + userId;
        Set<String> tokens = redisTemplate.opsForSet().members(key);
        if (tokens != null && !tokens.isEmpty()) {
            // 删除主索引
            for (String t : tokens) {
                redisTemplate.delete("token:" + t);
            }
        }
        // 删除反向索引
        redisTemplate.delete(key);
    }

    /**
     * 获取 Token 剩余过期时间（秒），-2 表示 key 不存在
     */
    public long getTokenTtl(String token) {
        return redisTemplate.getExpire("token:" + token, TimeUnit.SECONDS);
    }

    // ===== Session 管理 =====

    /**
     * 存储用户 Session
     */
    public void saveSession(Long userId, Object sessionData, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(sessionData);
            redisTemplate.opsForValue().set("session:" + userId, json, ttl);
        } catch (Exception e) {
            throw new RuntimeException("序列化 Session 失败", e);
        }
    }

    /**
     * 获取用户 Session
     */
    public <T> T getSession(Long userId, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get("session:" + userId);
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
        String countStr = redisTemplate.opsForValue().get("login_limit:" + username);
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
