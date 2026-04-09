package com.project.market_service.wish.application.port.in;

import com.project.market_service.wish.presentation.dto.ToggleWishResponse;
import com.project.market_service.wish.presentation.dto.WishResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishUseCase {
    ToggleWishResponse toggleWish(Long productId, Long userId);

    Page<WishResponse> getWishProducts(Long userId, Pageable pageable);
}
