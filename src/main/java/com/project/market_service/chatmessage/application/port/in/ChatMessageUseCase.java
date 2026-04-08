package com.project.market_service.chatmessage.application.port.in;

import com.project.market_service.chatmessage.presentation.dto.ChatMessageRequest;

public interface ChatMessageUseCase {
    void processHandleMessage(ChatMessageRequest request, Long roomId, Long userId);
}
