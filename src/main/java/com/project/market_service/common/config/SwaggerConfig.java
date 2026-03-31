package com.project.market_service.common.config;


import static com.project.market_service.common.constants.AuthConstants.JWT_SCHEME_NAME;
import static com.project.market_service.common.constants.AuthConstants.JWT_TYPE;
import static com.project.market_service.common.constants.AuthConstants.TOKEN_TYPE;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT_SCHEME_NAME);

        Components components = new Components()
                .addSecuritySchemes(JWT_SCHEME_NAME, new SecurityScheme()
                        .name(JWT_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(TOKEN_TYPE)
                        .bearerFormat(JWT_TYPE)
                );

        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }


    private Info apiInfo() {
        return new Info()
                .title("Market Service API Document")
                .description("중고 거래 서비스 API 명세서")
                .version("1.0.0");
    }

    @Bean
    public OperationCustomizer globalApiResponses() {
        return (operation, handlerMethod) -> {
            operation.getResponses().addApiResponse("400", new ApiResponse().description("잘못된 요청"));
            operation.getResponses().addApiResponse("401", new ApiResponse().description("인증 실패"));
            operation.getResponses().addApiResponse("403", new ApiResponse().description("권한 부족"));
            operation.getResponses().addApiResponse("404", new ApiResponse().description("존재하지 않는 데이터"));
            operation.getResponses().addApiResponse("409", new ApiResponse().description("중복된 데이터"));
            operation.getResponses().addApiResponse("500", new ApiResponse().description("서버 오류"));
            return operation;
        };
    }
}
