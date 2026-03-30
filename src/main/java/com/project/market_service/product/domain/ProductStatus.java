package com.project.market_service.product.domain;

import com.project.market_service.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus implements BaseEnum {

    SELLING("selling", "판매 중"),
    RESERVED("reserved", "예약"),
    COMPLETED("completed", "완료");

    private final String code;
    private final String desc;
}
