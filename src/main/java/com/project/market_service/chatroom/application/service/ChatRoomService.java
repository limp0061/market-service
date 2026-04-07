package com.project.market_service.chatroom.application.service;

import com.project.market_service.chatroom.application.port.in.ChatRoomUseCase;
import com.project.market_service.chatroom.application.port.out.ChatRoomCache;
import com.project.market_service.chatroom.application.port.out.ChatRoomRepository;
import com.project.market_service.chatroom.domain.ChatRoom;
import com.project.market_service.chatroom.exception.ChatRoomErrorCode;
import com.project.market_service.chatroom.presentation.dto.ChatRoomResponse;
import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.common.exception.InvalidValueException;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.product.exception.ProductErrorCode;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import com.project.market_service.user.domain.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService implements ChatRoomUseCase {

    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ChatRoomCache chatRoomCache;

    @Override
    @Transactional
    public ChatRoomResponse createChatRoom(Long productId, Long buyerId) {

        Optional<ChatRoom> chatRoom = chatRoomRepository.findByProductIdAndBuyerId(productId, buyerId);
        if (chatRoom.isPresent()) {
            return ChatRoomResponse.from(chatRoom.get());
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND,
                        "productId: " + productId));

        User seller = product.getUser();
        if (seller.getId().equals(buyerId)) {
            throw new InvalidValueException(ChatRoomErrorCode.CANNOT_CHAT_WITH_SELF);
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND, "buyerId: " + buyerId));

        ChatRoom createdChatRoom = chatRoomRepository.save(
                ChatRoom.create(product, buyer, seller)
        );

        chatRoomCache.addParticipants(createdChatRoom.getId(), buyer.getId(), seller.getId());

        log.info("[ChatRoom Create] productId: {}, productName: {}, buyerId: {}, sellerId: {}", product.getId(),
                product.getName(), buyer.getId(), seller.getId());
        return ChatRoomResponse.from(createdChatRoom);
    }

    @Override
    public List<ChatRoomResponse> getMyChatRooms(Long userId) {
        return chatRoomRepository.getMyChatRooms(userId);
    }
}
