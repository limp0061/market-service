package com.project.market_service.common.exception;

import lombok.Getter;

@Getter
public class InvalidStateException extends BusinessException {

    private final ErrorCode errorCode;

    public InvalidStateException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
