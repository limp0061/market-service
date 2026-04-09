package com.project.market_service.product.infrastructure.persistence;

import com.project.market_service.product.domain.Product;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface JpaProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.wishCount = p.wishCount + 1 WHERE p.id = :id")
    void increaseWishCount(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.wishCount = p.wishCount - 1 WHERE p.id = :id")
    void decreaseWishCount(@Param("id") Long id);
}
