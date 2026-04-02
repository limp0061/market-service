package com.project.market_service.product.application.service;

import static com.project.market_service.common.util.GeoUtils.createPoint;

import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.category.exception.CategoryErrorCode;
import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.common.file.FileService;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductImage;
import com.project.market_service.product.domain.ProductImageRepository;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.product.exception.ProductErrorCode;
import com.project.market_service.product.presentation.dto.ProductDetailResponse;
import com.project.market_service.product.presentation.dto.ProductResponse;
import com.project.market_service.product.presentation.dto.ProductSaveRequest;
import com.project.market_service.product.presentation.dto.ProductSaveResponse;
import com.project.market_service.product.presentation.dto.ProductSearchRequest;
import com.project.market_service.product.presentation.dto.ProductUpdateRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusResponse;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import com.project.market_service.user.domain.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public ProductSaveResponse saveProduct(
            ProductSaveRequest request,
            List<MultipartFile> images,
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        List<String> imageUrls = (images == null || images.isEmpty())
                ? List.of()
                : fileService.uploadProductImage(images);

        Point location = createPoint(request.lng(), request.lat());

        Product product = Product.create(
                user, category, request.name(),
                request.description(), request.price(), imageUrls,
                location, request.address()
        );

        Product savedProduct = productRepository.save(product);

        return ProductSaveResponse.from(savedProduct);
    }

    @Transactional
    public ProductSaveResponse updateProduct(
            Long productId, ProductUpdateRequest request,
            List<MultipartFile> images, Long userId
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        product.validateOwner(userId);
        product.validateUpdatable();

        Category category = product.getCategory();
        if (!category.getId().equals(request.categoryId())) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        }

        List<String> imageUrls = (images == null || images.isEmpty())
                ? new ArrayList<>()
                : fileService.uploadProductImage(images);

        if (request.remainingImages() != null && !imageUrls.isEmpty()) {
            imageUrls.addAll(request.remainingImages());
        }

        Point location = createPoint(request.lng(), request.lat());

        product.updateCategory(
                category, request.name(),
                request.description(), request.price(), imageUrls,
                location, request.address()
        );

        return ProductSaveResponse.from(product);
    }

    public Page<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable) {
        return productRepository.searchProducts(request, pageable);
    }

    @Transactional
    public void deleteProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        product.validateOwner(userId);
        product.validateDeletable();

        product.clearImages();
        product.softDelete();
    }

    @Transactional
    public UpdateProductStatusResponse updateProductStatus(
            Long productId, UpdateProductStatusRequest request, Long userId
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        product.validateOwner(userId);
        product.changeStatus(request.productStatus());

        return UpdateProductStatusResponse.from(product);
    }

    @Transactional
    public ProductDetailResponse getProductDetail(Long productId, Double curLat, Double curLng) {

        int updated = productRepository.increaseViewCount(productId);
        if (updated == 0) {
            throw new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        ProductDetailResponse product = productRepository.findWithDistinctById(productId, curLat, curLng)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        List<String> images = productImageRepository.findByProductId(productId)
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        product.setImageUrls(images);

        return product;
    }
}


