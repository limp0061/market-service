package com.project.market_service.wish.application.service;

import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.product.application.port.out.ProductPort;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.exception.ProductErrorCode;
import com.project.market_service.user.application.port.out.UserPort;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import com.project.market_service.wish.application.port.in.WishUseCase;
import com.project.market_service.wish.application.port.out.WishPort;
import com.project.market_service.wish.domain.Wish;
import com.project.market_service.wish.presentation.dto.ToggleWishResponse;
import com.project.market_service.wish.presentation.dto.WishResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishService implements WishUseCase {

    private final WishPort wishPort;
    private final ProductPort productPort;
    private final UserPort userPort;

    @Override
    @Transactional
    public ToggleWishResponse toggleWish(Long productId, Long userId) {
        Product product = productPort.findByIdWithLock(productId)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND,
                        "productId: " + productId));

        User user = userPort.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND, "userId: " + userId));

        Optional<Wish> wish = wishPort.findByProductIdAndUserId(productId, userId);
        if (wish.isEmpty()) {
            wishPort.save(Wish.create(user, product));
            productPort.increaseWishCount(productId);
            return ToggleWishResponse.of(true, product.getWishCount());
        }

        productPort.decreaseWishCount(productId);
        wishPort.delete(wish.get());
        return ToggleWishResponse.of(false, product.getWishCount());
    }

    @Override
    public Page<WishResponse> getWishProducts(Long userId, Pageable pageable) {
        return wishPort.getWishProducts(userId, pageable);
    }
}
