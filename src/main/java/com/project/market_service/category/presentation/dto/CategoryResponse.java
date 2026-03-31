package com.project.market_service.category.presentation.dto;

import com.project.market_service.category.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(
        @Schema(description = "카테고리 고유 식별 변호", example = "1")
        Long categoryId,

        @Schema(description = "카테고리 명", example = "가전제품")
        String categoryName,

        @Schema(description = "부모 카테고리 ID (최상위일 경우 null)", example = "null")
        Long parentId
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null
        );
    }
}
