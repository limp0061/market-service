package com.project.market_service.category.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CategorySaveRequest(

        @Schema(description = "카테고리 명", example = "가전제품", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "카테고리 명을 입력해주세요")
        String name,

        @Schema(description = "상위 카테고리 ID (최상위 카테고리인 경우 null)", example = "1", nullable = true)
        @Positive(message = "부모 카테고리 ID는 양수여야 합니다")
        Long parentId
) {
}
