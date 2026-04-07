package com.project.market_service.chatmessage.infrastructure.persistence;

import com.project.market_service.chatmessage.domain.ChatMessage;
import com.project.market_service.chatmessage.domain.ChatMessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final MongoChatMessageRepository mongoRepository;

    @Override
    public ChatMessage save(ChatMessage message) {
        return mongoRepository.save(message);
    }

    @Override
    public List<ChatMessage> findByRoomId(Long roomId) {
        return mongoRepository.findByRoomId(roomId);
    }
}