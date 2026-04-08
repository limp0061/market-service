package com.project.market_service.chatroom.application.port.in;

import com.project.market_service.chatroom.presentation.dto.ChatRoomResponse;
import java.util.List;

public interface ChatRoomUseCase {
    ChatRoomResponse createChatRoom(Long productId, Long buyerId);

    List<ChatRoomResponse> getMyChatRooms(Long userId);
}
