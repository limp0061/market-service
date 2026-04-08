package com.project.market_service.chatmessage.application.port.out;

import com.project.market_service.chatmessage.domain.ChatMessage;
import java.util.List;

public interface ChatMessageRepository {

    ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> findByRoomId(Long roomId);
}
