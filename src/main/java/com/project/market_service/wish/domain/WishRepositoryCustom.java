package com.project.market_service.wish.domain;

import com.project.market_service.wish.presentation.dto.WishResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishRepositoryCustom {
    Page<WishResponse> getWishProducts(Long userId, Pageable pageable);
}
