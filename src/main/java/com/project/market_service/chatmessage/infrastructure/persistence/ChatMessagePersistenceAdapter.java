package com.project.market_service.chatmessage.infrastructure.persistence;

import com.project.market_service.chatmessage.application.port.out.ChatMessageRepository;
import com.project.market_service.chatmessage.domain.ChatMessage;
import com.project.market_service.chatmessage.presentation.dto.ChatPagingRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessagePersistenceAdapter implements ChatMessageRepository {

    private final MongoChatMessageRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ChatMessage save(ChatMessage message) {
        return mongoRepository.save(message);
    }

    @Override
    public List<ChatMessage> findByRoomId(Long roomId) {
        return mongoRepository.findByRoomId(roomId);
    }

    @Override
    public List<ChatMessage> findMessagesByRoomId(ChatPagingRequest request) {
        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(request.roomId()));

        if (request.lastMessageId() != null && !request.lastMessageId().isBlank()) {
            query.addCriteria(Criteria.where("_id").lt(new ObjectId(request.lastMessageId())));
        }

        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(request.size());

        return mongoTemplate.find(query, ChatMessage.class);
    }
}