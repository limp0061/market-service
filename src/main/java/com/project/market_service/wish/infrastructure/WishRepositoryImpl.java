package com.project.market_service.wish.infrastructure;

import static com.project.market_service.common.util.QuerydslUtils.getOrderSpecifier;
import static com.project.market_service.product.domain.QProduct.product;
import static com.project.market_service.user.domain.QUser.user;
import static com.project.market_service.wish.domain.QWish.wish;

import com.project.market_service.wish.domain.WishRepositoryCustom;
import com.project.market_service.wish.presentation.dto.WishResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WishRepositoryImpl implements WishRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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
}
