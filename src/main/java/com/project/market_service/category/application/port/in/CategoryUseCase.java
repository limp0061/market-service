package com.project.market_service.category.application.port.in;

import com.project.market_service.category.presentation.dto.CategoryResponse;
import com.project.market_service.category.presentation.dto.CategorySaveRequest;

public interface CategoryUseCase {
    CategoryResponse saveCategory(CategorySaveRequest request);

    CategoryResponse updateCategory(CategorySaveRequest request, Long id);

    void deleteCategory(Long id);
}
