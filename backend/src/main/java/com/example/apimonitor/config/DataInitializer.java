package com.example.apimonitor.config;

import com.example.apimonitor.model.AppUser;
import com.example.apimonitor.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Creates the 3 demo users on first start. */
@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedUsers(AppUserRepository repo, PasswordEncoder encoder) {
        return args -> {
            create(repo, encoder, "admin", "admin123", "ADMIN");
            create(repo, encoder, "operator", "operator123", "OPERATOR");
            create(repo, encoder, "viewer", "viewer123", "VIEWER");
        };
    }

    private void create(AppUserRepository repo, PasswordEncoder enc, String user, String pass, String role) {
        if (repo.findByUsername(user).isEmpty()) {
            AppUser u = new AppUser();
            u.setUsername(user);
            u.setPassword(enc.encode(pass));
            u.setRole(role);
            repo.save(u);
        }
    }
}
