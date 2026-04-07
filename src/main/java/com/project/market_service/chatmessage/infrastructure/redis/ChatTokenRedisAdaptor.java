package com.project.market_service.chatmessage.infrastructure.redis;

import static com.project.market_service.common.constants.RedisConstants.CHAT_TOKEN_PREFIX;

import com.project.market_service.chatmessage.domain.ChatTokenCache;
import com.project.market_service.common.redis.RedisManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatTokenRedisAdaptor implements ChatTokenCache {

    private final RedisManager redisManager;
    private static final long TOKEN_EXPIRATION_SEC = 30;

    @Override
    public void save(String token, Long userId) {
        String key = CHAT_TOKEN_PREFIX + token;
        redisManager.set(key, userId, TOKEN_EXPIRATION_SEC, TimeUnit.SECONDS);
    }
}
