package com.project.market_service.common.interceptor;

import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.chatroom.application.service.ChatRoomValidator;
import com.project.market_service.common.exception.UnAuthorizationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final ChatRoomValidator chatRoomValidator;
    private static final String SUB_CHAT_ROOM_URI = "/sub/chat/room/";
    private static final String PUB_CHAT_MESSAGE = "/pub/chat/message/";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (accessor == null || accessor.getCommand() == null) {
                return message;
            }

            switch (accessor.getCommand()) {
                case CONNECT -> {
                    return message;
                }
                case SUBSCRIBE -> {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith(SUB_CHAT_ROOM_URI)) {
                        Long roomId = extractRoomId(destination, SUB_CHAT_ROOM_URI);
                        Object userObjectId = accessor.getSessionAttributes().get("userId");
                        if (userObjectId == null) {
                            throw new UnAuthorizationException(AuthErrorCode.AUTH_FORBIDDEN);
                        }

                        Long userId = Long.valueOf(userObjectId.toString());
                        chatRoomValidator.validateUserInRoom(roomId, userId);

                        return message;
                    }
                }
                case SEND -> {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith(PUB_CHAT_MESSAGE)) {
                        Long roomId = extractRoomId(destination, PUB_CHAT_MESSAGE);
                        Long userId = getUserId(accessor);
                        chatRoomValidator.validateUserInRoom(roomId, userId);
                        return message;
                    }
                }
            }
        } catch (UnAuthorizationException e) {
            log.error("[STOMP] Unauthorized access attempt: {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[STOMP] Unhandled exception occurred during message delivery", e);
            throw new MessageDeliveryException(message, e.getMessage());
        }

        return message;
    }

    private Long getUserId(StompHeaderAccessor accessor) {
        Object userObjectId = accessor.getSessionAttributes().get("userId");
        if (userObjectId == null) {
            throw new UnAuthorizationException(AuthErrorCode.AUTH_FORBIDDEN);
        }

        return Long.valueOf(userObjectId.toString());
    }

    private Long extractRoomId(String destination, String prefix) {
        return Long.parseLong(destination.substring(prefix.length()));
    }
}
