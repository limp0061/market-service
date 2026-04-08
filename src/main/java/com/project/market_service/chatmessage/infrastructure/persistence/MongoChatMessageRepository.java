package com.project.market_service.chatmessage.infrastructure.persistence;

import com.project.market_service.chatmessage.domain.ChatMessage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

interface MongoChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRoomId(Long roomId);
}
