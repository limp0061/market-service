package com.project.market_service.common.scheduler;

public interface SchedulerTask {
    String getName();

    void run();
}
