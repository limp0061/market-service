package com.project.market_service.common.scheduler;

import com.project.market_service.product.application.service.ProductAsyncViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductViewCountAsyncScheduler extends AbstractScheduler {

    private final ProductAsyncViewService productAsyncViewService;

    @Override
    public String getName() {
        return "Product-view-count-async-scheduler";
    }

    @Override
    public void run() {
        productAsyncViewService.asyncViewProcess();
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void schedule() {
        super.execute();
    }
}
