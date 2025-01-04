package com.zorii.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zorii.carsharing.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by not existing email")
    void findByEmail_NotExistingEmail_ReturnsEmptyOptional() {
        String notExistingEmail = "nonexistent@example.com";

        Optional<User> actual = userRepository.findByEmail(notExistingEmail);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Find user by existing email")
    @Sql(scripts = "/database/insert-user-with-email.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_ExistingEmail_ReturnsUser() {
        String existingEmail = "user@example.com";

        Optional<User> actual = userRepository.findByEmail(existingEmail);

        assertTrue(actual.isPresent());
        assertEquals(existingEmail, actual.get().getEmail());
    }

    @Test
    @DisplayName("Check existence of not existing email")
    void existsByEmail_NotExistingEmail_ReturnsFalse() {
        String notExistingEmail = "nonexistent@example.com";

        boolean actual = userRepository.existsByEmail(notExistingEmail);

        assertFalse(actual);
    }

    @Test
    @DisplayName("Check existence of existing email")
    @Sql(scripts = "/database/insert-user-with-email.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        String existingEmail = "user@example.com";

        boolean actual = userRepository.existsByEmail(existingEmail);

        assertTrue(actual);
    }

    @Test
    @DisplayName("Find users by role")
    @Sql(scripts = "/database/insert-users-with-roles.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByRole_ExistingRole_ReturnsUsersList() {
        User.Role existingRole = User.Role.MANAGER;

        List<User> actual = userRepository.findByRole(existingRole);

        assertEquals(2, actual.size());
        assertTrue(actual.stream().allMatch(user -> user.getRole() == existingRole));
    }
}
