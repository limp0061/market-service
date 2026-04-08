package com.project.market_service.chatmessage.domain;

import com.project.market_service.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chatting_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {

    @Id
    private String id;

    @Field("room_id")
    private Long roomId;

    @Field("sender_id")
    private Long senderId;

    private String content;

    public static ChatMessage create(Long roomId, Long senderId, String content) {
        return ChatMessage.builder()
                .roomId(roomId)
                .senderId(senderId)
                .content(content)
                .build();
    }

    @Builder
    public ChatMessage(Long roomId, Long senderId, String content) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
    }
}
