package com.project.market_service.chatroom.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public record ChatRoomCreatRequest(

        @Schema(description = "상품 고유 식별 번호", example = "1", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "상품 ID는 필수 입니다")
        Long productId
) {
}
