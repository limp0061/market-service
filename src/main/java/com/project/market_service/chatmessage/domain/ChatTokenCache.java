package com.project.market_service.chatmessage.domain;

public interface ChatTokenCache {
    void save(String token, Long userId);
}
