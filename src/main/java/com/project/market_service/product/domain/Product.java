package com.project.market_service.product.domain;

import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.category.domain.Category;
import com.project.market_service.common.domain.BaseEntity;
import com.project.market_service.common.exception.InvalidStateException;
import com.project.market_service.common.exception.UnAuthorizationException;
import com.project.market_service.product.exception.ProductErrorCode;
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
import org.locationtech.jts.geom.Point;

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
    private long viewCount = 0;

    @Column(nullable = false)
    private long wishCount = 0;

    @Column(nullable = false, columnDefinition = "POINT SRID 4326")
    private Point location;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    public void addProductImage(ProductImage productImage) {
        this.images.add(productImage);
        productImage.setProduct(this);
    }

    public static Product create(
            User user, Category category, String name,
            String description, BigDecimal price,
            List<String> imageUrls, Point location, String address
    ) {
        Product product = Product.builder()
                .user(user)
                .category(category)
                .name(name)
                .description(description)
                .price(price)
                .status(ProductStatus.SELLING)
                .location(location)
                .address(address)
                .build();

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
    private Product(Long id, User user, Category category, String name, String description,
                    BigDecimal price, ProductStatus status, Point location, String address
    ) {
        this.id = id;
        this.user = user;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.viewCount = 0L;
        this.wishCount = 0L;
        this.location = location;
        this.address = address;
    }

    public void updateCategory(Category category, String name, String description, BigDecimal price,
                               List<String> imageUrls, Point location, String address
    ) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.address = address;

        if (imageUrls != null) {
            this.images.clear();
            for (int i = 0; i < imageUrls.size(); i++) {
                this.addProductImage(ProductImage.create(imageUrls.get(i), i));
            }
        }
    }

    public void clearImages() {
        this.images.clear();
    }

    public void validateUpdatable() {
        if (status == ProductStatus.DELETED) {
            throw new InvalidStateException(ProductErrorCode.ALREADY_DELETED);
        }

        if (status != ProductStatus.SELLING && status != ProductStatus.INACTIVE) {
            throw new InvalidStateException(ProductErrorCode.INVALID_PRODUCT_STATE);
        }
    }

    public void validateDeletable() {
        if (!status.canUpdateState(ProductStatus.DELETED)) {
            throw new InvalidStateException(ProductErrorCode.INVALID_PRODUCT_STATE);
        }
    }

    public void changeStatus(ProductStatus newStatus) {
        if (!this.status.canUpdateState(newStatus)) {
            throw new InvalidStateException(ProductErrorCode.INVALID_PRODUCT_STATE);
        }
        this.status = newStatus;
    }

    public void validateOwner(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new UnAuthorizationException(AuthErrorCode.AUTH_FORBIDDEN);
        }
    }

    public void softDelete() {
        super.softDelete();
        this.status = ProductStatus.DELETED;
    }
}
