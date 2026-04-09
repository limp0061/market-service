package com.project.market_service.auth.application.port.in;

import com.project.market_service.auth.presentation.dto.LoginRequest;
import com.project.market_service.auth.presentation.dto.LoginResponse;
import com.project.market_service.auth.presentation.dto.SignUpRequest;
import com.project.market_service.auth.presentation.dto.SignUpResponse;
import com.project.market_service.auth.presentation.dto.TokenResponse;

public interface AuthUseCase {
    SignUpResponse signUp(SignUpRequest request);

    LoginResponse userLogin(LoginRequest request);

    TokenResponse reissue(String token);

    void logout(Long userId, String accessToken);
}
