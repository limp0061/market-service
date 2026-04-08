package com.project.market_service.product.presentation;

import static com.project.market_service.common.util.GeoUtils.createPoint;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.common.file.FileService;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import com.project.market_service.config.IntegrationTestBase;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.product.domain.ProductStatus;
import com.project.market_service.product.presentation.dto.ProductSaveRequest;
import com.project.market_service.product.presentation.dto.ProductUpdateRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusRequest;
import com.project.market_service.user.application.port.out.UserRepository;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserRole;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


class ProductControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @MockitoBean
    private FileService fileService;

    Long categoryId;
    Long productId;
    Long userId;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.signUp("user_test", "user_test", "password1234!");
        userRepository.save(user);
        userId = user.getId();

        System.out.println("### DEBUG: Saved User ID = " + user.getId());

        Category electronics = categoryRepository.save(Category.create("디지털기기"));
        Category furniture = categoryRepository.save(Category.create("가구/인테리어"));
        categoryRepository.saveAll(List.of(electronics, furniture));
        categoryId = electronics.getId();

        Point location = createPoint(120, 37);

        Product product = Product.create(
                user, electronics, "상품", "쌉니다", new BigDecimal("10000"),
                List.of("image1.png", "image2.png"), location, "풍무동"
        );
        productRepository.save(product);
        productId = product.getId();

        Product p1 = Product.create(user, electronics, "아이폰 15 프로", "거의 새거입니다.", new BigDecimal("1200000"),
                null, createPoint(126.9725, 37.5565), "서울 중구 봉래동");

        Product p2 = Product.create(user, electronics, "아이폰 맥스", "풀박스 구성입니다.", new BigDecimal("450000"),
                null, createPoint(126.9721, 37.5446), "서울 용산구 남영동");

        Product p3 = Product.create(user, furniture, "이케아 책상", "이사로 인해 급처합니다.", new BigDecimal("50000"),
                null, createPoint(126.9450, 37.5130), "서울 동작구 노량진동");

        Product p4 = Product.create(user, electronics, "맥북 에어 M2", "상태 최상입니다.", new BigDecimal("1300000"),
                null, createPoint(127.0276, 37.4979), "서울 강남구 역삼동");

        productRepository.saveAll(List.of(p1, p2, p3, p4));

        JwtUserInfo userInfo = new JwtUserInfo(userId, "user_test", UserRole.USER.getCode());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userInfo, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("상품 등록 성공")
    void saveProduct_success() throws Exception {
        ProductSaveRequest request = new ProductSaveRequest(
                categoryId, "냉장고_테스트", "냉장고 싸게 팝니다", new BigDecimal("100000"),
                30.00, 127.00, "풍무동"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        given(fileService.uploadProductImage(anyList()))
                .willReturn(List.of("https://s3-mock-url.com/test.jpg"));

        mockMvc.perform(multipart("/api/v1/products")
                        .file(requestPart)
                        .file(imagePart)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productName").value("냉장고_테스트"))
                .andExpect(jsonPath("$.data.productStatus").value(ProductStatus.SELLING.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 수정 성공")
    void updateProduct_success() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest(
                categoryId, "변경_테스트", "냉장고 싸게 팝니다", new BigDecimal("100000"),
                30.00, 127.00, "풍무동", List.of("image1.png")
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "images",
                "test.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/products/" + productId)
                        .file(requestPart)
                        .file(imagePart)

                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productName").value("변경_테스트"))
                .andDo(print());
    }

    @Test
    @DisplayName("물품 삭제 성공")
    void deleteProduct_success() throws Exception {

        mockMvc.perform(delete("/api/v1/products/" + productId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("물품 상태 변경")
    void updateProductStatus_success() throws Exception {
        UpdateProductStatusRequest request = new UpdateProductStatusRequest(ProductStatus.RESERVED);

        mockMvc.perform(patch("/api/v1/products/" + productId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productStatus").value(ProductStatus.RESERVED.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("물품 조회 - 카테고리/키워드")
    void getProduct_success_category_keyword() throws Exception {

        mockMvc.perform(get("/api/v1/products")
                        .param("categoryId", categoryId.toString())
                        .param("keyword", "아이폰")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andDo(print());
    }

    @Test
    @DisplayName("뭎룸 조회 - 가격")
    void getProducts_success_price() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .param("minPrice", "50000")
                        .param("maxPrice", "500000")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andDo(print());
    }

    @Test
    @DisplayName("물품 조회 - 반경 3km")
    void getProducts_success_distance() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .param("curLat", "37.5350")
                        .param("curLng", "126.9740")
                        .param("distance", "3")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andDo(print());
    }

    @Test
    @DisplayName("물품 조회 - 조건에 맞는 결과가 없는 경우")
    void getProducts_empty_result() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .param("keyword", "존재하지않는상품명")
                        .param("minPrice", "99999999")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    @DisplayName("물품 상세 조회")
    void getProduct_detail() throws Exception {
        mockMvc.perform(get("/api/v1/products/" + productId)
                        .param("curLat", "37")
                        .param("curLng", "126")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.viewCount").value(1))
                .andExpect(jsonPath("$.data.imageUrls", hasSize(2)))
                .andDo(print());
    }

}