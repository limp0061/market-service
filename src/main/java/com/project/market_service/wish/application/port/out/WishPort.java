package com.project.market_service.wish.application.port.out;

import com.project.market_service.wish.domain.Wish;
import com.project.market_service.wish.presentation.dto.WishResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishPort {
    Optional<Wish> findByProductIdAndUserId(Long productId, Long userId);

    Page<WishResponse> getWishProducts(Long userId, Pageable pageable);

    Wish save(Wish wish);

    void delete(Wish wish);
}
