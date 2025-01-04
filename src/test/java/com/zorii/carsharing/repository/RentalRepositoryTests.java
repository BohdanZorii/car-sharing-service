package com.zorii.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zorii.carsharing.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTests {

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("Find rentals by user id where actual return date is null")
    @Sql(scripts = "/database/setup-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-rentals.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserIdAndActualReturnDateIsNull_ReturnsRentals() {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        List<Rental> actual = rentalRepository.findByUserIdAndActualReturnDateIsNull(userId);

        assertEquals(1, actual.size());
        assertTrue(actual.stream().allMatch(rental -> rental.getActualReturnDate() == null));
    }

    @Test
    @DisplayName("Find rentals by user id where actual return date is not null")
    @Sql(scripts = "/database/setup-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-rentals.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserIdAndActualReturnDateIsNotNull_ReturnsRentals() {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");

        List<Rental> actual = rentalRepository.findByUserIdAndActualReturnDateIsNotNull(userId);

        assertEquals(2, actual.size());
        assertNotNull(actual.get(0).getActualReturnDate());
    }

    @Test
    @DisplayName("Find rental by id and user email")
    @Sql(scripts = "/database/setup-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-rentals.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdAndUserEmail_ReturnsRental() {
        UUID rentalId = UUID.fromString("123e4567-e89b-12d3-a456-426614174100");
        String email = "customer1@example.com";

        Optional<Rental> actual = rentalRepository.findByIdAndUserEmail(rentalId, email);

        assertTrue(actual.isPresent());
        assertEquals(rentalId, actual.get().getId());
    }

    @Test
    @DisplayName("Find rentals where return date is before today and actual return date is null")
    @Sql(scripts = "/database/setup-rentals.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/database/delete-all-rentals.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByReturnDateBeforeAndActualReturnDateIsNull_ReturnsRentals() {
        LocalDate today = LocalDate.of(2025, 1, 13);
        
        List<Rental> actual = rentalRepository.findByReturnDateBeforeAndActualReturnDateIsNull(today);

        assertEquals(2, actual.size());
        assertTrue(actual.stream().allMatch(rental -> rental.getReturnDate().isBefore(today) && rental.getActualReturnDate() == null));
    }
}
