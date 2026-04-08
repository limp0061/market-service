package com.project.market_service.user.infrastructure.persistence;

import com.project.market_service.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface JpaUserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    @Query("SELECT u.name FROM User u WHERE u.id = :id")
    Optional<String> findNameById(@Param("id") Long id);
}
