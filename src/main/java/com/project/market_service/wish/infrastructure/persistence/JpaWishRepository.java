package com.project.market_service.wish.infrastructure.persistence;

import com.project.market_service.wish.domain.Wish;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaWishRepository extends JpaRepository<Wish, Long> {
    Optional<Wish> findByProductIdAndUserId(Long productId, Long userId);
}
