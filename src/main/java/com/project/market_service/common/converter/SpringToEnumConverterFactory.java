package com.project.market_service.common.converter;

import com.project.market_service.common.enums.BaseEnum;
import com.project.market_service.common.exception.CommonErrorCode;
import com.project.market_service.common.exception.InvalidValueException;
import java.util.Arrays;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SpringToEnumConverterFactory implements ConverterFactory<String, BaseEnum> {

    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return code -> {
            if (!StringUtils.hasText(code)) {
                return null;
            }

            return Arrays.stream(targetType.getEnumConstants())
                    .filter(e -> e.getCode().equalsIgnoreCase(code))
                    .findFirst()
                    .orElseThrow(() -> new InvalidValueException(CommonErrorCode.INVALID_INPUT));
        };
    }
}
