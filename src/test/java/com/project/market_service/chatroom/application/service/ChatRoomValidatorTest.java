package com.project.market_service.chatroom.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.project.market_service.chatroom.application.dto.ChatRoomParticipants;
import com.project.market_service.chatroom.application.port.out.ChatRoomCache;
import com.project.market_service.chatroom.application.port.out.ChatRoomRepository;
import com.project.market_service.chatroom.exception.ChatRoomErrorCode;
import com.project.market_service.common.exception.UnAuthorizationException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatRoomValidatorTest {

    @Mock
    ChatRoomCache chatRoomCache;
    @Mock
    ChatRoomRepository chatRoomRepository;
    @InjectMocks
    private ChatRoomValidator chatRoomValidator;

    @Test
    @DisplayName("채팅방에 포함된 사용자인지 검증 - redis에 정보가 있는 경우")
    void validate_userInRedisRoom_success() {
        // given
        Long roomId = 1L;
        Long userId = 1L;
        given(chatRoomCache.isUsersInRoom(roomId, userId)).willReturn(true);

        // when
        chatRoomValidator.validateUserInRoom(roomId, userId);

        // then
        then(chatRoomRepository).should(never()).findParticipantsByRoomId(anyLong(), anyLong());
        then(chatRoomCache).should(never()).addParticipants(anyLong(), anyLong());
    }

    @Test
    @DisplayName("채팅방에 포함된 사용자인지 검증 성공 - redis에 정보가 없는 경우")
    void validate_userInRoom_success() {
        // given
        Long roomId = 1L;
        Long buyerId = 1L;
        Long sellerId = 2L;
        ChatRoomParticipants participants = new ChatRoomParticipants(buyerId, sellerId);
        given(chatRoomCache.isUsersInRoom(roomId, buyerId)).willReturn(false);
        given(chatRoomRepository.findParticipantsByRoomId(roomId, buyerId)).willReturn(Optional.of(participants));

        // when
        chatRoomValidator.validateUserInRoom(roomId, buyerId);

        // then
        then(chatRoomRepository).should(times(1)).findParticipantsByRoomId(roomId, buyerId);
        then(chatRoomCache).should(times(1)).addParticipants(eq(roomId), eq(buyerId), eq(sellerId));
    }

    @Test
    @DisplayName("채팅방에 포함된 사용자가 아닌 경우")
    void validate_userInRoom_fail() {
        // given
        Long roomId = 1L;
        Long userId = 1L;
        given(chatRoomCache.isUsersInRoom(roomId, userId)).willReturn(false);
        given(chatRoomRepository.findParticipantsByRoomId(roomId, userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatRoomValidator.validateUserInRoom(roomId, userId))
                .isInstanceOf(UnAuthorizationException.class)
                .hasMessage(ChatRoomErrorCode.NOT_CHATROOM_PARTICIPANT.getMessage());

        then(chatRoomRepository).should(times(1)).findParticipantsByRoomId(roomId, userId);
        then(chatRoomCache).should(never()).addParticipants(anyLong(), anyLong());
    }

}