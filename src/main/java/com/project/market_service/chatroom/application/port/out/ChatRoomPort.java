package com.project.market_service.chatroom.application.port.out;

import com.project.market_service.chatroom.application.dto.ChatRoomParticipants;
import com.project.market_service.chatroom.domain.ChatRoom;
import com.project.market_service.chatroom.presentation.dto.ChatRoomResponse;
import java.util.List;
import java.util.Optional;

public interface ChatRoomPort {
    Optional<ChatRoom> findByProductIdAndBuyerId(Long productId, Long buyerId);

    ChatRoom save(ChatRoom chatRoom);

    List<ChatRoomResponse> getMyChatRooms(Long userId);

    Optional<ChatRoom> findById(Long id);

    List<ChatRoom> saveAll(Iterable<ChatRoom> chatRooms);

    Optional<ChatRoomParticipants> findParticipantsByRoomId(Long roomId, Long userId);
}
