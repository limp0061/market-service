package com.project.market_service.category.exception;

import com.project.market_service.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements ErrorCode {

    DUPLICATE_CATEGORY(HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다"),
    PARENT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상위 카테고리입니다"),
    CATEGORY_CIRCULAR_REFERENCE(HttpStatus.BAD_REQUEST, "자기 자신이나 하위 카테고리를 부모로 지정할 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
