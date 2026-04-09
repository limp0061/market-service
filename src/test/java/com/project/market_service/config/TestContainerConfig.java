package com.project.market_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    @Bean
    @ServiceConnection // 주소를 자동으로 매핑해줌
    public MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>("mysql:8.4");
    }

    @Bean
    @ServiceConnection
    public MongoDBContainer mongoContainer() {
        return new MongoDBContainer("mongo:6.0");
    }

    @Bean
    @ServiceConnection("redis")
    public GenericContainer<?> redisContainer() {
        return new GenericContainer<>("redis:7.2-alpine")
                .withExposedPorts(6379);
    }
}
