package com.project.market_service.user.domain;

import com.project.market_service.common.enums.BaseEnum;
import com.project.market_service.common.exception.CommonErrorCode;
import com.project.market_service.common.exception.InvalidValueException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole implements BaseEnum {
    USER("USER", "사용자"),
    ADMIN("ADMIN", "관리자");

    private final String code;
    private final String desc;

    public static UserRole getByCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(CommonErrorCode.INVALID_INPUT));
    }
}
