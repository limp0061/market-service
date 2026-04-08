package com.project.market_service.user.application.sevice;

import com.project.market_service.user.application.port.in.UserUseCase;
import com.project.market_service.user.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserUseCase {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "userNames")
    public String getName(Long id) {
        return userRepository.findNameById(id)
                .orElse("알 수 없음");
    }
}
