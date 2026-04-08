package com.project.market_service.chatroom.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chat_room_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomUser {

    @Id
    private String id;

    @Field("room_id")
    private Long roomId;

    @Field("user_id")
    private Long userId;

    @Field("last_read_at")
    private LocalDateTime lastReadAt;

    public static ChatRoomUser create(Long roomId, Long userId) {
        return ChatRoomUser.builder()
                .roomId(roomId)
                .userId(userId)
                .lastReadAt(LocalDateTime.now())
                .build();
    }

    @Builder
    public ChatRoomUser(Long roomId, Long userId, LocalDateTime lastReadAt) {
        this.roomId = roomId;
        this.userId = userId;
        this.lastReadAt = lastReadAt;
    }
}
