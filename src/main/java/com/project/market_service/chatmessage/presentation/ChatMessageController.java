package com.project.market_service.chatmessage.presentation;

import com.project.market_service.chatmessage.application.service.ChatMessageService;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService messageService;

    @MessageMapping("/chat/message/{roomId}")
    public void handleMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            SimpMessageHeaderAccessor accessor
    ) {
        String userId = (String) accessor.getSessionAttributes().get("userId");
        messageService.processHandleMessage(request, roomId, Long.parseLong(userId));
    }
}
