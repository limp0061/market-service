package com.project.market_service.chatroom.domain;

import com.project.market_service.chatroom.presentation.dto.ChatRoomResponse;
import java.util.List;

public interface ChatRoomRepositoryCustom {
    List<ChatRoomResponse> getMyChatRooms(Long userId);
}
