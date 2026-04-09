package com.project.market_service.user.application.port.out;

import com.project.market_service.user.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserPort {
    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    Optional<String> findNameById(Long id);

    Optional<User> findById(Long id);

    User save(User user);

    List<User> saveAll(Iterable<User> users);

    void deleteAll();
}
