package com.project.market_service.category.infrastructure.persistence;

import com.project.market_service.category.application.port.out.CategoryPort;
import com.project.market_service.category.domain.Category;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPort {

    private final JpaCategoryRepository jpaCategoryRepository;

    @Override
    public boolean existsByName(String name) {
        return jpaCategoryRepository.existsByName(name);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaCategoryRepository.findById(id);
    }

    @Override
    public Category save(Category category) {
        return jpaCategoryRepository.save(category);
    }

    @Override
    public List<Category> saveAll(Iterable<Category> categories) {
        return jpaCategoryRepository.saveAll(categories);
    }

    @Override
    public void deleteAll() {
        jpaCategoryRepository.deleteAll();
    }
}
