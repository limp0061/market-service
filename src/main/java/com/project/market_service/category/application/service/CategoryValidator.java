package com.project.market_service.category.application.service;

import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.category.exception.CategoryErrorCode;
import com.project.market_service.common.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public void validateDuplicateCategoryName(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new InvalidValueException(CategoryErrorCode.DUPLICATE_CATEGORY);
        }
    }

    public void validateCircularReference(Long categoryId, Category parent) {
        while (parent != null) {
            if (parent.getId().equals(categoryId)) {
                throw new InvalidValueException(CategoryErrorCode.CATEGORY_CIRCULAR_REFERENCE);
            }

            parent = parent.getParent();
        }
    }
}
