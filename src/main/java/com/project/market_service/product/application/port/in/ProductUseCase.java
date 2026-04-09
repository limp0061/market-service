package com.project.market_service.product.application.port.in;

import com.project.market_service.product.presentation.dto.ProductDetailResponse;
import com.project.market_service.product.presentation.dto.ProductResponse;
import com.project.market_service.product.presentation.dto.ProductSaveRequest;
import com.project.market_service.product.presentation.dto.ProductSaveResponse;
import com.project.market_service.product.presentation.dto.ProductSearchRequest;
import com.project.market_service.product.presentation.dto.ProductUpdateRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductUseCase {
    ProductSaveResponse saveProduct(ProductSaveRequest request, List<MultipartFile> images, Long userId);

    ProductSaveResponse updateProduct(Long productId, ProductUpdateRequest request, List<MultipartFile> images,
                                      Long userId);

    Page<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable);

    void deleteProduct(Long productId, Long userId);

    UpdateProductStatusResponse updateProductStatus(Long productId, UpdateProductStatusRequest request, Long userId);

    ProductDetailResponse getProductDetail(Long productId, Double curLat, Double curLng, Long userId);
}
