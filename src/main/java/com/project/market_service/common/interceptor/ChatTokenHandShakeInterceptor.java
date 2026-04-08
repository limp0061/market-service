package com.project.market_service.common.interceptor;

import static com.project.market_service.common.constants.RedisConstants.CHAT_TOKEN_PREFIX;

import com.project.market_service.common.redis.RedisManager;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class ChatTokenHandShakeInterceptor implements HandshakeInterceptor {

    private final RedisManager redisManager;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String query = request.getURI().getQuery();
        String token = extractChatToken(query);

        String userId = redisManager.get(CHAT_TOKEN_PREFIX + token, String.class);
        if (!StringUtils.hasText(userId)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        redisManager.delete(CHAT_TOKEN_PREFIX + token);

        attributes.put("userId", userId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
    }

    private String extractChatToken(String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }
        return Arrays.stream(query.split("&"))
                .filter(q -> q.startsWith("ticket="))
                .map(q -> q.split("=")[1])
                .findFirst()
                .orElse(null);

    }
}
