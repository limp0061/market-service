package com.project.market_service.category.application.service;

import com.project.market_service.category.domain.Category;
import com.project.market_service.category.application.port.out.CategoryPort;
import com.project.market_service.category.exception.CategoryErrorCode;
import com.project.market_service.common.exception.DuplicateException;
import com.project.market_service.common.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryPort categoryPort;

    public void validateDuplicateCategoryName(String name) {
        if (categoryPort.existsByName(name)) {
            throw new DuplicateException(CategoryErrorCode.DUPLICATE_CATEGORY, "CategoryName: " + name);
        }
    }

    public void validateCircularReference(Long categoryId, Category parent) {
        while (parent != null) {
            if (parent.getId().equals(categoryId)) {
                throw new InvalidValueException(CategoryErrorCode.CATEGORY_CIRCULAR_REFERENCE,
                        "Parent CategoryName: " + parent.getName());
            }

            parent = parent.getParent();
        }
    }
}
