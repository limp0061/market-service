package com.project.market_service.chatroom.infrastructure.persistence;

import com.project.market_service.chatroom.domain.ChatRoomUser;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

interface MongoChatRoomUserRepository extends MongoRepository<ChatRoomUser, String> {
    Optional<ChatRoomUser> findByRoomIdAndUserId(Long roomId, Long userId);
}