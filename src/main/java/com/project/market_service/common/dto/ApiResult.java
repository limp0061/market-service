package com.project.market_service.common.dto;

import com.project.market_service.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

public record ApiResult<T>(

        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,

        @Schema(description = "응답 데이터")
        T data,

        @Schema(description = "에러 코드 (실패 시)")
        String code,

        @Schema(description = "에러 메시지 (실패 시)")
        String message
) {
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, data, null, null);
    }

    public static ApiResult<Void> error(ErrorCode errorCode) {
        return new ApiResult<>(false, null, errorCode.name(), errorCode.getMessage());
    }

    public static <T> ApiResult<T> error(T data, ErrorCode errorCode) {
        return new ApiResult<>(false, data, errorCode.name(), errorCode.getMessage());
    }
}
