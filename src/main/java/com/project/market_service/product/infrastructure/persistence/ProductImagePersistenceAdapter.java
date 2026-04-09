package com.project.market_service.product.infrastructure.persistence;

import com.project.market_service.product.domain.ProductImage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductImagePersistenceAdapter implements
        com.project.market_service.product.application.port.out.ProductImagePort {

    private final ProductImagePort jpaProductImageRepository;

    @Override
    public List<ProductImage> findByProductId(Long productId) {
        return jpaProductImageRepository.findByProductId(productId);
    }
}
