package com.project.market_service.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfig {

    private RedisServer redisServer;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    @PostConstruct
    public void startRedisServer() throws IOException {
        redisServer = RedisServer.newRedisServer()
                .port(redisPort)
                .build();

        redisServer.start();
    }

    @PreDestroy
    public void stopRedisServer() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}

