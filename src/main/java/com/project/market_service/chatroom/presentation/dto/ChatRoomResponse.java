package com.project.market_service.chatroom.presentation.dto;

import com.project.market_service.chatroom.domain.ChatRoom;
import com.project.market_service.product.domain.Product;
import com.project.market_service.user.domain.User;

public record ChatRoomResponse(
        Long chatRoomId,
        Long partnerId,
        String partnerName,
        Long productId,
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
