package com.project.market_service.chatmessage.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatTokenResponse(
        @Schema(description = "채팅 토큰 (30초)")
        String chatToken
) {
}
