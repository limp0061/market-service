package com.project.market_service.user.application.sevice;

import com.project.market_service.common.exception.DuplicateException;
import com.project.market_service.user.application.port.out.UserPort;
import com.project.market_service.user.domain.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserPort userPort;

    public void validateDuplicate(String loginId) {
        if (userPort.existsByLoginId(loginId)) {
            throw new DuplicateException(UserErrorCode.DUPLICATE_LOGIN_ID, "loginId: " + loginId);
        }
    }
}
