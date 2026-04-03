package com.project.market_service.wish.presentation.dto;

import com.project.market_service.product.domain.ProductStatus;
import java.math.BigDecimal;

public record WishResponse(
        Long productId,
        String productName,
        Long sellerId,
        String sellerName,
        BigDecimal price,
        Long wishCount,
        ProductStatus productStatus
) {
}
