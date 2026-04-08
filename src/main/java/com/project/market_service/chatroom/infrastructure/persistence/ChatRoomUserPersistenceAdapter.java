package com.project.market_service.chatroom.infrastructure.persistence;

import com.project.market_service.chatroom.application.port.out.ChatRoomUserRepository;
import com.project.market_service.chatroom.domain.ChatRoomUser;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomUserPersistenceAdapter implements ChatRoomUserRepository {

    private final MongoChatRoomUserRepository mongoChatRoomUserRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public LocalDateTime getLastReadAt(Long roomId, Long userId) {
        return mongoChatRoomUserRepository.findByRoomIdAndUserId(roomId, userId)
                .map(ChatRoomUser::getLastReadAt)
                .orElse(null);
    }

    @Override
    public List<ChatRoomUser> saveAllChatRoomUser(Iterable<ChatRoomUser> chatRoomUsers) {
        return mongoChatRoomUserRepository.saveAll(chatRoomUsers);
    }

    @Override
    public void updateLastReadAt(Long roomId, Long userId) {
        Query query = new Query(
                Criteria.where("roomId").is(roomId)
                        .and("userId").is(userId)
        );

        Update update = new Update().set("lastReadAt", LocalDateTime.now());

        mongoTemplate.updateFirst(query, update, ChatRoomUser.class);
    }
}
