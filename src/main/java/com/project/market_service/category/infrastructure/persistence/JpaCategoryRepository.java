package com.project.market_service.category.infrastructure.persistence;

import com.project.market_service.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaCategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
