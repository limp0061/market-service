package com.project.market_service.common.exception;

import lombok.Getter;

@Getter
public class DuplicateException extends BusinessException {
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateException(ErrorCode errorCode, Object data) {
        super(errorCode, data);

    }
}
