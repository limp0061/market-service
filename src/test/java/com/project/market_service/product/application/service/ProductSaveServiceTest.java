package com.project.market_service.product.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.category.exception.CategoryErrorCode;
import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.common.file.FileService;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.product.presentation.dto.ProductSaveRequest;
import com.project.market_service.product.presentation.dto.ProductSaveResponse;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import com.project.market_service.user.domain.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ProductSaveServiceTest {

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

    private ProductSaveRequest request;
    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("테스터").build();
        category = Category.builder().id(1L).name("전자기기").build();

        request = new ProductSaveRequest(
                category.getId(),
                "중고 아이폰", "상태 좋아요", new BigDecimal("500000"),
                37.123, 127.123, "경기도 김포시"
        );
    }

    @Test
    @DisplayName("이미지가 포함된 상품을 저장한다")
    void saveProduct_withImages_success() {
        // given
        MockMultipartFile file1 = new MockMultipartFile("test1", "test1.jpg", "image/jpeg", "test1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("test2", "test2.jpg", "image/jpeg", "test2".getBytes());
        List<MultipartFile> images = List.of(file1, file2);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(fileService.uploadProductImage(anyList())).willReturn(List.of("https://url1.png", "https://url2.png"));

        Product savedProduct = Product.builder().id(100L).name(request.name()).build();
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        // when
        ProductSaveResponse response = productService.saveProduct(request, images, 1L);

        // then
        then(productRepository).should().save(productCaptor.capture());
        Product capturedProduct = productCaptor.getValue();

        assertAll(
                () -> assertThat(response.productName()).isEqualTo("중고 아이폰"),
                () -> assertThat(capturedProduct.getName()).isEqualTo(request.name()),
                () -> assertThat(capturedProduct.getPrice()).isEqualByComparingTo(request.price()),
                () -> assertThat(capturedProduct.getCategory().getId()).isEqualTo(request.categoryId()),
                () -> assertThat(capturedProduct.getImages()).hasSize(2)
        );
        then(fileService).should(times(1)).uploadProductImage(anyList());
    }

    @Test
    @DisplayName("이미지가 포함되지 않은 상품을 저장한다")
    void saveProduct_noImages_success() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        Product savedProduct = Product.builder().id(100L).name(request.name()).build();
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);

        // when
        productService.saveProduct(request, List.of(), 1L);

        // then
        then(fileService).should(never()).uploadProductImage(anyList());
        then(productRepository).should(times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("유저가 존재하지 않으면 저장에 실패한다")
    void saveProduct_fail_userNotFound() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.saveProduct(request, List.of(), 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());

        then(categoryRepository).should(never()).findById(anyLong());
        then(productRepository).should(never()).save(any());
    }


    @Test
    @DisplayName("카테고리가 존재하지 않으면 저장에 실패한다")
    void saveProduct_fail_categoryNotFound() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.saveProduct(request, List.of(), 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(CategoryErrorCode.CATEGORY_NOT_FOUND.getMessage());

        then(userRepository).should(times(1)).findById(anyLong());
        then(productRepository).should(never()).save(any());
    }
}