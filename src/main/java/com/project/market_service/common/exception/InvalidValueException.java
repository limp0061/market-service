package com.project.market_service.common.exception;

import lombok.Getter;

@Getter
public class InvalidValueException extends BusinessException {
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidValueException(ErrorCode errorCode, Object data) {
        super(errorCode, data);
    }
}
