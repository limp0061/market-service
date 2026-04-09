# 중고 마켓 서비스

- 헥사고날 아키텍쳐 기반의 중고거래 백엔드 API
## 📌 프로젝트 개요
 
중고 거래을 위해 물품 등록, 위치 기반 검색 및 1:1 채팅을 제공하는 REST API입니다.

Backend
- Core: Java 17, Spring Boot 3.x
- Security: Spring Security, JWT
- Communication: STOMP, WebSocket (1:1 채팅)
- Validation: Spring Validation, Lombok
- Logging: Log4j2(비동기 로깅)

Data & Persistence
- RDB: MySQL 8.4 (Spatial Index를 활용한 위치 기반 상품 검색 최적화)
- NoSQL: MongoDB (채팅 메시지 이력 저장)
- Cache/Lock: Redis (조회수 성능 최적화 및 분산 환경 동시성 제어)
- ORM: Spring Data JPA, QueryDSL (복잡한 동적 쿼리 해결)

Infrastructure & Test
- DevOps: Docker (컨테이너 기반 환경 구성)
- Test: Testcontainers (MySQL, Redis 등 독립적인 테스트 컨테이너 환경 구축)
- Documentation: Swagger (OpenAPI 3.0 기반 API 명세 자동화)

## 아키텍처 구조 (Hexagonal)
- domain: 핵심 엔티티
- application: In/Out 포트 및 서비스
- infrastructure: DB, Redis 등 외부 기술 구현(Adapter)
- presentation: Rest API 컨트롤러

## 기능 및 트러블 슈팅
### 주요 기능
- 위치 기반 상품 검색: MySQL Spatial Index를 활용하여 반경 N km 내 상품 필터링
- 비동기 조회수 시스템: Redis에서 조회수를 캐싱하고 Scheduler를 통해 DB에 배치 업데이트(Bulk Update)하여 DB 부하 최적화
- 실시간 1:1 채팅: WebSocket & STOMP 기반의 채팅 시스템
- 
### 트러블 슈팅
웹소켓 인증 방식
1. 초기 접근 (Security Filter): 브라우저 WebSocket API가 Custom Header를 지원하지 않아 JWT를 헤더에 담을 수 없는 제약 확인.
2. STOMP 인터셉터 (ChannelInterceptor): CONNECT 프레임에 JWT를 실어 보냈으나, Handshake 성공(101 응답) 후에야 인증 실패가 확인되어 클라이언트에게 불명확한 에러(갑작스러운 연결 종료)를 전달하는 문제 발생.
3. 최종 해결 (Ticket-Based):
- 임시 티켓 발급: 기존 HTTP JWT 인증을 통해 30초 유효한 **일회용 티켓(UUID)**을 발급.
- 사전 검증: 웹소켓 Handshake 시 URI 파라미터로 티켓 전달. HandshakeInterceptor에서 101 응답 전 검증을 완료하여 인증 실패 시 즉시 401 반환.
- 결과: URI에 JWT가 노출되지 않아 보안을 강화하고, 클라이언트에게 명확한 인증 에러 피드백 제공 가능.


## API 명세(Swagger)
- 접속 주소: http://localhost:8080/swagger-ui/index.html (어플리케이션 실행 이후)

## 🚀 실행 방법
Docket Desktop을 설치 및 실행
```bash
./gradlew clean build
./gradlew bootRun
```