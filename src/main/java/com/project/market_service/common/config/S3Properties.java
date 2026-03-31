package com.project.market_service.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudflare.r2")
public record S3Properties(
        String endpoint,
        String bucket,
        String accessKey,
        String secretKey,
        String publicUrl
) {
}
