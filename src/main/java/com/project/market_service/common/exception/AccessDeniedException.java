package com.project.market_service.common.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
