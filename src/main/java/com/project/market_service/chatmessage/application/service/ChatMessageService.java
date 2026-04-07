package com.project.market_service.chatmessage.application.service;

import com.project.market_service.chatmessage.application.port.in.ChatMessageUseCase;
import com.project.market_service.chatmessage.application.port.out.ChatMessageRepository;
import com.project.market_service.chatmessage.domain.ChatMessage;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageRequest;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageResponse;
import com.project.market_service.chatroom.application.port.out.ChatRoomRepository;
import com.project.market_service.chatroom.domain.ChatRoom;
import com.project.market_service.chatroom.exception.ChatRoomErrorCode;
import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import com.project.market_service.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService implements ChatMessageUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ChatMessageResponse processHandleMessage(ChatMessageRequest request, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.roomId())
                .orElseThrow(() -> new EntityNotFoundException(ChatRoomErrorCode.CHATROOM_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND));

        ChatMessage chatMessage = ChatMessage.create(request.roomId(), request.senderId(), request.content());
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/sub/chatroom/room/" + request.roomId(), savedChatMessage);
        return null;
    }
}
