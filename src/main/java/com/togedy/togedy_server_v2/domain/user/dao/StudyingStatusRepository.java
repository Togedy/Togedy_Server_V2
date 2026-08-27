package com.togedy.togedy_server_v2.domain.user.dao;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudyingStatusRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String STUDYING_STATUS_PREFIX = "studyingUser:";
    private static final long STUDYING_STATUS_TTL = 150;

    public void save(Long userId) {
        redisTemplate.opsForValue()
                .set(STUDYING_STATUS_PREFIX + userId,
                        String.valueOf(userId),
                        Duration.ofSeconds(STUDYING_STATUS_TTL)
                );
    }

    public boolean isExist(Long userId) {
        return redisTemplate.hasKey(STUDYING_STATUS_PREFIX + userId);
    }

    public void delete(Long userId) {
        redisTemplate.delete(STUDYING_STATUS_PREFIX + userId);
    }
}
