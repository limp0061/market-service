package com.project.market_service.product.presentation.dto;

import com.project.market_service.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public record UpdateProductStatusRequest(
        @Schema(description = "변경하려는 상품상태", example = "reserved", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "변경하려는 상품상태를 입력해주세요")
        ProductStatus productStatus
) {
}
