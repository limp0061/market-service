package com.project.market_service.common.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidGenerator {
    
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
