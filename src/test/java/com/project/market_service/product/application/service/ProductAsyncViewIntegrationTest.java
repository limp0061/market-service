package com.project.market_service.product.application.service;

import static com.project.market_service.common.util.GeoUtils.createPoint;
import static org.assertj.core.api.Assertions.assertThat;

import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.common.constants.RedisConstants;
import com.project.market_service.common.redis.RedisManager;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserRepository;
import com.project.market_service.user.domain.UserRole;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductAsyncViewIntegrationTest {

    @Autowired
    private ProductAsyncViewService productAsyncViewService;
    @Autowired
    private RedisManager redisManager;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    Long productId;
    Long userId;

    @BeforeEach
    void setUp() {
        User user = User.signUp("user_test", "user_test", "password1234!");
        userRepository.save(user);
        userId = user.getId();

        Category category = Category.create("카테고리");
        categoryRepository.save(category);

        Product product = Product.create(user, category, "아이폰 15 프로", "거의 새거입니다.", new BigDecimal("1200000"),
                null, createPoint(126.9725, 37.5565), "서울 중구 봉래동");

        productRepository.save(product);
        productId = product.getId();

        JwtUserInfo userInfo = new JwtUserInfo(user.getId(), "user_test", UserRole.USER.getCode());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userInfo, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Redis에 쌓인 조회수가 스케줄러 로직을 통해 DB로 동기화되어야 한다.")
    void syncRedisViewCountToDb() {

        // given
        String viewCountKey = String.format(RedisConstants.PRODUCT_VIEW_COUNT, productId);
        redisManager.setIfAbsent(viewCountKey, "100", 1, TimeUnit.HOURS);
        redisManager.increment(viewCountKey);
        redisManager.increment(viewCountKey);
        redisManager.increment(viewCountKey);

        // when
        productAsyncViewService.asyncViewProcess();

        // then
        Product updatedProduct = productRepository.findById(productId).orElseThrow();

        assertThat(updatedProduct.getViewCount()).isEqualTo(103);

        boolean hasLock = redisManager.hasKey(RedisConstants.PRODUCT_VIEW_SCHEDULE_LOCK);
        assertThat(hasLock).isFalse();
    }

    @AfterEach
    void tearDown() {
        // DB 정리
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Redis 정리
        redisManager.delete(String.format(RedisConstants.PRODUCT_VIEW_COUNT, productId));
        redisManager.delete(RedisConstants.PRODUCT_VIEW_SCHEDULE_LOCK);
    }

}