package com.project.market_service.user.domain;

import com.project.market_service.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 입니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "중복된 로그인 아이디 입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
