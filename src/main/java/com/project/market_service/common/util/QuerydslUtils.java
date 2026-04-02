package com.project.market_service.common.util;


import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

public class QuerydslUtils {
    public static OrderSpecifier<?>[] getOrderSpecifier(Sort sort, EntityPathBase<?> entityPath) {
        PathBuilder<?> pathBuilder = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata());

        return sort.stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    try {
                        String cond = order.getProperty();
                        return new OrderSpecifier(direction, pathBuilder.get(cond));
                    } catch (Exception e) {
                        return new OrderSpecifier(Order.DESC, pathBuilder.get("createdAt"));
                    }
                })
                .toArray(OrderSpecifier[]::new);
    }
}
