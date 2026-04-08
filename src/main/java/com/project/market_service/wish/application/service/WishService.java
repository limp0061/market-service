package com.project.market_service.wish.application.service;

import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.product.exception.ProductErrorCode;
import com.project.market_service.user.application.port.out.UserRepository;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import com.project.market_service.wish.domain.Wish;
import com.project.market_service.wish.domain.WishRepository;
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
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ToggleWishResponse toggleWish(Long productId, Long userId) {
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND,
                        "productId: " + productId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND, "userId: " + userId));

        Optional<Wish> wish = wishRepository.findByProductIdAndUserId(productId, userId);
        if (wish.isEmpty()) {
            wishRepository.save(Wish.create(user, product));
            productRepository.increaseWishCount(productId);
            return ToggleWishResponse.of(true, product.getWishCount());
        }

        productRepository.decreaseWishCount(productId);
        wishRepository.delete(wish.get());
        return ToggleWishResponse.of(false, product.getWishCount());
    }

    public Page<WishResponse> getWishProducts(Long userId, Pageable pageable) {
        return wishRepository.getWishProducts(userId, pageable);
    }
}
