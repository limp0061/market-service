package com.project.market_service.product.domain;

import com.project.market_service.category.domain.Category;
import com.project.market_service.common.domain.BaseEntity;
import com.project.market_service.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    private int wishCount = 0;

    private Double lat;

    private Double lng;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    public void addProductImage(ProductImage productImage) {
        this.images.add(productImage);
        productImage.setProduct(this);
    }

    public static Product create(User user, Category category, String name, String description,
                                 BigDecimal price, ProductStatus status, List<String> imageUrls
    ) {
        Product product = Product.builder()
                .user(user)
                .category(category)
                .name(name)
                .description(description)
                .price(price)
                .status(status).build();

        product.initImages(imageUrls);
        return product;
    }

    private void initImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        for (int i = 0; i < imageUrls.size(); i++) {
            this.addProductImage(ProductImage.create(imageUrls.get(i), i));
        }
    }

    @Builder
    private Product(User user, Category category, String name, String description,
                    BigDecimal price, ProductStatus status
    ) {
        this.user = user;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.viewCount = 0;
        this.wishCount = 0;
    }
}
