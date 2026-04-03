package com.project.market_service.product.application.service;


import static com.project.market_service.common.constants.RedisConstants.LOCKED;
import static com.project.market_service.common.constants.RedisConstants.PRODUCT_USER_VIEW;
import static com.project.market_service.common.constants.RedisConstants.PRODUCT_VIEW_COUNT;
import static com.project.market_service.common.constants.RedisConstants.PRODUCT_VIEW_PATTERN;
import static com.project.market_service.common.constants.RedisConstants.PRODUCT_VIEW_SCHEDULE_LOCK;
import static com.project.market_service.common.constants.RedisConstants.REDIS_SCAN_COUNT;
import static com.project.market_service.common.constants.RedisConstants.VIEWED;

import com.project.market_service.common.constants.RedisConstants;
import com.project.market_service.common.redis.RedisManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductViewCountService {

    private final RedisManager redisManager;

    public long increaseViewCount(Long productId, Long userId) {
        String userViewKey = String.format(PRODUCT_USER_VIEW, productId, userId);
        String viewCountKey = String.format(PRODUCT_VIEW_COUNT, productId);

        boolean isFirstView = redisManager.setIfAbsent(userViewKey, VIEWED, 1, TimeUnit.DAYS);

        if (isFirstView) {
            return redisManager.increment(viewCountKey);
        }

        String count = redisManager.getCount(viewCountKey);
        return count == null ? 0 : Long.parseLong(count);
    }

    public Map<Long, Long> getViewCounts() {

        Map<Long, Long> productViewCounts = new HashMap<>();
        List<String> keys = redisManager.scanKeys(PRODUCT_VIEW_PATTERN, REDIS_SCAN_COUNT);
        for (String key : keys) {
            productViewCounts.put(Long.parseLong(extractProductId(key)), Long.valueOf(redisManager.getCount(key)));
        }

        return productViewCounts;
    }

    public boolean acquireLock() {
        return redisManager.setIfAbsent(PRODUCT_VIEW_SCHEDULE_LOCK, LOCKED, 7, TimeUnit.MINUTES);
    }

    public void releaseLock() {
        redisManager.delete(RedisConstants.PRODUCT_VIEW_SCHEDULE_LOCK);
    }

    private String extractProductId(String key) {
        return key.split(":")[1];
    }

    public void deleteViewCountKeys(Set<Long> productIds) {
        List<String> keys = productIds.stream()
                .map(id -> String.format(RedisConstants.PRODUCT_VIEW_COUNT, id))
                .toList();
        redisManager.deleteKeys(keys);
    }
}
