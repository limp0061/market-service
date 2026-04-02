package com.project.market_service.category.presentation;

import com.project.market_service.category.application.service.CategoryService;
import com.project.market_service.category.presentation.dto.CategoryResponse;
import com.project.market_service.category.presentation.dto.CategorySaveRequest;
import com.project.market_service.common.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ADMIN-CATEGORY", description = "관리자 카테고리 관리")
@RestController
@RequestMapping("/api/v1/admin/category")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 등록")
    @ApiResponse(responseCode = "201", description = "카테고리 등록 성공")
    @PostMapping
    public ResponseEntity<ApiResult<CategoryResponse>> saveCategory(
            @Valid @RequestBody CategorySaveRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResult.success(categoryService.saveCategory(request))
        );
    }

    @Operation(summary = "카테고리 수정")
    @ApiResponse(responseCode = "200", description = "카테고리 수정 성공")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult<CategoryResponse>> updateCategory(
            @Valid @RequestBody CategorySaveRequest request,
            @PathVariable @Positive(message = "올바른 카테고리 ID 형식이 아닙니다") Long id
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(categoryService.updateCategory(request, id))
        );
    }

    @Operation(summary = "카테고리 삭제")
    @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult<Void>> deleteCategory(
            @PathVariable @Positive(message = "올바른 카테고리 ID 형식이 아닙니다") Long id
    ) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResult.success(null));
    }
}
