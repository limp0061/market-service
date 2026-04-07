package com.project.market_service.chatmessage.presentation.dto;

public record ChatMessageRequest(
        Long roomId,
        Long senderId,
        String content
) {
}
