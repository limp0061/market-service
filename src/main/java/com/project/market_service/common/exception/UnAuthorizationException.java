package com.project.market_service.common.exception;

import lombok.Getter;

@Getter
public class UnAuthorizationException extends BusinessException {
    public UnAuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
