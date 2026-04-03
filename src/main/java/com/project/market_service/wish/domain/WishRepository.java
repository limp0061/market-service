package com.project.market_service.wish.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long>, WishRepositoryCustom {
    Optional<Wish> findByProductIdAndUserId(Long productId, Long userId);
}
