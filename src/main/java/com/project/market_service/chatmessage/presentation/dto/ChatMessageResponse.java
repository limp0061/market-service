package com.project.market_service.chatmessage.presentation.dto;

import com.project.market_service.chatmessage.domain.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ChatMessageResponse(

        @Schema(description = "채팅 고유 식별 번호", example = "69d5b7553ba4c801fe2a357f")
        String messageId,

        @Schema(description = "채팅방 고유 식별 번호", example = "1")
        Long roomId,

        @Schema(description = "채팅 보내는 사람 고유 식별 번호", example = "1")
        Long senderId,

        @Schema(description = "채팅 보내는 사람 이름", example = "홍길동")
        String senderName,

        @Schema(description = "채팅 메시지", example = "채팅 내역")
        String content,

        @Schema(description = "채팅 시간", example = "2026-04-08 11:04:52")
        String timestamp
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ChatMessageResponse of(ChatMessage chatMessage, String senderName) {
        return new ChatMessageResponse(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                chatMessage.getSenderId(),
                senderName,
                chatMessage.getContent(),
                chatMessage.getCreatedAt() != null ? chatMessage.getCreatedAt().format(FORMATTER)
                        : LocalDateTime.now().format(FORMATTER)
        );
    }
}
