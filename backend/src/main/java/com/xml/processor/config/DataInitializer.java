package com.xml.processor.config;

import com.xml.processor.model.User;
import com.xml.processor.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // Create admin user
                User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    Set.of("ADMIN", "USER")
                );
                userRepository.save(admin);

                // Create regular user
                User user = new User(
                    "user",
                    passwordEncoder.encode("user123"),
                    Set.of("USER")
                );
                userRepository.save(user);
            }
        };
    }
} 