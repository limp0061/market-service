package com.project.market_service.chatmessage.presentation.dto;

public record ChatMessageResponse(
        Long roomId,
        Long senderId,
        String senderName,
        String content,
        String timestamp
) {
}
