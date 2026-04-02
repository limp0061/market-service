package com.project.market_service.product.exception;

import com.project.market_service.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),
    ALREADY_SOLD(HttpStatus.BAD_REQUEST, "이미 판매 종료된 상품입니다"),
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 상품입니다"),
    INVALID_PRODUCT_STATE(HttpStatus.BAD_REQUEST, "현재 상품 상태에서는 진행할 수 없는 요청입니다");
    private final HttpStatus httpStatus;
    private final String message;
}
