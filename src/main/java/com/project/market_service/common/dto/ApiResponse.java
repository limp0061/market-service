package com.project.market_service.common.dto;

import com.project.market_service.common.exception.ErrorCode;

public record ApiResponse<T>(
        boolean success,
        T data,
        String code,
        String message
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, errorCode.name(), errorCode.getMessage());
    }

    public static <T> ApiResponse<T> error(T data, ErrorCode errorCode) {
        return new ApiResponse<>(false, data, errorCode.name(), errorCode.getMessage());
    }
}
