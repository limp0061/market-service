package com.project.market_service.chatmessage.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.project.market_service.chatmessage.application.port.out.ChatMessageRepository;
import com.project.market_service.chatmessage.domain.ChatMessage;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageRequest;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageResponse;
import com.project.market_service.chatmessage.presentation.dto.ChatPagingRequest;
import com.project.market_service.chatroom.application.port.out.ChatRoomUserRepository;
import com.project.market_service.chatroom.application.service.ChatRoomValidator;
import com.project.market_service.user.application.port.in.UserUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    UserUseCase userUseCase;

    @Mock
    ChatMessageRepository chatMessageRepository;

    @Mock
    ChatRoomValidator chatRoomValidator;

    @Mock
    ChatRoomUserRepository chatRoomUserRepository;

    @Mock
    SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    @DisplayName("채팅 메시지 보내기 성공")
    void processHandleMessage_success() {
        Long roomId = 1L;
        Long userId = 1L;
        ChatMessageRequest request = new ChatMessageRequest("안녕하세요");

        // 도메인 객체는 실제 객체를 생성해서 사용 (Mocking X)
        ChatMessage chatMessage = ChatMessage.create(roomId, userId, request.content());

        given(chatMessageRepository.save(any(ChatMessage.class))).willReturn(chatMessage);
        given(userUseCase.getName(userId)).willReturn("홍길동");

        ArgumentCaptor<ChatMessageResponse> responseCaptor = ArgumentCaptor.forClass(ChatMessageResponse.class);

        // when
        chatMessageService.processHandleMessage(request, roomId, userId);

        // then
        then(messagingTemplate).should().convertAndSend(anyString(), responseCaptor.capture());
        ChatMessageResponse capturedResponse = responseCaptor.getValue();

        assertAll(
                () -> assertThat(capturedResponse.content()).isEqualTo("안녕하세요"),
                () -> assertThat(capturedResponse.senderName()).isEqualTo("홍길동")
        );
        then(messagingTemplate).should().convertAndSend(anyString(), responseCaptor.capture());

    }

    @Test
    @DisplayName("채팅 내역 조회 시 읽음 처리")
    void getChatMessages_success() {
        // given
        Long roomId = 1L;
        Long userId = 1L;
        ChatPagingRequest request = new ChatPagingRequest(roomId, null, 10);

        given(chatMessageRepository.findMessagesByRoomId(request)).willReturn(List.of());

        // when
        chatMessageService.getChatMessages(request, userId);

        // then
        then(chatRoomValidator).should().validateUserInRoom(roomId, userId); // 권한 체크 확인
        then(chatRoomUserRepository).should().updateLastReadAt(roomId, userId); // 읽음 처리 확인
    }
}