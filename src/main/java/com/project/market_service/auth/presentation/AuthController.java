package com.project.market_service.auth.presentation;

import com.project.market_service.auth.application.port.in.AuthUseCase;
import com.project.market_service.auth.presentation.dto.LoginRequest;
import com.project.market_service.auth.presentation.dto.LoginResponse;
import com.project.market_service.auth.presentation.dto.SignUpRequest;
import com.project.market_service.auth.presentation.dto.SignUpResponse;
import com.project.market_service.auth.presentation.dto.TokenResponse;
import com.project.market_service.common.dto.ApiResult;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import com.project.market_service.common.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "로그인 및 회원가입 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @Operation(summary = "회원가입",
            security = {}
    )
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResult<SignUpResponse>> signUp(
            @Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success(authUseCase.signUp(request)));
    }

    @Operation(summary = "로그인",
            security = {}
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginResponse>> authLogin(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(authUseCase.userLogin(loginRequest))
        );
    }

    @Operation(summary = "토큰 재발급")
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResult<TokenResponse>> reissue(
            HttpServletRequest request
    ) {
        String token = JwtUtil.extractToken(request);
        return ResponseEntity.ok().body(
                ApiResult.success(authUseCase.reissue(token))
        );
    }

    @Operation(summary = "로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공 (클라이언트의 토큰도 삭제 필요)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(
            @AuthenticationPrincipal JwtUserInfo userInfo,
            HttpServletRequest request
    ) {
        String token = JwtUtil.extractToken(request);
        authUseCase.logout(userInfo.userId(), token);
        return ResponseEntity.ok().body(ApiResult.success(null));
    }

}
