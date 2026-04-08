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
import com.project.market_service.user.application.port.out.UserRepository;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductViewCountService productViewCountService;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public ProductSaveResponse saveProduct(
            ProductSaveRequest request,
            List<MultipartFile> images,
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND, "userId: " + userId));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND,
                        "categoryId: " + request.categoryId()));

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

        log.info("[Product Save Success] ProductId: {}, ProductName: {}", savedProduct.getId(), product.getName());
        return ProductSaveResponse.from(savedProduct);
    }

    @Transactional
    public ProductSaveResponse updateProduct(
            Long productId, ProductUpdateRequest request,
            List<MultipartFile> images, Long userId
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND,
                                "ProductId: " + productId));

        product.validateOwner(userId);
        product.validateUpdatable();

        Category category = product.getCategory();
        if (!category.getId().equals(request.categoryId())) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException(CategoryErrorCode.CATEGORY_NOT_FOUND,
                            "CategoryId: " + request.categoryId()));
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
        log.info("[Product Update Success] ProductId: {}, ProductName: {}", productId, product.getName());
        return ProductSaveResponse.from(product);
    }

    public Page<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable) {
        log.debug("[Product Search] Request: {}, Page: {}", request, pageable);
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
        // TODO: 배치에서 softDelete된 상품의 연관 wish 정리 필요
        log.info("[Product Delete Success] ProductId: {}, ProductName: {}", productId, product.getName());
    }

    @Transactional
    public UpdateProductStatusResponse updateProductStatus(
            Long productId, UpdateProductStatusRequest request, Long userId
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND,
                        "productId: " + productId));

        String pre = product.getStatus().name();
        product.validateOwner(userId);
        product.changeStatus(request.productStatus());

        log.info("[Product Update Status Success] ProductId: {}, ProductName: {}, {}-> {}", productId,
                product.getName(), pre, request.productStatus().name());
        return UpdateProductStatusResponse.from(product);
    }

    @Transactional
    public ProductDetailResponse getProductDetail(Long productId, Double curLat, Double curLng, Long userId) {
        log.debug("[Product Detail] ProductId: {}, Location: {},{}", productId, curLat, curLng);

        ProductDetailResponse product = productRepository.findWithDistinctById(productId, curLat, curLng)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        List<String> images = productImageRepository.findByProductId(productId)
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        product.setImageUrls(images);

        long viewCount = productViewCountService.increaseViewCount(product.getProductId(), userId);
        product.setViewCount(product.getViewCount() + viewCount);

        return product;
    }

    @Transactional
    public void batchUpdateViewCount(Map<Long, Long> viewCounts, int batchSize) {
        if (viewCounts.isEmpty()) {
            return;
        }
        List<Entry<Long, Long>> entries = new ArrayList<>(viewCounts.entrySet());
        jdbcTemplate.batchUpdate("UPDATE products SET view_count = view_count + ? WHERE product_id = ?",
                entries,
                batchSize,
                (ps, entry) -> {
                    ps.setLong(1, entry.getValue());
                    ps.setLong(2, entry.getKey());
                }
        );
    }
}


