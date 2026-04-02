package com.project.market_service.common.filter;

import static com.project.market_service.common.util.IpUtils.extractIp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Order(1)
@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final String MDC_TRACE_KEY = "traceId";
    private static final String MDC_REQ_KEY = "requestURI";
    private static final String MDC_IP_KEY = "clientIp";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            MDC.put(MDC_TRACE_KEY, UUID.randomUUID().toString().replace("-", "").substring(0, 8));
            MDC.put(MDC_REQ_KEY, request.getRequestURI());
            MDC.put(MDC_IP_KEY, extractIp(request));
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
