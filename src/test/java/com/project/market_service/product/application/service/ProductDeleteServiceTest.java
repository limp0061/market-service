package com.project.market_service.product.application.service;

import static com.project.market_service.common.util.GeoUtils.createPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.category.domain.Category;
import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.common.exception.UnAuthorizationException;
import com.project.market_service.product.application.port.out.ProductPort;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductStatus;
import com.project.market_service.product.exception.ProductErrorCode;
import com.project.market_service.user.domain.User;
import java.math.BigDecimal;
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

@ExtendWith(MockitoExtension.class)
class ProductDeleteServiceTest {

    @Mock
    private ProductPort productPort;

    @InjectMocks
    private ProductService productService;
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
    }

    @Test
    @DisplayName("상품이 정상적으로 삭제된다")
    void deleteProduct_success() {
        // given
        Long productId = 100L;
        given(productPort.findById(productId)).willReturn(Optional.of(product));

        // when
        productService.deleteProduct(productId, 1L);

        // then
        assertAll(
                () -> assertThat(product.isDeleted()).isTrue(),
                () -> assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED)
        );
        then(productPort).should(never()).delete(any());
    }

    @Test
    @DisplayName("상품이 존재하지 않는 경우 삭제에 실패한다")
    void deleteProduct_fail_productNotFound() {
        // given
        given(productPort.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.deleteProduct(100L, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());

        assertThat(product.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("본인의 상품이 아니면 삭제에 실패한다")
    void deleteProduct_fail_forbidden() {
        // given
        Long productId = 100L;
        Long otherUserId = 999L;
        given(productPort.findById(productId)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> productService.deleteProduct(productId, otherUserId))
                .isInstanceOf(UnAuthorizationException.class)
                .hasMessage(AuthErrorCode.AUTH_FORBIDDEN.getMessage());

        assertThat(product.isDeleted()).isFalse();
    }
}