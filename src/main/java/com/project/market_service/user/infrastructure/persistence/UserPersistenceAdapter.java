package com.project.market_service.user.infrastructure.persistence;

import com.project.market_service.user.application.port.out.UserPort;
import com.project.market_service.user.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public boolean existsByLoginId(String loginId) {
        return jpaUserRepository.existsByLoginId(loginId);
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return jpaUserRepository.findByLoginId(loginId);
    }

    @Override
    public Optional<String> findNameById(Long id) {
        return jpaUserRepository.findNameById(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public List<User> saveAll(Iterable<User> users) {
        return jpaUserRepository.saveAll(users);
    }

    @Override
    public void deleteAll() {
        jpaUserRepository.deleteAll();
    }
}
