package com.project.market_service.wish.domain;


import com.project.market_service.common.domain.BaseTimeEntity;
import com.project.market_service.product.domain.Product;
import com.project.market_service.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "wishes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wish extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public static Wish create(User user, Product product) {
        return Wish.builder()
                .user(user)
                .product(product)
                .build();
    }

    @Builder
    public Wish(Long id, User user, Product product) {
        this.id = id;
        this.user = user;
        this.product = product;
    }
}

