package com.project.market_service.category.application.service;

import com.project.market_service.category.application.port.in.CategoryUseCase;
import com.project.market_service.category.domain.Category;
import com.project.market_service.category.application.port.out.CategoryPort;
import com.project.market_service.category.exception.CategoryErrorCode;
import com.project.market_service.category.presentation.dto.CategoryResponse;
import com.project.market_service.category.presentation.dto.CategorySaveRequest;
import com.project.market_service.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService implements CategoryUseCase {

    private final CategoryValidator categoryValidator;
    private final CategoryPort categoryPort;

    @Override
    @Transactional
    public CategoryResponse saveCategory(CategorySaveRequest request) {
        categoryValidator.validateDuplicateCategoryName(request.name());

        Category category = Category.create(request.name());

        if (request.parentId() != null) {
            Category parent = categoryPort.findById(request.parentId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(CategoryErrorCode.PARENT_CATEGORY_NOT_FOUND,
                                    "ParentId: " + request.parentId()));
            parent.addChildCategory(category);
        }

        Category savedCategory = categoryPort.save(category);
        log.info("[Create Category Success] CategoryId: {}, CategoryName: {}", savedCategory.getId(),
                savedCategory.getName());
        return CategoryResponse.from(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(CategorySaveRequest request, Long id) {
        Category category = categoryPort.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND, "CategoryId" + id));

        if (!category.getName().equals(request.name())) {
            categoryValidator.validateDuplicateCategoryName(request.name());
        }

        category.updateName(request.name());

        if (request.parentId() != null) {
            Category parent = categoryPort.findById(request.parentId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND,
                                    "ParentId: " + request.parentId()));

            categoryValidator.validateCircularReference(id, parent);
            parent.addChildCategory(category);
        }

        log.info("[Update Category Success] CategoryId: {}, CategoryName: {}", id, category.getName());
        return CategoryResponse.from(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryPort.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND, "CategoryId" + id));

        category.softDelete();
        log.info("[Delete Category Success] CategoryId: {}, CategoryName: {}", id, category.getName());
    }
}
