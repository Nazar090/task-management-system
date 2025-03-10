package com.example.managementsystem.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.managementsystem.config.CustomPostgresContainer;
import com.example.managementsystem.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

@Sql(scripts = {
        "classpath:database/cleanup/cleanup-tables.sql",
        "classpath:database/initialize/add-users-and-roles.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {
    private static final CustomPostgresContainer postgresContainer =
            CustomPostgresContainer.getInstance();

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenUserWithEmailExists() {
        boolean exists = userRepository.existsByEmail("user1@example.com");
        assertTrue(exists, "User with specified email should exist");
    }

    @Test
    void findByEmail_ShouldReturnUserWithRoles_WhenUserWithEmailExists() {
        Optional<User> foundUser = userRepository.findByEmail("user2@example.com");

        assertTrue(foundUser.isPresent(),
                "User with specified email should be found");
        assertFalse(foundUser.get().getRoles().isEmpty(),
                "User should have roles eagerly loaded");
    }
}
