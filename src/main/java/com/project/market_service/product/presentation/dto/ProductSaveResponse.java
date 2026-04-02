package com.project.market_service.product.presentation.dto;

import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record ProductSaveResponse(

        @Schema(description = "상품 식별 고유 번호", example = "1")
        Long productId,

        @Schema(description = "상품 명", example = "상품 등록")
        String productName,

        @Schema(description = "상품 상태", example = "SELLING")
        ProductStatus productStatus,

        @Schema(description = "상품 등록 일자", example = "2026-03-31T10:00:00")
        LocalDateTime createdAt
) {


    public static ProductSaveResponse from(Product product) {
        return new ProductSaveResponse(
                product.getId(),
                product.getName(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }
}
