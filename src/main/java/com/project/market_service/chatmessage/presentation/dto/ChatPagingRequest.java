package com.project.market_service.chatmessage.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public record ChatPagingRequest(

        @Schema(description = "채팅방 고유 식별 번호", example = "1", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "채팅방 아이디를 입력해주세요")
        Long roomId,

        @Schema(description = "마지막 채팅 메시지 고유 식별 번호", example = "69d5b7553ba4c801fe2a357f")
        String lastMessageId,

        @Schema(description = "채팅 갯수", example = "20")
        @NotNull(message = "화면에 표시할 채팅 갯수는 필수입니다")
        int size
) {
}
