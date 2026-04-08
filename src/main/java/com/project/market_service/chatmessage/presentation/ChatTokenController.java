package com.project.market_service.chatmessage.presentation;

import com.project.market_service.chatmessage.application.service.ChatTokenService;
import com.project.market_service.chatmessage.presentation.dto.ChatTokenResponse;
import com.project.market_service.common.dto.ApiResult;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CHAT")
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatTokenController {

    private final ChatTokenService chatTokenService;

    @Operation(summary = "채팅 토큰 발급")
    @ApiResponse(responseCode = "201", description = "채팅 토큰 발급 성공")
    @PostMapping("/ticket")
    public ResponseEntity<ApiResult<ChatTokenResponse>> issueChatToken(
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResult.success(chatTokenService.createChatToken(userInfo.userId()))
        );
    }
}
