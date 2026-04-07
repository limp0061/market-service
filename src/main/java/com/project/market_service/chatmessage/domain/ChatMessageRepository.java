package com.project.market_service.chatmessage.domain;

import java.util.List;

public interface ChatMessageRepository {

    ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> findByRoomId(Long roomId);
}
