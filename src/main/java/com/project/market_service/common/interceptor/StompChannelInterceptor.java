package com.project.market_service.common.interceptor;

import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.chatroom.application.port.out.ChatRoomCache;
import com.project.market_service.chatroom.application.service.ChatRoomValidator;
import com.project.market_service.common.exception.UnAuthorizationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    private final ChatRoomCache chatRoomCache;
    private static final String SUB_CHAT_ROOM_URI = "/sub/chat/room/";

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
                        Long roomId = extractRoomId(destination);
                        Object userObjectId = accessor.getSessionAttributes().get("userId");
                        if (userObjectId == null) {
                            throw new UnAuthorizationException(AuthErrorCode.AUTH_FORBIDDEN);
                        }

                        Long userId = Long.valueOf(userObjectId.toString());
                        chatRoomValidator.validateUserInRoom(roomId, userId);

                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

                        Set<Long> subscribedRooms = (Set<Long>) sessionAttributes.get("subscribedRooms");
                        if (subscribedRooms == null) {
                            subscribedRooms = new HashSet<>();
                            sessionAttributes.put("subscribedRooms", subscribedRooms);
                        }

                        subscribedRooms.add(roomId);
                        accessor.getSessionAttributes().put("subscribedRooms", subscribedRooms);

                        chatRoomCache.updateActiveRoom(userId, roomId);

                        return message;
                    }
                }
                case SEND -> {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith(SUB_CHAT_ROOM_URI)) {
                        Long roomId = extractRoomId(destination);
                        Long userId = getUserId(accessor);
                        chatRoomValidator.validateUserInRoom(roomId, userId);

                        chatRoomCache.updateActiveRoom(userId, roomId);
                        return message;
                    }
                }

                case UNSUBSCRIBE -> {

                    String roomIdHeader = accessor.getFirstNativeHeader("roomId");
                    if (roomIdHeader != null) {
                        Long roomId = Long.valueOf(roomIdHeader);
                        Long userId = getUserId(accessor);

                        // 세션 리스트에서 제거
                        Set<Long> subscribedRooms = (Set<Long>) accessor.getSessionAttributes()
                                .get("subscribedRooms");
                        if (subscribedRooms != null) {
                            subscribedRooms.remove(roomId);
                        }
                        // Redis 상태 삭제 (이제 이 방은 안 보니까 알림 받아야 함)
//                        chatRoomCache.deleteActiveRoom(userId, roomId);
                    }

                }
                case DISCONNECT -> {
                    // TODO:: 앱을 닫았거나 로그아웃 (접속 중인 방 목록에서 삭제 - redis)
           /*         Long userId = geteUserId(accessor);
                    Set<Long> subscribedRooms = (Set<Long>) accessor.getSessionAttributes().get("subscribedRooms");

                    if (subscribedRooms != null && userId != null) {
                        // 방을 여러 개 열어뒀어도 한꺼번에 청소!
                        subscribedRooms.forEach(roomId -> chatRoomCache.deleteActiveRoom(userId, roomId));
                    }*/
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

    private Long extractRoomId(String destination) {
        return Long.parseLong(destination.substring(SUB_CHAT_ROOM_URI.length()));
    }
}
