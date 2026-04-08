package com.project.market_service.chatmessage.application.service;

import com.project.market_service.chatmessage.application.port.in.ChatMessageUseCase;
import com.project.market_service.chatmessage.application.port.out.ChatMessageRepository;
import com.project.market_service.chatmessage.domain.ChatMessage;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageRequest;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageResponse;
import com.project.market_service.chatmessage.presentation.dto.ChatPagingRequest;
import com.project.market_service.chatroom.application.port.out.ChatRoomUserRepository;
import com.project.market_service.chatroom.application.service.ChatRoomValidator;
import com.project.market_service.user.application.port.in.UserUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService implements ChatMessageUseCase {

    private final UserUseCase userUseCase;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomValidator chatRoomValidator;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void processHandleMessage(ChatMessageRequest request, Long roomId, Long userId) {

        ChatMessage chatMessage = ChatMessage.create(roomId, userId, request.content());
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        String senderName = userUseCase.getName(userId);

        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                ChatMessageResponse.of(savedChatMessage, senderName));
    }

    @Override
    public List<ChatMessageResponse> getChatMessages(ChatPagingRequest request, Long userId) {
        chatRoomValidator.validateUserInRoom(request.roomId(), userId);

        List<ChatMessageResponse> list = chatMessageRepository.findMessagesByRoomId(request)
                .stream()
                .map(chat -> ChatMessageResponse.of(chat, userUseCase.getName(chat.getSenderId())))
                .toList();

        chatRoomUserRepository.updateLastReadAt(request.roomId(), userId);

        return list;
    }
}
