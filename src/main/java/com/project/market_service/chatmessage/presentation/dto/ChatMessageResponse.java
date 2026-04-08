package com.project.market_service.chatmessage.presentation.dto;

import com.project.market_service.chatmessage.domain.ChatMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ChatMessageResponse(
        Long roomId,
        Long senderId,
        String senderName,
        String content,
        String timestamp
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ChatMessageResponse of(ChatMessage chatMessage, String senderName) {
        return new ChatMessageResponse(
                chatMessage.getRoomId(),
                chatMessage.getSenderId(),
                senderName,
                chatMessage.getContent(),
                chatMessage.getCreatedAt() != null ? chatMessage.getCreatedAt().format(FORMATTER)
                        : LocalDateTime.now().format(FORMATTER)
        );
    }
}
