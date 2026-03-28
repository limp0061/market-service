package com.project.market_service.auth.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(

        @Schema(description = "Jwt Access Token")
        String accessToken,

        @Schema(description = "Jwt Refresh Token")
        String refreshToken
) {
}
