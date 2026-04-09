package com.project.market_service.product.infrastructure.persistence;

import static com.project.market_service.category.domain.QCategory.category;
import static com.project.market_service.common.util.QuerydslUtils.getOrderSpecifier;
import static com.project.market_service.product.domain.QProduct.product;
import static com.project.market_service.user.domain.QUser.user;

import com.project.market_service.product.application.port.out.ProductPort;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductStatus;
import com.project.market_service.product.presentation.dto.ProductDetailResponse;
import com.project.market_service.product.presentation.dto.ProductResponse;
import com.project.market_service.product.presentation.dto.ProductSearchRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPort {

    private final JpaProductRepository jpaProductRepository;
    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Page<ProductResponse> searchProducts(ProductSearchRequest request, Pageable pageable) {

        boolean hasLocation = request.curLat() != null && request.curLng() != null;
        Double searchRadius = request.distance() != null ? request.distance() : 5.0;

        NumberTemplate<Double> distancePath = hasLocation ? Expressions.numberTemplate(Double.class,
                "ST_Distance_Sphere({0}, ST_GeomFromText({1}, 4326))",
                product.location,
                String.format("POINT(%f %f)", request.curLat(), request.curLng())
        ) : null;

        BooleanExpression[] predicates = {
                product.status.ne(ProductStatus.DELETED),
                containsKeyword(request.keyword()),
                minPrice(request.minPrice()),
                maxPrice(request.maxPrice()),
                eqCategory(request.categoryId()),
                hasLocation ? boundingBox(request.curLat(), request.curLng(), searchRadius) : null,
                hasLocation ? withinDistance(distancePath, searchRadius) : null
        };

        JPAQuery<ProductResponse> query = queryFactory.select(
                        Projections.constructor(ProductResponse.class,
                                product.id,
                                user.name,
                                category.name,
                                product.name,
                                product.price,
                                product.status,
                                product.viewCount,
                                product.wishCount,
                                product.address,
                                hasLocation ? distancePath : Expressions.asNumber(0.0),
                                product.createdAt
                        )
                )
                .from(product)
                .leftJoin(product.user, user)
                .leftJoin(product.category, category)
                .where(predicates)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (hasLocation && isDistanceSort(pageable)) {
            query.orderBy(distancePath.asc());
        } else {
            query.orderBy(getOrderSpecifier(pageable.getSort(), product));
        }

        List<ProductResponse> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory.select(product.count())
                .from(product)
                .where(predicates);
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Product> saveAll(Iterable<Product> products) {
        return jpaProductRepository.saveAll(products);
    }

    @Override
    public void deleteAll() {
        jpaProductRepository.deleteAll();
    }

    @Override
    public void delete(Product product) {
        jpaProductRepository.delete(product);
    }

    @Override
    public Optional<ProductDetailResponse> findWithDistinctById(Long productId, Double curLat, Double curLng) {

        boolean hasLocation = curLat != null && curLng != null;

        NumberTemplate<Double> distancePath = hasLocation ? Expressions.numberTemplate(Double.class,
                "ST_Distance_Sphere({0}, ST_GeomFromText({1}, 4326))",
                product.location,
                String.format("POINT(%f %f)", curLat, curLng)
        ) : null;

        return Optional.ofNullable(queryFactory.select(
                        Projections.constructor(ProductDetailResponse.class,
                                product.id,
                                user.id,
                                user.name,
                                category.id,
                                category.name,
                                product.name,
                                product.description,
                                product.price,
                                Expressions.constant(Collections.emptyList()),
                                product.status,
                                product.viewCount,
                                product.wishCount,
                                product.address,
                                hasLocation ? distancePath : Expressions.asNumber(0.0),
                                product.createdAt
                        )
                )
                .from(product)
                .where(product.id.eq(productId))
                .leftJoin(product.user, user)
                .leftJoin(product.category, category)
                .fetchOne());
    }

    private BooleanExpression withinDistance(NumberTemplate<Double> distancePath, Double searchDistance) {
        if (searchDistance == null) {
            return null;
        }
        return distancePath.loe(searchDistance * 1000);
    }

    private BooleanExpression boundingBox(Double lat, Double lng, Double radiusKm) {
        if (lat == null || lng == null || radiusKm == null) {
            return null;
        }

        double meterPerDegree = 111.0;
        double latDelta = radiusKm / meterPerDegree;
        double lngDelta = radiusKm / (meterPerDegree * Math.cos(Math.toRadians(lat)));

        return Expressions.booleanTemplate(
                "MBRContains(ST_GeomFromText({0}, 4326), {1})",
                String.format("POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                        lat - latDelta, lng - lngDelta,
                        lat - latDelta, lng + lngDelta,
                        lat + latDelta, lng + lngDelta,
                        lat + latDelta, lng - lngDelta,
                        lat - latDelta, lng - lngDelta
                ),
                product.location
        ).eq(true);
    }

    private BooleanExpression eqCategory(Long categoryId) {
        return categoryId != null ? category.id.eq(categoryId) : null;
    }

    private BooleanExpression minPrice(Long minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    private BooleanExpression maxPrice(Long maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }

    private BooleanExpression containsKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? product.name.contains(keyword) : null;
    }

    private boolean isDistanceSort(Pageable pageable) {
        return pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equals("distance"));
    }

    @Override
    public Optional<Product> findByIdWithLock(Long id) {
        return jpaProductRepository.findByIdWithLock(id);
    }

    @Override
    public void increaseWishCount(Long id) {
        jpaProductRepository.increaseWishCount(id);
    }

    @Override
    public void decreaseWishCount(Long id) {
        jpaProductRepository.decreaseWishCount(id);
    }

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id);
    }


    @Override
    @Transactional
    public void batchUpdateViewCount(Map<Long, Long> viewCounts, int batchSize) {
        if (viewCounts.isEmpty()) {
            return;
        }
        List<Entry<Long, Long>> entries = new ArrayList<>(viewCounts.entrySet());
        jdbcTemplate.batchUpdate("UPDATE products SET view_count = view_count + ? WHERE product_id = ?",
                entries,
                batchSize,
                (ps, entry) -> {
                    ps.setLong(1, entry.getValue());
                    ps.setLong(2, entry.getKey());
                }
        );
    }
}
