package com.project.market_service.category.application.port.out;

import com.project.market_service.category.domain.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryPort {
    boolean existsByName(String name);

    Optional<Category> findById(Long id);

    Category save(Category category);

    List<Category> saveAll(Iterable<Category> categories);

    void deleteAll();
}
