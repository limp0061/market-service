package com.project.market_service.auth.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.auth.presentation.dto.LoginRequest;
import com.project.market_service.auth.presentation.dto.SignUpRequest;
import com.project.market_service.config.TestRedisConfig;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import com.project.market_service.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestRedisConfig.class)
@Transactional
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User user = User.signUp(
                "홍길동",
                "hong1234",
                passwordEncoder.encode("password1234!")
        );
        userRepository.save(user);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() throws Exception {
        SignUpRequest request = new SignUpRequest("유저1", "user1", "user1234!");

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.loginId").value("user1"))
                .andDo(print());
    }

    @Test
    @DisplayName("아이디 중복 회원가입 실패")
    void signUp_duplicateLoginId_fail() throws Exception {
        SignUpRequest request = new SignUpRequest("h", "hong1234", "user1234!");

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(UserErrorCode.DUPLICATE_LOGIN_ID.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("hong1234", "password1234!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 불일치 로그인 실패")
    void login_wrongPassword_fail() throws Exception {
        LoginRequest request = new LoginRequest("hong1234", "wrongPassword1!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.LOGIN_FAILED.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 성공 - 블랙리스트 등록 확인")
    void logout_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("hong1234", "password1234!");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String accessToken = JsonPath.read(responseBody, "$.data.accessToken");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());
    }
}