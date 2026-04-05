package com.project.market_service.chatroom.presentation;

import com.project.market_service.chatroom.application.service.ChatRoomService;
import com.project.market_service.chatroom.presentation.dto.ChatRoomCreatRequest;
import com.project.market_service.chatroom.presentation.dto.ChatRoomResponse;
import com.project.market_service.common.dto.ApiResult;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CHATROOM", description = "채팅방 생성 및 조회")
@RestController
@RequestMapping("/api/v1/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 생성")
    @ApiResponse(responseCode = "200", description = "채팅방 성공/조회 반환 성공")
    @PostMapping
    public ResponseEntity<ApiResult<ChatRoomResponse>> createChatRoom(
            @Valid @RequestBody ChatRoomCreatRequest request,
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(chatRoomService.createChatRoom(request.productId(), userInfo.userId()))
        );
    }

    @Operation(summary = "채팅창 목록 조회")
    @ApiResponse(responseCode = "200", description = "내 채팅방 목록 반환 성공")
    @GetMapping
    public ResponseEntity<ApiResult<List<ChatRoomResponse>>> getAllChatRooms(
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(chatRoomService.getMyChatRooms(userInfo.userId()))
        );
    }

}

