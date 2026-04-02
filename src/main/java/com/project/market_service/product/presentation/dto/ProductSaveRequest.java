package com.project.market_service.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.Range;

public record ProductSaveRequest(

        @Schema(description = "상품 카테고리", example = "1", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "상품 카테고리를 선택해주세요")
        Long categoryId,

        @Schema(description = "상품 명", example = "상품", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "상품 명을 입력해주세요")
        String name,

        @Schema(description = "상품 설명", example = "이 상품은 Product 입니다", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "상품 설명을 입력해주세요")
        String description,

        @Schema(description = "상품 가격", example = "100000", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "상품 가격을 입력해주세요")
        @DecimalMin(value = "0", message = "가격은 0원 이상이어야 합니다.")
        BigDecimal price,

        @Schema(description = "거래 희망 위도", example = "37.5665", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "위치 정보(위도)는 필수입니다.")
        @Range(min = -90, max = 90, message = "위도 범위가 올바르지 않습니다.")
        Double lat,

        @Schema(description = "거래 희망 경도", example = "126.9780", requiredMode = RequiredMode.REQUIRED)
        @Range(min = -180, max = 180, message = "경도 범위가 올바르지 않습니다.")
        @NotNull(message = "위치 정보(경도)는 필수입니다.")
        Double lng,

        @Schema(description = "위치", example = "풍무동", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "위치를 선택해주세요")
        String address
) {
}
