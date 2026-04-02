package com.project.market_service.category.application.service;

import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.category.exception.CategoryErrorCode;
import com.project.market_service.category.presentation.dto.CategoryResponse;
import com.project.market_service.category.presentation.dto.CategorySaveRequest;
import com.project.market_service.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryValidator categoryValidator;
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse saveCategory(CategorySaveRequest request) {
        categoryValidator.validateDuplicateCategoryName(request.name());

        Category category = Category.create(request.name());

        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException(CategoryErrorCode.PARENT_CATEGORY_NOT_FOUND));
            parent.addChildCategory(category);
        }

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.from(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(CategorySaveRequest request, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CategoryErrorCode.PARENT_CATEGORY_NOT_FOUND));

        if (!category.getName().equals(request.name())) {
            categoryValidator.validateDuplicateCategoryName(request.name());
        }

        category.updateName(request.name());

        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

            categoryValidator.validateCircularReference(id, parent);
            parent.addChildCategory(category);
        }

        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(CategoryErrorCode.CATEGORY_CIRCULAR_REFERENCE));

        category.softDelete();
    }
}
