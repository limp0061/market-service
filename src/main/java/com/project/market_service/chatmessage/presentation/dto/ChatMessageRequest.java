package com.project.market_service.chatmessage.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(

        @Schema(description = "채팅 메시지", example = "채팅 내역", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "채팅 메시지를 입력해주세요")
        String content
) {
}
