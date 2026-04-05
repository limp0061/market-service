package com.project.market_service.chatroom.infrastructure;

import static com.project.market_service.chatroom.domain.QChatRoom.chatRoom;
import static com.project.market_service.product.domain.QProduct.product;

import com.project.market_service.chatroom.domain.ChatRoomRepositoryCustom;
import com.project.market_service.chatroom.presentation.dto.ChatRoomResponse;
import com.project.market_service.user.domain.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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

    private BooleanExpression eqBuyerId(Long userId) {
        return chatRoom.buyer.id.eq(userId);
    }

    private BooleanExpression eqSellerId(Long userId) {
        return chatRoom.seller.id.eq(userId);
    }
}
