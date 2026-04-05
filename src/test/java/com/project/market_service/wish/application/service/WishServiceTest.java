package com.project.market_service.wish.application.service;

import static com.project.market_service.common.util.GeoUtils.createPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.project.market_service.category.domain.Category;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserRepository;
import com.project.market_service.wish.domain.Wish;
import com.project.market_service.wish.domain.WishRepository;
import com.project.market_service.wish.presentation.dto.ToggleWishResponse;
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
class WishServiceTest {

    @Mock
    WishRepository wishRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private WishService wishService;

    Product product;
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("테스터").build();
        Category category = Category.builder().id(1L).name("전자기기").build();

        Point location = createPoint(120, 37);
        product = Product.create(
                user, category, "상품", "쌉니다", new BigDecimal("10000"),
                List.of("image1.png", "image2.png"), location, "풍무동"
        );

    }

    @Test
    @DisplayName("상품을 위시리스트에 추가한다")
    void doWish() {
        // given
        given(productRepository.findByIdWithLock(anyLong())).willReturn(Optional.of(product));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(wishRepository.findByProductIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

        // when
        ToggleWishResponse response = wishService.toggleWish(1L, 1L);

        //then
        assertThat(response.isWished()).isTrue();
        then(wishRepository).should().save(any(Wish.class));
        then(productRepository).should().increaseWishCount(anyLong());
    }

    @Test
    @DisplayName("상품을 위스트리스트에서 제거한다")
    void removeWish() {

        Wish wish = Wish.create(user, product);
        // given
        given(productRepository.findByIdWithLock(anyLong())).willReturn(Optional.of(product));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(wishRepository.findByProductIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(wish));

        // when
        ToggleWishResponse response = wishService.toggleWish(1L, 1L);

        //then
        assertThat(response.isWished()).isFalse();
        then(wishRepository).should().delete(wish);
        then(productRepository).should().decreaseWishCount(anyLong());
    }
}