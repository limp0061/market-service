package com.project.market_service.common.scheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractScheduler implements SchedulerTask {

    public void execute() {
        log.info("[Scheduler Start]: {}", getName());
        long startTime = System.currentTimeMillis();

        try {
            run();
        } catch (Exception e) {
            log.error("[Scheduler Error]: {}", getName(), e);
        }
        long execution = System.currentTimeMillis() - startTime;
        log.info("[Scheduler Finish]: {}, (소요시간: {}ms)", getName(), execution);
    }
}
