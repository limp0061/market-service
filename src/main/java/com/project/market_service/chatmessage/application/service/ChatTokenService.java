package com.project.market_service.chatmessage.application.service;

import com.project.market_service.chatmessage.application.port.out.ChatTokenCache;
import com.project.market_service.chatmessage.presentation.dto.ChatTokenResponse;
import com.project.market_service.common.util.UuidGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatTokenService {

    private final UuidGenerator uuidGenerator;
    private final ChatTokenCache chatTokenCache;

    public ChatTokenResponse createChatToken(Long userId) {

        String chatToken = uuidGenerator.generate();

        chatTokenCache.save(chatToken, userId);

        return new ChatTokenResponse(chatToken);
    }
}
