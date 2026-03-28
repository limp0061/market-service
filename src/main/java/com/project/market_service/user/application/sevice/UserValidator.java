package com.project.market_service.user.application.sevice;

import com.project.market_service.common.exception.DuplicateException;
import com.project.market_service.user.domain.UserErrorCode;
import com.project.market_service.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateDuplicate(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new DuplicateException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
    }
}
