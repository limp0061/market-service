package com.project.market_service.chatroom.application.port.out;

import com.project.market_service.chatroom.domain.ChatRoomUser;
import java.time.LocalDateTime;
import java.util.List;

public interface ChatRoomUserPort {
    LocalDateTime getLastReadAt(Long roomId, Long userId);

    List<ChatRoomUser> saveAllChatRoomUser(Iterable<ChatRoomUser> chatRoomUsers);

    void updateLastReadAt(Long roomId, Long userId);
}
