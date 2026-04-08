package com.project.market_service.chatroom.presentation;

import static com.project.market_service.common.util.GeoUtils.createPoint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.chatroom.application.port.out.ChatRoomRepository;
import com.project.market_service.chatroom.domain.ChatRoom;
import com.project.market_service.chatroom.presentation.dto.ChatRoomCreatRequest;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import com.project.market_service.config.IntegrationTestBase;
import com.project.market_service.product.domain.Product;
import com.project.market_service.product.domain.ProductRepository;
import com.project.market_service.user.application.port.out.UserRepository;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserRole;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class ChatRoomControllerTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    User buyer;
    User seller;
    Product product;
    Category category;

    @BeforeEach
    void setUp() throws Exception {
        buyer = User.signUp("구매자", "buyerId", "password1234!");
        seller = User.signUp("판매자", "sellerId", "password1234!");
        userRepository.saveAll(List.of(buyer, seller));

        category = Category.create("전자기기");
        categoryRepository.save(category);

        product = Product.create(seller, category, "아이폰 S6", "거의 새거입니다.", new BigDecimal("1200000"),
                null, createPoint(126.9725, 37.5565), "서울 중구 봉래동");

        productRepository.save(product);

        login(buyer);
    }

    @Test
    @DisplayName("채팅방 생성 성공")
    void createChatRoom_success() throws Exception {
        ChatRoomCreatRequest request = new ChatRoomCreatRequest(product.getId());

        mockMvc.perform(post("/api/v1/chatrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(product.getId()))
                .andExpect(jsonPath("$.data.partnerId").value(seller.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("채팅방 목록 조회 성공")
    void getMyChatRooms_success() throws Exception {

        login(seller);

        User sellerA = User.signUp("판매자A", "sellerAId", "password1234!");
        User buyerA = User.signUp("구매자A", "buyerAId", "password1234!");
        User buyerB = User.signUp("구매자B", "buyerBId", "password1234!");
        User buyerC = User.signUp("구매자C", "buyerCId", "password1234!");
        userRepository.saveAll(List.of(sellerA, buyerA, buyerB, buyerC));

        Product product2 = Product.create(sellerA, category, "아이폰 S6", "거의 새거입니다.", new BigDecimal("1200000"),
                null, createPoint(126.9725, 37.5565), "서울 중구 봉래동");
        productRepository.save(product2);

        ChatRoom chatRoomA = ChatRoom.create(product, buyer, seller);
        ChatRoom chatRoomB = ChatRoom.create(product, buyerA, seller);
        ChatRoom chatRoomC = ChatRoom.create(product, buyerB, seller);
        ChatRoom chatRoomD = ChatRoom.create(product2, buyer, sellerA);
        ChatRoom chatRoomE = ChatRoom.create(product2, seller, sellerA);
        chatRoomRepository.saveAll(List.of(chatRoomA, chatRoomB, chatRoomC, chatRoomD, chatRoomE));

        mockMvc.perform(get("/api/v1/chatrooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.size()").value(4))
                .andExpect(jsonPath("$.data[?(@.partnerName == '구매자A')]").exists())
                .andDo(print());
    }

    private void login(User user) {
        JwtUserInfo userInfo = new JwtUserInfo(user.getId(), user.getLoginId(), UserRole.USER.getCode());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userInfo, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}