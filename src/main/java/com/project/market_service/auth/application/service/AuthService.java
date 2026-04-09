package com.project.market_service.auth.application.service;

import static com.project.market_service.common.constants.AuthConstants.TOKEN_LOG_OUT;
import static com.project.market_service.common.constants.RedisConstants.BLACKLIST_TOKEN_PREFIX;
import static com.project.market_service.common.constants.RedisConstants.REFRESH_TOKEN_PREFIX;

import com.project.market_service.auth.application.port.in.AuthUseCase;
import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.auth.presentation.dto.LoginRequest;
import com.project.market_service.auth.presentation.dto.LoginResponse;
import com.project.market_service.auth.presentation.dto.SignUpRequest;
import com.project.market_service.auth.presentation.dto.SignUpResponse;
import com.project.market_service.auth.presentation.dto.TokenResponse;
import com.project.market_service.common.exception.EntityNotFoundException;
import com.project.market_service.common.exception.UnAuthorizationException;
import com.project.market_service.common.redis.RedisManager;
import com.project.market_service.common.security.jwt.JwtProvider;
import com.project.market_service.user.application.port.out.UserPort;
import com.project.market_service.user.application.sevice.UserValidator;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserErrorCode;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements AuthUseCase {

    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserPort userPort;
    private final JwtProvider jwtProvider;
    private final RedisManager redisManager;

    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        userValidator.validateDuplicate(request.loginId());

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.signUp(
                request.userName(),
                request.loginId(),
                encodedPassword
        );

        User savedUser = userPort.save(user);

        log.info("[SignUp Success] New User Create. ID: {}, LoginId: {}", savedUser.getId(), savedUser.getLoginId());
        return SignUpResponse.from(savedUser);
    }

    @Override
    public LoginResponse userLogin(LoginRequest request) {
        User user = userPort.findByLoginId(request.loginId())
                .orElseThrow(() ->
                        new UnAuthorizationException(AuthErrorCode.LOGIN_FAILED, "LoginId: " + request.loginId()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnAuthorizationException(AuthErrorCode.LOGIN_FAILED, "LoginId: " + request.loginId());
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getLoginId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        redisManager.set(
                REFRESH_TOKEN_PREFIX + user.getId(),
                refreshToken,
                jwtProvider.getRefreshExpiration(),
                TimeUnit.MILLISECONDS
        );

        log.info("[Login Success] User: {}", user.getLoginId());
        return new LoginResponse(user.getId(), user.getLoginId(), accessToken, refreshToken);
    }

    @Override
    public TokenResponse reissue(String token) {
        if (!StringUtils.hasText(token)) {
            throw new UnAuthorizationException(AuthErrorCode.TOKEN_NOT_FOUND, "Token: " + token);
        }

        Long userId = jwtProvider.getUserId(token);

        String refreshToken = redisManager.get(REFRESH_TOKEN_PREFIX + userId, String.class);
        if (!StringUtils.hasText(refreshToken) || !refreshToken.equals(token)) {
            throw new UnAuthorizationException(AuthErrorCode.INVALID_TOKEN, "UserId: " + userId);
        }

        User user = userPort.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND, "UserId: " + userId));

        String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getLoginId(), user.getUserRole());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());
        redisManager.set(
                REFRESH_TOKEN_PREFIX + user.getId(),
                newRefreshToken,
                jwtProvider.getRefreshExpiration(),
                TimeUnit.MILLISECONDS
        );

        log.info("[Reissue Token Success] Token rotated for UserId: {}", user.getId());
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(Long userId, String accessToken) {
        redisManager.delete(REFRESH_TOKEN_PREFIX + userId);

        if (StringUtils.hasText(accessToken)) {
            redisManager.set(
                    BLACKLIST_TOKEN_PREFIX + accessToken,
                    TOKEN_LOG_OUT,
                    jwtProvider.remainExpiration(accessToken),
                    TimeUnit.MILLISECONDS);
        }

        log.info("[Logout Success] UserId: {}", userId);
    }
}
