package com.project.market_service.product.domain;

import com.project.market_service.common.enums.BaseEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus implements BaseEnum {

    SELLING("selling", "판매 중"),
    RESERVED("reserved", "예약 중"),
    INACTIVE("inactive", "판매 중지"),
    SOLD("sold", "판매 완료"),
    DELETED("deleted", "삭제됨");

    private final String code;
    private final String desc;

    public boolean canUpdateState(ProductStatus status) {
        return switch (this) {
            case SELLING -> List.of(RESERVED, INACTIVE, DELETED).contains(status);
            case RESERVED -> List.of(SELLING, SOLD).contains(status);
            case INACTIVE -> List.of(SELLING, DELETED).contains(status);
            default -> false;
        };
    }
}
