package com.project.market_service.common.initialize;


import com.project.market_service.category.domain.Category;
import com.project.market_service.category.domain.CategoryRepository;
import com.project.market_service.user.domain.User;
import com.project.market_service.user.domain.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class DataInitialIze implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {

        String encPassword = passwordEncoder.encode("password1234!");
        User user = User.signUp("사용자", "user1", encPassword);
        User admin = User.signUpAdmin("관리자", "hong1234", encPassword);
        userRepository.saveAll(List.of(user, admin));

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);
    }
}
