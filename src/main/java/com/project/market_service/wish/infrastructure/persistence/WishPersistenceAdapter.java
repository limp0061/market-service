package com.project.market_service.wish.infrastructure.persistence;

import static com.project.market_service.common.util.QuerydslUtils.getOrderSpecifier;
import static com.project.market_service.product.domain.QProduct.product;
import static com.project.market_service.user.domain.QUser.user;
import static com.project.market_service.wish.domain.QWish.wish;

import com.project.market_service.wish.application.port.out.WishPort;
import com.project.market_service.wish.domain.Wish;
import com.project.market_service.wish.presentation.dto.WishResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WishPersistenceAdapter implements WishPort {

    private final JpaWishRepository jpaWishRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Wish> findByProductIdAndUserId(Long productId, Long userId) {
        return jpaWishRepository.findByProductIdAndUserId(productId, userId);
    }

    @Override
    public Page<WishResponse> getWishProducts(Long userId, Pageable pageable) {
        List<WishResponse> content = queryFactory.select(
                        Projections.constructor(WishResponse.class,
                                product.id,
                                product.name,
                                user.id,
                                user.name,
                                product.price,
                                product.wishCount,
                                product.status
                        )
                )
                .from(wish)
                .join(wish.product, product)
                .join(product.user, user)
                .where(wish.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort(), wish))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(wish.count())
                .from(wish)
                .where(wish.user.id.eq(userId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Wish save(Wish wish) {
        return jpaWishRepository.save(wish);
    }

    @Override
    public void delete(Wish wish) {
        jpaWishRepository.delete(wish);
    }
}
