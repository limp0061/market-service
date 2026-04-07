package com.project.market_service.chatroom.application.service;

import com.project.market_service.chatroom.application.dto.ChatRoomParticipants;
import com.project.market_service.chatroom.application.port.out.ChatRoomCache;
import com.project.market_service.chatroom.application.port.out.ChatRoomRepository;
import com.project.market_service.chatroom.exception.ChatRoomErrorCode;
import com.project.market_service.common.exception.UnAuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomValidator {

    private final ChatRoomCache chatRoomCache;
    private final ChatRoomRepository chatRoomRepository;

    public void validateUserInRoom(Long roomId, Long userId) {

        if (chatRoomCache.isUsersInRoom(roomId, userId)) {
            return;
        }

        ChatRoomParticipants chatRoomParticipants = chatRoomRepository.findParticipantsByRoomId(roomId, userId)
                .orElseThrow(() -> new UnAuthorizationException(ChatRoomErrorCode.NOT_CHATROOM_PARTICIPANT));

        chatRoomCache.addParticipants(roomId, chatRoomParticipants.buyerId(), chatRoomParticipants.sellerId());
    }
}
