package com.project.market_service.common.dto;

public record FieldErrorDetail(
        String field,
        String reason
) {
}
