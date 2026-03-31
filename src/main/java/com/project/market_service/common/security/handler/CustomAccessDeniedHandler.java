package com.project.market_service.common.security.handler;

import static com.project.market_service.common.utils.ResponseUtils.sendError;

import com.project.market_service.auth.exception.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.info("Access Denied : " + accessDeniedException.getMessage());
        log.info("Request URI : " + request.getRequestURI());

        sendError(response, AuthErrorCode.AUTH_FORBIDDEN);
    }
}
