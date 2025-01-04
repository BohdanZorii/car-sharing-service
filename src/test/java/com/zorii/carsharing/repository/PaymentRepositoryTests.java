package com.zorii.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zorii.carsharing.model.Payment;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PaymentRepositoryTests {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Find payment by session ID")
    @Sql(scripts = "/database/setup-rentals.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/insert-payments.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-payments.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-rentals.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findBySessionId_ReturnsPayment() {
        String sessionId = "session-123";

        Optional<Payment> actual = paymentRepository.findBySessionId(sessionId);

        assertTrue(actual.isPresent());
        assertEquals(sessionId, actual.get().getSessionId());
    }
}
