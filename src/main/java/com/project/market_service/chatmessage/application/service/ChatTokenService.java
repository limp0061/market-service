package com.project.market_service.chatmessage.application.service;

import static com.project.market_service.common.constants.RedisConstants.CHAT_TOKEN_PREFIX;

import com.project.market_service.chatmessage.presentation.dto.ChatTokenResponse;
import com.project.market_service.common.redis.RedisManager;
import com.project.market_service.common.util.UuidGenerator;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatTokenService {

    private final UuidGenerator uuidGenerator;
    private final RedisManager redisManager;

    public ChatTokenResponse createChatToken(Long userId) {

        String chatToken = uuidGenerator.generate();

        redisManager.set(CHAT_TOKEN_PREFIX + chatToken, userId, 30, TimeUnit.SECONDS);

        return new ChatTokenResponse(chatToken);
    }
}
