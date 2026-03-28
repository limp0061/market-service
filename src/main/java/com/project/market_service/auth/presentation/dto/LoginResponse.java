package com.project.market_service.auth.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(

        @Schema(description = "사용자 고유 식별 변호", example = "1")
        Long userId,

        @Schema(description = "로그인 아이디", example = "hong1234")
        String loginId,

        @Schema(description = "Jwt Access Token")
        String accessToken,

        @Schema(description = "Jwt Refresh Token")
        String refreshToken
) {
}
