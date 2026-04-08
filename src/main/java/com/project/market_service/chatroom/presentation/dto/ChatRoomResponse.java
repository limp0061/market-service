package com.project.market_service.chatroom.presentation.dto;

import com.project.market_service.chatroom.domain.ChatRoom;
import com.project.market_service.product.domain.Product;
import com.project.market_service.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record ChatRoomResponse(

        @Schema(description = "채팅방 고유 식별 번호", example = "1")
        Long chatRoomId,

        @Schema(description = "채팅방 상대 고유 식별 번호", example = "1")
        Long partnerId,

        @Schema(description = "채팅방 상대 이름", example = "홍길동")
        String partnerName,

        @Schema(description = "채팅방 상품 고유 식별 번호", example = "1")
        Long productId,

        @Schema(description = "채팅방 상품 이름", example = "아이폰")
        String productName
) {
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        Product product = chatRoom.getProduct();
        User seller = chatRoom.getSeller();
        return new ChatRoomResponse(
                chatRoom.getId(),
                seller.getId(),
                seller.getName(),
                product.getId(),
                product.getName()
        );
    }
}
