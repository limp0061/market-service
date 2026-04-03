package com.project.market_service.common.constants;

public final class RedisConstants {
    private RedisConstants() {
    }

    public static final String REFRESH_TOKEN_PREFIX = "refresh:";
    public static final String BLACKLIST_TOKEN_PREFIX = "blacklist:";
    public static final String PRODUCT_USER_VIEW = "product:%d:user:%d";
    public static final String PRODUCT_VIEW_COUNT = "product:%d:view-count";
    public static final String PRODUCT_VIEW_PATTERN = "product:*:view-count";
    public static final String PRODUCT_VIEW_SCHEDULE_LOCK = "product:view-schedule-lock";
    public static final int REDIS_SCAN_COUNT = 100;
    public static final String LOCKED = "locked";
    public static final String VIEWED = "viewed";
}
