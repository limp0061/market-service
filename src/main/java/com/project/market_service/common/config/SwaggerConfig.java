package com.project.market_service.common.config;


import static com.project.market_service.common.constants.AuthConstants.JWT_SCHEME_NAME;
import static com.project.market_service.common.constants.AuthConstants.JWT_TYPE;
import static com.project.market_service.common.constants.AuthConstants.TOKEN_TYPE;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
}
