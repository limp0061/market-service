package com.project.market_service.product.application.port.out;

import com.project.market_service.product.domain.Product;
import com.project.market_service.product.presentation.dto.ProductDetailResponse;
import com.project.market_service.product.presentation.dto.ProductResponse;
import com.project.market_service.product.presentation.dto.ProductSearchRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductPort {
    Optional<Product> findByIdWithLock(Long id);

    void increaseWishCount(Long id);

    void decreaseWishCount(Long id);

    Product save(Product product);

    Optional<Product> findById(Long id);

    Optional<ProductDetailResponse> findWithDistinctById(Long id, Double curLat, Double curLng);

    Page<ProductResponse> searchProducts(ProductSearchRequest request, Pageable pageable);

    List<Product> saveAll(Iterable<Product> products);

    void deleteAll();

    void delete(Product product);

    void batchUpdateViewCount(Map<Long, Long> viewCounts, int batchSize);
}
