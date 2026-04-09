package com.project.market_service.chatmessage.application.port.out;

import com.project.market_service.chatmessage.domain.ChatMessage;
import com.project.market_service.chatmessage.presentation.dto.ChatPagingRequest;
import java.util.List;

public interface ChatMessagePort {

    ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> findByRoomId(Long roomId);

    List<ChatMessage> findMessagesByRoomId(ChatPagingRequest request);
}
