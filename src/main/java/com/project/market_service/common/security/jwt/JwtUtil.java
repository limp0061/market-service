package com.project.market_service.common.security.jwt;

import static com.project.market_service.common.constants.AuthConstants.AUTH_HEADER;
import static com.project.market_service.common.constants.AuthConstants.TOKEN_PREFIX;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public final class JwtUtil {

    private JwtUtil() {
    }

    public static String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTH_HEADER);

        if (!StringUtils.hasText(authorization) || !authorization.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        return authorization.replace(TOKEN_PREFIX, "");
    }
}
