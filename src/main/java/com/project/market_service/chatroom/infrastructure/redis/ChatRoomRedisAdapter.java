package com.project.market_service.chatroom.infrastructure.redis;

import com.project.market_service.chatroom.application.port.out.ChatRoomCache;
import com.project.market_service.common.constants.RedisConstants;
import com.project.market_service.common.redis.RedisManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomRedisAdapter implements ChatRoomCache {

    private final RedisManager redisManager;

    @Override
    public void addParticipants(Long roomId, Long... userIds) {
        if (userIds != null && userIds.length > 0) {
            String redisKey = String.format(RedisConstants.CHAT_ROOM_USER_LIST, roomId);
            redisManager.addToSet(redisKey, (Object[]) userIds);
            redisManager.expire(redisKey, 7, TimeUnit.DAYS);
        }
    }

    @Override
    public boolean isUsersInRoom(Long roomId, Long userId) {
        String redisKey = String.format(RedisConstants.CHAT_ROOM_USER_LIST, roomId);
        return redisManager.isMember(redisKey, userId);
    }

    @Override
    public void updateActiveRoom(Long userId, Long roomId) {
        String redisKey = String.format(RedisConstants.CHAT_ACTIVE_USER_ROOM, roomId, userId);
        redisManager.addToSet(redisKey, RedisConstants.ACTIVE_VALUE, 1, TimeUnit.HOURS);
    }
}
