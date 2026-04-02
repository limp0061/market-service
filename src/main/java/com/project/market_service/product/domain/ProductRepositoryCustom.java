package com.project.market_service.product.domain;

import com.project.market_service.product.presentation.dto.ProductDetailResponse;
import com.project.market_service.product.presentation.dto.ProductResponse;
import com.project.market_service.product.presentation.dto.ProductSearchRequest;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<ProductResponse> searchProducts(ProductSearchRequest request, Pageable pageable);

    Optional<ProductDetailResponse> findWithDistinctById(Long productId, Double curLat, Double curLng);
}
