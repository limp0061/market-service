package com.project.market_service.chatmessage.presentation;

import com.project.market_service.chatmessage.application.port.in.ChatMessageUseCase;
import com.project.market_service.chatmessage.presentation.dto.ChatMessageResponse;
import com.project.market_service.chatmessage.presentation.dto.ChatPagingRequest;
import com.project.market_service.common.dto.ApiResult;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CHAT")
@RestController
@RequestMapping("/api/v1/chatrooms")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageUseCase chatMessageUseCase;

    @Operation(summary = "채팅 메시지 조회")
    @ApiResponse(responseCode = "200", description = "채팅 메시지 조회 성공")
    @GetMapping("/messages")
    public ResponseEntity<ApiResult<List<ChatMessageResponse>>> getChatMessages(
            @ModelAttribute ChatPagingRequest request,
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(chatMessageUseCase.getChatMessages(request, userInfo.userId()))
        );
    }
}
