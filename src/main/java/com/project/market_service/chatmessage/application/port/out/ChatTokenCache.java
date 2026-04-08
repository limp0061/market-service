package com.project.market_service.chatmessage.application.port.out;

public interface ChatTokenCache {
    void save(String token, Long userId);
}
