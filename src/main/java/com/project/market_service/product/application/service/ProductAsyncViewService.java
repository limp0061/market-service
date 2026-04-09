package com.project.market_service.product.application.service;

import com.project.market_service.product.application.port.in.ViewCountCache;
import com.project.market_service.product.application.port.out.ProductPort;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductAsyncViewService {

    private final ViewCountCache viewCountCache;
    private final ProductPort productPort;
    private static final int BATCH_SIZE = 100;

    public void asyncViewProcess() {
        if (!viewCountCache.acquireLock()) {
            log.info("[Skip] Already Async Product View Count");
            return;
        }

        try {
            Map<Long, Long> viewCounts = viewCountCache.getViewCounts();
            if (!viewCounts.isEmpty()) {
                productPort.batchUpdateViewCount(viewCounts, BATCH_SIZE);
                viewCountCache.deleteViewCountKeys(viewCounts.keySet());
                log.info("[Success] Sync {} product view counts to DB", viewCounts.size());
            }
        } finally {
            viewCountCache.releaseLock();
        }
    }
}
