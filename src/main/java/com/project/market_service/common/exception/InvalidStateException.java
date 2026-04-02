package com.project.market_service.common.exception;

import lombok.Getter;

@Getter
public class InvalidStateException extends BusinessException {
    public InvalidStateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidStateException(ErrorCode errorCode, Object data, ErrorCode errorCode1) {
        super(errorCode, data);
    }
}
