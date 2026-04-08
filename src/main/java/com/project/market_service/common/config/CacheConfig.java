package com.project.market_service.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 1. 공통 기본 설정 (Default Configuration)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                // 키 생성 규칙: '캐시이름:키' 형태로 저장 (:: 대신 : 사용)
                .computePrefixWith(cacheName -> cacheName + ":")
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 값 저장: JSON 형태로 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                // DB에 없는 값(null)은 캐싱하지 않음 (Cache Ghost 방지)
                .disableCachingNullValues()
                // 기본 만료 시간: 1시간
                .entryTtl(Duration.ofHours(1));

        // 2. 캐시별 개별 설정 (Custom Configuration)
        Map<String, RedisCacheConfiguration> configurations = new HashMap<>();
        configurations.put("userNames", defaultConfig.entryTtl(Duration.ofDays(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configurations)
                .build();
    }
}
