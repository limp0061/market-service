package com.project.market_service.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.market_service.common.dto.ApiResult;
import com.project.market_service.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendError(HttpServletResponse response, ErrorCode errorCode)
            throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ApiResult<Void> apiResult = ApiResult.error(errorCode);
        String responseBody = objectMapper.writeValueAsString(apiResult);

        response.getWriter().write(responseBody);
    }
}
