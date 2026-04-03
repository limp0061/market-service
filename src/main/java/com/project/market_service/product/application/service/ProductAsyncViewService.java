package com.project.market_service.product.application.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductAsyncViewService {

    private final ProductViewCountService redisService;
    private final ProductService productService;
    private static final int BATCH_SIZE = 100;

    public void asyncViewProcess() {
        if (!redisService.acquireLock()) {
            log.info("[Skip] Already Async Product View Count");
            return;
        }

        try {
            Map<Long, Long> viewCounts = redisService.getViewCounts();
            if (!viewCounts.isEmpty()) {
                productService.batchUpdateViewCount(viewCounts, BATCH_SIZE);
                redisService.deleteViewCountKeys(viewCounts.keySet());
                log.info("[Success] Sync {} product view counts to DB", viewCounts.size());
            }
        } finally {
            redisService.releaseLock();
        }
    }
}
