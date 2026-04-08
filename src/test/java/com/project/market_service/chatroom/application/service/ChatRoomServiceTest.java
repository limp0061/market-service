package com.project.market_service.chatroom.application.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.project.market_service.chatroom.application.port.out.ChatRoomCache;
import com.project.market_service.chatroom.application.port.out.ChatRoomRepository;
import com.project.market_service.chatroom.domain.ChatRoom;
import com.project.market_service.chatroom.exception.ChatRoomErrorCode;
import com.project.market_service.chatroom.presentation.dto.ChatRoomResponse;
import com.project.market_service.common.exception.InvalidValueException;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.user.application.port.out.UserRepository;
import com.project.market_service.user.domain.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ChatRoomCache chatRoomCache;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private User seller;
    private User buyer;
    private Product product;

    @BeforeEach
    void setUp() {
        seller = User.builder().id(1L).build();
        buyer = User.builder().id(2L).build();
        product = Product.builder()
                .id(10L)
                .name("맥북 프로")
                .user(seller)   // seller 소유
                .build();
    }

    @Test
    @DisplayName("채팅방이 없으면 새로 생성한다")
    void createChatRoom_success() {
        // given
        ChatRoom savedRoom = ChatRoom.builder()
                .id(200L)
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .build();

        given(chatRoomRepository.findByProductIdAndBuyerId(product.getId(), buyer.getId())).willReturn(
                Optional.empty());
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(userRepository.findById(buyer.getId())).willReturn(Optional.of(buyer));
        given(chatRoomRepository.save(any(ChatRoom.class)))
                .willReturn(savedRoom);

        // when
        ChatRoomResponse response = chatRoomService.createChatRoom(product.getId(), buyer.getId());

        // then
        assertThat(response.chatRoomId()).isEqualTo(200L);

        ArgumentCaptor<ChatRoom> chatRoomCaptor = ArgumentCaptor.forClass(ChatRoom.class);
        then(chatRoomRepository).should().save(chatRoomCaptor.capture());

        ChatRoom captured = chatRoomCaptor.getValue();
        assertAll(
                () -> assertThat(captured.getBuyer().getId()).isEqualTo(2L),
                () -> assertThat(captured.getSeller().getId()).isEqualTo(1L),
                () -> assertThat(captured.getProduct().getId()).isEqualTo(10L),
                () -> assertThat(captured.getProduct().getName()).isEqualTo("맥북 프로")
        );
    }

    @Test
    @DisplayName("채팅방이 이미 존재하면 기존 채팅방을 반환한다")
    void existingChatRoom() {
        //given
        ChatRoom existing = ChatRoom.builder()
                .id(200L)
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .build();

        given(chatRoomRepository.findByProductIdAndBuyerId(product.getId(), buyer.getId())).willReturn(
                Optional.of(existing));

        // when
        ChatRoomResponse response = chatRoomService.createChatRoom(product.getId(), buyer.getId());

        // then
        assertThat(response.chatRoomId()).isEqualTo(200L);

        assertAll(
                () -> assertThat(response.partnerId()).isEqualTo(1L),
                () -> assertThat(response.productId()).isEqualTo(10L),
                () -> assertThat(response.productName()).isEqualTo("맥북 프로")
        );
    }

    @Test
    @DisplayName("자기 자신과 채팅방을 생성할 수 없습니다")
    void creatChatRoom_failed_self_chatRoom() {
        buyer = User.builder().id(1L).build();
        product = Product.builder()
                .id(10L)
                .name("맥북 프로")
                .user(seller)
                .build();

        given(chatRoomRepository.findByProductIdAndBuyerId(product.getId(), buyer.getId())).willReturn(
                Optional.empty());
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> chatRoomService.createChatRoom(product.getId(), buyer.getId()))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(ChatRoomErrorCode.CANNOT_CHAT_WITH_SELF.getMessage());

        then(userRepository).should(never()).findById(anyLong());
        then(chatRoomRepository).should(never()).save(any(ChatRoom.class));
    }

}