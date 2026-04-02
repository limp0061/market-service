package com.project.market_service.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.viewCount = p.viewCount+1 WHERE p.id = :productId")
    int increaseViewCount(@Param("productId") Long productId);
}
