package com.project.market_service.product.application.port.out;

import com.project.market_service.product.domain.ProductImage;
import java.util.List;

public interface ProductImagePort {
    List<ProductImage> findByProductId(Long productId);
}
