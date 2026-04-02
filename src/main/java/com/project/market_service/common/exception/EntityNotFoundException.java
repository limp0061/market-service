package com.project.market_service.common.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityNotFoundException(ErrorCode errorCode, Object data) {
        super(errorCode, data);
    }
}
