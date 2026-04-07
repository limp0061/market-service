package com.project.market_service.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    // ==== 객체 ====
    public void set(String key, Object value, long duration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, duration, timeUnit);
    }

    public void addToSet(String key, Object... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? objectMapper.convertValue(value, clazz) : null;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean isMember(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    public void expire(String key, int duration, TimeUnit timeUnit) {
        redisTemplate.expire(key, duration, timeUnit);
    }

    // ==== 카운터 / 문자열 ====
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public String getCount(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit)
        );
    }

    public List<String> scanKeys(String pattern, int count) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(count).build();

        List<String> keys = new ArrayList<>();
        try (Cursor<byte[]> cursor = stringRedisTemplate.getConnectionFactory().getConnection().scan(options)) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        } catch (Exception e) {
            log.error("[Redis Scan Error] pattern: {}", pattern, e);
        }

        return keys;
    }

    public void deleteKeys(Collection<String> keys) {
        if (keys.isEmpty()) {
            return;
        }
        stringRedisTemplate.delete(keys);
    }

}