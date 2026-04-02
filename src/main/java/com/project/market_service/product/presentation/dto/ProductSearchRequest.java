package com.project.market_service.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ProductSearchRequest(
        @Schema(description = "카테고리 ID", example = "1")
        Long categoryId,

        @Schema(description = "검색 키워드", example = "노트북")
        String keyword,

        @Schema(description = "최소 가격", example = "5000")
        @Min(0)
        Long minPrice,

        @Schema(description = "최대 가격", example = "50000")
        @Min(0)
        Long maxPrice,

        @Schema(description = "현재 위도", example = "37.6")
        Double curLat,

        @Schema(description = "현재 경도", example = "126.7")
        Double curLng,

        @Schema(description = "위치 검색 범위(km), 최대 10km", example = "5.0")
        @Min(value = 1, message = "최소 검색 범위는 1km입니다.")
        @Max(value = 10, message = "최대 검색 범위는 10km입니다.")
        Double distance
) {
}
