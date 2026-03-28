package com.project.market_service.auth.presentation.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.market_service.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record SignUpResponse(

        @Schema(description = "사용자 고유 식별 변호", example = "1")
        Long userId,

        @Schema(description = "사용자 이름", example = "홍길동")
        String userName,

        @Schema(description = "로그인 아이디", example = "hong1234")
        String loginId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt
) {
    public static SignUpResponse from(User user) {
        return new SignUpResponse(
                user.getId(),
                user.getName(),
                user.getLoginId(),
                user.getCreatedAt()
        );
    }
}
