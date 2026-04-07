package com.project.market_service.chatroom.infrastructure.persistence;

import static com.project.market_service.chatroom.domain.QChatRoom.chatRoom;
import static com.project.market_service.product.domain.QProduct.product;

import com.project.market_service.chatroom.application.dto.ChatRoomParticipants;
import com.project.market_service.chatroom.application.port.out.ChatRoomRepository;
import com.project.market_service.chatroom.domain.ChatRoom;
import com.project.market_service.chatroom.presentation.dto.ChatRoomResponse;
import com.project.market_service.user.domain.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomPersistenceAdapter implements ChatRoomRepository {

    private final JpaChatRoomRepository jpaChatRoomRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoomResponse> getMyChatRooms(Long userId) {

        QUser buyer = new QUser("buyer");
        QUser seller = new QUser("seller");
        return queryFactory.select(
                        Projections.constructor(ChatRoomResponse.class,
                                chatRoom.id,
                                new CaseBuilder()
                                        .when(chatRoom.buyer.id.eq(userId))
                                        .then(chatRoom.seller.id)
                                        .otherwise(chatRoom.buyer.id),
                                new CaseBuilder()
                                        .when(chatRoom.buyer.id.eq(userId))
                                        .then(chatRoom.seller.name)
                                        .otherwise(chatRoom.buyer.name),
                                product.id,
                                product.name
                        )
                ).from(chatRoom)
                .join(chatRoom.product, product)
                .join(chatRoom.seller, seller)
                .join(chatRoom.buyer, buyer)
                .where(eqBuyerId(userId).or(eqSellerId(userId)))
                .fetch();
    }

    @Override
    public Optional<ChatRoom> findByProductIdAndBuyerId(Long productId, Long buyerId) {
        return jpaChatRoomRepository.findByProductIdAndBuyerId(productId, buyerId);
    }

    @Override
    public Optional<ChatRoom> findById(Long id) {
        return jpaChatRoomRepository.findById(id);
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return jpaChatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> saveAll(Iterable<ChatRoom> chatRooms) {
        return jpaChatRoomRepository.saveAll(chatRooms);
    }

    @Override
    public Optional<ChatRoomParticipants> findParticipantsByRoomId(Long roomId, Long userId) {
        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(ChatRoomParticipants.class,
                                        chatRoom.buyer.id,
                                        chatRoom.seller.id
                                )
                        )
                        .from(chatRoom)
                        .where(chatRoom.id.eq(roomId),
                                isUserParticipant(userId)
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression isUserParticipant(Long userId) {
        return (chatRoom.buyer.id.eq(userId).and(chatRoom.buyerIn.isTrue()))
                .or(chatRoom.seller.id.eq(userId).and(chatRoom.sellerIn.isTrue()));
    }

    private BooleanExpression eqBuyerId(Long userId) {
        return chatRoom.buyer.id.eq(userId);
    }

    private BooleanExpression eqSellerId(Long userId) {
        return chatRoom.seller.id.eq(userId);
    }
}
