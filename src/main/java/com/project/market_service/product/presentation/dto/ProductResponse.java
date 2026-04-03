package com.project.market_service.product.presentation.dto;

import com.project.market_service.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        @Schema(description = "상품 고유 식별 번호", example = "1")
        Long productId,
        @Schema(description = "판매자 명", example = "홍길동")
        String sellerName,
        @Schema(description = "카테고리 명", example = "가전제품")
        String categoryName,
        @Schema(description = "상품 명", example = "냉장고")
        String productName,
        @Schema(description = "상품 가격", example = "1200000")
        BigDecimal price,
        @Schema(description = "상품 상태", example = "SELLING")
        ProductStatus productStatus,
        @Schema(description = "조회 수", example = "120")
        Long viewCount,
        @Schema(description = "찜하기 수", example = "15")
        Long wishCount,
        @Schema(description = "장소", example = "풍무동")
        String address,
        @Schema(description = "거리", example = "1.5")
        Double distance,
        @Schema(description = "상품 등록 일자", example = "2026-04-02T13:49:00")
        LocalDateTime createdAt
) {
}
