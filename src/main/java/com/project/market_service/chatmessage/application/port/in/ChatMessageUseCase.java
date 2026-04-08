package com.project.market_service.chatmessage.application.port.in;

import com.project.market_service.chatmessage.presentation.dto.ChatMessageRequest;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageResponse;
import com.project.market_service.chatmessage.presentation.dto.ChatPagingRequest;
import java.util.List;

public interface ChatMessageUseCase {
    void processHandleMessage(ChatMessageRequest request, Long roomId, Long userId);

    List<ChatMessageResponse> getChatMessages(ChatPagingRequest request, Long userId);
}
