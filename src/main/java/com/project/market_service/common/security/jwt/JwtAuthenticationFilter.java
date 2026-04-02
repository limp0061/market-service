package com.project.market_service.common.security.jwt;

import static com.project.market_service.common.constants.RedisConstants.BLACKLIST_TOKEN_PREFIX;
import static com.project.market_service.common.util.ResponseUtils.sendError;

import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.common.exception.BusinessException;
import com.project.market_service.common.exception.CommonErrorCode;
import com.project.market_service.common.redis.RedisManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisManager redisManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = JwtUtil.extractToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (redisManager.hasKey(BLACKLIST_TOKEN_PREFIX + token)) {
                sendError(response, AuthErrorCode.TOKEN_LOGGED_OUT);
                return;
            }

            JwtUserInfo userInfo = jwtProvider.getUserInfo(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userInfo, null,
                    userInfo.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BusinessException e) {
            log.warn("JWT Authentication fail: {}", e.getMessage());
            sendError(response, e.getErrorCode());
            return;
        } catch (Exception e) {
            log.error("Unexpected Filter Error", e);
            sendError(response, CommonErrorCode.SERVER_ERROR);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/signup") ||
                path.startsWith("/api/v1/auth/reissue");
    }
}
