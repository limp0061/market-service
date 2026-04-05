package com.project.market_service.chatroom.domain;

import com.project.market_service.common.domain.BaseEntity;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chatrooms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_product_buyer",
                        columnNames = {"product_id", "buyer_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private boolean buyerIn = false;

    @Column(nullable = false)
    private boolean sellerIn = false;

    public static ChatRoom create(Product product, User buyer, User seller) {
        return ChatRoom.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .build();
    }

    @Builder
    private ChatRoom(Long id, Product product, User buyer, User seller) {
        this.id = id;
        this.product = product;
        this.buyer = buyer;
        this.seller = seller;
        this.buyerIn = true;
        this.sellerIn = true;
    }
}
