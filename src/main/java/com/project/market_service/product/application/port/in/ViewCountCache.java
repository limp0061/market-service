package com.project.market_service.product.application.port.in;

import java.util.Map;
import java.util.Set;

public interface ViewCountCache {
    long increaseViewCount(Long productId, Long userId);

    Map<Long, Long> getViewCounts();

    boolean acquireLock();

    void releaseLock();

    void deleteViewCountKeys(Set<Long> productIds);
}
