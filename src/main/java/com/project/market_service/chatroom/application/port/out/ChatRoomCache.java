package com.project.market_service.chatroom.application.port.out;

public interface ChatRoomCache {
    void addParticipants(Long roomId, Long... values);

    boolean isUsersInRoom(Long roomId, Long userId);

    void updateActiveRoom(Long userId, Long roomId);
}
