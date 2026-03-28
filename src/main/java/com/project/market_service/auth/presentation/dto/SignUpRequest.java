package com.project.market_service.auth.presentation.dto;

import com.project.market_service.auth.presentation.annotation.ValidLoginId;
import com.project.market_service.auth.presentation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(

        @Schema(description = "사용자 이름", example = "홍길동", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "사용자 이름을 입력해주세요.")
        String userName,

        @Schema(description = "로그인 아이디", example = "hong1234", requiredMode = RequiredMode.REQUIRED)
        @ValidLoginId
        String loginId,

        @Schema(description = "비밀번호", example = "password1234!", requiredMode = RequiredMode.REQUIRED)
        @ValidPassword
        String password
) {
}
