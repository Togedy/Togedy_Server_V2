package com.togedy.togedy_server_v2.domain.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + userId, refreshToken, Duration.ofDays(7));
    }

    public Optional<String> findByUserId(Long userId) {
        return Optional.of(redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId));
    }

    public Boolean deleteByUserId(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId));
    }
}
