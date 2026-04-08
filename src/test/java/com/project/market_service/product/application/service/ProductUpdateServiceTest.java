package com.project.market_service.product.application.service;

import static com.project.market_service.common.util.GeoUtils.createPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.category.exception.CategoryErrorCode;
import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.common.exception.InvalidStateException;
import com.project.market_service.common.exception.UnAuthorizationException;
import com.project.market_service.common.file.FileService;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductImage;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.product.domain.ProductStatus;
import com.project.market_service.product.exception.ProductErrorCode;
import com.project.market_service.product.presentation.dto.ProductSaveResponse;
import com.project.market_service.product.presentation.dto.ProductUpdateRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusResponse;
import com.project.market_service.user.application.port.out.UserRepository;
import com.project.market_service.user.domain.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ProductUpdateServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private FileService fileService;

    @InjectMocks
    private ProductService productService;

    private ProductUpdateRequest request;
    private Product product;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(1L).name("테스터").build();
        Category category = Category.builder().id(1L).name("전자기기").build();

        Point location = createPoint(120, 37);
        product = Product.create(
                user, category, "상품", "쌉니다", new BigDecimal("10000"),
                List.of("image1.png", "image2.png"), location, "풍무동"
        );

        request = new ProductUpdateRequest(
                category.getId(),
                "중고 아이폰", "상태 좋아요", new BigDecimal("500000"),
                37.123, 127.123, "경기도 김포시", List.of("image2.png")
        );
    }

    @Test
    @DisplayName("상품 수정이 정상적으로 수정된다")
    void updateProduct_success() {
        // given
        Long productId = 100L;
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(fileService.uploadProductImage(anyList()))
                .willReturn(new ArrayList<>(List.of("new1.jpg", "new2.jpg")));

        MockMultipartFile file1 = new MockMultipartFile("test1", "test1.jpg", "image/jpeg", "test1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("test2", "test2.jpg", "image/jpeg", "test2".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        // when
        ProductSaveResponse response = productService.updateProduct(productId, request, files, 1L);

        // then
        assertAll(
                () -> assertThat(response.productName()).isEqualTo("중고 아이폰"),
                () -> assertThat(product.getImages()).hasSize(3),
                () -> assertThat(product.getImages())
                        .extracting(ProductImage::getImageUrl)
                        .containsExactly("new1.jpg", "new2.jpg", "image2.png")
        );
    }

    @Test
    @DisplayName("상품이 존재하지 않으면 수정에 실패한다")
    void updateProduct_fail_productNotFound() {
        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.updateProduct(100L, request, List.of(), 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());

        then(categoryRepository).should(never()).findById(anyLong());
        then(fileService).should(never()).uploadProductImage(anyList());
    }

    @Test
    @DisplayName("카테고리가 존재하지 않으면 수정에 실패한다")
    void updateProduct_fail_categoryNotFound() {
        // given
        Long newCategoryId = 2L;
        given(productRepository.findById(100L)).willReturn(Optional.of(product));
        given(categoryRepository.findById(newCategoryId)).willReturn(Optional.empty());

        ProductUpdateRequest differentCategoryRequest = new ProductUpdateRequest(
                newCategoryId,
                request.name(), request.description(), request.price(),
                request.lat(), request.lng(), request.address(), request.remainingImages()
        );

        // when & then
        assertThatThrownBy(() -> productService.updateProduct(100L, differentCategoryRequest, List.of(), 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(CategoryErrorCode.CATEGORY_NOT_FOUND.getMessage());

        then(categoryRepository).should().findById(newCategoryId);
        then(fileService).should(never()).uploadProductImage(anyList());
    }

    @Test
    @DisplayName("본인의 상품이 아니면 수정에 실패한다")
    void updateProduct_fail_forbidden() {
        Long productId = 100L;
        Long otherUserId = 999L;
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> productService.updateProduct(productId, request, List.of(), otherUserId))
                .isInstanceOf(UnAuthorizationException.class)
                .hasMessage(AuthErrorCode.AUTH_FORBIDDEN.getMessage());

        assertThat(product.getStatus()).isEqualTo(ProductStatus.SELLING);
    }

    @Test
    @DisplayName("판매 중인 상품을 예약 상태로 변경한다")
    void updateProductStatus_success() {
        // given
        Long productId = 100L;
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        UpdateProductStatusRequest updateStatusRequest = new UpdateProductStatusRequest(ProductStatus.RESERVED);

        // when
        UpdateProductStatusResponse response = productService.updateProductStatus(productId,
                updateStatusRequest, 1L);

        //then
        assertAll(
                () -> assertThat(response.productStatus()).isEqualTo(ProductStatus.RESERVED),
                () -> assertThat(product.getStatus()).isEqualTo(ProductStatus.RESERVED)
        );
    }

    @Test
    @DisplayName("판매 중인 상품을 완료 상태로 변경하면 변경이 실패한다")
    void updateProductStatus_fail_invalidStatus() {
        // given
        Long productId = 100L;
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        UpdateProductStatusRequest updateStatusRequest = new UpdateProductStatusRequest(ProductStatus.SOLD);

        // when & then
        assertThatThrownBy(() -> productService.updateProductStatus(productId, updateStatusRequest, 1L))
                .isInstanceOf(InvalidStateException.class)
                .hasMessage(ProductErrorCode.INVALID_PRODUCT_STATE.getMessage());

        assertThat(product.getStatus()).isEqualTo(ProductStatus.SELLING);
    }
}