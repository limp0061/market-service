package com.project.market_service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다"),
    FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "파일 업로드에 실패했습니다"),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "파일이 존재하지 않습니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않은 파일 타입입니다"),
    INVALID_FILE_NAME_EMPTY(HttpStatus.BAD_REQUEST, "파일 이름이 없습니다"),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "허용되지 않은 파일 확장자입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
