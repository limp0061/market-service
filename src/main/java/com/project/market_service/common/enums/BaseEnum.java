package com.project.market_service.common.enums;

public interface BaseEnum {

    String getCode();

    default String getDesc() {
        return "";
    }

}
