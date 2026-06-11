package com.zhufeng.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // 密钥（至少32位）
    private static final String SECRET = "zhufeng-resume-secret-key-2024-very-long-secret";
    // 默认过期时间（7天）
    private static final long DEFAULT_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // 生成 Token（默认 7 天）
    public static String generateToken(Long userId, String username) {
        return generateToken(userId, username, Duration.ofMillis(DEFAULT_EXPIRE_TIME));
    }

    // 生成 Token（自定义过期时长）
    public static String generateToken(Long userId, String username, Duration ttl) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        long expireMs = ttl != null ? ttl.toMillis() : DEFAULT_EXPIRE_TIME;
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(getKey())
                .compact();
    }

    // 解析 Token
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 获取用户ID
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    // 获取用户名
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    // 验证 Token 是否过期
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}