package com.project.market_service.common.initialize;


import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Profile("!test")
public class DataInitialIze implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {

        String encPassword = passwordEncoder.encode("password1234!");
        User user = User.signUp("사용자", "user1", encPassword);
        User admin = User.signUpAdmin("관리자", "hong1234", encPassword);
        userRepository.saveAll(List.of(user, admin));

        Category electronics = categoryRepository.save(Category.create("디지털기기"));
        Category furniture = categoryRepository.save(Category.create("가구/인테리어"));

        // 3. 공간 데이터 팩토리 설정 (SRID 4326)
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        // 4. 테스트용 상품 생성 (기준 위치: 서울역 37.5559, 126.9723)
        // 상품 1: 아주 가까운 거리 (약 200m)
        Product p1 = createProduct(user, electronics, "아이폰 15 프로", "거의 새거입니다.", new BigDecimal("1200000"),
                126.9725, 37.5565, "서울 중구 봉래동", factory);

        // 상품 2: 적당히 가까운 거리 (약 1.5km)
        Product p2 = createProduct(user, electronics, "에어팟 맥스", "풀박스 구성입니다.", new BigDecimal("450000"),
                126.9721, 37.5446, "서울 용산구 남영동", factory);

        // 상품 3: 필터링 경계 (약 4.8km - 5km 검색 시 포함되어야 함)
        Product p3 = createProduct(user, furniture, "이케아 책상", "이사로 인해 급처합니다.", new BigDecimal("50000"),
                126.9450, 37.5130, "서울 동작구 노량진동", factory);

        // 상품 4: 검색 범위를 벗어난 곳 (약 12km)
        Product p4 = createProduct(user, electronics, "맥북 에어 M2", "상태 최상입니다.", new BigDecimal("1300000"),
                127.0276, 37.4979, "서울 강남구 역삼동", factory);

        // 상품 5: 삭제된 상품 (검색에서 제외되어야 함)
        Product p5 = createProduct(user, electronics, "고장난 모니터", "부품용으로 가져가세요.", new BigDecimal("10000"),
                126.9723, 37.5559, "서울 중구", factory);
        p5.softDelete();

        productRepository.saveAll(List.of(p1, p2, p3, p4, p5));
    }

    private Product createProduct(User user, Category category, String name, String description,
                                  BigDecimal price, double lng, double lat, String address,
                                  GeometryFactory factory) {
        return Product.create(
                user,
                category,
                name,
                description,
                price,
                Collections.emptyList(),
                factory.createPoint(new Coordinate(lng, lat)),
                address
        );
    }
}
