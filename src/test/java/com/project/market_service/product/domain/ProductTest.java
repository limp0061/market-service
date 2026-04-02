package com.project.market_service.product.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.project.market_service.common.exception.InvalidStateException;
import com.project.market_service.common.exception.UnAuthorizationException;
import com.project.market_service.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    @DisplayName("판매 중이거나 판매 취소 상태일때는 수정이 가능하다")
    void validateUpdatable_success() {
        Product product = Product.builder()
                .status(ProductStatus.SELLING)
                .build();

        assertThatCode(product::validateUpdatable)
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("판매 완료(SOLD) 상태 일때는 수정 시 예외가 발생한다")
    void validateUpdatable_fail() {
        Product product = Product.builder()
                .status(ProductStatus.SOLD)
                .build();

        assertThatThrownBy(product::validateUpdatable)
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    @DisplayName("판매 중이거나 판매 취소 상태일때는 삭제가 가능하다")
    void validateDeletable_success() {
        Product product = Product.builder()
                .status(ProductStatus.INACTIVE)
                .build();

        assertThatCode(product::validateDeletable)
                .doesNotThrowAnyException();
    }


    @Test
    @DisplayName("이미 삭제된 파일은 삭제 시 예외가 발생한다")
    void validateDeletable_fail() {
        Product product = Product
                .builder()
                .status(ProductStatus.DELETED)
                .build();

        assertThatThrownBy(product::validateDeletable)
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    @DisplayName("자신이 등록한 상품이면 검증을 통과한다")
    void validateOwner_success() {
        User owner = User.builder().id(1L).build();
        Product product = Product.builder().user(owner).build();

        assertThatCode(() -> product.validateOwner(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("자신이 등록하지 않은 상품에 접근하면 예외가 발생한다")
    void validateOwner_fail() {
        User owner = User.builder().id(1L).build();
        Product product = Product.builder().user(owner).build();

        assertThatThrownBy(() -> product.validateOwner(2L))
                .isInstanceOf(UnAuthorizationException.class);
    }

    @Test
    @DisplayName("삭제 시 판매 상품이 삭제 상태로 변경된다")
    void softDelete_success() {
        Product product = Product.builder()
                .status(ProductStatus.SELLING).build();

        product.softDelete();

        assertThat(product.isDeleted()).isTrue();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED);
    }
}