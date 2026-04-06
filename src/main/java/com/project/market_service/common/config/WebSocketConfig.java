package com.project.market_service.common.config;

import com.project.market_service.common.interceptor.ChatTokenHandShakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker // 상속받은 인터페이스를 통해 STOMP 메시지를 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatTokenHandShakeInterceptor chatTokenHandShakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 서버에서 클라이언트로 메시지를 보낼 때 사용할 접두사 (구독 경로)
        // 클라이언트는 "/sub/chatroom/room/1" 과 같은 경로를 Subscribe(구독)
        config.enableSimpleBroker("/sub");

        // 클라이언트에서 서버로 메시지를 보낼 때 사용할 접두사 (발행 경로)
        // 클라이언트가 "/pub/chatroom/message"로 메시지를 보내면 @MessageMapping이 붙은 컨트롤러로 배달
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 최초 웹소켓 연결을 위한 엔드포인트 설정
        // 클라이언트는 "ws://localhost:8080/ws/chatroom" 경로로 연결을 시도
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*") // CORS 허용 (실제 운영시에는 특정 도메인만 지정)
                .addInterceptors(chatTokenHandShakeInterceptor);
//                .withSockJS(); // 웹소켓을 지원하지 않는 브라우저를 위한 대체 옵션(SockJS) 활성화
    }
}
