package com.project.market_service.product.infrastructure.persistence;

import com.project.market_service.product.domain.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProductImagePort extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);
}
