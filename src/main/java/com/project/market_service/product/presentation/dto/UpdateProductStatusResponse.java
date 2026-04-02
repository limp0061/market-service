package com.project.market_service.product.presentation.dto;

import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductStatus;
import java.time.LocalDateTime;

public record UpdateProductStatusResponse(
        Long productId,
        ProductStatus productStatus,
        LocalDateTime updatedAt
) {

    public static UpdateProductStatusResponse from(Product product) {
        return new UpdateProductStatusResponse(
                product.getId(),
                product.getStatus(),
                product.getUpdatedAt()
        );
    }
}
